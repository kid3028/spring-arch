package com.qull.springarch.beans.factory.annotation;

import com.qull.springarch.beans.exception.BeansException;
import com.qull.springarch.beans.factory.BeanCreationException;
import com.qull.springarch.beans.factory.config.AutowireCapableBeanFactory;
import com.qull.springarch.beans.factory.config.InstantiationAwareBeanPostProcessor;
import com.qull.springarch.core.annotation.AnnotationUtils;
import com.qull.springarch.stereotype.Autowired;
import com.qull.springarch.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/17 7:30
 */
public class AutowiredAnnotationProcessor implements InstantiationAwareBeanPostProcessor {

    private AutowireCapableBeanFactory factory;

    private String requiredParameterName = "required";

    private boolean requiredParameterValue = true;

    private final Set<Class<? extends Annotation>> autowiredAnnotationTypes = new LinkedHashSet<>();

    public AutowiredAnnotationProcessor() {
        this.autowiredAnnotationTypes.add(Autowired.class);
    }

    /**
     * 获取该类下可进行依赖注入的元数据
     * @param clazz
     * @return
     */
    public InjectionMetadata buildAutowiringMetadata(Class<?> clazz) {
        LinkedList<InjectionElement> elements =  new LinkedList<>();
        Class<?> targetClass = clazz;

        do {
            LinkedList<InjectionElement> currElements = new LinkedList<>();
            // 遍历该class的所有属性
            for (Field field : targetClass.getDeclaredFields()) {
                // 寻找该字段上可用于依赖注入的注解
                Annotation annotation = findAutowiredAnnotation(field);
                // 注解不空，添加到自动注入元素列表
                if (annotation != null) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    // 判断是否是必要的
                    boolean required = determineRequiredStatus(annotation);
                    // 添加到自动注入元素集合中
                    currElements.add(new AutowiredFieldElement(field, required, factory));
                }
            }
            for (Method method : targetClass.getDeclaredMethods()) {
                // 处理方法注入
            }
            // 加入自动注入集合
            elements.addAll(0, currElements);
            // 向父类进行查找
            targetClass = targetClass.getSuperclass();
        }while (targetClass != null && targetClass != Object.class);
        // 构建InjectionMetadata对象返回， 包含该类及该类的父类所需要的所有自动注入依赖
        return new InjectionMetadata(clazz, elements);
    }

    /**
     * 判断一个自动注入的字段是否是必须的
     * @param annotation
     * @return
     */
    private boolean determineRequiredStatus(Annotation annotation) {
        try {
            // 获取 this.requiredParameterName参数对应的方法
            Method method = ReflectionUtils.findMethod(annotation.annotationType(), this.requiredParameterName);
            if (method == null) {
                // Annotation like @Inject and @Value don't have a method (attribute) named 'require'
                // -> default to required status
                return true;
            }
            // 调用required方法获取值
            return (this.requiredParameterValue == (Boolean) ReflectionUtils.invokeMethod(method, annotation));
        }catch (Exception e) {
            // An exception was thrown during reflective invocation of the required attribute
            // -> default to required status
            return true;
        }

    }

    public void setBeanFactory(AutowireCapableBeanFactory factory) {
        this.factory = factory;
    }


    @Override
    public Object beforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    @Override
    public boolean afterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    /**
     * 通过依赖注入完成属性赋值
     * 1.扫描该class下所有的可注入依赖
     * 2.遍历依赖信息，执行注入
     * @param bean
     * @param beanName
     * @throws BeansException
     */
    @Override
    public void postProcessPropertyValue(Object bean, String beanName) throws BeansException {
        // 获取可自动注入的依赖信息
        InjectionMetadata injectionMetadata = this.buildAutowiringMetadata(bean.getClass());
        try {
            injectionMetadata.inject(bean);
        }catch (Throwable e) {
            throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", e);
        }
    }

    /**
     * 获取该对象上可以进行依赖注入的注解
     * @param accessibleObject
     * @return
     */
    private Annotation findAutowiredAnnotation(AccessibleObject accessibleObject) {
        // this.autowiredAnnotationTypes 支持依赖注入的注解
        for (Class<? extends Annotation> type : this.autowiredAnnotationTypes) {
            // 解析该对象上type类型的注解
            Annotation annotaion = AnnotationUtils.getAnnotation(accessibleObject, type);
            // 找到一个即可返回
            if (annotaion != null) {
                return annotaion;
            }
        }
        return null;
    }

    @Override
    public Object beforeInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    @Override
    public Object afterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
