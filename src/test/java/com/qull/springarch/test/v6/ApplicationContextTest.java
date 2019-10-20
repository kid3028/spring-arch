package com.qull.springarch.test.v6;

import com.qull.springarch.context.support.ClassPathXmlApplicationContext;
import com.qull.springarch.service.v6.IPetStoreService;
import com.qull.springarch.util.MessageTracker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/19 23:54
 */
public class ApplicationContextTest {

    @Test
    public void testGetBeanProperty() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("petstore-v6.xml");
        IPetStoreService petStoreService = (IPetStoreService) context.getBean("petStore");

        System.out.println(petStoreService);

        petStoreService.placeOrder();

        List<String> msgs = MessageTracker.getMsgs();

        Assert.assertEquals(3, msgs.size());
        Assert.assertEquals("start tx...", msgs.get(0));
        Assert.assertEquals("place order...", msgs.get(1));
        Assert.assertEquals("commit tx...", msgs.get(2));
    }

    @Before
    public void setup() {
        MessageTracker.clear();
    }
}
