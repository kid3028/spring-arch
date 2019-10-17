package com.qull.springarch.test.v4;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/17 14:54
 */
@Suite.SuiteClasses({ApplicationContextTest.class, AutowiredAnnotationProcessorTest.class, ClassPathBeanDefinitionScannerTest.class,
ClassReaderTest.class, MetadataReaderTest.class, PackageResourceLoaderTest.class, XmlBeanDefinitionReaderTest.class})
@RunWith(Suite.class)
public class V4TestAllSuites {
}
