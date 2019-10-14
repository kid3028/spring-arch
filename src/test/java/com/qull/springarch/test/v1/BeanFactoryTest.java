package com.qull.springarch.test.v1;

import com.qull.springarch.beans.factory.BeanCreationException;
import com.qull.springarch.beans.factory.BeanDefinition;
import com.qull.springarch.beans.factory.BeanDefinitionStoreExpcetion;
import com.qull.springarch.beans.factory.BeanFactory;
import com.qull.springarch.beans.factory.support.DefaultBeanFactory;
import com.qull.springarch.service.v1.PetStoreService;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 14:57
 */
public class BeanFactoryTest {

    @Test
    public void testGetBean() {
        BeanFactory factory = new DefaultBeanFactory("petstore-v1.xml");
        BeanDefinition bd = factory.getBeanDefinition("petStore");

        Assert.assertEquals("com.qull.springarch.service.v1.PetStoreService", bd.getBeanClassName());

        PetStoreService petStore = (PetStoreService) factory.getBean("petStore");
        Assert.assertNotNull(petStore);
    }

    @Test
    public void testInvalidBean() {
        BeanFactory factory = new DefaultBeanFactory("petstore-v1.xml");
        try {
            factory.getBean("invalidBean");
        }catch (BeanCreationException e) {
            return;
        }
        Assert.fail();
    }

    @Test(expected = BeanDefinitionStoreExpcetion.class)
    public void testInvalidXML() {
        BeanFactory factory = new DefaultBeanFactory("invalid-v1.xml");
    }


}
