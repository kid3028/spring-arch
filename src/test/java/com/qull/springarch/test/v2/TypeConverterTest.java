package com.qull.springarch.test.v2;

import com.qull.springarch.beans.SimpleTypeConverter;
import com.qull.springarch.beans.exception.TypeMismatchException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/15 21:39
 */
public class TypeConverterTest {

    @Test
    public void testConvertStringToInt() {
        SimpleTypeConverter converter = new SimpleTypeConverter();
        Integer i = converter.convertIfNecessary("3", Integer.class);

        Assert.assertEquals(3, i.intValue());

        try {
            converter.convertIfNecessary("3.1", Integer.class);
        } catch (TypeMismatchException e) {
            return;
        }
        Assert.fail();
    }

    @Test
    public void testConverterStringToBoolean() {
        SimpleTypeConverter converter = new SimpleTypeConverter();
        Boolean b = converter.convertIfNecessary("true", Boolean.class);
        Assert.assertEquals(true, b);

        try {
            converter.convertIfNecessary("xxxxxxxxxxxx", Boolean.class);
        } catch (TypeMismatchException e) {
            return;
        }

        Assert.fail();
    }
}
