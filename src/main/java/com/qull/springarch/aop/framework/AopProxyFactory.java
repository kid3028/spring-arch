package com.qull.springarch.aop.framework;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/19 21:52
 */
public interface AopProxyFactory {

    /**
     * Create a new proxy object.
     * Uses the AopProxy's default class loader (if necessary for proxy creation)
     * usually, the thread context class loader.
     * @return the new proxy object (never {@code null})
     */
    Object getProxy();

    /**
     * Create a new proxy object.
     * Use the given class loader(if necessary for proxy creation).
     * {@code null} will simply be passed down and thus lead to the low-level
     * proxy facility's default, which is usually different from the default chosen
     * by the AopProxy implementation's {@link #getProxy()} method.
     *
     * @param classLoader the class loader to create the proxy with (or {@code null} for the low-level proxy facility's default)
     * @return the new proxy object (never {@code null})
     */
    Object getProxy(ClassLoader classLoader);
}
