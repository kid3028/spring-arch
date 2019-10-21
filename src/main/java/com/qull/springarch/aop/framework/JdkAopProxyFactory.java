package com.qull.springarch.aop.framework;

import com.qull.springarch.aop.Advice;
import com.qull.springarch.util.Assert;
import com.qull.springarch.util.ClassUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/19 23:43
 */
public class JdkAopProxyFactory implements AopProxyFactory, InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(JdkAopProxyFactory.class);

    private final AopConfig config;

    public JdkAopProxyFactory(AopConfigSupport config) {
        Assert.notNull(config, "AdvisedSupport must not be null");
        if (config.getAdvices().size() == 0) {
            throw new AopConfigException("No Advices specified");
        }
        this.config = config;

    }


    @Override
    public Object getProxy() {
        return getProxy(ClassUtils.getDefaultClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        if (log.isDebugEnabled()) {
            log.debug("Creating JDK dynamic proxy : target source is : " + config.getTargetObject());
        }
        Class<?>[] proxiedInterfaces = config.getProxiedInterfaces();
        return Proxy.newProxyInstance(classLoader, proxiedInterfaces, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object target = config.getTargetObject();

        Object retval;
        // 获取方法匹配的通知
        List<Advice> chain = this.config.getAdvices(method);

        // 通知为空，直接执行方法调用
        if (chain.isEmpty()) {
            retval = method.invoke(target, args);
        }
        // 通知不为空，加入通知
        else {
            List<MethodInterceptor> interceptors = new ArrayList<>();
            interceptors.addAll(chain);
            retval = new ReflectiveMethodInvocation(target, method, args, interceptors).proceed();
        }

        return retval;
    }
}
