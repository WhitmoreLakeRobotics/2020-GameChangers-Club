package org.firstinspires.ftc.teamcode;

public class Throttle extends Object {

    Settings.throttle_names name = Settings.throttle_names.unknown;
    double currValue = 0;
    double squareValue = 0;

    Throttle(Settings.throttle_names tn) {
        name = tn;
    }

    public void setName(Settings.throttle_names value) {
        name = value;
    }

    public Settings.throttle_names getName() {
        return name;
    }

    public void setValue(double value) {
        currValue = value;
    }

    public double getValue() {
        return currValue;
    }

    public double getSquared() {
        int sign = 1;
        if (currValue < 0) {
            sign = -1;
        }
        return Math.abs(Math.pow(currValue, 2)) * sign;
    }

}
