package com.qull.springarch.test.v2;

import com.qull.springarch.context.ApplicationContext;
import com.qull.springarch.context.support.ClassPathXmlApplicationContext;
import com.qull.springarch.service.v2.PetStoreService;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/15 21:47
 */
public class ApplicationContextTest {

    @Test
    public void testGetBean() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("petstore-v2.xml");
        PetStoreService petStore = (PetStoreService) applicationContext.getBean("petStore");

        Assert.assertNotNull(petStore.getAccountDao());
        Assert.assertNotNull(petStore.getItemDao());

        Assert.assertEquals("qull", petStore.getOwner());
        Assert.assertEquals(2, petStore.getVersion());

    }
}
