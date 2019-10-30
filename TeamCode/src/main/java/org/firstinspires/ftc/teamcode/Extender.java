package org.firstinspires.ftc.teamcode;

/* Extender controls the extending slide on the robot
    - locking motors in brake mode for initial hanger
*/


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;


//@TeleOp(name = "Extender", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class Extender extends BaseHardware {
    //Encoder positions for the EXTENDER

    public static final int RESTMODE = 0;
    public static final int EXTENDERPOS_TOL = 40;
    public static final double EXTENDERPOWER_EXTEND = .375;
    public static final double EXTENDERPOWER_RETRACT = -.375;
    public static final double EXTENDERPOWER_INIT = -.125;
    public static final double ExtenderStickDeadBand = .2;
    private static final String TAGExtender = "8492-Extender";
    private Settings.PARENTMODE parentMode_Current = null;
    private Settings.CHASSIS_TYPE chassisType = null;
    double EXTENDERPOWER_desired = 0;
    double EXTENDERPOWER_current = 0;
    boolean cmdComplete = false;
    boolean underStickControl = false;

    int extenderPosition_Start_Pos = 0;
    int extenderPosition_Pos1 = 95;
    int extenderPosition_Pos2 = 190;
    int extenderPosition_Pos3 = 288;
    int extenderPosition_CURRENT = extenderPosition_Start_Pos;

    ExtenderStates extenderStateCurrent = ExtenderStates.UNKNOWN;


    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();

    //set the HANGER powers... We will need different speeds for up and down.
    private ExtenderStates intakeArm = null;
    private double initMotorPower = 0;
    private double currentMotorpower = 0.5;

// Boolean to check if movement complete


// boolean to check if auton or stick control


    // declare motors
    private DcMotor EXT1 = null;
    private DigitalChannel extenderTCH = null;


    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {

        // telemetry.addData("Status", "Initialized");

        /* eg: Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step (using the FTC Robot Controller app on the phone).
         */

        EXT1 = hardwareMap.dcMotor.get("EXT1");
        EXT1.setDirection(DcMotor.Direction.REVERSE);
        EXT1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        RobotLog.aa(TAGExtender, "extenderPos: " + EXT1);
        //do not know what digital channel is check here for errors ******
        extenderTCH = hardwareMap.get(DigitalChannel.class, "extenderTCH");
        extenderTCH.setMode(DigitalChannel.Mode.INPUT);
        telemetry.addData("Extender", "Initialized");

    }

    //*********************************************************************************************
    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        //initPowerHang();
    }

    //*********************************************************************************************
    public void setParentMode(Settings.PARENTMODE pm) {

        parentMode_Current = pm;
    }

    //*********************************************************************************************
    public void setChassisType (Settings.CHASSIS_TYPE ct){
        chassisType = ct;
    }
    //*********************************************************************************************

    private void initExtenderTCH() {
        ElapsedTime runtime = new ElapsedTime();
        runtime.reset();
        EXT1.setPower(EXTENDERPOWER_INIT);
        if (chassisType == Settings.CHASSIS_TYPE.CHASSIS_COMPETITION) {
            while (extenderTCH.getState()) {
                if (runtime.milliseconds() > 1500) {
                    break;
                }
            }
        }
        EXT1.setPower(0);
    }

    //*********************************************************************************************
    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        // this is always called by chassis
        EXT1.setPower(0);
        switch (parentMode_Current) {
            case PARENT_MODE_AUTO:
                autoStart();
                break;
            case PARENT_MODE_TELE:
                teleStart();
                break;
            default:
                break;
        }
    }

    //*********************************************************************************************
    public void autoStart() {
        // This is only called by chassis when running Auto OpModes
        initExtenderTCH();
        EXT1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        EXT1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    //*********************************************************************************************

    public void teleStart() {
        // This is only called by chassis when running Tele Modes

    }

    //*********************************************************************************************

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        SetMotorPower(EXTENDERPOWER_desired);
    }
    //*********************************************************************************************

    private void SetMotorPower(double newMotorPower) {
        // Safety checks to prevent too low or too high
        extenderPosition_CURRENT = EXT1.getCurrentPosition();
        double newPower = newMotorPower;
        //RobotLog.aa(TAGExtender, "Curr Position: " + extenderPosition_CURRENT);
        //RobotLog.aa(TAGExtender, "set pwr : " + newPower);

        if (ExtenderStates.MOVE_TO_START_POS == extenderStateCurrent) {
            //if were within start tolerance, stop
            if (isInStartPos()) {
                newPower = 0;
                extenderStateCurrent = ExtenderStates.START_POS;
            }
        }

        if (ExtenderStates.MOVING_TO_POS1 == extenderStateCurrent) {
            //if were within start tolerance, stop
            if (isInPos1()) {
                newPower = 0;
                extenderStateCurrent = ExtenderStates.POS1;
            }
        }

        if (ExtenderStates.MOVING_TO_POS2 == extenderStateCurrent) {
            //if were within start tolerance, stop
            if (isInPos2()) {
                newPower = 0;
                extenderStateCurrent = ExtenderStates.POS2;
            }
        }

        if (ExtenderStates.MOVING_TO_POS3 == extenderStateCurrent) {
            //if were within start tolerance, stop
            if (isInPos3()) {
                newPower = 0;
                extenderStateCurrent = ExtenderStates.POS3;
            }
        }


        if (ExtenderStates.STICK_CONTROL == extenderStateCurrent) {
            // if under stick control then do not move less then start_pos
            if (extenderPosition_CURRENT < extenderPosition_Start_Pos) {
                newPower = 0;
            }
            // if under stick control then do not move greater than pos3
            if (extenderPosition_CURRENT > extenderPosition_Pos3) {
                newPower = 0;
            }
        }


        // if on the switch and we are trying to retract set newPower to stop value
        if (!extenderTCH.getState() && (newPower < 0)) {
            newPower = 0;
            extenderStateCurrent = ExtenderStates.START_POS;
        }

        //only set the power to the hardware when it is being changed.
        //only set the power to the hardware when it is being changed.
        if (newPower != EXTENDERPOWER_current) {
            EXTENDERPOWER_current = newPower;
            EXTENDERPOWER_desired = newPower;
            EXT1.setPower(EXTENDERPOWER_desired);
        }
    }
    //*********************************************************************************************

    private boolean testInPosition(int currPos, int desiredPos) {

        boolean retValue = false;

        if ((currPos >= (desiredPos - EXTENDERPOS_TOL)) && (EXTENDERPOWER_current > 0)) {
            retValue = true;

        }
        if ((currPos <= (desiredPos + EXTENDERPOS_TOL)) && (EXTENDERPOWER_current < 0)) {
            retValue = true;
        }
        cmdComplete = retValue;
        return (retValue);
    }

    //*********************************************************************************************
    public boolean getCommandComplete() {
        return cmdComplete;
    }

    //*********************************************************************************************

    public boolean isInStartPos() {
        return testInPosition(extenderPosition_CURRENT, extenderPosition_Start_Pos);
    }
    //*********************************************************************************************

    public boolean isInPos1() {
        return testInPosition(extenderPosition_Pos1, extenderPosition_Pos1);
    }
    //*********************************************************************************************

    public boolean isInPos2() {
        return testInPosition(extenderPosition_CURRENT, extenderPosition_Pos2);
    }
    //*********************************************************************************************

    public boolean isInPos3() {
        return testInPosition(extenderPosition_CURRENT, extenderPosition_Pos3);
    }
    //*********************************************************************************************
    // somebody pressed a button or ran Auton to send command to move to a given location.
    // create new process
    private void cmd_MoveToTarget(int TargetTicks) {
        telemetry.addData("moving", TargetTicks);
        if (TargetTicks > extenderPosition_CURRENT) {
            EXTENDERPOWER_desired = EXTENDERPOWER_EXTEND;
        } else if (TargetTicks < extenderPosition_CURRENT) {
            EXTENDERPOWER_desired = EXTENDERPOWER_RETRACT;
        } else {
            EXTENDERPOWER_desired = 0;
        }
    }  // cmd_MoveToTarget

    //*********************************************************************************************

    public void cmd_MoveToStart() {
        extenderStateCurrent = ExtenderStates.MOVE_TO_START_POS;
        cmd_MoveToTarget(extenderPosition_Start_Pos);
    }

    //*********************************************************************************************

    public void cmd_MoveToPos1() {
        extenderStateCurrent = ExtenderStates.MOVING_TO_POS1;
        cmd_MoveToTarget(extenderPosition_Pos1);
    }
    //*********************************************************************************************

    public void cmd_MoveToPos2() {
        extenderStateCurrent = ExtenderStates.MOVING_TO_POS2;
        cmd_MoveToTarget(extenderPosition_Pos2);
    }
    //*********************************************************************************************


    public void cmd_MoveToPos3() {
        extenderStateCurrent = ExtenderStates.MOVING_TO_POS3;
        cmd_MoveToTarget(extenderPosition_Pos3);
    }
    //*********************************************************************************************


    public void cmd_stickControl(double extenderThrottle) {

        if (Math.abs(extenderThrottle) > Math.abs(ExtenderStickDeadBand)) {
            extenderStateCurrent = ExtenderStates.STICK_CONTROL;
            EXTENDERPOWER_desired = extenderThrottle;
        }
    }
    //*********************************************************************************************

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        EXTENDERPOWER_current = 0;
        EXTENDERPOWER_desired = 0;
        SetMotorPower(EXTENDERPOWER_desired);
    }
    //*********************************************************************************************

    public enum ExtenderStates {
        START_POS,
        MOVE_TO_START_POS,
        MOVING_TO_POS1,
        POS1,
        MOVING_TO_POS2,
        POS2,
        MOVING_TO_POS3,
        POS3,
        UNKNOWN,
        STICK_CONTROL
    }
    //*********************************************************************************************

}
