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
        List<Element> children = element.elements(); // aop:config -> aop:aspect
        for (Element ele : children) {
            String localName = ele.getName(); // aspect
//            if (POINTCUT.equals(localName)) {
//                parsetPointcut(childEle, registry);
//            }else if(ADVISOR.equals(localName)) {
//                parseAdvisor(childEle, registry);
//            }else if(ASPECT.equals(localName)) {
//                parseAspect(childEle, registry);
//            }

            // 解析<aop:aspect>标签
            if (ASPECT.equals(localName)) {
                parseAspect(ele, registry);
            }
        }
        return null;
    }

    /**
     * 解析<aop:aspect>标签
     * @param aspectElement
     * @param registry
     */
    private void parseAspect(Element aspectElement, BeanDefinitionRegistry registry) {
        // id
        String aspectId = aspectElement.attributeValue(ID);
        // ref
        String aspectName = aspectElement.attributeValue(REF);

        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        List<RuntimeBeanReference> beanReferences = new ArrayList<>();

        List<Element> elements = aspectElement.elements(); // aop:aspect --> aop:pointcut aop:before aop:after aop:throwing
        boolean adviceFoundAlready = false;
        // 遍历 aop:pointcut aop:before aop:after aop:throwing
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            // 是否是通知节点 aop:before aop:after aop:throwing aop:around
            if (isAdviceNode(element)) {
                if (!adviceFoundAlready) {
                    adviceFoundAlready = true;
                    if (!StringUtils.hasText(aspectName)) {
                        return;
                    }
                    beanReferences.add(new RuntimeBeanReference(aspectName));
                }
                // 创建通知的BeanDefinition
                GenericBeanDefinition advisorDefinition = parseAdvice(aspectName, i, aspectElement, element, registry, beanDefinitions, beanReferences);
                // 收集
                beanDefinitions.add(advisorDefinition);
            }
        }
        // 遍历解析<aop:pointcut>标签
        List<Element> pointcuts = aspectElement.elements(POINTCUT);
        for (Element pointcut : pointcuts) {
            parsePointcut(pointcut, registry);
        }
        

    }


    /**
     *
     * @param aspectName 通知对象名称 tx TransactionManager
     * @param order 顺序
     * @param aspectElement <aop:aspect ref="tx"></>
     * @param adviceElement 通知节点 aop:before aop:after aop:throwing aop:around
     * @param registry
     * @param beanDefinitions
     * @param beanReferences
     * @return
     */
    private GenericBeanDefinition parseAdvice(String aspectName, int order, Element aspectElement, Element adviceElement, BeanDefinitionRegistry registry, List<BeanDefinition> beanDefinitions, List<RuntimeBeanReference> beanReferences) {
        // 记录方法
        GenericBeanDefinition methodDefinition = new GenericBeanDefinition(MethodLocatingFactory.class);
        // 通知对象 tx
        methodDefinition.getPropertyValues().add(new PropertyValue("targetBeanName", aspectName));
        // 通知方法 <aop:before pointcut-ref="tx" method="start">
        methodDefinition.getPropertyValues().add(new PropertyValue("methodName", adviceElement.attributeValue("method")));
        // 标记bean为合成bean
        methodDefinition.setSynthetic(true);

        // create instance factory definition
        GenericBeanDefinition aspectFactoryDef = new GenericBeanDefinition(AspectInstanceFactory.class);
        aspectFactoryDef.getPropertyValues().add(new PropertyValue("aspectBeanName", aspectName));
        // 标记bean为合成bean
        aspectFactoryDef.setSynthetic(true);

        // 设置切入点到通知，并完成注册
        GenericBeanDefinition adviceDef = createAdviceDefinition(adviceElement, registry, aspectName, order, methodDefinition, aspectFactoryDef, beanDefinitions, beanReferences);
        adviceDef.setSynthetic(true);

        // register the final advisor
        BeanDefinitionReaderUtils.registerWithGeneratedName(adviceDef, registry);
        return adviceDef;
    }

    /**
     * 创建通知的BeanDefinition
     * @param adviceElement aop:before aop:after aop:throwing aop:around
     * @param registry
     * @param aspectName 通知对象名称 tx
     * @param order
     * @param methodDefinition 通知方法定义
     * @param aspectFactoryDef 通知对象对象对应的切面
     * @param beanDefinitions
     * @param beanReferences
     * @return
     */
    private GenericBeanDefinition createAdviceDefinition(Element adviceElement, BeanDefinitionRegistry registry, String aspectName, int order, GenericBeanDefinition methodDefinition, GenericBeanDefinition aspectFactoryDef, List<BeanDefinition> beanDefinitions, List<RuntimeBeanReference> beanReferences) {
        // 创建BeanDefinition定义
        GenericBeanDefinition adviceDefinition = new GenericBeanDefinition(getAdviceClass(adviceElement));
        // 设置aspectName属性
        adviceDefinition.getPropertyValues().add(new PropertyValue(ASPECT_NAME_PROPERTY, aspectName));

        // 通知方法构造
        ConstructorArgument constructorArgument = adviceDefinition.getConstructorArgument();
        constructorArgument.addArgumentValue(methodDefinition);

        // 解析切入点
        Object pointcut = parsePointcutProperty(adviceElement);
        // 通知标签有point那么解析结果为BeanDefinition
        if (pointcut instanceof BeanDefinition) {
            constructorArgument.addArgumentValue(pointcut);
            beanDefinitions.add((BeanDefinition) pointcut);
        }
        // 通知标签有point-ref那么解析结果为String
        else if (pointcut instanceof String) {
            // 生成对应的运行时对象
            RuntimeBeanReference pointcutRef = new RuntimeBeanReference((String) pointcut);
            // 将pointcut作为通知的构造参数
            constructorArgument.addArgumentValue(pointcutRef);
            beanReferences.add(pointcutRef);
        }

        // 通知对象工厂
        constructorArgument.addArgumentValue(aspectFactoryDef);
        return adviceDefinition;
    }

    /**
     * 解析<aop:pointcut>标签
     * @param pointcutElement
     * @param registry
     * @return
     */
    private GenericBeanDefinition parsePointcut(Element pointcutElement, BeanDefinitionRegistry registry) {
        // id
        String id = pointcutElement.attributeValue(ID);
        // expression 切入点的最新表达式
        String expression = pointcutElement.attributeValue(EXPRESSION);

        GenericBeanDefinition pointcutDefinition = null;

        // this.parseStat.push(new PointcutEntry(id));
        // 创建 pointcut的BeanDefinition
        pointcutDefinition = createPointcutDefinition(expression);
//         pointcutDefinition.setSource(parserContext.extractSource(pointcutElement));

        String pointcutBeanName = id;
        // 注册pointcutBeanDefinition
        if (StringUtils.hasText(pointcutBeanName)) {
            registry.registerBeanDefinition(pointcutBeanName, pointcutDefinition);
        }else {
            BeanDefinitionReaderUtils.registerWithGeneratedName(pointcutDefinition, registry);
        }
        return pointcutDefinition;
    }


    /**
     * 解析出pointcut
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
            // 根据切入表达式创建BeanDefinition
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

    /**
     * 通过切面表达式创建BeanDefinition
     * @param expression
     * @return
     */
    private GenericBeanDefinition createPointcutDefinition(String expression) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition(AspectJExpressionPointcut.class);
        // 为什么要设置成prototype
        beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        // 设置为合成bean
        beanDefinition.setSynthetic(true);
        // 将切入表达式作为属性
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

    /**
     * 是否是通知节点
     * aop:before aop:after aop:throwing aop:round节点为通知节点
     * @param element
     * @return
     */
    private boolean isAdviceNode(Element element) {
        String name = element.getName();
        return (BEFORE.equals(name) || AFTER.equals(name) || AFTER_RETURNING_ELEMENT.equals(name) || AFTER_THROWING_ELEMENT.equals(name) || AROUND.equals(name));
    }
}
