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
 * Assertions. Contains static methods which take a boolean condition and throw
 * an exception if these conditions do not hold. Useful for debugging and during
 * development.
 */
public class Assert {

    /**
     * Assert a condition is true
     * @param condition throw an exception if the given condition does not hold
     */
    public static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionException();
        }
    }
    
    /**
     * Assert a condition is true
     * @param reason why the condition should hold
     * @param condition throw an exception if the given condition does not hold
     */
    public static void assertTrue(String reason, boolean condition) {
        if (!condition) {
            throw new AssertionException(reason);
        }
    }

    /**
     * Assert a condition is false
     * @param condition throw an exception if the given condition holds
     */
    public static void assertFalse(boolean condition) {
        assertTrue(!condition);
    }
    
    /**
     * Assert a condition is false
     * @param reason why the condition should not hold
     * @param condition throw an exception if the given condition holds
     */
    public static void assertFalse(String reason, boolean condition) {
        assertTrue(reason, !condition);
    }
    
    /**
     * Assert a reference is not null
     * @param obj throw an exception if this reference is null
     */
    public static void assertNonNull(Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
    }
    
    /**
     * Assert a reference is not null
     * @param reason why the reference cannot be null
     * @param obj throw an exception if this reference is null
     */
    public static void assertNonNull(String reason, Object obj) {
        if (obj == null) {
            throw new NullPointerException(reason);
        }
    }

    /**
     * Really just a RuntimeException in disguise (more immediately obvious
     * name to appear in stack traces)
     */
    public static class AssertionException extends RuntimeException {

        /**
         * Create a new AssertionException with generic cause
         */
        public AssertionException() {
            super("Unspecified assertion failure");
        }

        /**
         * Create a new AssertionException
         * @param cause the cause for this exception to have occurred
         */
        public AssertionException(String cause) {
            super(cause);
        }

    }

}
