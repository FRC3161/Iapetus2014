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
    private final PID leftEncoder, rightEncoder, turningPid;
    private volatile float target;
    private final Object notifier;
    private float reversedDrive;
    
    private final DriveTask DRIVE = new DriveTask() {
        public void run() {
            final double skew = turningPid.pid(0.0f);
            leftDrive.set(reversedDrive * leftEncoder.pid((int) target) + skew);
            rightDrive.set(reversedDrive * rightEncoder.pid((int) target) - skew);
            if (leftEncoder.atTarget() || rightEncoder.atTarget()) {
                synchronized (notifier) {
                    notifier.notifyAll();
                }
            }
            
            if (turningPid.atTarget()) {
                turningPid.clear();
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
            final double pidVal = turningPid.pid(target);
            leftDrive.set(reversedDrive * pidVal);
            rightDrive.set(reversedDrive * -pidVal);
            if (turningPid.atTarget()) {
                synchronized (notifier) {
                    notifier.notifyAll();
                }
                turningPid.clear();
            }
        }
    };
    
    private DriveTask task = DRIVE;
    
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
        this.notifier = new Object();
        this.target = 0.0f;
        this.reversedDrive = 1.0f;
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
     * Used with drive() and turn() to define target distances/rotations.
     * Positive degrees may be either clockwise or anticlockwise, depending on
     * the setup of your particular AnglePidSrc. Positive distance targets
     * may likewise drive your robot backward. The method reversed() can be
     * used as a convenience to flip these behaviours.
     * @param target the target distance in ticks or degrees of rotation,
     * depending on the task
     * @return this object
     */
    public PIDDrivetrain target(final float target) {
        this.target = target;
        return this;
    }
    
    /**
     * Use if your encoders count backward when you drive forward.
     * This is used in conjunction with target(float val) and
     * drive(). This method MUST be called before setTicksTarget
     * if it is to be used at all.
     * @return this object
     */
    public PIDDrivetrain reversed() {
        reversedDrive = -1.0f;
        return this;
    }
    
    /**
     * Change the task from driving straight to turning
     * @param task the task type to switch to
     * @return PIDDrivetrain this object
     */
    private PIDDrivetrain setTask(final DriveTask task) {
        this.task = task;
        return this;
    }
    
    public PIDDrivetrain drive() {
        setTask(this.DRIVE);
        return this;
    }
    
    public PIDDrivetrain turn() {
        setTask(this.TURN);
        return this;
    }

    /**
     * Iteratively PID loop.
     */
    protected void task() {
        task.run();
    }
    
    /**
     * Suspends the calling thread until the target is reached, at which point it will be awoken again.
     * This Subsystem's background thread will then be canceled immediately after the waiting thread
     * resumes.
     * Suspends the calling thread until the target is reached, at which point it will be awoken again
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public void await() throws InterruptedException {
        leftEncoder.clear();
        rightEncoder.clear();
        turningPid.clear();
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