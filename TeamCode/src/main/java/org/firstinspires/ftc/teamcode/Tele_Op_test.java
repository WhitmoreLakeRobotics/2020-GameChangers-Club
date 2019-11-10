

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
    //    // Declare OpMode members.
    private boolean gp1_prev_a = false;
    private boolean gp1_prev_b = false;
    private boolean gp1_prev_x = false;
    private boolean gp1_prev_y = false;
    private boolean gp1_prev_right_bumper = false;
    private boolean gp1_prev_left_bumper = false;
    private boolean gp1_prev_dpad_up = false;
    private boolean gp1_prev_dpad_down = false;
    private boolean gp1_prev_dpad_left = false;
    private boolean gp1_prev_dpad_right = false;

    private boolean gp2_prev_a = false;
    private boolean gp2_prev_b = false;
    private boolean gp2_prev_x = false;
    private boolean gp2_prev_y = false;
    private boolean gp2_prev_right_bumper = false;
    private boolean gp2_prev_left_bumper = false;
    private boolean gp2_prev_dpad_up = false;
    private boolean gp2_prev_dpad_down = false;
    private boolean gp2_prev_dpad_left = false;
    private boolean gp2_prev_dpad_right = false;
    private double LeftMotorPower = 0;
    private double RightMotorPower = 0;

    //*********************************************************************************************
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        //----------------------------------------------------------------------------------------------
        // Safety Management
        //
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
        RBTChassis.setMaxPower(Settings.CHASSIS_POWER_NORMAL);
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

        //can not do anything until hDrive is zeroed and ready
        if (RBTChassis.subHDrive.getCurrentMode() == HDrive.HDriveMode.Initializing) {
            return;
        }

        //***********   Gamepad 1 controls ********

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


        //***********   Pushers
        if (CommonLogic.oneShot(gamepad1.a,gp1_prev_a)) {
            RBTChassis.subPushers.cmdMoveAllDown();
        }

        if (CommonLogic.oneShot(gamepad1.b, gp1_prev_b)) {
            RBTChassis.subPushers.cmdMoveAllUp();
        }

        // Bumpers high and lower Powers for the wheels
        if (CommonLogic.oneShot(gamepad1.left_bumper, gp1_prev_left_bumper)) {
            RBTChassis.setMaxPower(Settings.CHASSIS_POWER_MAX);
        }

        if (CommonLogic.oneShot(gamepad1.right_bumper, gp1_prev_right_bumper)) {
            RBTChassis.setMaxPower(Settings.CHASSIS_POWER_NORMAL);
        }

        //***********  Grabbers
        if (CommonLogic.oneShot(gamepad1.dpad_right, gp1_prev_dpad_right)) {
            if (RBTChassis.subGrabbers.getIsUpRight()) {
                RBTChassis.subGrabbers.cmdMoveDownRight();
            }
        }

        if (CommonLogic.oneShot(gamepad1.dpad_up, gp1_prev_dpad_up)) {
            if (RBTChassis.subGrabbers.getIsDownRight()) {
                RBTChassis.subGrabbers.cmdMoveUpRight();
            }
        }

        if (CommonLogic.oneShot(gamepad1.dpad_left, gp1_prev_dpad_left)) {
            if (RBTChassis.subGrabbers.getIsDownLeft()) {
                RBTChassis.subGrabbers.cmdMoveUpLeft();
            }
        }

        if (CommonLogic.oneShot(gamepad1.dpad_down, gp1_prev_dpad_down)) {
            if (RBTChassis.subGrabbers.getIsUpLeft()) {
                RBTChassis.subGrabbers.cmdMoveDownLeft();
            }
        }

        //***********   Gamepad 2 controls ********

        // Bumpers close and open the gripper
        if (CommonLogic.oneShot(gamepad2.left_bumper, gp2_prev_left_bumper)) {
            if (RBTChassis.subGripper.getIsOpen()) {
                RBTChassis.subGripper.cmd_close();
            }
        }

        if (CommonLogic.oneShot(gamepad2.right_bumper, gp2_prev_right_bumper)) {
            if (RBTChassis.subGripper.getIsClosed()) {
                RBTChassis.subGripper.cmd_open();
            }
        }

        //if (Math.abs(gamepad2.right_stick_y) > Settings.JOYSTICK_DEADBAND_STICK) {
        //    RBTChassis.subExtender.cmd_stickControl(gamepad2.right_stick_y);
        //}

        if (CommonLogic.oneShot(gamepad2.a, gp2_prev_a)) {
            RBTChassis.subExtender.cmd_MoveToStart();
        }

        if (CommonLogic.oneShot(gamepad2.b, gp2_prev_b)) {
            RBTChassis.subExtender.cmd_MoveToPos1();
        }

        if (CommonLogic.oneShot(gamepad2.x, gp2_prev_x)) {
            RBTChassis.subExtender.cmd_MoveToPos3();
        }

        if (CommonLogic.oneShot(gamepad2.y, gp2_prev_y)) {
            RBTChassis.subExtender.cmd_MoveToPos2();
        }

        if (Math.abs(gamepad2.left_stick_y) > Settings.JOYSTICK_DEADBAND_STICK) {
            //RBTChassis.subLifter.cmd_stickControl(gamepad2.left_stick_y);
        }

        if (CommonLogic.oneShot(gamepad2.dpad_up, gp2_prev_dpad_up)) {
            RBTChassis.subLifter.incPositionIndex();
        }

        if (CommonLogic.oneShot(gamepad2.dpad_down, gp2_prev_dpad_down)) {
            RBTChassis.subLifter.decPositionIndex();
        }

        // Update the previous status for gamepad 1
        gp1_prev_a = gamepad1.a;
        gp1_prev_b = gamepad1.b;
        gp1_prev_x = gamepad1.x;
        gp1_prev_y = gamepad1.y;
        gp1_prev_left_bumper = gamepad1.left_bumper;
        gp1_prev_right_bumper = gamepad1.right_bumper;
        gp1_prev_dpad_down = gamepad1.dpad_down;
        gp1_prev_dpad_left = gamepad1.dpad_left;
        gp1_prev_dpad_up= gamepad1.dpad_up;
        gp1_prev_dpad_right = gamepad1.dpad_right;

        // Update the previous status for gamepad 2
        gp2_prev_a = gamepad2.a;
        gp2_prev_b = gamepad2.b;
        gp2_prev_x = gamepad2.x;
        gp2_prev_y = gamepad2.y;
        gp2_prev_left_bumper = gamepad2.left_bumper;
        gp2_prev_right_bumper = gamepad2.right_bumper;
        gp2_prev_dpad_down = gamepad2.dpad_down;
        gp2_prev_dpad_left = gamepad2.dpad_left;
        gp2_prev_dpad_up= gamepad2.dpad_up;
        gp2_prev_dpad_right = gamepad2.dpad_right;

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

