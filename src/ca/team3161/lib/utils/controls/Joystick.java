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

import edu.wpi.first.wpilibj.GenericHID;

/**
 * A thin wrapper over the FRC Joystick class, with built-in Y-axis inversion
 * and deadzone
 */
public class Joystick {
    
    private JoystickMode mode;
    private float inversion;
    private final float DEADZONE;
    private final GenericHID backingHID;
    
    /**
     * @param port which USB port this is plugged into, as reported by the Driver Station
     */
    public Joystick(final int port) {
        this(port, 0.0f);
    }
    
    /**
     * @param port which USB port this is plugged into, as reported by the Driver Station
     * @param deadzone Axis values less than this in absolute value will be ignored
     */
    public Joystick(final int port, final float deadzone) {
        this(port, deadzone, new LinearJoystickMode());
    }
    
    /**
     * @param port which USB port this is plugged into, as reported by the Driver Station
     * @param deadzone Axis values less than this in absolute value will be ignored
     * @param mode the joystick input scaling mode
     */
    public Joystick(final int port, final float deadzone, final JoystickMode mode) {
        this.backingHID = new edu.wpi.first.wpilibj.Joystick(port);
        this.inversion = 1.0f;
        this.DEADZONE = deadzone;
        this.mode = mode;
    }
    
    /**
     * Invert the Y-Axis of this Joystick
     * @param inverted if the Y-Axis should be inverted
     */
    public void setInverted(final boolean inverted) {
        if (inverted) {
            inversion = -1.0f;
        } else {
            inversion = 1.0f;
        }
    }
    
    /**
     * Set the JoystickMode after the Joystick has already been constructed
     * @param mode
     */
    public void setMode(final JoystickMode mode) {
        this.mode = mode;
    }
    
    /**
     * Get the X-axis reading from this Joystick
     * @return the value
     */
    public double getX() {
        if (Math.abs(backingHID.getX()) < DEADZONE) {
            return 0.0;
        }
        return mode.adjust(backingHID.getX());
    }
    
    /**
     * Get the Y-axis reading from this Joystick
     * @return the value
     */
    public double getY() {
        if (Math.abs(backingHID.getY()) < DEADZONE) {
            return 0.0;
        }
        return inversion * mode.adjust(backingHID.getY());
    }
    
    /**
     * Check if a button is pressed
     * @param button identifier for the button to check
     * @return the button's pressed state
     */
    public boolean getButton(final int button) {
        return backingHID.getRawButton(button);
    }
    
    /**
     * Get an arbitrary axis reading from this Joystick
     * @param axis identifier for the axis to check
     * @return the value
     */
    public double getRawAxis(final int axis) {
        return backingHID.getRawAxis(axis);
    }
    
}
