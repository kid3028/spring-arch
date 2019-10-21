package com.qull.springarch.beans.factory.support;

import com.qull.springarch.beans.ConstructorArgument;
import com.qull.springarch.beans.SimpleTypeConverter;
import com.qull.springarch.beans.TypeConverter;
import com.qull.springarch.beans.factory.BeanCreationException;
import com.qull.springarch.beans.factory.BeanDefinition;
import com.qull.springarch.beans.factory.config.ConfigurableBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 10:04
 */
public class ConstructorResolver {

    private static final Logger log = LoggerFactory.getLogger(ConstructorResolver.class);

    private AbstractBeanFactory factory;

    public ConstructorResolver(AbstractBeanFactory factory) {
        this.factory = factory;
    }
//
//    public Object autowireConstructor(BeanDefinition bd) {
//        return findConstructor(bd);
//    }
//
//    private Object findConstructor(BeanDefinition bd) {
//
//        try {
//            ConstructorArgument constructorArgument = bd.getConstructorArgument();
//            Class<?> c1 = factory.getBeanClassLoader().loadClass(bd.getBeanClassName());
//            if (constructorArgument.getArgumentCount() == 0) {
//                return c1.getConstructor();
//            }
//            List<ConstructorArgument.ValueHolder> args = constructorArgument.getArgumentValues();
//            SimpleTypeConverter converter = new SimpleTypeConverter();
//            Constructor<?>[] constructors = c1.getConstructors();
//            refind : for (Constructor<?> constructor : constructors) {
//                if (constructorArgument.getArgumentCount() == constructor.getParameterCount()) {
//                    List<Object> arguments = new ArrayList<>();
//                    Class<?>[] parameterTypes = constructor.getParameterTypes();
//                    for (int i = 0; i < parameterTypes.length; i++) {
//                        Object value = args.get(i).getValue();
//                        if(value instanceof RuntimeBeanReference) {
//                            RuntimeBeanReference ref = (RuntimeBeanReference) value;
//                            if (parameterTypes[i].isAssignableFrom(factory.getBeanClassLoader().loadClass(factory.getBeanDefinition(ref.getBeanName()).getBeanClassName()))) {
//                                arguments.add(factory.getBean(ref.getBeanName()));
//                            }else {
//                                break refind;
//                            }
//                        }else if(value instanceof TypedStringValue){
//                            TypedStringValue originValue = (TypedStringValue) value;
//                            Object converterValue = converter.convertIfNecessary(originValue.getValue(), parameterTypes[i]);
//                            arguments.add(converterValue);
//                        }
//                    }
//                    return constructor.newInstance(arguments);
//                }
//            }
//            throw new IllegalArgumentException("cannot find suitable constructor");
//        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
//            throw new IllegalArgumentException("bean of '" + bd.getBeanClassName() +"' create failed");
//        }
//    }

    /**
     * 寻找匹配的构造器，创建实例并返回
     * @param bd
     * @return
     */
    public Object autowireConstructor(final BeanDefinition bd) {
        Constructor<?> constructorToUse = null;
        Object[] argsToUse = null;
        Class<?> beanClass = null;

        try{
            // 获取bean的Class对象
            beanClass = this.factory.getBeanClassLoader().loadClass(bd.getBeanClassName());
        } catch (ClassNotFoundException e) {
            throw new BeanCreationException(bd.getBeanId() + " Instantiation of bean failed");
        }

        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(factory);
        // 获取该Class所有构造器列表
        Constructor<?>[] condidates = beanClass.getConstructors();
        // 获取BeanDefinition中解析到的构造参数
        ConstructorArgument constructorArgument = bd.getConstructorArgument();
        TypeConverter typeConverter = new SimpleTypeConverter();
        // 遍历构造器列表
        for (int i = 0; i < condidates.length; i++) {
            // 该构造器的参数列表类型
            Class<?>[] parameterTypes = condidates[i].getParameterTypes();
            // 构造参数与BeanDefinition中的不匹配，返回，开始下一次循环
            if (parameterTypes.length != constructorArgument.getArgumentCount()) {
                continue;
            }

            argsToUse = new Object[parameterTypes.length];

            // 匹配构造器
            boolean result = this.valueMatchTypes(parameterTypes, constructorArgument.getArgumentValues(), argsToUse, valueResolver, typeConverter);
            // 找到了匹配的构造器，停止遍历
            if (result) {
                constructorToUse = condidates[i];
                break;
            }
        }
        // 没有找到匹配的构造器，抛出异常
        if (constructorToUse == null) {
            throw new BeanCreationException(bd.getBeanId() + " cannot find a appropriate constructor");
        }

        try {
            // 利用构造器创建实例
            return constructorToUse.newInstance(argsToUse);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeanCreationException(bd.getBeanId() + " cannot create instance using " + constructorToUse);
        }
    }

    /**
     * 对构造器进行参数匹配
     * @param parameterTypes
     * @param valueHolders
     * @param argsToUse
     * @param valueResolver
     * @param typeConverter
     * @return
     */
    private boolean valueMatchTypes(Class<?>[] parameterTypes, List<ConstructorArgument.ValueHolder> valueHolders, Object[] argsToUse, BeanDefinitionValueResolver valueResolver, TypeConverter typeConverter) {
        // 遍历构造参数
        for (int i = 0; i < parameterTypes.length; i++) {
            // 获取参数的值，可能是TypeStringValue，也可能是RuntimeBeanReference
            ConstructorArgument.ValueHolder valueHolder = valueHolders.get(i);
            Object originValue = valueHolder.getValue();
            try{
                // 获取到真正的值
                Object resolveValue = valueResolver.resolveValueIfNecessary(originValue);
                // 如果参数类型是int， 但是值是字符串，例如“3”，还需要转型
                // 如果转型失败，则抛出异常，说明这个构造函数不可用
                Object convertValue = typeConverter.convertIfNecessary(resolveValue, parameterTypes[i]);
                // 转型成功，记录下来
                argsToUse[i] = convertValue;
            }catch(Exception e) {
                log.error("autowire value failed , {}", e);
                return false;
            }
        }
        return true;
    }

}
