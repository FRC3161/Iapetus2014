package ca.team3161.lib.robot;

import ca.team3161.iapetus.RobotConstants;
import ca.team3161.lib.utils.Utils;
import edu.wpi.first.wpilibj.SpeedController;

import java.util.Enumeration;

/**
 * Created by andrew on 7/9/14.
 */
public class RampedDrivetrain extends Drivetrain {

    /**
     * Create a new Drivetrain instance
     * @param controllerArray an array of SpeedController objects. May be
     * all the same type, or may be mixed.
     */
    public RampedDrivetrain(final SpeedController[] controllerArray) {
        super(controllerArray);
    }

    /**
     * Set the pwm value (-1.0 to 1.0)
     * @param pwm the PWM value to assign to each SpeedController in the collection
     */
    public void set(double pwm) {
        // PWM value must be between -1 and 1
        pwm = Utils.normalizePwm(pwm);
        // velocity ramps
        pwm = velocityRamps(pwm, get(), RobotConstants.Drive.VELOCITY_RAMP_THRESHOLD);
        final Enumeration e = motorControllers.elements();
        while (e.hasMoreElements()) {
            final SpeedController controller = (SpeedController) e.nextElement();
            controller.set(inversion * pwm);
        }
    }

    /**
     * Don't use this!
     * @param pwm the PWM value to assign to each SpeedController in the collection
     * @param syncGroup
     */
    public void set(double pwm, final byte syncGroup) {
        // PWM value must be between -1 and 1
        pwm = Utils.normalizePwm(pwm);
        // velocity ramps
        pwm = velocityRamps(pwm, get(), RobotConstants.Drive.VELOCITY_RAMP_THRESHOLD);
        final Enumeration e = motorControllers.elements();
        while (e.hasMoreElements()) {
            final SpeedController controller = (SpeedController) e.nextElement();
            controller.set(inversion * pwm, syncGroup);
        }
    }

    /**
     * Velocity ramps to keep our motors from drawing way more
     * current than needed due to rapid changes in Joystick values
     * (aka smooth out acceleration a bit)
     * @param input the raw joystick value
     * @param currentMotorSpeed the current motor velocity
     * @param threshold the threshold value before applying a ramp
     * @return a step towards the joystick value
     */
    private static double velocityRamps(final double input, final double currentMotorSpeed, final double threshold) {
        if (input - currentMotorSpeed >= threshold) return (currentMotorSpeed + threshold); //if there is a 0.1+ diff between motor & joystick values while accelerating, insert ramp
        return input; //if there is a smaller gap, or if decelerating, continue normally
    }

}
