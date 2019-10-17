package com.qull.springarch.beans.factory.annotation;

import com.qull.springarch.beans.exception.BeansException;
import com.qull.springarch.beans.factory.BeanCreationException;
import com.qull.springarch.beans.factory.config.AutowireCapableBeanFactory;
import com.qull.springarch.beans.factory.config.InstantiationAwareBeanPostProcessor;
import com.qull.springarch.core.annotation.AnnotationUtils;
import com.qull.springarch.stereotype.Autowired;
import com.qull.springarch.util.ReflectionUtils;

import java.awt.print.PrinterAbortException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.PublicKey;
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

    public InjectionMetadata buildAutowiringMetadata(Class<?> clazz) {
        LinkedList<InjectionElement> elements =  new LinkedList<>();
        Class<?> targetClass = clazz;

        do {
            LinkedList<InjectionElement> currElements = new LinkedList<>();
            for (Field field : targetClass.getDeclaredFields()) {
                Annotation annotation = findAutowiredAnnotation(field);
                if (annotation != null) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    boolean required = determineRequiredStatus(annotation);
                    currElements.add(new AutowiredFieldElement(field, required, factory));
                }
            }
            for (Method method : targetClass.getDeclaredMethods()) {
                // 处理方法注入
            }
            elements.addAll(0, currElements);
            targetClass = targetClass.getSuperclass();
        }while (targetClass != null && targetClass != Object.class);
        return new InjectionMetadata(clazz, elements);
    }

    private boolean determineRequiredStatus(Annotation annotation) {
        try {
            Method method = ReflectionUtils.findMethod(annotation.annotationType(), this.requiredParameterName);
            if (method == null) {
                // Annotation like @Inject and @Value don't have a method (attribute) named 'require'
                // -> default to required status
                return true;
            }
            return (this.requiredParameterValue == (Boolean) ReflectionUtils.invokeMethod(method, annotation));
        }catch (Exception e) {
            // An exception was thrown during reflective invocation of the required attribute
            // -> default to required status
            return true;
        }

    }

    private Annotation findAutowiredAnnotation(AccessibleObject accessibleObject) {
        for (Class<? extends Annotation> type : this.autowiredAnnotationTypes) {
            Annotation annotaion = AnnotationUtils.getAnnotaion(accessibleObject, type);
            if (annotaion != null) {
                return annotaion;
            }
        }
        return null;
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

    @Override
    public void postProcessPropertyValue(Object bean, String beanName) throws BeansException {
        InjectionMetadata injectionMetadata = this.buildAutowiringMetadata(bean.getClass());
        try {
            injectionMetadata.inject(bean);
        }catch (Throwable e) {
            throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", e);
        }
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
