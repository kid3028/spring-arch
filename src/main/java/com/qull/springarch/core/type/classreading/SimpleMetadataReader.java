package com.qull.springarch.core.type.classreading;

import com.qull.springarch.beans.factory.BeanDefinitionStoreException;
import com.qull.springarch.core.io.Resource;
import com.qull.springarch.core.type.AnnotationMetadata;
import com.qull.springarch.core.type.ClassMetadata;
import com.qull.springarch.util.Assert;
import org.springframework.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 16:54
 */
public class SimpleMetadataReader implements MetadataReader {

    private final ClassMetadata classMetadata;

    private final AnnotationMetadata annotationMetadata;

    private final Resource resource;

    public SimpleMetadataReader(Resource resource) {
        Assert.notNull(resource, "Resource must be not null");
        ClassReader reader;
        try(InputStream is = resource.getInputStream()) {
            reader = new ClassReader(resource.getInputStream());
        }catch (IOException e) {
            throw new BeanDefinitionStoreException("resource handler failed", e);
        }
        AnnotationMetadataReadingVisitor visitor = new AnnotationMetadataReadingVisitor();
        reader.accept(visitor, ClassReader.SKIP_DEBUG);
        this.classMetadata = visitor;
        this.annotationMetadata = visitor;
        this.resource = resource;
    }

    @Override
    public Resource getResource() {
        return this.resource;
    }

    @Override
    public ClassMetadata getClassMetadata() {
        return this.classMetadata;
    }

    @Override
    public AnnotationMetadata getAnnotationMetadata() {
        return this.annotationMetadata;
    }
}
