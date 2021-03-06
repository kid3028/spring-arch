package com.qull.springarch.beans;

import com.qull.springarch.beans.exception.TypeMismatchException;
import com.qull.springarch.beans.propertyeditors.CustomBooleanEditor;
import com.qull.springarch.beans.propertyeditors.CustomNumberEditor;
import com.qull.springarch.util.ClassUtils;

import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/15 11:11
 */
public class SimpleTypeConverter implements TypeConverter {

    private Map<Class<?>, PropertyEditor> defaultEditors;

    public SimpleTypeConverter(){}

    /**
     * 进行值转换
     * @param value
     * @param requiredType
     * @param <T>
     * @return
     * @throws TypeMismatchException
     */
    @Override
    public <T> T convertIfNecessary(Object value, Class<T> requiredType) throws TypeMismatchException {
        // 如果当前值能够赋予到requiredType，执行进行转换
        if (ClassUtils.isAssignableValue(requiredType, value)) {
            return (T)value;
        }else {
            // 如果是字符串
            if (value instanceof String) {
                // 查找对应的editor
                PropertyEditor editor = findDefaultEditor(requiredType);
                try {
                    // 设置为editor属性
                    editor.setAsText((String) value);
                }catch (IllegalArgumentException e) {
                    throw new TypeMismatchException(value, requiredType);
                }
                // 获取editor转换出来的值
                return (T)editor.getValue();
            }else {
                throw new RuntimeException("Todo : can't convert value for " + value + " class : " + requiredType);
            }
        }
    }

    private <T> PropertyEditor findDefaultEditor(Class<T> requiredType) {
        PropertyEditor editor = this.getDefaultEditor(requiredType);
        if (editor == null) {
            throw new RuntimeException("Editor for " + requiredType + " has not been implemented");
        }
        return editor;
    }

    private <T> PropertyEditor getDefaultEditor(Class<T> requiredType) {
        if (this.defaultEditors == null) {
            this.createDefaultEditors();
        }
        return this.defaultEditors.get(requiredType);
    }

    private void createDefaultEditors() {
        this.defaultEditors = new HashMap<>(64);

        // Spring's CustomBooleanEditor accepts more flag values than JDK's default editor
        this.defaultEditors.put(boolean.class, new CustomBooleanEditor(false));
        this.defaultEditors.put(Boolean.class, new CustomBooleanEditor(true));

        // The JDK does not contain default editors for number wrapper types!
        // Override JDK primitive number editors with our own CustomNumberEditor.
		/*this.defaultEditors.put(byte.class, new CustomNumberEditor(Byte.class, false));
		this.defaultEditors.put(Byte.class, new CustomNumberEditor(Byte.class, true));
		this.defaultEditors.put(short.class, new CustomNumberEditor(Short.class, false));
		this.defaultEditors.put(Short.class, new CustomNumberEditor(Short.class, true));*/
        this.defaultEditors.put(int.class, new CustomNumberEditor(Integer.class, false));
        this.defaultEditors.put(Integer.class, new CustomNumberEditor(Integer.class, true));
		/*this.defaultEditors.put(long.class, new CustomNumberEditor(Long.class, false));
		this.defaultEditors.put(Long.class, new CustomNumberEditor(Long.class, true));
		this.defaultEditors.put(float.class, new CustomNumberEditor(Float.class, false));
		this.defaultEditors.put(Float.class, new CustomNumberEditor(Float.class, true));
		this.defaultEditors.put(double.class, new CustomNumberEditor(Double.class, false));
		this.defaultEditors.put(Double.class, new CustomNumberEditor(Double.class, true));
		this.defaultEditors.put(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, true));
		this.defaultEditors.put(BigInteger.class, new CustomNumberEditor(BigInteger.class, true));*/

    }
}
