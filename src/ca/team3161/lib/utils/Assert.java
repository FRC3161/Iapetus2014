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
public class Assert {

    /**
     * @param condition throw an exception if the given condition does not hold
     */
    public static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionException();
        }
    }
    
    /**
     * @param reason why the condition should hold
     * @param condition throw an exception if the given condition does not hold
     */
    public static void assertTrue(String reason, boolean condition) {
        if (!condition) {
            throw new AssertionException(reason);
        }
    }

    /**
     * @param condition throw an exception if the given condition holds
     */
    public static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionException();
        }
    }
    
    /**
     * @param reason why the condition should not hold
     * @param condition throw an exception if the given condition holds
     */
    public static void assertFalse(String reason, boolean condition) {
        if (condition) {
            throw new AssertionException(reason);
        }
    }
    
    /**
     * @param obj throw an exception if this reference is null
     */
    public static void assertNonNull(Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
    }
    
    /**
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
