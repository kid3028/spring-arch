package com.qull.springarch.aop.aspectj;

import com.qull.springarch.aop.Advice;
import com.qull.springarch.aop.MethodMatcher;
import com.qull.springarch.aop.Pointcut;
import com.qull.springarch.aop.framework.AopConfigSupport;
import com.qull.springarch.aop.framework.AopProxyFactory;
import com.qull.springarch.aop.framework.CglibProxyFactory;
import com.qull.springarch.aop.framework.JdkAopProxyFactory;
import com.qull.springarch.beans.exception.BeansException;
import com.qull.springarch.beans.factory.config.BeanPostProcessor;
import com.qull.springarch.beans.factory.config.ConfigurableBeanFactory;
import com.qull.springarch.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 为aop对象创建代理代理
 * @author kzh
 * @description
 * @DATE 2019/10/19 21:02
 */
public class AspectJAutoProxyCreator implements BeanPostProcessor {

    ConfigurableBeanFactory beanFactory;

    @Override
    public Object beforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * 后处理， 如果有切面信息，那么需要构造代理对象
     * 1.没有通知，返回原对象
     * 2.有通知，返回代理对象
     * @param bean 原对象
     * @param beanName 原对象名称
     * @return 如果有切面信息，那么返回代理对象，否则返回原对象
     * @throws BeansException
     */
    @Override
    public Object afterInitialization(Object bean, String beanName) throws BeansException {
        // 如果这个Bean本身就是Advice及其子类，那就不需要再生成动态代理了
        if(isInfrastructureClass(bean.getClass())) {
            return bean;
        }
        // 该bean匹配的通知
        List<Advice> advices = getCandidateAdvices(bean);
        // 如果通知为空，直接返回原bean对象，否则创建对应的代理对象
        if (advices.isEmpty()) {
            return bean;
        }
        // 切面通知不空，创建代理，织入通知代码
        return createProxy(advices, bean);
    }

    /**
     * 创建代理对象，织入通知代码
     * @param advices 需要织入的代理
     * @param bean 原bean
     * @return 返回已经织入通知代码的动态代理对象
     */
    private Object createProxy(List<Advice> advices, Object bean) {
        AopConfigSupport config = new AopConfigSupport();
        // 将匹配的advice加入配置
        for (Advice advice : advices) {
            config.addAdvice(advice);
        }
        // 获取到该class实现的接口，包括父类的接口
        Set<Class> targetInterfaces = ClassUtils.getAllInterfacesForClassAsSet(bean.getClass());
        // 将接口加入配置
        for (Class targetInterface : targetInterfaces) {
            config.addInterface(targetInterface);
        }

        // aop的被通知对象为当前bean
        config.setTargetObject(bean);

        AopProxyFactory proxyFactory = null;
        // 如果该class没有实现接口，使用cglib做动态代理
        if (config.getProxiedInterfaces().length == 0) {
            proxyFactory = new CglibProxyFactory(config);
        }
        // 有接口实现，那么使用jdk的动态代理
        else {
            // jdk代理
            proxyFactory = new JdkAopProxyFactory(config);
        }
        // 返回生成的动态代理
        return proxyFactory.getProxy();
    }

    /**
     * 获取该类的所有通知对象
     * @param bean
     * @return
     */
    private List<Advice> getCandidateAdvices(Object bean) {
        // 获取到容器中所有的通知对象
        List<Object> advices = this.beanFactory.getBeansByType(Advice.class);
        List<Advice> result = new ArrayList<>();
        for (Object obj : advices) {
            // 获取到通知对象的切入点
            Pointcut pc = ((Advice) obj).getPointcut();
            // 判断该class中是否有与切入点匹配的方法
            if(canApply(pc, bean.getClass())) {
                // 有匹配，则将该切面加入
                result.add((Advice) obj);
            }
        }
        return result;
    }

    /**
     * 判断目标class中是否有与切入点匹配的方法
     * @param pc
     * @param targetClass
     * @return
     */
    private boolean canApply(Pointcut pc, Class<?> targetClass) {
        // 切入点对应的匹配器
        MethodMatcher methodMatcher = pc.getMethodMatcher();

        // 获取到该class所有的接口，包括父类的接口
        Set<Class> classes = new LinkedHashSet<>(ClassUtils.getAllInterfacesForClassAsSet(targetClass));
        classes.add(targetClass);
        for (Class clazz : classes) {
            // 获取到每个class的方法
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                // 对方法进行匹配，只需要匹配到一个就可以返回
                if (methodMatcher.matches(method)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInfrastructureClass(Class<?> beanClass) {
        return Advice.class.isAssignableFrom(beanClass);
    }

    public void setBeanFactory(ConfigurableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
