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

package ca.team3161.iapetus;

import ca.team3161.lib.robot.DoubleSolenoid;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;

/**
 *
 * Class needs to control:  victor (winch) gauged by linear potentiometer (magnetopot), solenoid (super shifter for gearbox), 
 *                          solenoid (for pickup), talon (roller), talon (fork)
 * 
 * Basic Overview
 * Shooter:
 *      Init: piston locked, motor cocked back
 *      Auto: fork adjust, roller up, piston release, time delay, piston lock, winch motor activate
 * Pickup Mode: fork piston down, roller down, roller motors activate
 * Travel mode: roller motors off, roller down, fork up
 * 
 * TBD:
 * Shooting modes (power and whatnot)
 */

public class Shooter {
    
    private final SpeedController winch = new Victor (7);
    private final DoubleSolenoid trigger = new DoubleSolenoid(new Solenoid (1), new Solenoid(2));
    private final DoubleSolenoid claw = new DoubleSolenoid(new Solenoid (3), new Solenoid(4));
    private final SpeedController roller = new Talon (8);
    private final SpeedController fork = new Talon (9);
    private final Potentiometer drawback = new AnalogPotentiometer (10);
    
    public Shooter() {
    }
    
    public void drawWinch(final double speed) {
        if (getDrawback() >= 1.0d) {
            return;
        }
        winch.set(normalize(speed));
    }
    
    public void pullTrigger() {
        trigger.set(true);
    }
    
    public void returnTrigger() {
        trigger.set(false);
    }
    
    public void resetTrigger() {
        trigger.reset();
    }
    
    public void openClaw() {
        claw.set(true);
    }
    
    public void closeClaw() {
        claw.set(false);
    }
    
    public void resetClaw() {
        claw.reset();
    }
    
    /**
     * @return whether the claw is closed or not.
     */
    public boolean getClaw() {
        return claw.get();
    }
    
    public void setRoller(final double speed) {
        roller.set(normalize(speed));
    }
    
    public void setFork(final double speed) {
        fork.set(normalize(speed));
    }
    
    /**
     * @return a double from 0.0 to 1.0 representing % drawback of the shooter rod
     */
    public double getDrawback() {
        return drawback.get();
    }
    
    private static double normalize(final double val) {
        if (val < -1.0d) return -1.0d;
        if (val > 1.0d) return 1.0d;
        return val;
    }
}