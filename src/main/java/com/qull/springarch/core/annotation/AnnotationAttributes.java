package com.qull.springarch.core.annotation;

import com.qull.springarch.util.Assert;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 15:45
 */
public class AnnotationAttributes extends LinkedHashMap<String, Object> {

    public AnnotationAttributes() {

    }

    public AnnotationAttributes(int initialCapacity) {
        super(initialCapacity);
    }

    public AnnotationAttributes(Map<String, Object> map) {
        super(map);
    }

    public String getString(String attributeName) {
        return doGet(attributeName, String.class);
    }

    public String[] getStringArray(String attributeName) {
        return doGet(attributeName, String[].class);
    }

    public boolean getBoolean(String attributeName) {
        return doGet(attributeName, Boolean.class);
    }

    public <N extends Number> N getNumber(String attributeName) {
        return (N) doGet(attributeName, Integer.class);
    }

    public <E extends Enum<?>> E getEnum(String attributeName) {
        return (E) doGet(attributeName, Enum.class);
    }

    public <T> Class<? extends T> getClass(String attributeName) {
        return doGet(attributeName, Class.class);
    }

    public Class<?>[] getClassArray(String attributeName) {
        return doGet(attributeName, Class[].class);
    }

    private <T> T doGet(String attributeName, Class<T> expectedType) {
        Object value = this.get(attributeName);
        Assert.notNull(value, "Attribute " + attributeName + " not found");
        return (T) value;
    }
 }
