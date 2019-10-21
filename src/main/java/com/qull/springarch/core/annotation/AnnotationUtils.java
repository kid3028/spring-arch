package com.qull.springarch.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/17 9:15
 */
public class AnnotationUtils {
    public static <T extends Annotation> T getAnnotation(AnnotatedElement ae, Class<T> annotationType) {
        T ann = ae.getAnnotation(annotationType);
        if (ann == null) {
            for (Annotation metaAnnotation : ae.getAnnotations()) {
                ann = metaAnnotation.annotationType().getAnnotation(annotationType);
                if (ann != null) {
                    break;
                }
            }
        }
        return ann;
    }
}
