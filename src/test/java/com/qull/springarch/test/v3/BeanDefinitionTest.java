package com.qull.springarch.test.v3;

import com.qull.springarch.beans.factory.BeanDefinition;
import com.qull.springarch.beans.ConstructorArgument;
import com.qull.springarch.beans.factory.support.DefaultBeanFactory;
import com.qull.springarch.beans.factory.xml.XmlBeanDefinitionReader;
import com.qull.springarch.config.RuntimeBeanReference;
import com.qull.springarch.config.TypedStringValue;
import com.qull.springarch.core.io.ClassPathResource;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 9:14
 */
public class BeanDefinitionTest {
    @Test
    public void testConstructorArgument() {
        DefaultBeanFactory factory = new DefaultBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions(new ClassPathResource("petstore-v3.xml"));
        BeanDefinition bd = factory.getBeanDefinition("petStore");

        Assert.assertEquals("com.qull.springarch.service.v3.PetStoreService", bd.getBeanClassName());

        ConstructorArgument args = bd.getConstructorArgument();
        List<ConstructorArgument.ValueHolder> valueHolders = args.getArgumentValues();

        Assert.assertEquals(3, valueHolders.size());

        RuntimeBeanReference ref1 = (RuntimeBeanReference) valueHolders.get(0).getValue();
        Assert.assertEquals("accountDao", ref1.getBeanName());
        RuntimeBeanReference ref2 = (RuntimeBeanReference) valueHolders.get(1).getValue();
        Assert.assertEquals("itemDao", ref2.getBeanName());

        TypedStringValue value1 = (TypedStringValue) valueHolders.get(2).getValue();
        Assert.assertEquals("3", value1.getValue());
        


    }
}
