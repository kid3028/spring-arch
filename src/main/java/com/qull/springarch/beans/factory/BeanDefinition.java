package com.qull.springarch.beans.factory;

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

}
