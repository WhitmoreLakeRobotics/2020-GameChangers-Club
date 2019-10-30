package org.firstinspires.ftc.teamcode;


public class Settings extends Object {

    //*********************************************************************************************
    // Rev product constants go HERE.   THye always should Start with REV_
    public static final int REV_CORE_HEX_MOTOR_TICKS_PER_REV = 288;
    public static final int REV_HD_40_MOTOR_TICKS_PER_REV = 1120;
    public static final int REV_HD_20_MOTOR_TICKS_PER_REV = 560;

    //----------------------------------------------------------------------------------------------
    // Safety Management
    //
    // These constants manage the duration we allow for callbacks to user code to run for before
    // such code is considered to be stuck (in an infinite loop, or wherever) and consequently
    // the robot controller application is restarted. They SHOULD NOT be modified except as absolutely
    // necessary as poorly chosen values might inadvertently compromise safety.
    //----------------------------------------------------------------------------------------------
    public static final int msStuckDetectInit = 10000;
    public static final int msStuckDetectInitLoop = 5000;
    public static final int msStuckDetectStart = 5000;
    public static final int msStuckDetectLoop = 5000;
    public static final int msStuckDetectStop = 1000;

    //*********************************************************************************************
    public static enum PARENTMODE {
        PARENT_MODE_AUTO,
        PARENT_MODE_TELE
    }

    public static enum CHASSIS_TYPE {
        CHASSIS_COMPETITION,
        CHASSIS_TEST
    }

}
