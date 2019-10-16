package com.qull.springarch.test.v3;

import com.qull.springarch.beans.factory.BeanDefinition;
import com.qull.springarch.beans.factory.support.ConstructorResolver;
import com.qull.springarch.beans.factory.support.DefaultBeanFactory;
import com.qull.springarch.beans.factory.xml.XmlBeanDefinitionReader;
import com.qull.springarch.core.io.ClassPathResource;
import com.qull.springarch.service.v3.PetStoreService;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 10:00
 */
public class ConstructorResolverTest {

    @Test
    public void testAutowireConstructor() {
        DefaultBeanFactory factory = new DefaultBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions(new ClassPathResource("petstore-v3.xml"));
        BeanDefinition bd = factory.getBeanDefinition("petStore");

        ConstructorResolver resolver = new ConstructorResolver(factory);
        PetStoreService petStoreService = (PetStoreService) resolver.autowireConstructor(bd);

        Assert.assertNotNull(petStoreService.getAccountDao());
        Assert.assertNotNull(petStoreService.getItemDao());

    }
}
