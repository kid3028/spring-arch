package com.qull.springarch.test.v1;

import com.qull.springarch.core.io.ClassPathResource;
import com.qull.springarch.core.io.FileSystemResource;
import com.qull.springarch.core.io.Resource;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 21:34
 */
public class ResourceTest {

    @Test
    public void testClassPathResource() {
        Resource resource = new ClassPathResource("petstore-v1.xml");
        InputStream is = null;
        try {
            is = resource.getInputStream();
            Assert.assertNotNull(is);
        } catch (IOException e) {
            e.printStackTrace();
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

    @Test
    public void testFileSystemResource() {
        Resource resource = new FileSystemResource("C:\\develop\\study\\day5\\spring-arch\\src\\test\\resources\\petstore-v1.xml");
        InputStream is = null;
        try {
            is = resource.getInputStream();
            Assert.assertNotNull(is);
        } catch (IOException e) {
            e.printStackTrace();
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
}
