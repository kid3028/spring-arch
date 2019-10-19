package com.qull.springarch.util;

import java.util.Arrays;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 14:28
 */
public class PrimeUtil {
    public static int[] getPrimes(int max) {
        if (max <= 2) {
            return new int[0];
        }
        int[] primes = new int[max];
        int count = 0;
        for (int num = 2; num < max; num++) {
            if(isPrime(num)) {
                primes[count++] = num;
            }
        }
        primes = Arrays.copyOfRange(primes, 0, count);
        return primes;

    }

    private static boolean isPrime(int num) {
        for(int i = 2; i < num / 2 + 1; i++) {
            // 如果if成立，则i不是质数，跳出循环
            if(num % i == 0) {
                return false;
            }
        }
        return true;
    }
}
