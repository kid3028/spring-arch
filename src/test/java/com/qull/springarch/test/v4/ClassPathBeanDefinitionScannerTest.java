package com.qull.springarch.test.v4;

import com.qull.springarch.beans.factory.BeanDefinition;
import com.qull.springarch.beans.factory.support.DefaultBeanFactory;
import com.qull.springarch.context.annotation.ScannedGenericBeanDefinition;
import com.qull.springarch.context.annotation.ClassPathBeanDefinitionScanner;
import com.qull.springarch.core.annotation.AnnotationAttributes;
import com.qull.springarch.core.type.AnnotationMetadata;
import com.qull.springarch.stereotype.Component;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 17:22
 */
public class ClassPathBeanDefinitionScannerTest {

    @Test
    public void testParseScannedBean() {
        DefaultBeanFactory factory = new DefaultBeanFactory();

        String basePackage = "com.qull.springarch.service.v4,com.qull.springarch.dao.v4";

        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(factory);
        scanner.doScan(basePackage);

        String annotation = Component.class.getName();

        {
            BeanDefinition bd = factory.getBeanDefinition("petStore");
            Assert.assertTrue(bd instanceof ScannedGenericBeanDefinition);
            ScannedGenericBeanDefinition sgbd = (ScannedGenericBeanDefinition) bd;
            AnnotationMetadata amd = sgbd.getMetadata();
            Assert.assertTrue(amd.hasAnnotation(annotation));
            AnnotationAttributes attributes = amd.getAnnotationAttributes(annotation);
            Assert.assertEquals("petStore", attributes.get("value"));
        }
    }
}
