package com.qull.springarch.test.v4;

import com.qull.springarch.core.annotation.AnnotationAttributes;
import com.qull.springarch.core.io.ClassPathResource;
import com.qull.springarch.core.type.AnnotationMetadata;
import com.qull.springarch.core.type.classreading.MetadataReader;
import com.qull.springarch.core.type.classreading.SimpleMetadataReader;
import com.qull.springarch.stereotype.Component;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 16:46
 */
public class MetadataReaderTest {
    @Test
    public void testGetMetadata()  {
        ClassPathResource resource = new ClassPathResource("com/qull/springarch/service/v4/PetStoreService.class");

        MetadataReader reader = new SimpleMetadataReader(resource);

        AnnotationMetadata amd = reader.getAnnotationMetadata();

        String annotation = Component.class.getName();

        Assert.assertTrue(amd.hasAnnotation(annotation));
        AnnotationAttributes attributes = amd.getAnnotationAttributes(annotation);
        Assert.assertEquals("petStore", attributes.get("value"));

        Assert.assertFalse(amd.isAbstract());
        Assert.assertFalse(amd.isInterface());
        Assert.assertEquals("com.qull.springarch.service.v4.PetStoreService", amd.getClassName());


    }

}
