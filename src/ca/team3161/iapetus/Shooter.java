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

import ca.team3161.lib.robot.PIDulum;
import ca.team3161.lib.robot.PotentiometerPidSrc;
import ca.team3161.lib.utils.Utils;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Victor;

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
    private final DoubleSolenoid trigger = new DoubleSolenoid(1, 2);
    private final DoubleSolenoid claw = new DoubleSolenoid(3, 4);
    private final SpeedController roller = new Talon (8);
    private final SpeedController fork = new Talon (9);
    private final DigitalInput drawbackStopSwitch = new DigitalInput(1);
    private final AnalogPotentiometer forkPot = new AnalogPotentiometer(2);
    //private final PotentiometerPidSrc pidPot = new PotentiometerPidSrc(forkPot, minVolt, maxVolt, 45, 180);
    //private final PIDulum pidulum = new PIDulum(pidPot, kP, kI, kD, offsetAngle, torqueConstant);
    
    public Shooter() {
    }
    
    public void disableAll() {
        winch.set(0.0d);
        trigger.set(DoubleSolenoid.Value.kOff);
        claw.set(DoubleSolenoid.Value.kOff);
        roller.set(0.0d);
        fork.set(0.0d);
    }
    
    /**
     * @param speed set the winch motor. The motor actually runs in reverse,
     * so this method negates the value before setting it so that positive
     * values wind the winch back.
     */
    public void drawWinch(final double speed) {
        if (getStopSwitch()) {
            winch.set(0.0d);
            return;
        }
        if (speed < 0.0d) { // do not run reverse!
            return;
        }
        winch.set(Utils.normalizePwm(-speed));
    }
    
    /**
     * Release the trigger pin
     */
    public void pullTrigger() {
        trigger.set(DoubleSolenoid.Value.kForward);
    }   
    /**
     * Set the trigger pin back in
     */
    public void returnTrigger() {
        trigger.set(DoubleSolenoid.Value.kReverse);
    }
    
    /**
     * Open the roller claw
     */
    public void openClaw() {
        claw.set(DoubleSolenoid.Value.kForward);
    }
    
    /**
     * Close the roller claw
     */
    public void closeClaw() {
        claw.set(DoubleSolenoid.Value.kReverse);
    }
    
    /**
     * @return whether the claw is closed or not.
     */
    public boolean getClaw() {
        return claw.get().equals(DoubleSolenoid.Value.kForward);
    }
    
    /**
     * @param speed set the PWM for the roller motors
     */
    public void setRoller(final double speed) {
        roller.set(Utils.normalizePwm(speed));
    }
    /*
    public void setForkAngle(final double angle) {
        final double result = pidulum.pd(angle);
        setFork(result);
    }
    */
    
    /**
     * @param speed set the PWM for the shoulder motor
     */
    public void setFork(final double speed) {
        fork.set(Utils.normalizePwm(speed));
    }
    
    public boolean getStopSwitch() {
        return drawbackStopSwitch.get();
    }

    public double getFork() {
        return forkPot.get();
    }
}
