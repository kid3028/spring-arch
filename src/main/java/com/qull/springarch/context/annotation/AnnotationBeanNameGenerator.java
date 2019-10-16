package com.qull.springarch.context.annotation;

import com.qull.springarch.beans.factory.BeanDefinition;
import com.qull.springarch.beans.factory.annotation.AnnotationBeanDefinition;
import com.qull.springarch.beans.factory.support.BeanDefinitionRegistry;
import com.qull.springarch.beans.factory.support.BeanNameGenerator;
import com.qull.springarch.core.annotation.AnnotationAttributes;
import com.qull.springarch.core.type.AnnotationMetadata;
import com.qull.springarch.util.ClassUtils;
import com.qull.springarch.util.StringUtils;

import java.beans.Introspector;
import java.util.Set;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 21:28
 */
public class AnnotationBeanNameGenerator implements BeanNameGenerator {
    @Override
    public String generateBeanNAme(BeanDefinition bd, BeanDefinitionRegistry registry) {
        if (bd instanceof AnnotationBeanDefinition) {
            String beanName = determineBeanNameFromAnnotation((AnnotationBeanDefinition) bd);
            if (StringUtils.hasText(beanName)) {
                return beanName;
            }
        }
        return buildDefaultBeanName(bd, registry);
    }

    private String buildDefaultBeanName(BeanDefinition bd, BeanDefinitionRegistry registry) {
        return buildDefaultBeanName(bd);
    }

    private String buildDefaultBeanName(BeanDefinition bd) {
        String shortClassName = ClassUtils.getShortName(bd.getBeanClassName());
        return Introspector.decapitalize(shortClassName);
    }

    private String determineBeanNameFromAnnotation(AnnotationBeanDefinition annotationDef) {
        AnnotationMetadata amd = annotationDef.getMetadata();
        Set<String> types = amd.getAnnotationTypes();
        String beanName = null;
        for (String type : types) {
            AnnotationAttributes attributes = amd.getAnnotationAttributes(type);
            if (attributes.get("value") != null) {
                Object value = attributes.get("value");
                if (value instanceof String) {
                    String strValue = (String) value;
                    if (StringUtils.hasText(strValue)) {
                        beanName = strValue;
                    }
                }
            }
        }
        return beanName;
    }
}
