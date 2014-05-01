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

/**
 * Pretty printing arrays, rounding doubles, and ensuring PWM values fall within
 * the range [-1.0, 1.0], etc.
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
    
    /**
     * Normalize a PWM value so it remains in the range [-1.0, 1.0]
     * If a value above 1.0 is given, 1.0 is returned. If a value below -1.0
     * is given, -1.0 is returned. For other values, they are simply returned
     * @param val the value to normalize
     * @return the normalized value
     */
    public static double normalizePwm(final double val) {
        if (val < -1.0d) return -1.0d;
        if (val > 1.0d) return 1.0d;
        return val;
    }
}
