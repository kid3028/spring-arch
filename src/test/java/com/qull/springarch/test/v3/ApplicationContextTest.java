package com.qull.springarch.test.v3;

import com.qull.springarch.context.ApplicationContext;
import com.qull.springarch.context.support.ClassPathXmlApplicationContext;
import com.qull.springarch.dao.v3.AccountDao;
import com.qull.springarch.dao.v3.ItemDao;
import com.qull.springarch.service.v3.PetStoreService;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 9:01
 */
public class ApplicationContextTest {

    @Test
    public void test() {
        ApplicationContext context = new ClassPathXmlApplicationContext("petstore-v3.xml");
        PetStoreService petStore = (PetStoreService) context.getBean("petStore");

        Assert.assertNotNull(petStore.getAccountDao());
        Assert.assertNotNull(petStore.getItemDao());

        Assert.assertTrue(petStore.getAccountDao() instanceof AccountDao);
        Assert.assertTrue(petStore.getItemDao() instanceof ItemDao);


    }
}
