package org.firstinspires.ftc.teamcode;


public class Switch extends Object {

    Settings.switch_names name = null;
    boolean prevState = false;
    boolean currState = false;

    Switch(Settings.switch_names sn) {
        name = sn;
    }

    public Settings.switch_names getName() {
        return name;
    }

    public boolean getState() {
        return currState;
    }

    public void setState(boolean state) {
        prevState = currState;
        currState = state;
    }

    public boolean getOneShotState() {

        return (prevState == false && currState == true);
    }

}