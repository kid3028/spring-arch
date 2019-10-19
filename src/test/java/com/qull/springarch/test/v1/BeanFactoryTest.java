package com.qull.springarch.test.v1;

import com.qull.springarch.beans.factory.BeanCreationException;
import com.qull.springarch.beans.factory.BeanDefinition;
import com.qull.springarch.beans.factory.BeanDefinitionStoreException;
import com.qull.springarch.beans.factory.support.DefaultBeanFactory;
import com.qull.springarch.beans.factory.xml.XmlBeanDefinitionReader;
import com.qull.springarch.core.io.ClassPathResource;
import com.qull.springarch.service.v1.PetStoreService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 14:57
 */
public class BeanFactoryTest {

    private DefaultBeanFactory factory;

    private XmlBeanDefinitionReader reader;

    @Before
    public void setup() {
        factory = new DefaultBeanFactory();
        reader = new XmlBeanDefinitionReader(factory);
    }

    @Test
    public void testGetBean() {
        reader.loadBeanDefinitions(new ClassPathResource("petstore-v1.xml"));
        BeanDefinition bd = factory.getBeanDefinition("petStore");
        Assert.assertEquals("com.qull.springarch.service.v1.PetStoreService", bd.getBeanClassName());
        Assert.assertTrue(bd.isSingleton());
        Assert.assertFalse(bd.isPrototype());
        Assert.assertEquals(BeanDefinition.SCOPE_DEFAULT, bd.getScope());
        PetStoreService petStore = (PetStoreService) factory.getBean("petStore");
        Assert.assertNotNull(petStore);
        PetStoreService petStore2 = (PetStoreService) factory.getBean("petStore");
        Assert.assertEquals(petStore, petStore2);
    }

    @Test
    public void testInvalidBean() {
        reader.loadBeanDefinitions(new ClassPathResource("petstore-v1.xml"));
        try {
            factory.getBean("invalidBean");
        }catch (BeanCreationException e) {
            return;
        }
        Assert.fail();
    }

    @Test(expected = BeanDefinitionStoreException.class)
    public void testInvalidXML() {
        reader.loadBeanDefinitions(new ClassPathResource("invalid-v1.xml"));
    }


}
