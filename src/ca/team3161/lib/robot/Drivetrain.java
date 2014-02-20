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

package ca.team3161.lib.robot;

import ca.team3161.lib.utils.Assert;
import ca.team3161.lib.utils.Utils;
import edu.wpi.first.wpilibj.SpeedController;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Implements a container for SpeedControllers.
 */
public class Drivetrain implements SpeedController {
    private final Vector motorControllers;
    private float inversion = 1.0f;
    
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
            inversion = -1.0f;
        } else {
            inversion = 1.0f;
        }
        return this;
    }
    
    /**
     * The current speed of this Drivetrain
     * @return the current PWM value of the SpeedController collection (-1.0 to 1.0)
     */
    public double get() {
        // All of the SpeedControllers will always be set to the same value,
        // so simply get the value of the first one.
        final SpeedController controller = (SpeedController) motorControllers.firstElement();
        return inversion * controller.get();
    }

    /**
     * The speeds of all SpeedControllers within this Drivetrain.
     * They should all be nearly identical, other than error due to floating point
     * precision.
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
        pwm = Utils.normalizePwm(pwm);
        final Enumeration e = motorControllers.elements();
        while (e.hasMoreElements()) {
            final SpeedController controller = (SpeedController) e.nextElement();
            controller.set(inversion * pwm);
        }
    }
    
    /**
     * Don't use this!
     * @param pwm the PWM value to assign to each SpeedController in the collection
     * @param syncGroup
     */
    public void set(double pwm, final byte syncGroup) {
        // PWM value must be between -1 and 1
        pwm = Utils.normalizePwm(pwm);
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
    
    /**
     * Call pidWrite on each SpeedController in this collection
     * @param output 
     */
    public void pidWrite(final double output) {
        final Enumeration e = motorControllers.elements();
        while (e.hasMoreElements()) {
            final SpeedController controller = (SpeedController) e.nextElement();
            controller.pidWrite(output);
        }
    }

}
