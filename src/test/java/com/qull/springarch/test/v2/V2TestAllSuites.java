package com.qull.springarch.test.v2;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/15 23:19
 */
@Suite.SuiteClasses({ApplicationContextTest.class, BeanDefinitionTest.class, BeanDefinitionValueResolverTest.class,
                    CustomBooleanEditorTest.class, CustomNumberEditorTest.class, TypeConverterTest.class
})
@RunWith(Suite.class)
public class V2TestAllSuites {
}
