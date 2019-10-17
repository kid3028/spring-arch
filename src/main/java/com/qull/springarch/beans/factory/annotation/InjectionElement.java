package com.qull.springarch.beans.factory.annotation;

import com.qull.springarch.beans.factory.config.AutowireCapableBeanFactory;

import java.lang.reflect.Member;
import java.security.PublicKey;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/17 7:34
 */
public abstract class InjectionElement {
    protected Member member;

    protected AutowireCapableBeanFactory factory;

    InjectionElement(Member member, AutowireCapableBeanFactory factory) {
        this.member = member;
        this.factory = factory;
    }

    public abstract void inject(Object target);
}
