package com.qull.springarch.beans.factory.annotation;

import com.qull.springarch.beans.factory.BeanCreationException;
import com.qull.springarch.beans.factory.config.AutowireCapableBeanFactory;
import com.qull.springarch.beans.factory.config.DependencyDescriptor;
import com.qull.springarch.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/17 7:39
 */
public class AutowiredFieldElement extends InjectionElement {
    boolean required;

    public AutowiredFieldElement(Field field, boolean required, AutowireCapableBeanFactory factory) {
        super(field, factory);
        this.required = required;
    }

    public Field getField() {
        return (Field) this.member;
    }

    @Override
    public void inject(Object target) {
        Field field = this.getField();
        try {
            DependencyDescriptor descriptor = new DependencyDescriptor(field, required);

            Object value = factory.resolveDependency(descriptor);

            if (value != null) {
                ReflectionUtils.makeAccessible(field);
                field.set(target, value);
            }
        } catch (IllegalAccessException e) {
            throw new BeanCreationException("Could not autowire field : " + field, e);
        }
    }
}
