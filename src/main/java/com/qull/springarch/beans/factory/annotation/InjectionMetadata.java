package com.qull.springarch.beans.factory.annotation;

import java.security.PublicKey;
import java.util.List;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/17 7:36
 */
public class InjectionMetadata {

    private final Class<?> targetClass;

    private List<InjectionElement> injectionElements;

    public InjectionMetadata(Class<?> targetClass, List<InjectionElement> injectionElements) {
        this.targetClass = targetClass;
        this.injectionElements = injectionElements;
    }

    public List<InjectionElement> getInjectionElements() {
        return this.injectionElements;
    }

    public void inject(Object target) {
        if (injectionElements == null || injectionElements.isEmpty()) {
            return;
        }

        for (InjectionElement element : injectionElements) {
            element.inject(target);
        }
    }
}
