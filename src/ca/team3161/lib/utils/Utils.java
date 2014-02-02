/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.team3161.lib.utils;

/**
 *
 * @author andrew
 */
public class Utils {
    
    /**
     * @param array an array to print
     * @return a prettier string representation of the array
     */
    public static String arrayToString(final Object[] array) {
        String result = "[";
        for (int i = 0; i < array.length; ++i) {
            result += array[i].toString();
            if (i < array.length - 1) {
                result += ", ";
            }
        }
        result += "]";
        return result;
    }
    
    // 19 is the maximum digits a double can represent
    private static final long[] TENS = new long[19];

    static {
        TENS[0] = 1;
        for (int i = 1; i < TENS.length; i++) {
            TENS[i] = 10 * TENS[i - 1];
        }
    }

    /**
     * @param val the double to round
     * @param precision how many significant figures
     * @return the rounded double
     */
    public static double round(final double val, int precision) {
        if (precision < 0) {
            precision = 0;
        }
        if (precision > 19) {
            precision = 19;
        }
        final double unscaled = val * TENS[precision];
        if (unscaled < Long.MIN_VALUE || unscaled > Long.MAX_VALUE) {
            return val;
        }
        final long unscaledLong = (long) unscaled;
        return (double) unscaledLong / TENS[precision];
    }
}
