package com.qull.springarch.context.annotation;

import com.qull.springarch.beans.factory.BeanDefinition;
import com.qull.springarch.beans.factory.BeanDefinitionStoreException;
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


    /**
     * 对base-base-package进行扫描，并对扫描到的bean进行注册
     * @param packageToScan
     * @return
     */
    public Set<BeanDefinition> doScan(String packageToScan) {
        // 多个包使用逗号分隔
        String[] basePackages = StringUtils.tokenizeToStringArray(packageToScan, ",");

        Set<BeanDefinition> beanDefinitions = new LinkedHashSet<>();
        // 遍历base-package
        for (String basePackage : basePackages) {
            // 获取该包下所有组件，生成BeanDefinition
            Set<BeanDefinition> candidates = findCandidateComponent(basePackage);
            for (BeanDefinition candidate : candidates) {
                beanDefinitions.add(candidate);
                // 注册BeanDefinition
                registry.registerBeanDefinition(candidate.getBeanId(), candidate);
            }
        }
        return beanDefinitions;
    }

    /**
     * 获取被标记为组件的class，组成为BeanDefinition
     * @param basePackage
     * @return
     */
    private Set<BeanDefinition> findCandidateComponent(String basePackage) {
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        // 获取该下所有可访问文件，包装成资源
        Resource[] resources = this.resourceLoader.getResources(basePackage);
        for (Resource resource : resources) {

            try {
                MetadataReader reader = new SimpleMetadataReader(resource);
                // 找到是否有组件类注解  这里只计算Component注解
                if (reader.getAnnotationMetadata().hasAnnotation(Component.class.getName())) {
                    // 创建ScannedGenericBeanDefinition
                    ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(reader.getAnnotationMetadata());
                    // 生成beanName
                    String beanName = this.beanNameGenerator.generateBeanName(sbd, this.registry);
                    sbd.setId(beanName);
                    // 加入集合
                    candidates.add(sbd);
                }
            } catch (Throwable e) {
                throw new BeanDefinitionStoreException("Failed to read candidate component class : " + resource, e);
            }
        }
        return candidates;
    }
}
