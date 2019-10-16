package com.qull.springarch.core.type.classreading;

import com.qull.springarch.core.io.Resource;
import com.qull.springarch.core.type.AnnotationMetadata;
import com.qull.springarch.core.type.ClassMetadata;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 16:49
 */
public interface MetadataReader {
    Resource getResource();
    ClassMetadata getClassMetadata();
    AnnotationMetadata getAnnotationMetadata();
}
