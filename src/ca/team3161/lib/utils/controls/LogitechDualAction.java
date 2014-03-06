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

package ca.team3161.lib.utils.controls;

import ca.team3161.lib.utils.Assert;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;

/**
 * A Gamepad implementation describing the Logitech DualAction gamepad
 */
public class LogitechDualAction implements Gamepad {
    
    /**
     * Left thumbstick X-axis
     */
    public static final int LEFT_STICK_X = 1;
    /**
     * Left thumbstick Y-axis
     */
    public static final int LEFT_STICK_Y = 2;
    /**
     * Right thumbstick X-axis
     */
    public static final int RIGHT_STICK_X = 3;
    /**
     * Right thumbstick Y-axis
     */
    public static final int RIGHT_STICK_Y = 4;
    /**
     * Directional pad horizontal
     */
    public static final int DPAD_HORIZONTAL = 5;
    /**
     * Directional pad vertical
     */
    public static final int DPAD_VERTICAL = 6;
    
    /**
     * Upper left shoulder button
     */
    public static final int LEFT_BUMPER = 5;
    /**
     * Upper right shoulder button
     */
    public static final int RIGHT_BUMPER = 6;
    /**
     * Lower left shoulder button
     */
    public static final int LEFT_TRIGGER = 7;
    /**
     * Lower right shoulder button
     */
    public static final int RIGHT_TRIGGER = 8;
    
    /**
     * Left function button
     */
    public static final int SELECT = 9;
    
    /**
     * Right function button
     */
    public static final int START = 10;

    /* The actual FIRST-provided input device that we are implementing a
    * convenience wrapper around.
    */
    private final GenericHID backingHID;
    
    private float inversion = 1.0f;
    private final float DEADZONE;
    
    /**
     * @param port the USB port for this controller
     * @param deadzone how large of a deadzone to use
     */
    public LogitechDualAction(final int port, final float deadzone) {
        Assert.assertTrue("Gamepad deadzone must be in range [0, 1]", deadzone >= 0.0d && deadzone <= 1.0d);
        backingHID = new Joystick(port); // Joystick happens to work well here, but any GenericHID is fine
        DEADZONE = deadzone;
    }
    
    /**
     * 
     * @return the backing Joystick instance, in case this is needed for some reason
     */
    public GenericHID getBackingHID() {
        return backingHID;
    }
    
    /**
     * Get a stick axis value
     * @param axis which axis to get
     * @return the value from this axis, or 0 if the raw value falls within the
     * deadzone
     */
    private float getAxisHelper(final int axis) {
        final float val = (float)backingHID.getRawAxis(axis);
        if (Math.abs(val) < DEADZONE) {
            return 0.0f;
        }
        return val;
    }
    
    /**
     * Get the left thumbstick X-axis
     * @return the X-axis value of the left joystick
     */
    public float getLeftX() {
        return getAxisHelper(LEFT_STICK_X);
    }
        
    /**
     * Get the left thumbstick Y-axis
     * @return the Y-axis value of the left joystick
     */
    public float getLeftY() {
        return inversion * getAxisHelper(LEFT_STICK_Y);
    }
        
    /**
     * Get the right thumbstick X-axis
     * @return the X-axis value of the right joystick
     */
    public float getRightX() {
        return getAxisHelper(RIGHT_STICK_X);
    }
        
    /**
     * Get the right thumbstick Y-axis
     * @return the Y-axis value of the right joystick
     */
    public float getRightY() {
        return inversion * getAxisHelper(RIGHT_STICK_Y);
    }
        
    /**
     * Get the directional pad horizontal
     * @return the horizontal value of the directional pad
     */
    public float getDpadHorizontal() {
        return getAxisHelper(DPAD_HORIZONTAL);
    }
        
    /**
     * Get the directional pad vertical
     * @return the vertical value of the directional pad
     */
    public float getDpadVertical() {
        return -getAxisHelper(DPAD_VERTICAL);
    }
    
    /**
     * @param button specifies which button to check
     * @return true iff the button is currently depressed
     */
    public boolean getButton(final int button) {
        return backingHID.getRawButton(button);
    }
    
    /**
     * Get the left bumper
     * @return whether the left bumper is currently depressed
     */
    public boolean getLeftBumper() {
        return backingHID.getRawButton(LEFT_BUMPER);
    }
    
    /**
     * Get the right bumper
     * @return whether the right bumper is currently depressed
     */
    public boolean getRightBumper() {
        return backingHID.getRawButton(RIGHT_BUMPER);
    }
        
    /**
     * Get the left trigger
     * @return whether the left trigger is currently depressed
     */
    public boolean getLeftTrigger() {
        return backingHID.getRawButton(LEFT_TRIGGER);
    }
        
    /**
     * Get the right trigger
     * @return whether the right trigger is currently depressed
     */
    public boolean getRightTrigger() {
        return backingHID.getRawButton(RIGHT_TRIGGER);
    }
    
    /**
     * Invert the Y-axes of the thumbsticks on this Gamepad
     * @param inverted true iff the Y-axes should be inverted
     * @return this Gamepad instance
     */
    public Gamepad setInverted(final boolean inverted) {
        if (inverted) {
            this.inversion = -1.0f;
        } else {
            this.inversion = 1.0f;
        }
        return this;
    }

    /**
     * Check if this gamepad's sticks are inverted
     * @return if the sticks are inverted
     */
    public boolean getInverted() {
        return this.inversion == -1.0d;
    }

} 
