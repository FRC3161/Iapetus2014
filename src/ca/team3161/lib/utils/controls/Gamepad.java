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
    public GenericHID getBackingHID();
    public double getLeftX();
    public double getLeftY();
    public double getRightX();
    public double getRightY();
    public double getDpadHorizontal();
    public double getDpadVertical();
    public boolean getButton(int button);
    public Gamepad setInverted(boolean inverted);
    public boolean getInverted();
}
