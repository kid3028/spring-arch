package com.qull.springarch.test.v4;

import com.qull.springarch.beans.factory.BeanDefinition;
import com.qull.springarch.beans.factory.support.DefaultBeanFactory;
import com.qull.springarch.beans.factory.xml.XmlBeanDefinitionReader;
import com.qull.springarch.context.annotation.ClassPathBeanDefinitionScanner;
import com.qull.springarch.context.annotation.ScannedGenericBeanDefinition;
import com.qull.springarch.core.annotation.AnnotationAttributes;
import com.qull.springarch.core.io.ClassPathResource;
import com.qull.springarch.core.type.AnnotationMetadata;
import com.qull.springarch.stereotype.Component;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 22:31
 */
public class XmlBeanDefinitionReaderTest {

    @Test
    public void testParseScanBean() {
        DefaultBeanFactory factory = new DefaultBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions(new ClassPathResource("petstore-v4.xml"));
        String annotation = Component.class.getName();

        {
            BeanDefinition bd = factory.getBeanDefinition("petStore");
            Assert.assertTrue(bd instanceof ScannedGenericBeanDefinition);
            ScannedGenericBeanDefinition sbd = (ScannedGenericBeanDefinition) bd;
            AnnotationMetadata amd = sbd.getMetadata();
            Assert.assertTrue(amd.hasAnnotation(annotation));
            AnnotationAttributes attributes = amd.getAnnotationAttributes(annotation);
            Assert.assertEquals("petStore", attributes.get("value"));
        }

        {
            BeanDefinition bd = factory.getBeanDefinition("accountDao");
            Assert.assertTrue(bd instanceof ScannedGenericBeanDefinition);
            ScannedGenericBeanDefinition sbd = (ScannedGenericBeanDefinition) bd;
            AnnotationMetadata amd = sbd.getMetadata();
            Assert.assertTrue(amd.hasAnnotation(annotation));
        }

        {
            BeanDefinition bd = factory.getBeanDefinition("itemDao");
            Assert.assertTrue(bd instanceof ScannedGenericBeanDefinition);
            ScannedGenericBeanDefinition sbd = (ScannedGenericBeanDefinition) bd;
            AnnotationMetadata amd = sbd.getMetadata();
            Assert.assertTrue(amd.hasAnnotation(annotation));
        }
    }
}
