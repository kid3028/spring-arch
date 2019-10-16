package com.qull.springarch.core.type.classreading;

import com.qull.springarch.core.annotation.AnnotationAttributes;
import com.qull.springarch.core.type.AnnotationMetadata;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Type;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 16:08
 */
public class AnnotationMetadataReadingVisitor extends ClassMetadataReadingVisitor implements AnnotationMetadata {

    private final Set<String> annotationSet = new LinkedHashSet<>();

    private final Map<String, AnnotationAttributes> attributesMap = new LinkedHashMap<>();

    public AnnotationMetadataReadingVisitor() {}

    @Override
    public AnnotationVisitor visitAnnotation(final String desc, boolean visible) {
        String className = Type.getType(desc).getClassName();
        this.annotationSet.add(className);
        return new AnnotationAttributesReadingVisitor(className, this.attributesMap);
    }

    @Override
    public Set<String> getAnnotationTypes() {
        return this.annotationSet;
    }

    @Override
    public boolean hasAnnotation(String annotationType) {
        return this.annotationSet.contains(annotationType);
    }

    @Override
    public AnnotationAttributes getAnnotationAttributes(String annotationType) {
        return this.attributesMap.get(annotationType);
    }
}
