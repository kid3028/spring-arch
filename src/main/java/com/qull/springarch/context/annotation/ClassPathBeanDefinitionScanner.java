package com.qull.springarch.context.annotation;

import com.qull.springarch.beans.factory.BeanDefinition;
import com.qull.springarch.beans.factory.BeanDefinitionStoreExpcetion;
import com.qull.springarch.beans.factory.support.BeanDefinitionRegistry;
import com.qull.springarch.beans.factory.support.BeanNameGenerator;
import com.qull.springarch.core.io.Resource;
import com.qull.springarch.core.io.support.PackageResourceLoader;
import com.qull.springarch.core.type.classreading.MetadataReader;
import com.qull.springarch.core.type.classreading.SimpleMetadataReader;
import com.qull.springarch.stereotype.Component;
import com.qull.springarch.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 17:29
 */
public class ClassPathBeanDefinitionScanner {

    private static final Logger log = LoggerFactory.getLogger(ClassPathBeanDefinitionScanner.class);

    private final BeanDefinitionRegistry registry;

    private PackageResourceLoader resourceLoader = new PackageResourceLoader();

    private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }


    public Set<BeanDefinition> doScan(String packageToScan) {
        String[] basePackages = StringUtils.tokenizeToStringArray(packageToScan, ",");

        Set<BeanDefinition> beanDefinitions = new LinkedHashSet<>();
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidates = findCandidateComponent(basePackage);
            for (BeanDefinition candidate : candidates) {
                beanDefinitions.add(candidate);
                registry.registerBeanDefinition(candidate.getBeanId(), candidate);
            }
        }
        return beanDefinitions;
    }

    private Set<BeanDefinition> findCandidateComponent(String basePackage) {
        Set<BeanDefinition> candidates = new LinkedHashSet<>();

        Resource[] resources = this.resourceLoader.getResources(basePackage);
        for (Resource resource : resources) {

            try {
                MetadataReader reader = new SimpleMetadataReader(resource);
                if (reader.getAnnotationMetadata().hasAnnotation(Component.class.getName())) {
                    ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(reader.getAnnotationMetadata());
                    String beanName = this.beanNameGenerator.generateBeanNAme(sbd, this.registry);
                    sbd.setId(beanName);
                    candidates.add(sbd);
                }
            } catch (Throwable e) {
                throw new BeanDefinitionStoreExpcetion("Failed to read candidate component class : " + resource, e);
            }
        }
        return candidates;
    }
}
