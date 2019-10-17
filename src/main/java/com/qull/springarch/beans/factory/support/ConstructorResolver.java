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

    private ConfigurableBeanFactory factory;

    public ConstructorResolver(ConfigurableBeanFactory factory) {
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

    public Object autowireConstructor(final BeanDefinition bd) {
        Constructor<?> constructorToUse = null;
        Object[] argsToUse = null;
        Class<?> beanClass = null;

        try{
            beanClass = this.factory.getBeanClassLoader().loadClass(bd.getBeanClassName());
        } catch (ClassNotFoundException e) {
            throw new BeanCreationException(bd.getBeanId() + " Instantiation of bean failed");
        }

        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(factory);
        Constructor<?>[] condidates = beanClass.getConstructors();
        ConstructorArgument constructorArgument = bd.getConstructorArgument();
        TypeConverter typeConverter = new SimpleTypeConverter();
        for (int i = 0; i < condidates.length; i++) {
            Class<?>[] parameterTypes = condidates[i].getParameterTypes();
            if (parameterTypes.length != constructorArgument.getArgumentCount()) {
                continue;
            }

            argsToUse = new Object[parameterTypes.length];

            boolean result = this.valueMatchTypes(parameterTypes, constructorArgument.getArgumentValues(), argsToUse, valueResolver, typeConverter);
            if (result) {
                constructorToUse = condidates[i];
                break;
            }
        }
        if (constructorToUse == null) {
            throw new BeanCreationException(bd.getBeanId() + " cannot find a appropriate constructor");
        }

        try {
            return constructorToUse.newInstance(argsToUse);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BeanCreationException(bd.getBeanId() + " cannot create instance using " + constructorToUse);
        }
    }

    private boolean valueMatchTypes(Class<?>[] parameterTypes, List<ConstructorArgument.ValueHolder> valueHolders, Object[] argsToUse, BeanDefinitionValueResolver valueResolver, TypeConverter typeConverter) {
        for (int i = 0; i < parameterTypes.length; i++) {
            ConstructorArgument.ValueHolder valueHolder = valueHolders.get(i);
            Object originValue = valueHolder.getValue();
            try{
                Object resolveValue = valueResolver.resolveValueIfNecessary(originValue);
                Object convertValue = typeConverter.convertIfNecessary(resolveValue, parameterTypes[i]);
                argsToUse[i] = convertValue;
            }catch(Exception e) {
                log.error("autowire value failed , {}", e);
                return false;
            }
        }
        return true;
    }

}
