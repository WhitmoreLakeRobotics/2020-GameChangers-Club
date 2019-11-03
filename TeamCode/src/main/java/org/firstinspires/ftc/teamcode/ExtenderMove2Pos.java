package org.firstinspires.ftc.teamcode;

/* Extender controls all actions involving Extender
    - locking motors in break mode for initial extender position
    - starts with a reset of the encoders -- if needed.
*/


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

public class ExtenderMove2Pos extends BaseHardware {
    //Encoder positions for the EXTENDER
    public static final int EXTENDERPOS_TOL = 8;
    public static final double EXTENDERPOWER_EXTEND = .375;
    public static final double EXTENDERPOWER_RETRACT = -.375;
    public static final double EXTENDERPOWER_INIT = -.125;
    public static final double ExtenderStickDeadBand = .2;
    private static final String TAGExtender = "8492-Extender";
    private static final double EXTENERPOWER_Move2Start = .3;
    double EXTENDERPOWER_desired = 0;
    double EXTENDERPOWER_current = 0;

    private final int EXTENDER_POSITION_START_POS = 0;
    private final int EXTENDER_POSITION_Pos1 = 80;
    private final int EXTENDER_POSITION_Pos2 = 160;
    private final int EXTENDER_POSITION_Pos3 = 240;

    int extenderPosition_CURRENT = EXTENDER_POSITION_START_POS;

    ExtenderStates extenderStateCurrent = ExtenderStates.UNKNOWN;


    /* Declare OpMode members. */
    //private ElapsedTime runtime = new ElapsedTime();

    //set the HANGER powers... We will need different speeds for up and down.
    private ExtenderStates intakeArm = null;
    private double initMotorPower = 0;
    private double currentMotorpower = 0.5;

// Boolean to check if movement complete


// boolean to check if auton or stick control


    // declare motors
    private DcMotor EXT1 = null;
    private DigitalChannel extenderTCH = null;

    //*********************************************************************************************
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
    }

    public void extenderMotorEncoderReset() {
        EXT1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        EXT1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
    //*********************************************************************************************
    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {

    }
    //*********************************************************************************************
    private void initExtenderTCH() {
        ElapsedTime runtime = new ElapsedTime();
        runtime.reset();
        EXT1.setPower(EXTENDERPOWER_INIT);
        while (extenderTCH.getState()) {
            if (runtime.milliseconds() > 1500) {
                break;
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
    }

    public void autoStart() {
        // This is only called by chassis when running Auto OpModes
        initExtenderTCH();
        EXT1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        EXT1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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
        // Safety checks to prevent too low or too high
        extenderPosition_CURRENT = EXT1.getCurrentPosition();
        RobotLog.aa(TAGExtender, "Curr Position: " + extenderPosition_CURRENT);

        switch (extenderStateCurrent) {
            case COMMANDED_TO_START_POS: {
                EXT1.setTargetPosition(EXTENDER_POSITION_START_POS);
                EXT1.setPower(EXTENERPOWER_Move2Start);
                extenderStateCurrent = ExtenderStates.MOVING_TO_START_POS;
                break;
            }

            case MOVING_TO_START_POS: {
                if (testInPosition(extenderPosition_CURRENT, EXTENDER_POSITION_START_POS)) {
                    extenderStateCurrent = ExtenderStates.AT_START_POS;
                }
                break;
            }

            case COMMANDED_TO_POS1: {
                EXT1.setTargetPosition(EXTENDER_POSITION_Pos1);
                extenderStateCurrent = ExtenderStates.MOVING_TO_POS1;
                break;
            }

            case MOVING_TO_POS1: {
                if (testInPosition(extenderPosition_CURRENT, EXTENDER_POSITION_Pos1)) {
                    extenderStateCurrent = ExtenderStates.AT_START_POS;
                }
                break;
            }

            case COMMANDED_TO_POS2: {
                EXT1.setTargetPosition(EXTENDER_POSITION_Pos2);
                extenderStateCurrent = ExtenderStates.MOVING_TO_POS1;
                break;
            }

            case MOVING_TO_POS2: {
                if (testInPosition(extenderPosition_CURRENT, EXTENDER_POSITION_Pos2)) {
                    extenderStateCurrent = ExtenderStates.AT_START_POS;
                }
                break;
            }

            case COMMANDED_TO_POS3: {
                EXT1.setTargetPosition(EXTENDER_POSITION_Pos3);
                extenderStateCurrent = ExtenderStates.MOVING_TO_POS3;
                break;
            }

            case MOVING_TO_POS3: {
                if (testInPosition(extenderPosition_CURRENT, EXTENDER_POSITION_Pos3)) {
                    extenderStateCurrent = ExtenderStates.AT_POS3;
                }
                break;
            }

            case STICK_CONTROL_RETRACT: {
                if (testInPosition(extenderPosition_CURRENT, EXTENDER_POSITION_START_POS)) {
                    EXT1.setTargetPosition(EXTENDER_POSITION_START_POS);
                } else {
                    EXT1.setTargetPosition(extenderPosition_CURRENT - EXTENDERPOS_TOL);
                }
                break;
            }

            case STICK_CONTROL_EXTEND: {
                if (testInPosition(extenderPosition_CURRENT, EXTENDER_POSITION_Pos3)) {
                    EXT1.setTargetPosition(EXTENDER_POSITION_Pos3);
                } else {
                    EXT1.setTargetPosition(extenderPosition_CURRENT + EXTENDERPOS_TOL);
                }
                break;
            }

            // If we are in an AT_POS_XX state then there is nothing to do... We are where we need to be
            default: {
                break;
            }
        }
    }
    //*********************************************************************************************
    private boolean testInPosition(int currPos, int desiredPos) {

        return (CommonLogic.inRange(currPos, desiredPos, EXTENDERPOS_TOL));
    }
    //*********************************************************************************************
    public boolean isInStartPos() {
        return testInPosition(extenderPosition_CURRENT, EXTENDER_POSITION_START_POS);
    }
    //*********************************************************************************************
    public boolean isInPos1() {
        return testInPosition(EXTENDER_POSITION_Pos1, EXTENDER_POSITION_Pos1);
    }
    //*********************************************************************************************
    public boolean isInPos2() {
        return testInPosition(extenderPosition_CURRENT, EXTENDER_POSITION_Pos2);
    }
    //*********************************************************************************************
    public boolean isInPos3() {
        return testInPosition(extenderPosition_CURRENT, EXTENDER_POSITION_Pos3);
    }

    //*********************************************************************************************
    public void cmd_MoveToStart() {
        // only move to start one time... IF we are there, already going there do not restart the command
        if (extenderStateCurrent != ExtenderStates.COMMANDED_TO_START_POS &&
                extenderStateCurrent != ExtenderStates.MOVING_TO_START_POS &&
                extenderStateCurrent != ExtenderStates.AT_START_POS) {
            extenderStateCurrent = ExtenderStates.COMMANDED_TO_START_POS;
        }
    }
    //*********************************************************************************************
    public void cmd_MoveToPos1() {
        // only move to POS1 one time... IF we are there, already going there do not restart the command
        if (extenderStateCurrent != ExtenderStates.COMMANDED_TO_POS1 &&
                extenderStateCurrent != ExtenderStates.MOVING_TO_POS1 &&
                extenderStateCurrent != ExtenderStates.AT_POS1) {
            extenderStateCurrent = ExtenderStates.COMMANDED_TO_POS1;
        }
    }
    //*********************************************************************************************
    public void cmd_MoveToPos2() {
        // only move to POS2 one time... IF we are there, already going there do not restart the command
        if (extenderStateCurrent != ExtenderStates.COMMANDED_TO_POS2 &&
                extenderStateCurrent != ExtenderStates.MOVING_TO_POS2 &&
                extenderStateCurrent != ExtenderStates.AT_POS2) {
            extenderStateCurrent = ExtenderStates.COMMANDED_TO_POS2;
        }
    }

    //*********************************************************************************************
    public void cmd_MoveToPos3() {
        // only move to POS2 one time... IF we are there, already going there do not restart the command
        if (extenderStateCurrent != ExtenderStates.COMMANDED_TO_POS3 &&
                extenderStateCurrent != ExtenderStates.MOVING_TO_POS3 &&
                extenderStateCurrent != ExtenderStates.AT_POS3) {
            extenderStateCurrent = ExtenderStates.COMMANDED_TO_POS3;
        }
    }

    //*********************************************************************************************
    public void cmd_stickControl(double extenderThrottle) {

        if (Math.abs(extenderThrottle) > Math.abs(ExtenderStickDeadBand)) {
            if (extenderThrottle < 0) {
                extenderStateCurrent = ExtenderStates.STICK_CONTROL_RETRACT;
            }

            if (extenderThrottle > 0) {
                extenderStateCurrent = ExtenderStates.STICK_CONTROL_EXTEND;
            }
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
        EXT1.setPower(EXTENDERPOWER_desired);
    }
    //*********************************************************************************************
    public enum ExtenderStates {
        COMMANDED_TO_START_POS,
        MOVING_TO_START_POS,
        AT_START_POS,
        COMMANDED_TO_POS1,
        MOVING_TO_POS1,
        AT_POS1,
        COMMANDED_TO_POS2,
        MOVING_TO_POS2,
        AT_POS2,
        COMMANDED_TO_POS3,
        MOVING_TO_POS3,
        AT_POS3,
        STICK_CONTROL_RETRACT,
        STICK_CONTROL_EXTEND,
        UNKNOWN
    }
}
