package com.qull.springarch.beans.factory.support;

import com.qull.springarch.beans.factory.BeanCreationException;
import com.qull.springarch.beans.factory.BeanDefinition;
import com.qull.springarch.beans.factory.BeanDefinitionStoreExpcetion;
import com.qull.springarch.beans.factory.BeanFactory;
import com.qull.springarch.util.ClassUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 15:06
 */
public class DefaultBeanFactory implements BeanFactory {

    private static final Logger log = LoggerFactory.getLogger(DefaultBeanFactory.class);

    public static final String ID_ATTRIBUTE = "id";

    public static final String CLASS_ATTRIBUTE = "class";

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public DefaultBeanFactory(String configFile) {
        loadBeanDefinition(configFile);
    }

    private void loadBeanDefinition(String configFile) {
        InputStream is = null;
        try {
            ClassLoader c1 = ClassUtils.getDefaultClassLoader();
            is = c1.getResourceAsStream(configFile);
            SAXReader reader = new SAXReader();
            Document doc = reader.read(is);

            Element root = doc.getRootElement(); // beans
            Iterator iter = root.elementIterator();
            while (iter.hasNext()) { // bean
                Element ele = (Element) iter.next();
                String id = ele.attributeValue(ID_ATTRIBUTE);
                String beanClassName = ele.attributeValue(CLASS_ATTRIBUTE);
                BeanDefinition bd = new GenericBeanDefinition(id, beanClassName);
                this.beanDefinitionMap.put(id, bd);
            }

        } catch (DocumentException e) {
            throw new BeanDefinitionStoreExpcetion("parse configFile '" + configFile + "' error", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public BeanDefinition getBeanDefinition(String beanId) {
        return this.beanDefinitionMap.get(beanId);
    }

    @Override
    public Object getBean(String beanId) {
        BeanDefinition bd = this.getBeanDefinition(beanId);
        if (bd == null) {
            log.error("bean {} not exists", beanId);
            throw new BeanCreationException("bean '" + beanId + "' not exists");
        }
        ClassLoader c1 = ClassUtils.getDefaultClassLoader();
        try {
            Class<?> beanClass = c1.loadClass(bd.getBeanClassName());
            return beanClass.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            log.error("create bean {} error : ", beanId, e);
            throw new BeanCreationException("create bean '" + beanId + "' error", e);
        }
    }
}
