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

package ca.team3161.lib.robot.pid;

import edu.wpi.first.wpilibj.interfaces.Potentiometer;

/**
 * A PID source that converts a rotary potentiometer's voltage output into degrees of
 * rotation
 */
public class PotentiometerPidSrc implements AnglePidSrc {
    
    private final Potentiometer pot;
    private final float minVolt, maxVolt, minAngle, maxAngle;
    
    /**
     * Create a new PotentiometerPidSrc instance
     * @param pot a Potentiometer object to measure voltages from
     * @param minVolt the minimum measured voltage from the potentiometer at the "small" movement endpoint of the system
     * @param maxVolt the maximum measured voltage from the potentiometer at the "large" movement endpoint of the system
     * @param minAngle the minimum angle the system can physically rotate to
     * @param maxAngle  the maximum angle the system can physically rotate to
     */
    public PotentiometerPidSrc(final Potentiometer pot,
            final float minVolt, final float maxVolt,
            final float minAngle, final float maxAngle) {
        this.pot = pot;
        this.minVolt = minVolt;
        this.maxVolt = maxVolt;
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
    }
    
    /**
     * Retrieve the original sensor used to construct this PIDSrc
     * @return the Potentiometer
     */
    public Potentiometer getSensor() {
        return pot;
    }
    
    /**
     * Inherited from PIDSrc
     * @return the measured value of this PIDSrc
     */
    public float getValue() {
        final float slope = (maxAngle - minAngle) / (maxVolt - minVolt);
        final float offset = minAngle - slope * minVolt;
        return (float)(slope * pot.get() + offset);
    }
    
    /**
     * Inherited from AnglePidSrc
     * @return the minimal angle of this sensor
     */
    public float getMinAngle() {
        return minAngle;
    }
    
    /**
     * Inherited from AnglePidSrc
     * @return the maximal angle of this sensor
     */
    public float getMaxAngle() {
        return maxAngle;
    }
    
}
