package com.qull.springarch.beans.factory.support;

import com.qull.springarch.beans.PropertyValue;
import com.qull.springarch.beans.SimpleTypeConverter;
import com.qull.springarch.beans.factory.BeanCreationException;
import com.qull.springarch.beans.factory.BeanDefinition;
import com.qull.springarch.beans.factory.BeanFactoryAware;
import com.qull.springarch.beans.factory.NoSuchBeanDefinitionException;
import com.qull.springarch.beans.factory.config.BeanPostProcessor;
import com.qull.springarch.beans.factory.config.ConfigurableBeanFactory;
import com.qull.springarch.beans.factory.config.DependencyDescriptor;
import com.qull.springarch.beans.factory.config.InstantiationAwareBeanPostProcessor;
import com.qull.springarch.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 15:06
 */
public class DefaultBeanFactory extends AbstractBeanFactory implements BeanDefinitionRegistry{

    private static final Logger log = LoggerFactory.getLogger(DefaultBeanFactory.class);

    private ClassLoader classLoader;

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    public DefaultBeanFactory() {

    }

    @Override
    public BeanDefinition getBeanDefinition(String beanId) {
        return this.beanDefinitionMap.get(beanId);
    }

    @Override
    public void registerBeanDefinition(String beanId, BeanDefinition bd) {
        this.beanDefinitionMap.put(beanId, bd);
    }

    /**
     * 获取执行名称的bean
     * 1.如果bean是单例的，尝试从缓存拿，没有则进行创建，并缓存
     * 2.bean不是单例的，每次都会创建出新的bean
     * @param beanId
     * @return
     */
    @Override
    public Object getBean(String beanId) {
        // 获取到指定bean的定义
        BeanDefinition bd = this.getBeanDefinition(beanId);
        if (bd == null) {
            log.error("bean {} not exists", beanId);
            throw new BeanCreationException("bean '" + beanId + "' not exists");
        }
        // bean是单例
        if (bd.isSingleton()) {
            Object bean = this.getSingleton(beanId);
            if (bean == null) {
                // 创建bean
                bean = createBean(bd);
                // 缓存创建好bean
                this.registrySingleton(beanId, bean);
            }
            return bean;
        }
        // 非单例bean
        return createBean(bd);

    }

    @Override
    public Class<?> getType(String beanId) throws NoSuchBeanDefinitionException {
        BeanDefinition bd = this.getBeanDefinition(beanId);
        if (bd == null) {
            throw new NoSuchBeanDefinitionException(beanId);
        }
        this.resolveBeanClass(bd);
        return bd.getBeanClass();
    }

    /**
     * 根据class获取该类型下的bean
     * @param type
     * @return
     */
    public List<Object> getBeansByType(Class<?> type) {
        List<Object> result = new ArrayList<>();
        List<String> beanIds = this.getBeanIdByType(type);
        // 遍历获取每一个bean
        for (String beanId : beanIds) {
            result.add(this.getBean(beanId));
        }
        return result;
    }

    /**
     * 获取指定类型下所有的bean的id
     * @param type
     * @return
     */
    public List<String> getBeanIdByType(Class<?> type) {
        List<String> result = new ArrayList<>();
        for (String beanName : this.beanDefinitionMap.keySet()) {
            if (type.isAssignableFrom(this.getType(beanName))) {
                result.add(beanName);
            }
        }
        return result;
    }

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.classLoader = beanClassLoader;
    }

    @Override
    public ClassLoader getBeanClassLoader() {
        return (this.classLoader != null ? this.classLoader : ClassUtils.getDefaultClassLoader());
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor postProcessor) {
        this.beanPostProcessors.add(postProcessor);
    }

    @Override
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }

    protected Object createBean(BeanDefinition bd) {
        // 创建实例
        Object bean = instantiateBean(bd);
        // 设置属性
        populateBean(bd, bean);
        // 1.为BeanFactoryAware设置BeanFactory 2.对非合成bean应用后置处理器
        bean = initializeBean(bd, bean);
        return bean;
    }

    private Object initializeBean(BeanDefinition bd, Object bean) {
        // 传递factory到BeanFactoryAware
        invokeAwareMethods(bean);
        // TODO 调用Bean的init方法暂不实现
        // bean不是合成的
        if (!bd.isSynthetic()) {
            // 调用后处理器
            return applyBeanPostProcessorAfterInitialization(bean, bd.getBeanId());
        }
        return bean;
    }

    /**
     * 对非合成的bean应用后置处理
     * @param existingBean
     * @param beanName
     * @return
     */
    private Object applyBeanPostProcessorAfterInitialization(Object existingBean, String beanName) {
        Object result = existingBean;
        for (BeanPostProcessor beanProcessor : getBeanPostProcessors()) {
            // 调用后置处理  自动注入  切面处理
            result = beanProcessor.afterInitialization(result, beanName);
            // 如果后置处理返回了null，那么该方法也返回null
            if (result == null) {
                return result;
            }
        }
        return result;
    }

    private void invokeAwareMethods(Object bean) {
        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(this);
        }
    }

    /**
     * 填充属性
     * 1.依赖注入  如果InjectMetadata中injectElements不为空，依次执行依赖注入
     * 2.属性值注入 属性列表不为空，解析并转换属性，设置属性值。
     * @param bd
     * @param bean
     */
    private void populateBean(BeanDefinition bd, Object bean) {

        for (BeanPostProcessor processor : this.getBeanPostProcessors()) {
            // 筛选出所有的InstantiationAwareBeanPostProcessor类型的处理器，执行后置处理
            // 这里是AutowiredAnnotationProcessor处理器，进行属性依赖注入
            if(processor instanceof InstantiationAwareBeanPostProcessor) {
                // dependency inject
                ((InstantiationAwareBeanPostProcessor) processor).postProcessPropertyValue(bean, bd.getBeanId());
            }
        }

        // 属性列表
        List<PropertyValue> pvs = bd.getPropertyValues();
        if (pvs == null || pvs.isEmpty()) {
            return;
        }

        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this);
        SimpleTypeConverter converter = new SimpleTypeConverter();
        
        try {
            for (PropertyValue pv : pvs) {
                String propertyName = pv.getName();
                Object originalValue = pv.getValue();
                // 解析属性值
                Object resolvedValue = valueResolver.resolveValueIfNecessary(originalValue);

                BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
                PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor pd : pds) {
                    if (pd.getName().equals(propertyName)) {
                        // 进行值转换，主要是针对 数值型字符串-->数值型 的转换
                        Object convertedValue = converter.convertIfNecessary(resolvedValue, pd.getPropertyType());
                        // 对属性进行赋值
                        pd.getWriteMethod().invoke(bean, convertedValue);
                        break;
                    }
                }
            }
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            throw new BeanCreationException("Failed to obtain BeanInfo for class [ " + bd.getBeanClassName() + "]", e);
        }
    }

    /**
     * 创建bean
     * @param bd
     * @return
     */
    private Object instantiateBean(BeanDefinition bd) {
        // 如果bean指定了构造器
        if (bd.hasConstructorArgumentValues()) {
            ConstructorResolver resolver = new ConstructorResolver(this);
            // 寻找匹配的构造器，创建实例并返回
            return resolver.autowireConstructor(bd);
        }
        // 没有指定构造器，直接newInstance
        else {
            ClassLoader c1 = this.getBeanClassLoader();
            String beanClassName = bd.getBeanClassName();
            try {
                Class<?> beanClass = c1.loadClass(beanClassName);
                return beanClass.newInstance();
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new BeanCreationException("create bean for '" + bd.getBeanClassName() + "' fail", e);
            }
        }
    }

    @Override
    public Object resolveDependency(DependencyDescriptor descriptor) {
        Class<?> typeToMatch = descriptor.getDependencyType();
        for (BeanDefinition bd : this.beanDefinitionMap.values()) {
            // 确保BeanDe有class对象
            resolveBeanClass(bd);
            Class<?> beanClass = bd.getBeanClass();
            if (typeToMatch.isAssignableFrom(beanClass)) {
                return this.getBean(bd.getBeanId());
            }
        }
        return null;
    }

    private void resolveBeanClass(BeanDefinition bd) {
        if (bd.hasBeanClass()) {
            return;
        }else {
            try {
                bd.resolveBeanClass(this.getBeanClassLoader());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Can't load class : " + bd.getBeanClassName());
            }
        }
    }
}
