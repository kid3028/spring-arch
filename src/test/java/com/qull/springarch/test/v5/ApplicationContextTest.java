package com.qull.springarch.test.v5;

import com.qull.springarch.context.support.ClassPathXmlApplicationContext;
import com.qull.springarch.service.v5.PetStoreService;
import com.qull.springarch.util.MessageTracker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/17 17:19
 */
public class ApplicationContextTest {

    @Before
    public void setup() {
        MessageTracker.clear();
    }

    @Test
    public void testPlaceOrder() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("petstore-v5.xml");
        PetStoreService petStore = (PetStoreService) context.getBean("petStore");

        Assert.assertNotNull(petStore.getAccountDao());
        Assert.assertNotNull(petStore.getItemDao());

        petStore.placeOrder();

        List<String> msgs = MessageTracker.getMsgs();

        Assert.assertEquals(3, msgs.size());
        Assert.assertEquals("start tx...", msgs.get(0));
        Assert.assertEquals("place order...", msgs.get(1));
        Assert.assertNotNull("commit tx...", msgs.get(2));

    }
}
