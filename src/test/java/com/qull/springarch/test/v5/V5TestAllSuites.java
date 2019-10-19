package com.qull.springarch.test.v5;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/19 23:37
 */
@Suite.SuiteClasses({ApplicationContextTest.class, BeanDefinitionTest.class, BeanFactoryTest.class, CglibAopProxyTest.class,
CGLIBTest.class, MethodLocatingFactoryTest.class, PointcutTest.class, ReflectiveMethodInvocationTest.class})
@RunWith(Suite.class)
public class V5TestAllSuites {
}
