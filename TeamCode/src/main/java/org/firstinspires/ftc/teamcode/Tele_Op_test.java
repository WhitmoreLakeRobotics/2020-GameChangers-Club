

//package org.firstinspires.ftc.robotcontroller.external.samples;
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.RobotLog;


@TeleOp(name = "Tele_Op_Test", group = "TeleOp")
//@Disabled
public class Tele_Op_test extends OpMode {
    private static final String TAGTeleop = "8492-Tele_Op_test";
    Chassis_Test RBTChassis = new Chassis_Test();
    // Declare OpMode members.
    boolean gamepad2_a_pressed = false;
    boolean gamepad2_b_pressed = false;
    boolean gamepad2_x_pressed = false;
    boolean gamepad2_y_pressed = false;
    private double LeftMotorPower = 0;
    private double RightMotorPower = 0;

    private double powerNormal = Settings.CHASSIS_POWER_NORMAL;
    private double powerMax = Settings.CHASSIS_POWER_MAX;


    //*********************************************************************************************
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {// Safety Management
        //----------------------------------------------------------------------------------------------
        // These constants manage the duration we allow for callbacks to user code to run for before
        // such code is considered to be stuck (in an infinite loop, or wherever) and consequently
        // the robot controller application is restarted. They SHOULD NOT be modified except as absolutely
        // necessary as poorly chosen values might inadvertently compromise safety.
        //----------------------------------------------------------------------------------------------
        msStuckDetectInit = Settings.msStuckDetectInit;
        msStuckDetectInitLoop = Settings.msStuckDetectInitLoop;
        msStuckDetectStart = Settings.msStuckDetectStart;
        msStuckDetectLoop = Settings.msStuckDetectLoop;
        msStuckDetectStop = Settings.msStuckDetectStop;

        telemetry.addData("Tele_Op_test", "Initialized");

        RBTChassis.hardwareMap = hardwareMap;
        RBTChassis.telemetry = telemetry;
        RBTChassis.setMaxPower(powerNormal);
        RBTChassis.init();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).


        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery


    }

    //*********************************************************************************************
    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        RBTChassis.init_loop();
    }

    //*********************************************************************************************
    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        Runtime.getRuntime();
        RBTChassis.start();
        RBTChassis.setMotorMode_RUN_WITHOUT_ENCODER();
    }

    //*********************************************************************************************
    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        RBTChassis.loop();

        // if the driver has any triggers pulled this means H drive only drive the H wheels
        // as straightly as possible
        if (gamepad1.left_trigger > Settings.JOYSTICK_DEADBAND_TRIGGER || gamepad1.right_trigger > Settings.JOYSTICK_DEADBAND_TRIGGER) {
            RBTChassis.subHDrive.cmdTeleop(CommonLogic.joyStickMath(gamepad1.left_trigger),
                    CommonLogic.joyStickMath(gamepad1.right_trigger));
            RBTChassis.cmdTeleOp(0, 0);
        }
        // stop the H drive and give joystick values to the other wheels.
        else {
            RBTChassis.subHDrive.cmdStop();
            RBTChassis.cmdTeleOp(CommonLogic.joyStickMath(-gamepad1.left_stick_y),
                    CommonLogic.joyStickMath(-gamepad1.right_stick_y));
        }


        if (gamepad1.a) {
            RBTChassis.subPushers.cmdMoveAllDown();
        }

        if (gamepad1.b) {
            RBTChassis.subPushers.cmdMoveAllUp();
        }

        // Bumpers high and lower Powers for the wheels
        if (gamepad1.left_bumper) {
            RBTChassis.setMaxPower(powerMax);
        }

        if (gamepad1.right_bumper) {
            RBTChassis.setMaxPower(powerNormal);
        }

        // Bumpers close and open the gripper
        if (gamepad2.left_bumper) {
            if (RBTChassis.subGripper.getIsOpen()) {
                RBTChassis.subGripper.cmd_close();
            }
        }

        if (gamepad2.right_bumper) {
            if (RBTChassis.subGripper.getIsClosed()) {
                RBTChassis.subGripper.cmd_open();
            }
        }

        if (gamepad1.dpad_right) {
            if (RBTChassis.subGrabbers.getIsUpRight()) {
                RBTChassis.subGrabbers.cmdMoveDownRight();
            }
        }

        if (gamepad1.dpad_up) {
            if (RBTChassis.subGrabbers.getIsDownRight()) {
                RBTChassis.subGrabbers.cmdMoveUpRight();
            }
        }

        if (gamepad1.dpad_left) {
            if (RBTChassis.subGrabbers.getIsDownLeft()) {
                RBTChassis.subGrabbers.cmdMoveUpLeft();
            }
        }

        if (gamepad1.dpad_down) {
            if (RBTChassis.subGrabbers.getIsUpLeft()) {
                RBTChassis.subGrabbers.cmdMoveDownLeft();
            }
        }



        if (Math.abs(gamepad2.right_stick_y) > Settings.JOYSTICK_DEADBAND_STICK) {
            RBTChassis.subExtender.cmd_stickControl(gamepad2.right_stick_y);
        }

        if (gamepad2.a) {
            RBTChassis.subExtender.cmd_MoveToStart();
        }
        if (gamepad2.b) {
            RBTChassis.subExtender.cmd_MoveToPos1();
        }
        if (gamepad2.x) {
            RBTChassis.subExtender.cmd_MoveToPos3();
        }
        if (gamepad2.y) {
            RBTChassis.subExtender.cmd_MoveToPos2();
        }

        if (gamepad2.dpad_up) {
            RBTChassis.subLifter.incPositionIndex();
        }

        if (gamepad2.dpad_down) {
            RBTChassis.subLifter.decPositionIndex();
        }

    }

    //*********************************************************************************************
    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {

        RBTChassis.stop();

    }

    //*********************************************************************************************
}
