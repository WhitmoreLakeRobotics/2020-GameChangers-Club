package org.firstinspires.ftc.teamcode;

public class CommonLogic extends Object {

    //*********************************************************************************************
    public static double CapMotorPower (double MotorPower, double negCapValue, double posCapValue){
        // logic to cap the motor power between a good range
        double retValue = MotorPower;

        if (MotorPower < negCapValue) {
            retValue = negCapValue;
        }

        if (MotorPower > posCapValue) {
            retValue = posCapValue;
        }

        return retValue;
    }

    //*********************************************************************************************
    public static double joyStickMath(double joyValue) {
        int sign = 1;
        double retValue = 0;
        if (joyValue < 0) {
            sign = -1;
        }
        return Math.abs(Math.pow(joyValue, 2)) * sign;
    }

}