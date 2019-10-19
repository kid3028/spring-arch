package com.qull.springarch.aop.config;

import com.qull.springarch.aop.aspectj.AspectJAfterReturningAdvice;
import com.qull.springarch.aop.aspectj.AspectJAfterThrowingAdvice;
import com.qull.springarch.aop.aspectj.AspectJBeforeAdvice;
import com.qull.springarch.aop.aspectj.AspectJExpressionPointcut;
import com.qull.springarch.beans.ConstructorArgument;
import com.qull.springarch.beans.PropertyValue;
import com.qull.springarch.beans.factory.BeanDefinition;
import com.qull.springarch.beans.factory.config.RuntimeBeanReference;
import com.qull.springarch.beans.factory.support.BeanDefinitionReaderUtils;
import com.qull.springarch.beans.factory.support.BeanDefinitionRegistry;
import com.qull.springarch.beans.factory.support.GenericBeanDefinition;
import com.qull.springarch.util.StringUtils;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/19 13:53
 */
public class ConfigBeanDefinitionParse /*implements  BeanDefinitionParse */{
    private static final String ASPECT = "aspect";

    private static final String EXPRESSION = "expression";

    private static final String ID = "id";

    private static final String POINTCUT = "pointcut";

    private static final String POINTCUT_REF = "pointcut-ref";

    private static final String REF = "ref";

    private static final String BEFORE = "before";

    private static final String AFTER = "after";

    private static final String AFTER_RETURNING_ELEMENT = "after-returning";

    private static final String AFTER_THROWING_ELEMENT = "after-throwing";

    private static final String AROUND = "around";

    private static final String ASPECT_NAME_PROPERTY = "aspectName";

    private static final String ADVISOR = "advisor";

    public BeanDefinition parse(Element element, BeanDefinitionRegistry registry) {
        List<Element> children = element.elements();
        for (Element ele : children) {
            String localName = ele.getName();
//            if (POINTCUT.equals(localName)) {
//                parsetPointcut(childEle, registry);
//            }else if(ADVISOR.equals(localName)) {
//                parseAdvisor(childEle, registry);
//            }else if(ASPECT.equals(localName)) {
//                parseAspect(childEle, registry);
//            }

            if (ASPECT.equals(localName)) {
                parseAspect(ele, registry);
            }
        }
        return null;
    }

    private void parseAspect(Element aspectElement, BeanDefinitionRegistry registry) {
        String aspectId = aspectElement.attributeValue(ID);
        String aspectName = aspectElement.attributeValue(REF);

        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        List<RuntimeBeanReference> beanReferences = new ArrayList<>();

        List<Element> elements = aspectElement.elements();
        boolean adviceFoundAlready = false;
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            if (isAdviceNode(element)) {
                if (!adviceFoundAlready) {
                    adviceFoundAlready = true;
                    if (!StringUtils.hasText(aspectName)) {
                        return;
                    }
                    beanReferences.add(new RuntimeBeanReference(aspectName));
                }
                GenericBeanDefinition advisorDefinition = parseAdvice(aspectName, i, aspectElement, element, registry, beanDefinitions, beanReferences);
                beanDefinitions.add(advisorDefinition);
            }
        }
        
        List<Element> pointcuts = aspectElement.elements(POINTCUT);
        for (Element pointcut : pointcuts) {
            parsePointcut(pointcut, registry);
        }
        

    }


    private GenericBeanDefinition parseAdvice(String aspectName, int order, Element aspectElement, Element adviceElement, BeanDefinitionRegistry registry, List<BeanDefinition> beanDefinitions, List<RuntimeBeanReference> beanReferences) {
        GenericBeanDefinition methodDefinition = new GenericBeanDefinition(MethodLocatingFactory.class);
        methodDefinition.getPropertyValues().add(new PropertyValue("targetBeanName", aspectName));
        methodDefinition.getPropertyValues().add(new PropertyValue("methodName", adviceElement.attributeValue("method")));
        methodDefinition.setSynthetic(true);

        // create instance factory definition
        GenericBeanDefinition aspectFactoryDef = new GenericBeanDefinition(AspectInstanceFactory.class);
        aspectFactoryDef.getPropertyValues().add(new PropertyValue("aspectBeanName", aspectName));
        aspectFactoryDef.setSynthetic(true);

        // register the pointcut
        GenericBeanDefinition adviceDef = createAdviceDefinition(adviceElement, registry, aspectName, order, methodDefinition, aspectFactoryDef, beanDefinitions, beanReferences);
        adviceDef.setSynthetic(true);

        // register the final advisor
        BeanDefinitionReaderUtils.registerWithGeneratedName(adviceDef, registry);
        return adviceDef;
    }

    private GenericBeanDefinition createAdviceDefinition(Element adviceElement, BeanDefinitionRegistry registry, String aspectName, int order, GenericBeanDefinition methodDefinition, GenericBeanDefinition aspectFactoryDef, List<BeanDefinition> beanDefinitions, List<RuntimeBeanReference> beanReferences) {
        GenericBeanDefinition adviceDefinition = new GenericBeanDefinition(getAdviceClass(adviceElement));
        adviceDefinition.getPropertyValues().add(new PropertyValue(ASPECT_NAME_PROPERTY, aspectName));

        ConstructorArgument constructorArgument = adviceDefinition.getConstructorArgument();
        constructorArgument.addArgumentValue(methodDefinition);

        Object pointcut = parsePointcutProperty(adviceElement);
        if (pointcut instanceof BeanDefinition) {
            constructorArgument.addArgumentValue(pointcut);
            beanDefinitions.add((BeanDefinition) pointcut);
        }else if (pointcut instanceof String) {
            RuntimeBeanReference pointcutRef = new RuntimeBeanReference((String) pointcut);
            constructorArgument.addArgumentValue(pointcutRef);
            beanReferences.add(pointcutRef);
        }
        constructorArgument.addArgumentValue(aspectFactoryDef);
        return adviceDefinition;
    }

    private GenericBeanDefinition parsePointcut(Element pointcutElement, BeanDefinitionRegistry registry) {
        String id = pointcutElement.attributeValue(ID);
        String expression = pointcutElement.attributeValue(EXPRESSION);

        GenericBeanDefinition pointcutDefinition = null;

        // this.parseStat.push(new PointcutEntry(id));
        pointcutDefinition = createPointcutDefinition(expression);
//         pointcutDefinition.setSource(parserContext.extractSource(pointcutElement));

        String pointcutBeanName = id;
        if (StringUtils.hasText(pointcutBeanName)) {
            registry.registerBeanDefinition(pointcutBeanName, pointcutDefinition);
        }else {
            BeanDefinitionReaderUtils.registerWithGeneratedName(pointcutDefinition, registry);
        }
        return pointcutDefinition;
    }


    /**
     * Parse the {@code pointcut} or {@code pointcut-ref} attributes of the supplied {@link Element} and add a {@code pointcut}
     * property as appropriate. Generates a {@link BeanDefinition} for the pointcut if necessary and returns
     * its bean name, otherwise return the bean name of the referred pointcut
     * @param adviceElement
     */
    private Object parsePointcutProperty(Element adviceElement) {
        if (adviceElement.attribute(POINTCUT) == null &&  adviceElement.attribute(POINTCUT_REF) == null) {
            /*parserContext.getReaderContext().error(
					"Cannot define both 'pointcut' and 'pointcut-ref' on <advisor> tag.",
					element, this.parseState.snapshot());*/
            return null;
        }else if(adviceElement.attribute(POINTCUT) != null) {
            // Create a pointcut for the anonymous pc and register it
            String expression = adviceElement.attributeValue(POINTCUT);
            GenericBeanDefinition pointcutDefinition = createPointcutDefinition(expression);
//            pointcutDefinition.setSource(parserContext.extractSource(adviceElement));
            return pointcutDefinition;
        }else if (adviceElement.attribute(POINTCUT_REF) != null) {
            String pointcutRef = adviceElement.attributeValue(POINTCUT_REF);
            if (!StringUtils.hasText(pointcutRef)) {
//                parserContext.getReaderContext().error("'pointcut' attribute contains empty value", element, this.parseState.snapshot())
                return null;
            }
            return pointcutRef;
        }else {
//            parserContext.getReaderContext().error("Must define one of 'pointcut' or 'pointcut-ref' on <advisor> tag", adviceElement, this.parseStat.snapshot());
            return null;
        }
    }

    private GenericBeanDefinition createPointcutDefinition(String expression) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition(AspectJExpressionPointcut.class);
        beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        beanDefinition.setSynthetic(true);
        beanDefinition.getPropertyValues().add(new PropertyValue(EXPRESSION, expression));
        return beanDefinition;
    }

    /**
     * Get the advice implementation class corresponding to the supplied {@link Element}
     * @param adviceElement
     * @return
     */
    private Class<?> getAdviceClass(Element adviceElement) {
        String elementName = adviceElement.getName();
        if (BEFORE.equals(elementName)) {
            return AspectJBeforeAdvice.class;
        }
        /*else if(AFTER.equals(elementName)) {
            return AspectJAfterAdvice.class;
        }*/
        else if(AFTER_RETURNING_ELEMENT.equals(elementName)) {
            return AspectJAfterReturningAdvice.class;
        }else if(AFTER_THROWING_ELEMENT.equals(elementName)) {
            return AspectJAfterThrowingAdvice.class;
        } 
        /*else if (AROUND.equals(elementName)) {
            return AspectJAroundAdvice.class;
        }*/
        else {
            throw new IllegalArgumentException("Unknown advice kind [" + elementName + "]");
        }
    }

    private boolean isAdviceNode(Element element) {
        String name = element.getName();
        return (BEFORE.equals(name) || AFTER.equals(name) || AFTER_RETURNING_ELEMENT.equals(name) || AFTER_THROWING_ELEMENT.equals(name) || AROUND.equals(name));
    }
}
