package com.qull.springarch.test.v1;

import com.qull.springarch.context.ApplicationContext;
import com.qull.springarch.context.support.FileSystemXmlApplicationContext;
import com.qull.springarch.context.support.ClassPathXmlApplicationContext;
import com.qull.springarch.service.v1.PetStoreService;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 21:23
 */
public class ApplicationContextTest {

    @Test
    public void testClassPathGetBean() {
        ApplicationContext context = new ClassPathXmlApplicationContext("petstore-v1.xml");
        PetStoreService petStoreService = (PetStoreService) context.getBean("petStore");
        Assert.assertNotNull(petStoreService);
    }

    @Test
    public void testFileSystemGetBean() {
        ApplicationContext context = new FileSystemXmlApplicationContext("C:\\develop\\study\\day5\\spring-arch\\src\\test\\resources\\petstore-v1.xml");
        PetStoreService petStoreService = (PetStoreService) context.getBean("petStore");
        Assert.assertNotNull(petStoreService);
    }
}
