package com.qull.springarch.test.v4;

import com.qull.springarch.beans.factory.annotation.AutowiredAnnotationProcessor;
import com.qull.springarch.beans.factory.annotation.AutowiredFieldElement;
import com.qull.springarch.beans.factory.annotation.InjectionElement;
import com.qull.springarch.beans.factory.annotation.InjectionMetadata;
import com.qull.springarch.beans.factory.config.DependencyDescriptor;
import com.qull.springarch.beans.factory.support.DefaultBeanFactory;
import com.qull.springarch.dao.v4.AccountDao;
import com.qull.springarch.dao.v4.ItemDao;
import com.qull.springarch.service.v4.PetStoreService;
import org.junit.Assert;
import org.junit.Test;

import javax.print.attribute.standard.MediaSize;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/17 7:17
 */
public class AutowiredAnnotationProcessorTest {
    AccountDao accountDao = new AccountDao();
    ItemDao itemDao = new ItemDao();
    DefaultBeanFactory factory = new DefaultBeanFactory(){
        @Override
        public Object resolveDependency(DependencyDescriptor descriptor) {
            if (descriptor.getDependencyType().equals(AccountDao.class)) {
                return accountDao;
            }
            if (descriptor.getDependencyType().equals(itemDao.getClass())) {
                return itemDao;
            }
            throw new RuntimeException("Can't support types except AccountDao and ItemDao");
        }
    };

    @Test
    public void testGetInjectionMetadata() {
        AutowiredAnnotationProcessor processor = new AutowiredAnnotationProcessor();
        processor.setBeanFactory(factory);
        InjectionMetadata injectionMetadata = processor.buildAutowiringMetadata(PetStoreService.class);
        List<InjectionElement> elements = injectionMetadata.getInjectionElements();
        Assert.assertEquals(2, elements.size());
        assertFieldExists(elements, "accountDao");
        assertFieldExists(elements, "itemDao");

        PetStoreService petStoreService = new PetStoreService();

        injectionMetadata.inject(petStoreService);

        Assert.assertTrue(petStoreService.getAccountDao() instanceof AccountDao);
        Assert.assertTrue(petStoreService.getItemDao() instanceof ItemDao);

    }

    private void assertFieldExists(List<InjectionElement> elements, String fieldName) {
        for (InjectionElement element : elements) {
            AutowiredFieldElement fieldElement = (AutowiredFieldElement) element;
            Field field = fieldElement.getField();
            if (field.getName().equals(fieldName)) {
                return;
            }
        }
        Assert.fail(fieldName + " dose not exists");
    }
}
