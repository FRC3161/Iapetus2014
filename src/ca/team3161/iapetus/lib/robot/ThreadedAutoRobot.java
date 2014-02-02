/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.team3161.iapetus.lib.robot;

import ca.team3161.iapetus.Constants;
import edu.wpi.first.wpilibj.IterativeRobot;

/**
 *
 * @author andrew
 */
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
    
    public void teleopPeriodic() {
        synchronized (modeLock) {
            teleopThreadsafe();
        }
    }
    
    public abstract void teleopThreadsafe();
    public abstract void autonomousThreaded() throws Exception;
}
