/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package ca.team3161.iapetus;

import ca.team3161.iapetus.lib.robot.ThreadedAutoRobot;
import ca.team3161.lib.utils.controls.LogitechDualAction;
import ca.team3161.lib.robot.Drivetrain;
import ca.team3161.lib.utils.Utils;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.Talon;
import ca.team3161.lib.utils.io.DriverStationLCD;
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
    private final SpeedController rightDrive = new Drivetrain (new SpeedController[] {new Talon (4), new Victor (5), new Talon (6)});
    
    private final LogitechDualAction gamepad = new LogitechDualAction (Constants.Gamepad.PORT, Constants.Gamepad.DEADZONE);
    private final DriverStationLCD dsLcd = DriverStationLCD.getInstance();
    
    private final Timer autoElapsedTimer = new Timer();

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        dsLcd.println(0, "Robot Init!");
    }
    
    /**
     * This method is invoked by a new Thread.
     * The main robot thread's role during autonomous is simply to maintain
     * core robot functionality, eg feeding the Watchdog and responding to
     * FMS events. A new Thread handles autonomous behaviour so that we can
     * use Thread.sleep() rather than checking Timer values.
     * @throws Exception 
     */
    public void autonomousThreaded() throws Exception {
        SpeedController allDrive = new Drivetrain(new SpeedController[] {leftDrive, rightDrive});
        dsLcd.println(1, "AUTO: DRIVE 0.5 0.5");
        allDrive.set(0.5);
        waitFor(1000);
        dsLcd.println(1, "AUTO: DRIVE 0.0 0.0");
        allDrive.set(0.0);
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
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopThreadsafe() {
        dsLcd.println(0, "Teleop running");
        dsLcd.println(1, "Left Drive: " + leftDrive.get());
        dsLcd.println(2, "Right Drive: " + rightDrive.get());

        leftDrive.set(gamepad.getLeftY() - gamepad.getRightX());
        rightDrive.set(gamepad.getLeftY() + gamepad.getRightX());
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
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    }

}
