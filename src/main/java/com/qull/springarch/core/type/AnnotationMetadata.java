package com.qull.springarch.core.type;

import com.qull.springarch.core.annotation.AnnotationAttributes;

import java.util.Set;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 16:09
 */
public interface AnnotationMetadata extends ClassMetadata{

    Set<String> getAnnotationTypes();

    boolean hasAnnotation(String annotationType);

    AnnotationAttributes getAnnotationAttributes(String annotationType);
}
