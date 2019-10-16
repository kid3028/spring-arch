package com.qull.springarch.beans;


import lombok.Data;
import lombok.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 9:18
 */
public class ConstructorArgument {
    private final List<ValueHolder> argumentValues = new ArrayList<>();

    public void addArgumentValue(Object value, String type) {
        this.argumentValues.add(new ValueHolder(value, type));
    }

    public void addValueHolder(ValueHolder valueHolder) {
        this.argumentValues.add(valueHolder);
    }

    public List<ValueHolder> getArgumentValues() {
        return Collections.unmodifiableList(this.argumentValues);
    }

    public int getArgumentCount() {
        return this.argumentValues.size();
    }

    public boolean isEmpty() {
        return this.argumentValues.isEmpty();
    }

    public void clear() {
        this.argumentValues.clear();
    }
    @Data
    public static class ValueHolder {
        private Object value;
        private String type;
        private String name;

        public ValueHolder(Object value) {
            this.value = value;
        }

        public ValueHolder(Object value, String type) {
            this.value = value;
            this.type = type;
        }

        public ValueHolder(Object value, String type, String name) {
            this.value = value;
            this.type = type;
            this.name = name;
        }
    }


}
