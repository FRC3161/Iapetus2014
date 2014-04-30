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

import ca.team3161.lib.utils.io.DriverStationLCD;
import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * A subclass of IterativeRobot. Autonomous is run in a new Thread, leaving the main robot thread
 * responsible (generally) solely for handling FMS events, Watchdog, etc. This allows
 * autonomous scripts to use convenient semantics such as Thread sleeping rather than periodically
 * checking Timer objects.
 */
public abstract class ThreadedAutoRobot extends IterativeRobot {
    
    private static final int MAX_AUTO_PERIOD_LENGTH = GameConstants.AUTONOMOUS.SECONDS * 1000;
    private volatile int accumulatedTime = 0;
    private final Object modeLock = new Object();
    private Thread autoThread;
    
    /**
     * The DriverStation virtual LCD display panel instance
     */
    protected final DriverStationLCD dsLcd = DriverStationLCD.getInstance();
    
    /** DO NOT override this in subclasses!
     * At the start of the autonomous period, spawn and start a new Thread
     * using the behaviour described in the concrete subclass implementation's
     * autonomousThreaded() method.
     * This new Thread allows us to use Thread.sleep rather than a timer,
     * while also not disrupting normal background functions of the
     * robot such as feeding the Watchdog or responding to FMS events.
     * modeLock is used to ensure that the robot is never simultaneously
     * executing both autonomous and teleop routines at the same time.
     */
    public final void autonomousInit() {
        accumulatedTime = 0;
        autoThread = new Thread(new Runnable() {
            public void run() {
                try {
                    synchronized (modeLock) {
                        autonomousThreaded();
                    }
                } catch (Exception e) {
                    dsLcd.println(0, "AUTO INTERRUPTED!");
                    e.printStackTrace();
                }
            }
        }, "AUTO THREAD");
        autoThread.start();
    }
    
    /**
    * IterativeRobot defines this, but we do not want it to be possible for anything
    * but teleopThreadsafe to be used during teleop. We can't remove or hide it,
    * so we make it empty and final instead.
    */
    public final void teleopContinuous() { }
    
    /**
     * Add a delay to the autonomous routine.
     * This also ensures that the autonomous routine does not continue
     * to run after the FMS notifies us that the autonomous period
     * has ended.
     * @param millis
     * @throws InterruptedException 
     */
    public final void waitFor(long millis) throws InterruptedException {
        accumulatedTime += millis;
        if (accumulatedTime > MAX_AUTO_PERIOD_LENGTH) {
            throw new InterruptedException("Auto is over!");
        }
        Thread.sleep(millis);
        if (!isAutonomous()) {
            throw new InterruptedException("Auto is over!");
        }
    }
    
    /**
     * Do not override this in subclasses, or else there may be no guarantee
     * that the autonomous thread and the main robot thread, executing teleop
     * code, will not attempt to run concurrently.
     */
    public final void teleopPeriodic() {
        if (autoThread != null) {
            autoThread.interrupt();
        }
        synchronized (modeLock) {
            teleopThreadsafe();
        }
    }
    
    /**
     * Called once when the robot enters the teleop mode.
     */
    public abstract void teleopInit();
    
    /**
     * Periodically called during robot teleop mode to enable operator control.
     * This is the only way teleop mode should be handled - do not directly call
     * teleopPeriodic from within this method or unbounded recursion will occur,
     * resulting in a stack overflow and crashed robot code. teleopContinuous
     * is likewise unsupported.
     */
    public abstract void teleopThreadsafe();

    /**
     * The one-shot autonomous "script" to be run in a new Thread
     * @throws Exception
     */
    public abstract void autonomousThreaded() throws Exception;
}
