/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.team3161.lib.robot;

import edu.wpi.first.wpilibj.Solenoid;

/**
 *
 * @author andrew
 */
public class DoubleSolenoid {
    
    private final Solenoid first, second;
    
    public DoubleSolenoid (final Solenoid first, final Solenoid second) {
        this.first = first;
        this.second = second;
    }
    
    public void set(final boolean state) {
        first.set(state);
        second.set(!state);
    }
    
    public boolean get() {
        return first.get();
    }
    
    public void reset() {
        first.set(false);
        second.set(false);
    }
}
