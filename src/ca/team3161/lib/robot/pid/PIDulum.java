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

/**
 * A PID controller for inverted pendulum systems (PID pendulum... get it?)
 */
public class PIDulum extends PID {
    
    private final float offsetAngle;
    private final float torqueConstant;
    
    /**
     * @param source the PIDSrc source sensor
     * @param deadband filter value - do not act when current error is within this bound
     * @param kP P constant
     * @param kI I constant
     * @param kD D constant
     * @param offsetAngle the balance point of the inverted pendulum
     * @param torqueConstant "feed forward" term constant to allow the pendulum to hold position against gravity
     */
    public PIDulum(final AnglePidSrc source, final float deadband,
            final float kP, final float kI, final float kD,
            final float offsetAngle, final float torqueConstant) {
        super(source, deadband, kP, kI, kD);
        this.offsetAngle = offsetAngle;
        this.torqueConstant = torqueConstant;
    }
    
    /**
     * Iterate the PID loop
     * @param target the desired target value. Units depend on the context of this PID
     * @return the output value to set to eg a SpeedController to reach the specified target
     */
    public float pid(final float target) {
        float kErr;
        float pOut;
        float iOut;
        float dOut;
        float feedForward;
        float output;

        kErr = (float)(target - source.getValue());

        deltaError = prevError - kErr;
        prevError = kErr;
        integralError += kErr;

        pOut = kErr * kP;
        iOut = integralError * kI;
        dOut = deltaError * kD;

        if (iOut > 1) {
            iOut = 1;
        }
        
        feedForward = torqueConstant * (float)(source.getValue() - offsetAngle);
        
        if (Math.abs(kErr) < deadband) {
            atTarget = true;
            return feedForward;
        } else {
            atTarget = false;
        }

        output = (pOut + iOut + dOut + feedForward);

        if (output > 1.0f) {
            return 1.0f;
        }
        if (output < -1.0f) {
            return -1.0f;
        }
        return output;
    }
    
}
