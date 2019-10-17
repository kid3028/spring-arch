package com.qull.springarch.beans.factory.config;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/15 16:39
 */
public class TypedStringValue {
    private String value;

    public TypedStringValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
