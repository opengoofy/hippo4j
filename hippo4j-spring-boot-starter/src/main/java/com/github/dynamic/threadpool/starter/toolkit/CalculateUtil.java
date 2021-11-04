package com.github.dynamic.threadpool.starter.toolkit;

/**
 * Calculate util.
 *
 * @author chen.ma
 * @date 2021/8/15 14:29
 */
public class CalculateUtil {

    public static int divide(int num1, int num2) {
        return ((int) (Double.parseDouble(num1 + "") / Double.parseDouble(num2 + "") * 100));
    }

}
