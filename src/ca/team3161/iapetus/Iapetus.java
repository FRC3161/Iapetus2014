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

/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package ca.team3161.iapetus;

import ca.team3161.lib.robot.ThreadedAutoRobot;
import ca.team3161.lib.utils.controls.LogitechDualAction;
import ca.team3161.lib.robot.Drivetrain;
import ca.team3161.lib.utils.Utils;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Timer;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Iapetus extends ThreadedAutoRobot {

    private final SpeedController leftDrive = new Drivetrain (new SpeedController[] {new Talon (1), new Victor (2), new Talon (3)}).setInverted(true);
    private final SpeedController rightDrive = new Drivetrain (new SpeedController[] {new Talon (4), new Victor (5), new Talon (6)}).setInverted(false);
    private final Shooter shooter = new Shooter();
    
    private final LogitechDualAction gamepad = new LogitechDualAction (Constants.Gamepad.PORT, Constants.Gamepad.DEADZONE);

    private final Timer autoElapsedTimer = new Timer(), shooterTriggerTimer = new Timer();
    private DriverStation.Alliance alliance;
    private final Relay underglowController = new Relay(8); // TODO: replace 8 with the actual Sidecar port
    private static final Relay.Value BLUE_UNDERGLOW = Relay.Value.kOn;
    private static final Relay.Value RED_UNDERGLOW = Relay.Value.kReverse;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        alliance = DriverStation.getInstance().getAlliance();
        gamepad.setInverted(true);
        dsLcd.println(0, "Alliance: " + alliance.name.toUpperCase());
        if (alliance.equals(DriverStation.Alliance.kBlue)) {
            underglowController.set(BLUE_UNDERGLOW);
        } else {
            underglowController.set(RED_UNDERGLOW);
        }
        shooter.disableAll();
    }

    /**
     * This method is invoked by a new Thread.
     * The main robot thread's role during autonomous is simply to maintain
     * core robot functionality, eg feeding the Watchdog and responding to
     * FMS events. A new Thread handles autonomous behaviour so that we can
     * use Thread.sleep() rather than checking Timer values.
     * This method's semantics are one-shot, as opposed to continuous looping
     * as provided by autonomousPeriodic or autonomousContinuous. This allows
     * simpler scripting and also avoids busy-wait conditions.
     * @throws Exception 
     */
    public void autonomousThreaded() throws Exception {
        SpeedController allDrive = new Drivetrain(new SpeedController[] {leftDrive, rightDrive});
        dsLcd.clear();
        dsLcd.println(1, "AUTO: DRIVE 0.5 0.5");
        allDrive.set(0.5);
        waitFor(1000);
        dsLcd.println(1, "AUTO: DRIVE 0.0 0.0");
        allDrive.set(0.0);
        shooter.openClaw();
        shooter.setFork(-0.5);
        waitFor(300);
        shooter.setFork(0.0);
        shooter.closeClaw();
        dsLcd.println(5, "AUTO: FINISHED");
    }

    /**
     * Put anything here that needs to be performed by the main robot thread
     * during the autonomous period. DO NOT use any Drivetrain, Gamepad,
     * Solenoid, etc. fields in here - these are reserved for use ONLY
     * within autonomousThreaded()!
     */
    public void autonomousPeriodic() {
        autoElapsedTimer.start(); // if it's already started, nothing happens
        dsLcd.println(0, "Auto elapsed: " + Utils.round(autoElapsedTimer.get(), 2) + "s");
    }

    /**
     * Runs through once at the start of teleop
     */
    public void teleopInit() {
        autoElapsedTimer.stop();
        autoElapsedTimer.reset();
    }

    /**
     * This function is called periodically during operator control, just like
     * teleopPeriodic in the IterativeRobot base class.
     * We use teleopThreadsafe instead to ensure safety with our separate
     * autonomous thread. DO NOT create a teleopPeriodic in this class or any
     * subclasses! Use teleopThreadsafe instead, only!
     */
    public void teleopThreadsafe() {
        dsLcd.clear();
        
        dsLcd.println(0, "Teleop running");
        dsLcd.println(1, "Left Drive: " + leftDrive.get());
        dsLcd.println(2, "Right Drive: " + rightDrive.get());
        dsLcd.println(3, "Stopswitch: " + shooter.getStopSwitch());

        //semi-arcade drive
        leftDrive.set(gamepad.getLeftY() + gamepad.getRightX());
        rightDrive.set(gamepad.getLeftY() - gamepad.getRightX());
        
        //trigger piston mechanism
        if (gamepad.getButton(2) && shooterTriggerTimer.get() < 0.25) {
            shooter.pullTrigger();
            shooterTriggerTimer.start();
        }
        
        if (shooterTriggerTimer.get() > 0.25) {
            shooterTriggerTimer.stop();
            shooterTriggerTimer.reset();
            shooter.returnTrigger();
            shooter.drawWinch(0.5);
        }
        
        if (shooter.getStopSwitch()) {
            shooter.drawWinch(0.0d);
        }
        
        //shoulder motor (fork) conroll
        if (gamepad.getDpadVertical() > 0.0) {
            shooter.setFork(0.25);
        }
        
        if (gamepad.getDpadVertical() < 0.0) {
            shooter.setFork(-0.25);
        }
        
        //roller up/down
        if (shooter.getClaw()) {
            shooter.setRoller(0.5);
        } else {
            shooter.setRoller(0.0);
        }
    }

    /**
     * Called once when the robot enters the disabled state
     */
    public void disabledInit() {
        leftDrive.disable();
        rightDrive.disable();
    }

    /**
     * Called periodically while the robot is in the disabled state
     */
    public void disabledPeriodic() {
        leftDrive.disable();
        rightDrive.disable();
        shooter.disableAll();
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    }

}
