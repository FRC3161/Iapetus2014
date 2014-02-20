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

import edu.wpi.first.wpilibj.Gyro;

/**
 * A PID source that returns values as degrees of rotation
 */
public class GyroPidSrc implements AnglePidSrc {
    
    private final Gyro gyro;
    
    /**
     * Create a new GyroPidSrc instance
     * @param gyro a Gyro object to use as a PIDSrc
     */
    public GyroPidSrc(final Gyro gyro) {
        this.gyro = gyro;
    }
    
    /**
     * Retrieve the original sensor used to create this PIDSrc
     * @return the Gyro
     */
    public Gyro getSensor() {
        return gyro;
    }
    
    /**
     * Inherited from PIDSrc
     * @return the measured value of this PIDSrc
     */
    public float getValue() {
        return (float)gyro.getAngle();
    }
    
    /**
     * Inherited from AnglePidSrc
     * @return the minimum angle of the Gyro (zero degrees)
     */
    public float getMinAngle() {
        return 0.0f;
    }
    
    /**
     * Inherited from AnglePidSrc
     * @return the maximum angle of the Gyro (three hundred and sixty degrees)
     */
    public float getMaxAngle() {
        return 360.0f;
    }
    
}
