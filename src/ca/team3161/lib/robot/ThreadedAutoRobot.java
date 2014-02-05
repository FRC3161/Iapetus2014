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

import ca.team3161.iapetus.Constants;
import edu.wpi.first.wpilibj.IterativeRobot;

public abstract class ThreadedAutoRobot extends IterativeRobot {
    
    private static final int MAX_AUTO_PERIOD_LENGTH = Constants.Game.AUTONOMOUS_SECONDS * 1000;
    private volatile int accumulatedTime = 0;
    private final Object modeLock = new Object();
    
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
    public void autonomousInit() {
        accumulatedTime = 0;
        new Thread(new Runnable() {
            public void run() {
                try {
                    synchronized (modeLock) {
                        autonomousThreaded();
                    }
                } catch (Exception e) {
                }
            }
        }).start();
    }
    
    /**
     * Add a delay to the autonomous routine.
     * This also ensures that the autonomous routine does not continue
     * to run after the FMS notifies us that the autonomous period
     * has ended.
     * @param millis
     * @throws InterruptedException 
     */
    public void waitFor(long millis) throws InterruptedException {
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
    public void teleopPeriodic() {
        synchronized (modeLock) {
            teleopThreadsafe();
        }
    }
    
    /**
     * Override these two methods in subclasses to complete autonomous
     * and teleop functionality
     */
    public abstract void teleopThreadsafe();

    /**
     * The one-shot autonomous "script" to be run in a new Thread
     * @throws Exception
     */
    public abstract void autonomousThreaded() throws Exception;
}
