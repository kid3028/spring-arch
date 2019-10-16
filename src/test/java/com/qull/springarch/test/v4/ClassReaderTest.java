package com.qull.springarch.test.v4;

import com.qull.springarch.core.annotation.AnnotationAttributes;
import com.qull.springarch.core.io.ClassPathResource;
import com.qull.springarch.core.type.classreading.AnnotationMetadataReadingVisitor;
import com.qull.springarch.core.type.classreading.ClassMetadataReadingVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.asm.ClassReader;

import java.io.IOException;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 14:56
 */
public class ClassReaderTest {

    @Test
    public void testGetClassMetadata() throws IOException {
        ClassPathResource resource = new ClassPathResource("com/qull/springarch/service/v4/PetStoreService.class");
        ClassReader reader = new ClassReader(resource.getInputStream());

        ClassMetadataReadingVisitor visitor = new ClassMetadataReadingVisitor();
        reader.accept(visitor, ClassReader.SKIP_DEBUG);

        Assert.assertFalse(visitor.isAbstract());
        Assert.assertFalse(visitor.isInterface());
        Assert.assertFalse(visitor.isFinal());
        Assert.assertEquals("com.qull.springarch.service.v4.PetStoreService", visitor.getClassName());
        Assert.assertEquals("java.lang.Object", visitor.getSuperClassName());
        Assert.assertEquals(0, visitor.getInterfaceNames().length);
    }

    @Test
    public void testGetAnnotation() throws IOException {
        ClassPathResource resource = new ClassPathResource("com/qull/springarch/service/v4/PetStoreService.class");
        ClassReader reader = new ClassReader(resource.getInputStream());

        AnnotationMetadataReadingVisitor vistor = new AnnotationMetadataReadingVisitor();
        reader.accept(vistor, ClassReader.SKIP_DEBUG);

        String annotation = "com.qull.springarch.stereotype.Component";
        Assert.assertTrue(vistor.hasAnnotation(annotation));
        AnnotationAttributes attributes = vistor.getAnnotationAttributes(annotation);
        Assert.assertEquals("petStore", attributes.get("value"));
    }
}
