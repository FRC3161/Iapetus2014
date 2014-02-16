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

import ca.team3161.lib.robot.Subsystem;
import ca.team3161.lib.robot.pid.PID;
import edu.wpi.first.wpilibj.SpeedController;

public class PIDDrivetrain extends Subsystem {
    
    private boolean atTarget;
    private final SpeedController leftDrive, rightDrive;
    private final PID leftEncoder, rightEncoder, turningPid /*gyro*/, bearingPid;
    private volatile float turningDegreesTarget = 0.0f;
    private volatile int leftTicksTarget = 0, rightTicksTarget = 0;
    private DriveTask t;
    private final Object notifier;
    
    public DriveTask DRIVE = new DriveTask() {
        public void run() {
            leftDrive.set(leftEncoder.pid(leftTicksTarget));
            rightDrive.set(rightEncoder.pid(rightTicksTarget));
            
            if (leftEncoder.atTarget() || rightEncoder.atTarget()) {
                synchronized (notifier) {
                    notifier.notifyAll();
                }
                atTarget = true;
            }
            
            if (leftEncoder.atTarget()) {
                leftEncoder.clear();
                leftTicksTarget = 0;
            }
            
            if (rightEncoder.atTarget()) {
                rightEncoder.clear();
                rightTicksTarget = 0;
            }
        }
    };
    
    public DriveTask TURN = new DriveTask() {
        public void run() {
            leftDrive.set(turningPid.pid(turningDegreesTarget));
            rightDrive.set(turningPid.pid(turningDegreesTarget));
            if (turningPid.atTarget()) {
                synchronized (notifier) {
                    notifier.notifyAll();
                }
                atTarget = true;
                turningPid.clear();
            }
        }
    };
    
    public PIDDrivetrain(final SpeedController leftDrive, final SpeedController rightDrive,
            final PID leftEncoder, final PID rightEncoder, final PID turningPid) {
        super(20, true, "PID DRIVE");
        this.leftDrive = leftDrive;
        this.rightDrive = rightDrive;
        this.leftEncoder = leftEncoder;
        this.rightEncoder = rightEncoder;
        this.turningPid = turningPid;
        this.bearingPid = new PID(turningPid.getSrc(), 0.0f, 0.0f, 0.0f, 0.0f);
        this.atTarget = false;
        this.notifier = new Object();
    }
    
    public void defineResources() {
        require(leftDrive);
        require(rightDrive);
    }
    
    public boolean atTarget() {
        return this.atTarget;
    }
    
    public void turnByDegrees(final float degrees) {
        this.atTarget = false;
        turningDegreesTarget = degrees;
    }
    
    public void setLeftTicksTarget(final int ticks) {
        this.atTarget = false;
        leftTicksTarget = -ticks;
    }
    
    public void setRightTicksTarget(final int ticks) {
        this.atTarget = false;
        rightTicksTarget = -ticks;
    }
    
    public void waitForTarget() throws InterruptedException {
        synchronized (notifier) {
            notifier.wait();
        }
    }
    
    public void setTask(final DriveTask t) {
        this.t = t;
    }

    protected void task() throws Exception {
        t.run();
    }
    
    public abstract class DriveTask implements Runnable {
    }
    
}
