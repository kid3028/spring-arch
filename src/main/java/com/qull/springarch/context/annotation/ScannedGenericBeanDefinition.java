package com.qull.springarch.context.annotation;

import com.qull.springarch.beans.factory.annotation.AnnotationBeanDefinition;
import com.qull.springarch.beans.factory.support.GenericBeanDefinition;
import com.qull.springarch.core.type.AnnotationMetadata;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 17:30
 */
public class ScannedGenericBeanDefinition extends GenericBeanDefinition implements AnnotationBeanDefinition {

    private final AnnotationMetadata metadata;

    public ScannedGenericBeanDefinition(AnnotationMetadata metadata) {
        super();
        this.metadata = metadata;
        setBeanClassName(this.metadata.getClassName());
    }

    public AnnotationMetadata getMetadata() {
        return this.metadata;
    }
}
