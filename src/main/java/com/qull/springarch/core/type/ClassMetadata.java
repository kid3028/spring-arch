package com.qull.springarch.core.type;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 16:10
 */
public interface ClassMetadata {

    String getClassName();

    boolean isInterface();

    boolean isAbstract();

    boolean isFinal();

    boolean hasSuperClass();

    String getSuperClassName();

    String[] getInterfaceNames();
}
