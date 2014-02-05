/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.team3161.lib.robot;

import ca.team3161.lib.utils.Assert;
import edu.wpi.first.wpilibj.SpeedController;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Implements a container for SpeedControllers.
 */
public class Drivetrain implements SpeedController {
    private final Vector motorControllers;
    private double inversion = 1.0d;
    
    /**
     * Create a new Drivetrain instance
     * @param controllerArray an array of SpeedController objects. May be
     * all the same type, or may be mixed.
     */
    public Drivetrain(final SpeedController[] controllerArray) {
        Assert.assertNonNull("SpeedController array cannot be null", controllerArray);
        Assert.assertFalse("Must have at least one SpeedController per Drivetrain", controllerArray.length == 0);
        // Require at least one controller
        // create the Vector, then copy all the SpeedControllers out of the
        // array into the Vector
        motorControllers = new Vector(controllerArray.length);
        for (int i = 0; i < controllerArray.length; i++) {
            motorControllers.addElement(controllerArray[i]);
        }
    }
    
    /**
     * Invert all PWM values for this Drivetrain.
     * @param inverted whether the PWM values should be inverted or not
     * @return this Drivetrain instance
     */
    public Drivetrain setInverted(final boolean inverted) {
        if (inverted) {
            inversion = -1.0d;
        } else {
            inversion = 1.0d;
        }
        return this;
    }
    
    /**
     * @return the current PWM value of the SpeedController collection (-1.0 to 1.0)
     */
    public double get() {
        // All of the SpeedControllers will always be set to the same value,
        // so simply get the value of the first one.
        final SpeedController controller = (SpeedController) motorControllers.firstElement();
        return inversion * controller.get();
    }

    /**
     * @return an array enumerating all the current PWM values of the SpeedController collection (-1.0 to 1.0)
     */
    public double[] getAll() {
        final double[] result = new double[motorControllers.size()];
        for (int i = 0; i < motorControllers.size(); ++i) {
            final SpeedController controller = (SpeedController) motorControllers.elementAt(i);
            result[i] = controller.get();
        }
        return result;
    }
    
    /**
     * Set the pwm value (-1.0 to 1.0)
     * @param pwm the PWM value to assign to each SpeedController in the collection
     */
    public void set(double pwm) {
        // PWM value must be between -1 and 1
        pwm = normalize(pwm);
        final Enumeration e = motorControllers.elements();
        while (e.hasMoreElements()) {
            final SpeedController controller = (SpeedController) e.nextElement();
            controller.set(inversion * pwm);
        }
    }
    
    /**
     *  Don't use this!
     * @param pwm the PWM value to assign to each SpeedController in the collection
     * @param syncGroup
     */
    public void set(double pwm, final byte syncGroup) {
        // PWM value must be between -1 and 1
        pwm = normalize(pwm);
        final Enumeration e = motorControllers.elements();
        while (e.hasMoreElements()) {
            final SpeedController controller = (SpeedController) e.nextElement();
            controller.set(inversion * pwm, syncGroup);
        }
    }
    
    /**
     * Disable each SpeedController in the collection
     */
    public void disable() {
        final Enumeration e = motorControllers.elements();
        while (e.hasMoreElements()) {
            final SpeedController controller = (SpeedController) e.nextElement();
            controller.disable();
        }
    }
    
    public void pidWrite(final double output) {
        final Enumeration e = motorControllers.elements();
        while (e.hasMoreElements()) {
            final SpeedController controller = (SpeedController) e.nextElement();
            controller.pidWrite(output);
        }
    }
    
    private double normalize(final double val) {
        if (val > 1.0d) return 1.0d;
        if (val < -1.0d) return -1.0d;
        return val;
    }
}
