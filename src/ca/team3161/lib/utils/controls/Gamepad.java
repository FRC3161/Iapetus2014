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
 * An interface defining a Gamepad controller. All Gamepads are expected to
 * have two thumbsticks, a directional pad, and some clickable buttons.
 * Not all Gamepads will have variable triggers, and not all raw button or
 * axis mappings are the same. These details are left to specific Gamepad
 * implementations.
 */
public interface Gamepad {

    /**
     * Get the backing input device of this Gamepad
     * @return the backing input device, eg Joystick
     */
    public GenericHID getBackingHID();

    /**
     * Get the left thumbstick X-axis
     * @return the X-axis of the left thumbstick
     */
    public float getLeftX();

    /**
     * Get the left thumbstick Y-axis
     * @return the Y-axis of the left thumbstick
     */
    public float getLeftY();

    /**
     * Get the right thumbstick X-axis
     * @return the X-axis of the right thumbstick
     */
    public float getRightX();

    /**
     * Get the right thumbstick Y-axis
     * @return the Y-axis of the right thumbstick
     */
    public float getRightY();

    /**
     * Get the directional pad horizontal
     * @return the left/right value of the directional pad
     */
    public float getDpadHorizontal();

    /**
     * Get the directional pad vertical
     * @return the up/down value of the directional pad
     */
    public float getDpadVertical();

    /**
     * Get the value of a button on the controller
     * @param button which button to check. The mapping from values here to
     * actual buttons will depend on the specific Gamepad implementation
     * @return whether the specified button is currently pressed or not
     */
    public boolean getButton(int button);

    /**
     * Invert the Y-axes of the left and right thumbsticks
     * @param inverted if the thumbsticks are to be inverted
     * @return this Gamepad instance
     */
    public Gamepad setInverted(boolean inverted);

    /**
     * Check if this Gamepad's thumbstick Y-axes are inverted
     * @return whether the Y-axes of the thumbsticks are inverted
     */
    public boolean getInverted();
}
