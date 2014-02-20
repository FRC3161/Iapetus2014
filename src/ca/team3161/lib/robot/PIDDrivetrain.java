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
    private final Object notifier;
    private int reversedDrive = 1;
    
    public static final int DRIVE_TASK = 0;
    public static final int TURN_TASK = 1;
    
    private final DriveTask DRIVE = new DriveTask() {
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
    
    private final DriveTask TURN = new DriveTask() {
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
    
    private DriveTask t = DRIVE;
    
    /**
     * Create a PIDDrivetrain instance. Intended to handle a single "event" in autonomous mode,
     * then be destroyed. Further events can be scripted by creating new PIDDrivetrains with this
     * method.
     * @param bundle a PIDBundle containing references to the controllers and sensors required by
     * this PIDDrivetrain
     * @return a new PIDDrivetrain instance
     */
    public static PIDDrivetrain build(final PIDBundle bundle) {
        return new PIDDrivetrain(bundle.leftDrive, bundle.rightDrive,
                bundle.leftEncoder, bundle.rightEncoder, bundle.turningPid);
    }
    
    private PIDDrivetrain(final SpeedController leftDrive, final SpeedController rightDrive,
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
     * Used with TURN_TASK to define how far to turn
     * Turn in place.
     * Positive degrees may be either clockwise or anticlockwise, depending on
     * the setup of your particular AnglePidSrc
     * @param degrees how many degrees to turn
     */
    public PIDDrivetrain turnByDegrees(final float degrees) {
        turningDegreesTarget = degrees;
        return this;
    }
    
    /**
     * Used with DRIVE_TASK to define how far to drive.
     * Positive values are intended to drive forward, but this may not
     * always be the case. "setReversedDrive()" is provided as a convenience
     * in this situation. setReversedDrive MUST be called before this method
     * if it is to be used at all.
     * @param ticks how many encoder ticks to drive
     * @return this object
     */
    public PIDDrivetrain setTicksTarget(final int ticks) {
        leftTicksTarget = reversedDrive * ticks;
        rightTicksTarget = reversedDrive * ticks;
        return this;
    }
    
    /**
     * Use if your encoders count backward when you drive forward.
     * This is used in conjunction with setTicksTarget(int ticks) and
     * setTask(DRIVE_TASK). This method MUST be called before setTicksTarget
     * if it is to be used at all.
     * @return this object
     */
    public PIDDrivetrain setReversedDrive() {
        reversedDrive = -1;
        return this;
    }
    
    /**
     * Change the task from driving straight to turning
     * @param t the task type to switch to
     * @return PIDDrivetrain this object
     */
    private PIDDrivetrain setTask(final DriveTask t) {
        leftEncoder.clear();
        rightEncoder.clear();
        turningPid.clear();
        bearingPid.clear();
        this.t = t;
        return this;
    }
    
    /**
     * Change the task from driving straight to turning
     * @param t an int representing the task to switch to. See PIDDrivetrain.*_TASK constants. The default is DRIVE_TASK
     * @return this object
     */
    public PIDDrivetrain setTask(final int t) {
        if (t == DRIVE_TASK) {
            this.setTask(this.DRIVE);
        } else if (t == TURN_TASK) {
            this.setTask(this.TURN);
        } else {
            this.setTask(this.DRIVE);
        }
        return this;
    }
    
    /**
     * Reset the state of the drivetrain
     */
    private void reset() {
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
     * Suspends the calling thread until the target is reached, at which point it will be awoken again.
     * This Subsystem's background thread will then be canceled immediately after the waiting thread
     * resumes.
     * Suspends the calling thread until the target is reached, at which point it will be awoken again
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public void await() throws InterruptedException {
        this.start();
        synchronized (notifier) {
            notifier.wait();
        }
        this.cancel();
    }
    
    /**
     * An action this PIDDrivetrain may carry out.
     */
    private abstract class DriveTask implements Runnable {
    }
    
    public static class PIDBundle {
        public final SpeedController leftDrive, rightDrive;
        public final PID leftEncoder, rightEncoder, turningPid;
        
        public PIDBundle(final SpeedController leftDrive, final SpeedController rightDrive,
                final PID leftEncoder, final PID rightEncoder, final PID turningPid) {
            this.leftDrive = leftDrive;
            this.rightDrive = rightDrive;
            this.leftEncoder = leftEncoder;
            this.rightEncoder = rightEncoder;
            this.turningPid = turningPid;
        }
    }
    
}