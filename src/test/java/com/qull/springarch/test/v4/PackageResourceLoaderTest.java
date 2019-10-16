package com.qull.springarch.test.v4;

import com.qull.springarch.core.io.support.PackageResourceLoader;
import com.qull.springarch.core.io.Resource;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 14:08
 */
public class PackageResourceLoaderTest {

    @Test
    public void testGetResources() {
        PackageResourceLoader loader = new PackageResourceLoader();
        Resource[] resources = loader.getResources("com.qull.springarch.dao.v4");
        Assert.assertEquals(2, resources.length);
    }
}
