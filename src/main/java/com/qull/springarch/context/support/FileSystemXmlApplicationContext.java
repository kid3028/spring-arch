package com.qull.springarch.context.support;

import com.qull.springarch.beans.factory.support.DefaultBeanFactory;
import com.qull.springarch.beans.factory.xml.XmlBeanDefinitionReader;
import com.qull.springarch.context.ApplicationContext;
import com.qull.springarch.core.io.FileSystemResource;
import com.qull.springarch.core.io.Resource;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 21:57
 */
public class FileSystemXmlApplicationContext extends AbstractApplicationContext {

    public FileSystemXmlApplicationContext(String path) {
        super(path);
    }

    @Override
    protected Resource getResource(String configFile) {
        return new FileSystemResource(configFile);
    }
}
