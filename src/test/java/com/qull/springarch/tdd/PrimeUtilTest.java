package com.qull.springarch.tdd;

import com.qull.springarch.util.PrimeUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 14:25
 */
public class PrimeUtilTest {

    @Test
    public void testGetPrimesForEmptyResult() {
        int[] excepted = {};

        Assert.assertArrayEquals(excepted, PrimeUtil.getPrimes(2));
        Assert.assertArrayEquals(excepted, PrimeUtil.getPrimes(0));
        Assert.assertArrayEquals(excepted, PrimeUtil.getPrimes(-2));
    }

    @Test
    public void testGetPrimes() {
        Assert.assertArrayEquals(new int[]{2,3,5,7}, PrimeUtil.getPrimes(9));
        Assert.assertArrayEquals(new int[]{2,3,5,7,11,13}, PrimeUtil.getPrimes(17));
        Assert.assertArrayEquals(new int[]{2,3,5,7,11,13,17,19,23,29}, PrimeUtil.getPrimes(30));
    }

}
