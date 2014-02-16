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
import ca.team3161.lib.robot.pid.EncoderPidSrc;
import ca.team3161.lib.robot.pid.GyroPidSrc;
import ca.team3161.lib.robot.pid.PID;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Relay;

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
                new PID(new EncoderPidSrc(leftEncoder), 50.0f, -0.005f, 0.0f, 0.0f),
                new PID(new EncoderPidSrc(rightEncoder), 50.0f, -0.005f, 0.0f, 0.0f),
                new PID(new GyroPidSrc(gyro), 1.0f, 0.05f, 0.0f, 0.0f));
    
    private final LogitechDualAction gamepad = new LogitechDualAction (Constants.Gamepad.PORT, Constants.Gamepad.DEADZONE);

    private DriverStation.Alliance alliance;
    private final Relay underglowController = new Relay(8); // TODO: replace 8 with the actual Sidecar port
    private static final Relay.Value BLUE_UNDERGLOW = Relay.Value.kOn;
    private static final Relay.Value RED_UNDERGLOW = Relay.Value.kReverse;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        gamepad.setInverted(true);
        alliance = DriverStation.getInstance().getAlliance();
        
        dsLcd.clear();
        dsLcd.println(0, "Alliance: " + alliance.name.toUpperCase());
        dsLcd.println(1, "I DRIVE: START");
        dsLcd.println(2, "I MODE: START");
        dsLcd.println(3, "I CLAW: CLOSE");
        dsLcd.println(4, "I PUNCHER: RESET");
        
        if (alliance.equals(DriverStation.Alliance.kBlue)) {
            underglowController.set(BLUE_UNDERGLOW);
        } else {
            underglowController.set(RED_UNDERGLOW);
        }
        
        shooter.disableAll();
        shooter.start();
        shooter.setForkAngle(Constants.Positions.START);
        shooter.closeClaw();
        shooter.drawWinch();
        restartEncoders();
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
        /*
        restartEncoders();
        pidDrive.start();
        pidDrive.setTask(pidDrive.DRIVE);
        pidDrive.setLeftTicksTarget(1500);
        pidDrive.setRightTicksTarget(1500);
        pidDrive.waitForTarget();
        pidDrive.cancel();
        */
        
        //pidDrive.setTask(pidDrive.TURN);
        //pidDrive.turnByDegrees(180);
        //pidDrive.waitForTarget();
        
        
        dsLcd.println(5, "AUTO: START");
        SpeedController allDrive = new Drivetrain(new SpeedController[] {leftDrive, rightDrive});
        dsLcd.println(1, "A DRIVE: 0.5 0.5");
        allDrive.set(0.5);
        
        dsLcd.println(2, "A MODE: SHOOTING");
        shooter.setForkAngle(Constants.Positions.SHOOTING);
        waitFor(1000);
        
        dsLcd.println(1, "A DRIVE: 0.0 0.0");
        allDrive.set(0.0);
        
        dsLcd.println(3, "A CLAW: OPEN");
        shooter.openClaw();
        waitFor(500);
        
        dsLcd.clear(4);
        dsLcd.println(4, "A PUNCHER: FIRING");
        shooter.fire();
        waitFor(300);
        
        dsLcd.println(4, "A PUNCHER: RESET");
        dsLcd.println(3, "A CLAW: CLOSE");
        shooter.closeClaw();
        
        dsLcd.println(1, "A DRIVE: -0.5 -0.5");
        allDrive.set(-0.5);
        
        dsLcd.println(2, "A MODE: INTAKE");
        shooter.setForkAngle(Constants.Positions.INTAKE);
        waitFor(1000);
        
        dsLcd.println(1, "A DRIVE: 0.5 -0.5");
        leftDrive.set(0.5);
        rightDrive.set(-0.5);
        waitFor(2000);
        
        dsLcd.println(1, "A DRIVE: 0.0 0.0");
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
        dsLcd.println(1, "L: " + leftEncoder.get());
        dsLcd.println(2, "R: " + rightEncoder.get());
    }

    /**
     * Runs through once at the start of teleop
     */
    public void teleopInit() {
        pidDrive.cancel();
        dsLcd.clear();
        dsLcd.println(0, "TELEOP MODE");
        dsLcd.println(2, "FORK MODE:");
        restartEncoders();
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
        leftDrive.set(gamepad.getLeftY() + gamepad.getRightX());
        rightDrive.set(gamepad.getLeftY() - gamepad.getRightX());
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
        
        if (gamepad.getDpadHorizontal() == 1.0 || gamepad.getDpadHorizontal() == -1.0) {
            shooter.setForkAngle(Constants.Positions.SHOOTING);
            //dsLcd.println(2, "FORK MODE: SHOOTING");
        }
        
        if (gamepad.getDpadVertical() < 0.0) {
            shooter.setForkAngle(Constants.Positions.INTAKE);
            //dsLcd.println(2, "FORK MODE: INTAKE");
        }
        
        //roller on/off
        if (gamepad.getRightTrigger()) {
            shooter.setRoller(0.5f);
            //dsLcd.println(3, "ROLLER: ON");
        } else {
            shooter.setRoller(0.0f);
            //dsLcd.println(3, "ROLLER: OFF");
        }
        
        //roller up/down
        if (gamepad.getLeftTrigger()) {
            shooter.closeClaw();
            //dsLcd.println(4, "CLAW: CLOSE");
        }
    
        if (gamepad.getLeftBumper()) {
            shooter.openClaw();
            //dsLcd.println(4, "CLAW: OPEN");
        }
        
        //dsLcd.println(4, "FORK ANGLE: " + Utils.round(shooter.getForkAngle(), 2));
        
        if (shooter.isFiring()) {
            //dsLcd.println(5, "SHOOTER: FIRING");
        } else if (!shooter.getStopSwitch()) {
            //dsLcd.println(5, "SHOOTER: RELOADING");
        } else {
            //dsLcd.println(5, "SHOOTER: LOADED");
        }
    }

    /**
     * Called once when the robot enters the disabled state
     */
    public void disabledInit() {
        pidDrive.cancel();
        leftEncoder.stop();
        rightEncoder.stop();
        leftEncoder.reset();
        rightEncoder.reset();
        leftDrive.disable();
        rightDrive.disable();
        shooter.disableAll();
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
