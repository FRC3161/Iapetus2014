/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.team3161.lib.utils.controls;

import ca.team3161.lib.utils.Assert;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;

/**
 * A Gamepad implementation describing the Logitech DualAction gamepad
 */
public class LogitechDualAction implements Gamepad {
    
    /** Axis mappings */
    public static final int
            LEFT_STICK_X = 1,
            LEFT_STICK_Y = 2,
            RIGHT_STICK_X = 3,
            RIGHT_STICK_Y = 4,
            DPAD_HORIZONTAL = 5,
            DPAD_VERTICAL = 6;
    
    /** "Trigger" button numbers */
    public static final int
            LEFT_BUMPER = 5,
            RIGHT_BUMPER = 6,
            LEFT_TRIGGER = 7,
            RIGHT_TRIGGER = 8;

    /* The actual FIRST-provided input device that we are implementing a
    * convenience wrapper around.
    */
    private final GenericHID backingHID;
    
    private double inversion = 1.0d;
    private final double DEADZONE;
    
    /**
     * @param port the USB port for this controller
     * @param deadzone how large of a deadzone to use
     */
    public LogitechDualAction(final int port, final double deadzone) {
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
    private double getAxisHelper(final int axis) {
        final double val = backingHID.getRawAxis(axis);
        if (Math.abs(val) < DEADZONE) {
            return 0.0d;
        }
        return val;
    }
    
    /**
     * @return the X-axis value of the left joystick
     */
    public double getLeftX() {
        return getAxisHelper(LEFT_STICK_X);
    }
        
    /**
     * @return the Y-axis value of the left joystick
     */
    public double getLeftY() {
        return inversion * getAxisHelper(LEFT_STICK_Y);
    }
        
    /**
     * @return the X-axis value of the right joystick
     */
    public double getRightX() {
        return getAxisHelper(RIGHT_STICK_X);
    }
        
    /**
     * @return the Y-axis value of the right joystick
     */
    public double getRightY() {
        return inversion * getAxisHelper(RIGHT_STICK_Y);
    }
        
    /**
     * @return the horizontal value of the directional pad
     */
    public double getDpadHorizontal() {
        return getAxisHelper(DPAD_HORIZONTAL);
    }
        
    /**
     * @return the vertical value of the directional pad
     */
    public double getDpadVertical() {
        return getAxisHelper(DPAD_VERTICAL);
    }
    
    /**
     * @param button specifies which button to check
     * @return true iff the button is currently depressed
     */
    public boolean getButton(final int button) {
        return backingHID.getRawButton(button);
    }
    
    /**
     * @return whether the left bumper is currently depressed
     */
    public boolean getLeftBumper() {
        return backingHID.getRawButton(LEFT_BUMPER);
    }
    
    /**
     * @return whether the right bumper is currently depressed
     */
    public boolean getRightBumper() {
        return backingHID.getRawButton(RIGHT_BUMPER);
    }
        
    /**
     * @return whether the left trigger is currently depressed
     */
    public boolean getLeftTrigger() {
        return backingHID.getRawButton(LEFT_TRIGGER);
    }
        
    /**
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
            this.inversion = -1.0d;
        } else {
            this.inversion = 1.0d;
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
