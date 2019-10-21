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
    /**
     * 生成beanName
     * @param bd
     * @param registry
     * @return
     */
    @Override
    public String generateBeanName(BeanDefinition bd, BeanDefinitionRegistry registry) {
        if (bd instanceof AnnotationBeanDefinition) {
            // 尝试从注解中获取beanName
            String beanName = determineBeanNameFromAnnotation((AnnotationBeanDefinition) bd);
            // beanName不空，返回
            if (StringUtils.hasText(beanName)) {
                return beanName;
            }
        }
        //使用默认方式生成beanName 注解中获取beanName失败，以当前bean 的class名称生成beanName
        return buildDefaultBeanName(bd, registry);
    }

    /**
     * 使用默认方式生成beanName
     * @param bd
     * @param registry
     * @return
     */
    private String buildDefaultBeanName(BeanDefinition bd, BeanDefinitionRegistry registry) {
        return buildDefaultBeanName(bd);
    }

    /**
     * 默认方式生成beanName
     * 获取class名称作为beanName
     * @param bd
     * @return
     */
    private String buildDefaultBeanName(BeanDefinition bd) {
        String shortClassName = ClassUtils.getShortName(bd.getBeanClassName());
        return Introspector.decapitalize(shortClassName);
    }

    /**
     * 尝试从注解中获取beanName
     * @param annotationDef
     * @return
     */
    private String determineBeanNameFromAnnotation(AnnotationBeanDefinition annotationDef) {
        // 获取bean的注解元数据
        AnnotationMetadata amd = annotationDef.getMetadata();
        Set<String> types = amd.getAnnotationTypes();
        String beanName = null;
        for (String type : types) {
            AnnotationAttributes attributes = amd.getAnnotationAttributes(type);
            // 以注解中value的属性值作为beanName
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
