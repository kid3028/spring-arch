package com.qull.springarch.beans.factory.config;

import com.qull.springarch.util.Assert;

import java.lang.reflect.Field;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/17 7:22
 */
public class DependencyDescriptor {
    private Field field;

    private boolean required;

    public DependencyDescriptor(Field field, boolean required) {
        Assert.notNull(field, "field must not be null");
        this.field = field;
        this.required = required;
    }

    public Class<?> getDependencyType() {
        if (this.field != null) {
            return field.getType();
        }
        throw new RuntimeException("only support field dependency");
    }

    public boolean isRequired() {
        return this.required;
    }
}
