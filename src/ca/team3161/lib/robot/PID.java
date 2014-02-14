package ca.team3161.lib.robot;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author 3161
 */
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Timer;


public class PID {

    
    public static Encoder sensorA;
    public static Encoder sensorB;
    public static Gyro sensorC;
    public static Gyro sensorD;

    
    public PID(Encoder sensor1, Encoder sensor2, Gyro sensor4) {
        
        sensorA = sensor1;
        sensorB = sensor2;
        sensorD = sensor4;
    }
    public PID(Gyro sensor3) {

        sensorC = sensor3;

    }
    double integral_errorR = 0;
    double prev_errorR = 0;
    double delta_errorR;
    double counter = 0;
    public double pidRight(double target, double Kp, double Ki, double Kd, double Kf) {

        double K_err;
        
        double p_out;
        double i_out;
        double d_out;
        double output;
        double fudge_out;
        


        
        K_err = target - sensorB.get();
        if(Math.abs(K_err) <= 150) {
            counter++;
        }
        else {
            counter = 0;
        }
        if(counter >= 10) {
            return 0;
        }
        


        delta_errorR = prev_errorR - K_err;

        prev_errorR = K_err;
        integral_errorR += K_err;

        p_out = K_err * Kp;
        i_out = integral_errorR * Ki;
        d_out = delta_errorR * Kd;
        fudge_out = sensorD.getAngle() * Kf;

        if (i_out > 1) {
            i_out = 1;
        }

        output = (p_out + i_out - d_out - fudge_out);

        if (output > 1) {

            return 1;
        }
        if (output < -1) {
            return -1;
        }
        return output;

    }
    double integral_errorL = 0;
    double prev_errorL = 0;
    double delta_errorL;
    int counter1 = 0;
    public double pidLeft(double target, double Kp, double Ki, double Kd, double Kf) {

        double K_err;
        
        double p_out;
        double i_out;
        double d_out;
        double output;
        
        double fudge_out = 0;


        //K_err = target - sensor;
        K_err = target - sensorA.get();
        if(Math.abs(K_err) <= 150) {
            counter1++;
        }
        else {
            counter1 = 0;
        }
        if(counter1 >= 10) {
            return 0;
        }


        delta_errorR = prev_errorR - K_err;

        prev_errorR = K_err;
        integral_errorR += K_err;

        p_out = K_err * Kp;
        i_out = integral_errorR * Ki;
        d_out = delta_errorR * Kd;
        fudge_out = sensorD.getAngle() * Kf;

        if (i_out > 1) {
            i_out = 1;
        }

        output = (p_out + i_out - d_out + fudge_out);

        if (output > 1) {

            return 1;
        }
        if (output < -1) {
            return -1;
        }
        return output;

    }

    
    double integral_errorTR = 0;
    double prev_errorTR = 0;
    double delta_errorTR;
    public double pidTurnRight(double target, double Kp, double Ki, double Kd) {

        double K_err;
        
        double p_out;
        double i_out;
        double d_out;
        double output;
        


        //K_err = target - sensor;
        K_err = target - sensorC.getAngle();
        

        delta_errorTR = prev_errorTR - K_err;
        
        prev_errorTR = K_err;
        integral_errorTR += K_err;

        p_out = K_err * Kp;
        i_out = integral_errorTR * Ki;
        d_out = delta_errorTR * Kd;

        if (i_out > 1) {
            i_out = 1;
        }

        output = (p_out + i_out - d_out);

        if (output > 1) {

            return 1;
        }
        if (output < -1) {
            return -1;
        }
        return output;

    }
    double integral_errorTL = 0;
    double prev_errorTL = 0;
    double delta_errorTL;
    public double pidTurnLeft(double target, double Kp, double Ki, double Kd) {
        
        double K_err;
        
        double p_out;
        double i_out;
        double d_out;
        double output;
        


        //K_err = target - sensor;
        K_err = target - sensorC.getAngle();
        
        
        delta_errorTL = prev_errorTL - K_err;
        //IO.sendMessage(1, "" + delta_error);
        prev_errorTL = K_err;
        integral_errorTL += K_err;

        p_out = K_err * Kp;
        i_out = integral_errorTL * Ki;
        d_out = delta_errorTL * Kd;

        if (i_out > 1) {
            i_out = 1;
        }

        output = (p_out + i_out + d_out);

        if (output > 1) {

            return 1;
        }
        if (output < -1) {
            return -1;
        }
        return output;

    }

    double prev_errorS = 0;
    double delta_errorS;
    Timer shootTimer = new Timer();

    public double pd(double target, double Kp, double Ki, double Kd, double Kf) {
        shootTimer.start();
        double K_err;

        double p_out;
        double d_out;
        double output;




        K_err = target - (sensorB.get() / shootTimer.get());


        delta_errorS = prev_errorS - K_err;

        prev_errorS = K_err;


        p_out = K_err * Kp;
        d_out = delta_errorS * Kd;



        output = (p_out + d_out);

        if (output > 1) {

            return 1;
        }
        if (output < -1) {
            return -1;
        }
        return output;

    }
    public static void clear() {
        double integral_errorTL = 0;
        double prev_errorTL = 0;
        double delta_errorTL = 0;
        double integral_errorTR = 0;
        double prev_errorTR = 0;
        double delta_errorTR = 0;
        double integral_errorR = 0;
        double prev_errorR = 0;
        double delta_errorR = 0;
        double prev_errorS = 0;
        double delta_errorS = 0;

    }


}
