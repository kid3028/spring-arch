package com.qull.springarch.aop.framework;

import com.qull.springarch.aop.Advice;
import com.qull.springarch.aop.Pointcut;
import com.qull.springarch.util.Assert;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for AOP proxy configuration managers.
 * There are not themselves AOP proxies, but subclass of this class are normally factories from
 * which AOP Proxy instances are obtained directly.
 *
 * this class frees subclasses of the housekeeping of Advices and Advisors, but doesn't actually
 * implement proxy creation methods, which are provided by subclass.
 *
 * This class is serializable, subclass need not be.
 * This class is used to hold snapshots of proxies
 * @author kzh
 * @description
 * @DATE 2019/10/18 22:12
 */
public class AopConfigSupport implements AopConfig {

    private boolean proxyTargetClass = false;

    private Object targetObject = null;

    private List<Advice> advices = new ArrayList<>();

    private List<Class> interfaces = new ArrayList<>();

    public AopConfigSupport() {}

    @Override
    public Class<?> getTargetClass() {
        return this.targetObject.getClass();
    }

    @Override
    public Object getTargetObject() {
        return this.targetObject;
    }

    @Override
    public void setTargetObject(Object targetObject) {
        this.targetObject = targetObject;
    }

    @Override
    public boolean isProxyTargetClass() {
        return this.proxyTargetClass;
    }

    @Override
    public Class<?>[] getProxiedInterfaces() {
        return this.interfaces.toArray(new Class<?>[0]);
    }

    public void addInterface(Class<?> proxiedInterface) {
        Assert.notNull(proxiedInterface, "Interface must not be null");
        if (!proxiedInterface.isInterface()) {
            throw new IllegalArgumentException("[" + proxiedInterface.getName() + "] is not an interface");
        }
        if (!this.interfaces.contains(proxiedInterface)) {
            this.interfaces.add(proxiedInterface);
        }
    }

    /**
     * Remove a proxied interface
     * Does nothing if the given interface isn't proxied
     *
     * @param proxiedInterface the interface to remove from proxy
     * @return {@code true} if the interface was removed; {@code fase} if the interface
     * was not found and hence could not be removed
     */
    public boolean removeInterface(Class<?> proxiedInterface) {
        return this.interfaces.remove(proxiedInterface);
    }

    @Override
    public boolean isInterfaceProxied(Class<?> proxiedInterface) {
        for (Class proxyIntf : this.interfaces) {
            if (proxiedInterface.isAssignableFrom(proxyIntf)) {
                return true;
            }

        }
        return false;
    }

    @Override
    public List<Advice> getAdvices() {
        return this.advices;
    }

    @Override
    public void addAdvice(Advice advice) {
        this.advices.add(advice);
    }


    /**
     * 过滤方法匹配的通知
     * @param method
     * @return
     */
    @Override
    public List<Advice> getAdvices(Method method) {
        List<Advice> result = new ArrayList<>();
        for (Advice advice : this.advices) {
            Pointcut pc = advice.getPointcut();
            if (pc.getMethodMatcher().matches(method)) {
                result.add(advice);
            }
        }

        return result;
    }
}
