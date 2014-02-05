/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.team3161.lib.utils.controls;

import edu.wpi.first.wpilibj.GenericHID;

/**
 * An interface defining a Gamepad controller. All Gamepads are expected to
 * have two thumbsticks, a directional pad, and some clickable buttons.
 * Not all Gamepads will have variable triggers, and not all raw button or
 * axis mappings are the same. These details are left to specific Gamepad
 * implementations.
 * @author andrew
 */
public interface Gamepad {

    /**
     * Get the backing input device of this Gamepad
     * @return the backing input device, eg Joystick
     */
    public GenericHID getBackingHID();

    /**
     * @return the X-axis of the left thumbstick
     */
    public double getLeftX();

    /**
     * @return the Y-axis of the left thumbstick
     */
    public double getLeftY();

    /**
     * @return the X-axis of the right thumbstick
     */
    public double getRightX();

    /**
     * @return the Y-axis of the right thumbstick
     */
    public double getRightY();

    /**
     * @return the left/right value of the directional pad
     */
    public double getDpadHorizontal();

    /**
     * @return the up/down value of the directional pad
     */
    public double getDpadVertical();

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
     * @return whether the Y-axes of the thumbsticks are inverted
     */
    public boolean getInverted();
}
