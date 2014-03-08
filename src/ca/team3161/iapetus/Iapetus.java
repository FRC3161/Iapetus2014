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
import ca.team3161.lib.robot.PIDDrivetrain;
import ca.team3161.lib.robot.pid.EncoderPidSrc;
import ca.team3161.lib.robot.pid.GyroPidSrc;
import ca.team3161.lib.robot.pid.PID;
import ca.team3161.lib.utils.Utils;
import ca.team3161.lib.utils.controls.Joystick;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
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
    private final Gyro gyro = new Gyro(1);
    private final Encoder leftEncoder = new Encoder(2, 3), rightEncoder = new Encoder(4, 5);
    private final PIDDrivetrain pidDrive = new PIDDrivetrain(leftDrive, rightDrive,
                new PID(new EncoderPidSrc(leftEncoder), 350.0f, -0.008f, /*-0.0075f*/0.0f, 0.018f),
                new PID(new EncoderPidSrc(rightEncoder), 350.0f, -0.008f, /*-0.0075f*/0.0f, 0.018f),
                new PID(new GyroPidSrc(gyro), 4.0f, 0.7f, 0.13f, 0.45f));
    private final Compressor compressor = new Compressor(7, 2);
    
    private final LogitechDualAction gamepad = new LogitechDualAction (Constants.Gamepad.PORT, Constants.Gamepad.DEADZONE);
    private final Joystick joystick = new Joystick(Constants.Joystick.PORT, Constants.Joystick.DEADZONE);

    private DriverStation.Alliance alliance;
    private final Relay underglowController = new Relay(1);
    private static final Relay.Value BLUE_UNDERGLOW = Relay.Value.kForward;
    private static final Relay.Value RED_UNDERGLOW = Relay.Value.kReverse;
    private static final Relay.Value PURPLE_BADASS_UNDERGLOW = Relay.Value.kOn;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        gamepad.setInverted(true);
        joystick.setInverted(true);
        alliance = DriverStation.getInstance().getAlliance();
        
        dsLcd.clear();
        dsLcd.println(0, "Alliance: " + alliance.name.toUpperCase());
        dsLcd.println(1, "I DRIVE: START");
        dsLcd.println(2, "I MODE: START");
        dsLcd.println(3, "I CLAW: CLOSE");
        dsLcd.println(4, "I PUNCHER: RESET");
        
        shooter.disableAll();
        shooter.start();
        shooter.setForkAngle(Constants.Positions.START);
        shooter.closeClaw();
        shooter.drawWinch();
        restartEncoders();
        compressor.start();
    }
    
    public void restartEncoders() {
        leftEncoder.stop();
        rightEncoder.stop();
        leftEncoder.reset();
        rightEncoder.reset();
        leftEncoder.start();
        rightEncoder.start();
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
        compressor.stop();
        underglowController.set(PURPLE_BADASS_UNDERGLOW);
        shooter.drawWinch();
        shooter.setForkAngle(Constants.Positions.START);
        shooter.closeClaw();
        restartEncoders();
        pidDrive.reset();
        pidDrive.start();
        
        /*
        FOR WHEN 1114 or someone needs 2ball
        */
        waitFor(8000);
        pidDrive.setTicksTarget(10000);
        pidDrive.waitForTarget();
        
        /* NORMAL AUTO ROUTINE */
        
        /*pidDrive.setTask(pidDrive.DRIVE);
        pidDrive.setTicksTarget(10000);
        pidDrive.waitForTarget();
        
        // next two commented lines are to try to ensure we are facing forward
        pidDrive.setTask(pidDrive.TURN);
        waitFor(1000);
        pidDrive.turnByDegrees(-(float)gyro.getAngle());
        
        shooter.setForkAngle(Constants.Positions.SHOOTING);
        shooter.setRoller(Constants.Shooter.ROLLER_SPEED);
        waitFor(1000);
        
        shooter.openClaw();
        shooter.setRoller(0.0f);
        waitFor(1500);
        
        shooter.fire();
        waitFor(750);
        */
        
        /* AUTO WITHOUT EXTRA THREADS */
        /*
        PID leftDrivePid = new PID(new EncoderPidSrc(leftEncoder), 350.0f, -0.008f, 0.0f, 0.018f);
        PID rightDrivePid = new PID(new EncoderPidSrc(rightEncoder), 350.0f, -0.008f, 0.0f, 0.018f);
        PID gyroPid = new PID(new GyroPidSrc(gyro), 4.0f, 0.7f, 0.13f, 0.45f);
        
        do {
            leftDrive.set(leftDrivePid.pid(12500) + gyroPid.pid(0.0f));
            rightDrive.set(rightDrivePid.pid(12500) - gyroPid.pid(0.0f));
            waitFor(50);
        } while (!leftDrivePid.atTarget() && !rightDrivePid.atTarget());
        leftDrivePid.clear();
        rightDrivePid.clear();
        restartEncoders();
        
        waitFor(750);
        
        do {
            leftDrive.set(gyroPid.pid(0.0f));
            rightDrive.set(gyroPid.pid(0.0f));
            waitFor(50);
        } while (!gyroPid.atTarget());
        gyroPid.clear();
        leftDrivePid.clear();
        rightDrivePid.clear();
        restartEncoders();
        
        shooter.openClaw();
        waitFor(1500);
        shooter.fire();
        waitFor(500);
        
        do {
            leftDrive.set(gyroPid.pid(180.0f));
            rightDrive.set(gyroPid.pid(180.0f));
            waitFor(50);
        } while (!gyroPid.atTarget());
        gyroPid.clear();
        leftDrivePid.clear();
        rightDrivePid.clear();
        restartEncoders();
        
        shooter.closeClaw();
        shooter.setForkAngle(Constants.Positions.START);
        */
        
        pidDrive.setTask(pidDrive.TURN);
        pidDrive.turnByDegrees(180.0f);
        pidDrive.waitForTarget();
        
        compressor.stop();
    }

    /**
     * Put anything here that needs to be performed by the main robot thread
     * during the autonomous period. DO NOT use any Drivetrain, Gamepad,
     * Solenoid, etc. fields in here - these are reserved for use ONLY
     * within autonomousThreaded()!
     */
    public void autonomousPeriodic() {
    }

    /**
     * Runs through once at the start of teleop
     */
    public void teleopInit() {
        compressor.start();
        alliance = DriverStation.getInstance().getAlliance();
        if (alliance.equals(DriverStation.Alliance.kBlue)) {
            underglowController.set(BLUE_UNDERGLOW);
        } else {
            underglowController.set(RED_UNDERGLOW);
        }
        pidDrive.cancel();
        shooter.setForkAngle(Constants.Positions.START);
        dsLcd.clear();
        dsLcd.println(0, "TELEOP MODE");
        dsLcd.println(2, "FORK MODE:");
        restartEncoders();
        compressor.start();
    }

    /**
     * This function is called periodically during operator control, just like
     * teleopPeriodic in the IterativeRobot base class.
     * We use teleopThreadsafe instead to ensure safety with our separate
     * autonomous thread. DO NOT create a teleopPeriodic in this class or any
     * subclasses! Use teleopThreadsafe instead, only!
     */
        
    public void teleopThreadsafe() {
        //semi-arcade drive
        leftDrive.set(joystick.getY() + joystick.getX());
        rightDrive.set(joystick.getY() - joystick.getX());
        dsLcd.println(1, "DRIVE: " + Utils.round(leftDrive.get(), 2) + " " + Utils.round(rightDrive.get(), 2));
        
        //dsLcd.println(1, "DRIVE: " + Utils.round(leftDrive.get(), 2) + " " + Utils.round(rightDrive.get(), 2));
        
        //trigger piston mechanism
        if (gamepad.getRightBumper()) {
            shooter.fire();
        }
        
        //shoulder motor (fork) control
        if (gamepad.getDpadVertical() > 0.0) {
            shooter.setForkAngle(Constants.Positions.START);
            //dsLcd.println(2, "FORK MODE: TRAVEL");
        }
        
        if (gamepad.getDpadHorizontal() == -1.0) {
            shooter.setForkAngle(Constants.Positions.SHOOTING);
            //dsLcd.println(2, "FORK MODE: SHOOTING");
        }
        
        if (gamepad.getDpadHorizontal() == 1.0) {
            shooter.setForkAngle(Constants.Positions.LOWGOAL);
        }
        
        if (gamepad.getButton(LogitechDualAction.SELECT)) {
            shooter.setForkAngle(Constants.Positions.TRUSS);
        }
        
        if (gamepad.getDpadVertical() < 0.0) {
            shooter.setForkAngle(Constants.Positions.INTAKE);
            //dsLcd.println(2, "FORK MODE: INTAKE");
        }
        
        //roller up/down
        if (gamepad.getButton(2)) {
            shooter.closeClaw();
            //dsLcd.println(4, "CLAW: CLOSE");
        }
    
        if (gamepad.getButton(1)) {
            shooter.openClaw();
            //dsLcd.println(4, "CLAW: OPEN");
        }
        
        /*if (gamepad.getButton(2)) {
            shooter.setRoller(0.0f);
        }*/
        
        if (gamepad.getLeftBumper()) {
            shooter.setRoller(Constants.Shooter.ROLLER_SPEED);
        }
        
        if (gamepad.getRightTrigger()) {
            shooter.setRoller(-Constants.Shooter.ROLLER_SPEED);
        }
        
        if (! (gamepad.getLeftBumper() || gamepad.getRightTrigger())) {
            shooter.setRoller(0.0f);
        }
    }

    /**
     * Called once when the robot enters the disabled state
     */
    public void disabledInit() {
        compressor.stop();
        pidDrive.cancel();
        leftEncoder.stop();
        rightEncoder.stop();
        leftEncoder.reset();
        rightEncoder.reset();
        leftDrive.disable();
        rightDrive.disable();
        shooter.disableAll();
        shooter.setForkAngle(Constants.Positions.START);
        shooter.closeClaw();
        compressor.stop();
        leftEncoder.reset();
        rightEncoder.reset();
    }

    /**
     * Called periodically while the robot is in the disabled state
     */
    public void disabledPeriodic() {
        leftDrive.disable();
        rightDrive.disable();
        shooter.disableAll();
        dsLcd.println(5, "POT: " + shooter.getRawPotValue());
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    }

}
