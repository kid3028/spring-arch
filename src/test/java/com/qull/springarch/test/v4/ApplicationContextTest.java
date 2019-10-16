package com.qull.springarch.test.v4;

import com.qull.springarch.context.ApplicationContext;
import com.qull.springarch.context.support.ClassPathXmlApplicationContext;
import com.qull.springarch.service.v4.PetStoreService;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 14:02
 */
public class ApplicationContextTest {

    @Test
    public void testGetBean() {
        ApplicationContext context = new ClassPathXmlApplicationContext("petstore-v4.xml");
        PetStoreService petStore = (PetStoreService) context.getBean("petStore");

        Assert.assertNotNull(petStore.getAccountDao());
        Assert.assertNotNull(petStore.getItemDao());
    }
}
