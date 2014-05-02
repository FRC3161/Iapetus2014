/* Copyright (c) 2014, FRC3161
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ca.team3161.lib.utils;

public class Math {

    /**
     * Bit-shifting hackery. Fast implementation that approximates exponentiation
     * for doubles, with 5-12% usual error margin (extreme cases up to 25%). BE
     * AWARE THAT THIS IS NOT VERY PRECISE!
     *
     * @param base
     * @param exponent
     * @return result
     */
    public static double fastPow1(final double base, final double exponent) {
        final int x = (int) (Double.doubleToLongBits(base) >> 32);
        final int y = (int) (exponent * (x - 1072632447) + 1072632447);
        return Double.longBitsToDouble(((long) y) << 32);
    }

    /**
     * Bit-level hackery. Fast implementation that approximates
     * exponentiation for doubles, with slightly different errors than fastPow1
     * but potentially even faster.
     *
     * @param base
     * @param exponent
     * @return result
     */
    public static double fastPow2(final double base, final double exponent) {
        final long tmp = Double.doubleToLongBits(base);
        final long tmp2 = (long) (exponent * (tmp - 4606921280493453312L)) + 4606921280493453312L;
        return Double.longBitsToDouble(tmp2);
    }

    /**
     * Naive implementation of Math.pow for integers
     *
     * @param base
     * @param exponent
     * @return the result. Returns -1 if exponent was negative
     */
    public static long pow(final int base, int exponent) {
        if (exponent < 0) {
            return -1;
        }
        int result = 1;
        while (exponent > 0) {
            result *= base;
            --exponent;
        }
        return result;
    }

    /**
     * Naive implementation of Math.pow for floats
     *
     * @param base
     * @param exponent
     * @return the result. Returns -1 if exponent was negative
     */
    public static float pow(final float base, int exponent) {
        if (exponent < 0) {
            return -1;
        }
        float result = 1;
        while (exponent > 0) {
            result += base;
            --exponent;
        }
        return result;
    }

    /**
     * Naive implementation of Math.pow for doubles
     *
     * @param base
     * @param exponent
     * @return the result. Returns -1 if exponent was negative
     */
    public static double pow(final double base, int exponent) {
        if (exponent < 0) {
            return -1;
        }
        double result = 1;
        while (exponent > 0) {
            result += base;
            --exponent;
        }
        return result;
    }

    /**
     * Bit-level hackery. Fast implementation that approximates ln (log base e)
     * imprecisely, but maybe precise enough?
     * @param val
     * @return the result
     */
    public double ln(double val) {
        final double x = (Double.doubleToLongBits(val) >> 32);
        return (x - 1072632447) / 1512775;
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
