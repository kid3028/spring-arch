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

    @Override
    public Object  getBean(String beanId) {
        BeanDefinition bd = this.getBeanDefinition(beanId);
        if (bd == null) {
            log.error("bean {} not exists", beanId);
            throw new BeanCreationException("bean '" + beanId + "' not exists");
        }
        if (bd.isSingleton()) {
            Object bean = this.getSingleton(beanId);
            if (bean == null) {
                bean = createBean(bd);
                this.registrySingleton(beanId, bean);
            }
            return bean;
        }
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

    public List<Object> getBeansByType(Class<?> type) {
        List<Object> result = new ArrayList<>();
        List<String> beanIds = this.getBeanIdByType(type);
        for (String beanId : beanIds) {
            result.add(this.getBean(beanId));
        }
        return result;
    }

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

        bean = initializeBean(bd, bean);
        return bean;
    }

    private Object initializeBean(BeanDefinition bd, Object bean) {
        invokeAwareMethods(bean);
        // TODO 调用Bean的init方法暂不实现
        if (!bd.isSynthetic()) {
            return applyBeanPostProcessorAfterInitialization(bean, bd.getBeanId());
        }
        return bean;
    }

    private Object applyBeanPostProcessorAfterInitialization(Object existingBean, String beanName) {
        Object result = existingBean;
        for (BeanPostProcessor beanProcessor : getBeanPostProcessors()) {
            result = beanProcessor.afterInitialization(result, beanName);
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

    private void populateBean(BeanDefinition bd, Object bean) {

        for (BeanPostProcessor processor : this.getBeanPostProcessors()) {
            if(processor instanceof InstantiationAwareBeanPostProcessor) {
                ((InstantiationAwareBeanPostProcessor) processor).postProcessPropertyValue(bean, bd.getBeanId());
            }
        }

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
                Object resolvedValue = valueResolver.resolveValueIfNecessary(originalValue);

                BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
                PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor pd : pds) {
                    if (pd.getName().equals(propertyName)) {
                        Object convertedValue = converter.convertIfNecessary(resolvedValue, pd.getPropertyType());
                        pd.getWriteMethod().invoke(bean, convertedValue);
                        break;
                    }
                }
            }
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            throw new BeanCreationException("Failed to obtain BeanInfo for class [ " + bd.getBeanClassName() + "]", e);
        }
    }

    private Object instantiateBean(BeanDefinition bd) {
        if (bd.hasConstructorArgumentValues()) {
            ConstructorResolver resolver = new ConstructorResolver(this);
            return resolver.autowireConstructor(bd);
        }else {
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
