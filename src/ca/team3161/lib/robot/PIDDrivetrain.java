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

package ca.team3161.lib.robot;

import ca.team3161.lib.robot.pid.PID;
import edu.wpi.first.wpilibj.SpeedController;

/**
 * A Drivetrain controller that uses PID objects and is able to accurately drive straight and turn by degrees.
 */
public class PIDDrivetrain extends Subsystem {
    
    private final SpeedController leftDrive, rightDrive;
    private final PID leftEncoder, rightEncoder, turningPid /*gyro*/, bearingPid;
    private volatile float turningDegreesTarget = 0.0f;
    private volatile int leftTicksTarget = 0, rightTicksTarget = 0;
    private DriveTask t;
    private final Object notifier;
    
    /**
     * The task defining the action of driving straight forward (or backward).
     */
    public DriveTask DRIVE = new DriveTask() {
        public void run() {
            final double skew = bearingPid.pid(0.0f);
            leftDrive.set(leftEncoder.pid(leftTicksTarget) + skew);
            rightDrive.set(rightEncoder.pid(rightTicksTarget) - skew);
            if (leftEncoder.atTarget() || rightEncoder.atTarget()) {
                synchronized (notifier) {
                    notifier.notifyAll();
                }
            }
            
            if (bearingPid.atTarget()) {
                bearingPid.clear();
            }
            
            if (leftEncoder.atTarget()) {
                leftEncoder.clear();
            }
            
            if (rightEncoder.atTarget()) {
                rightEncoder.clear();
            }
        }
    };
    
    /**
     * The task defining the action of turning in place.
     */
    public DriveTask TURN = new DriveTask() {
        public void run() {
            final double pidVal = turningPid.pid(turningDegreesTarget);
            leftDrive.set(pidVal);
            rightDrive.set(-pidVal);
            if (turningPid.atTarget()) {
                synchronized (notifier) {
                    notifier.notifyAll();
                }
                turningPid.clear();
            }
        }
    };
    
    /**
     * Create a new PIDDrivetrain instance
     * @param leftDrive the left side drivetrain SpeedController
     * @param rightDrive the right side drivetrain SpeedController
     * @param leftEncoder the left side drivetrain Encoder
     * @param rightEncoder the right side drivetrain Encoder
     * @param turningPid an AnglePidSrc (eg Gyro) to maintain a straight heading
     */
    public PIDDrivetrain(final SpeedController leftDrive, final SpeedController rightDrive,
            final PID leftEncoder, final PID rightEncoder, final PID turningPid) {
        super(20, true, "PID Drivetrain");
        this.leftDrive = leftDrive;
        this.rightDrive = rightDrive;
        this.leftEncoder = leftEncoder;
        this.rightEncoder = rightEncoder;
        this.turningPid = turningPid;
        this.bearingPid = new PID(turningPid.getSrc(), 0.0f, 0.3f, 0.0f, 0.0f);
        this.notifier = new Object();
    }

    /**
     * Require the SpeedControllers and PID objects
     */
    protected void defineResources() {
        require(leftDrive);
        require(rightDrive);
        require(leftEncoder);
        require(rightEncoder);
        require(turningPid);
    }
    
    /**
     * Turn in place.
     * Positive degrees may be either clockwise or anticlockwise, depending on
     * the setup of your particular AnglePidSrc
     * @param degrees how many degrees to turn
     */
    public void turnByDegrees(final float degrees) {
        turningDegreesTarget = degrees;
    }
    
    /**
     * Drive forward a number of encoder ticks.
     * @param ticks how many ticks to drive
     */
    public void setTicksTarget(final int ticks) {
        leftTicksTarget = -ticks;
        rightTicksTarget = -ticks;
    }
    
    /**
     * Change the task from driving straight to turning
     * @param t the task type to switch to
     */
    public void setTask(final DriveTask t) {
        leftEncoder.clear();
        rightEncoder.clear();
        turningPid.clear();
        bearingPid.clear();
        this.t = t;
    }
    
    /**
     * Reset the state of the drivetrain to fresh
     */
    public void reset() {
        leftTicksTarget = 0;
        rightTicksTarget = 0;
        turningDegreesTarget = 0.0f;
        leftEncoder.clear();
        rightEncoder.clear();
        turningPid.clear();
        bearingPid.clear();
    }

    /**
     * Iteratively PID loop.
     */
    protected void task() {
        t.run();
    }
    
    /**
     * Suspends the calling thread until the target is reached, at which point it will be awoken again
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public void waitForTarget() throws InterruptedException {
        synchronized (notifier) {
            notifier.wait();
        }
    }
    
    /**
     * An action this PIDDrivetrain may carry out.
     */
    public abstract class DriveTask implements Runnable {
    }
    
}