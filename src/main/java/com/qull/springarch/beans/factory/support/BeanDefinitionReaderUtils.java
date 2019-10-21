package com.qull.springarch.beans.factory.support;

import com.qull.springarch.beans.factory.BeanDefinition;
import com.qull.springarch.beans.factory.BeanDefinitionStoreException;
import com.qull.springarch.util.StringUtils;

/**
 * Utility method that are useful for bean definition reader implementations.
 * Mainly intended fo internal use
 * @author kzh
 * @description
 * @DATE 2019/10/19 19:58
 */
public class BeanDefinitionReaderUtils {

    /**
     * Separator for generated bean names. If a class name or parent name is not unique,
     * "#1", "#2" etc will the append, until the name becomes unique
     */
    public static final String GENERATED_BEAN_NAME_SEPARATOR = "#";

    /**
     * Generate a bean name for the given bean definition, unique within the given bean factory.
     *
     * @param definition the bean definition to generate a bean name for
     * @param registry the bean factory that the definition is going to be registered with (to check for existing bean name)
     * @param isInnerBean whether the given bean definition will be registered as inner bean or as top-level bean(allowing
     *                    for special name generation for inner beans versus top-level beans)
     * @return the generated bean name
     * @throws BeanDefinitionStoreException
     */
    public static String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry, boolean isInnerBean) throws BeanDefinitionStoreException {
        String generatedBeanName = definition.getBeanClassName();
        if (generatedBeanName == null) {

        }
        if (!StringUtils.hasText(generatedBeanName)) {
            throw new BeanDefinitionStoreException("Unnamed bean definition specifies neither class nor parent not factory-bean - can't generate bean name");
        }
        String id = generatedBeanName;
        if (isInnerBean) {
            // Inner bean : generate identity hashcode suffix
            id = generatedBeanName + GENERATED_BEAN_NAME_SEPARATOR + Integer.toHexString(System.identityHashCode(definition));
        }else {
            // Top-level bean: use plain class name
            // increase counter until the id is unique
            int counter = -1;
            if (counter == -1 || (registry.getBeanDefinition(id) != null)) {
                counter++;
                id = generatedBeanName + GENERATED_BEAN_NAME_SEPARATOR + counter;

            }
        }
        return id;
    }

    /**
     * 根据BeanDefinition生成beanName并完成注册  class#toHexString(hashcode)
     * @param beanDefinition
     * @param registry
     * @return
     * @throws BeanDefinitionStoreException
     */
    public static String registerWithGeneratedName(BeanDefinition beanDefinition, BeanDefinitionRegistry registry) throws BeanDefinitionStoreException {
        String generatedName = generateBeanName(beanDefinition, registry, false);
        registry.registerBeanDefinition(generatedName, beanDefinition);
        return generatedName;
    }

}
