package com.qull.springarch.beans.factory;

import com.qull.springarch.beans.ConstructorArgument;
import com.qull.springarch.beans.PropertyValue;

import java.util.List;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 15:06
 */
public interface BeanDefinition {

    public static final String SCOPE_SINGLETON = "singleton";

    public static final String SCOPE_PROTOTYPE = "prototype";

    public static final String SCOPE_DEFAULT = "";


    String getBeanClassName();

    boolean isSingleton();

    boolean isPrototype();

    void setScope(String scope);

    String getScope();

    List<PropertyValue> getPropertyValues();

    ConstructorArgument getConstructorArgument();

    String getBeanId();

    boolean hasConstructorArgumentValues();
}
