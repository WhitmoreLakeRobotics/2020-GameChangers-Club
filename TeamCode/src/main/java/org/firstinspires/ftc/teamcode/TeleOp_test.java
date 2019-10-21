

//package org.firstinspires.ftc.robotcontroller.external.samples;
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.RobotLog;

//@TeleOp_testp(name = "Teleop-TestChassis", group = "TeleOp_test")
@TeleOp(name = "Teleop-TestChassis", group = "TeleOp")
//@Disabled
public class TeleOp_test extends OpMode {
    private static final String TAGTeleop = "8492-Teleop";
    Chassis_Test RBTChassis = new Chassis_Test();
    // Declare OpMode members.
    boolean gamepad2_a_pressed = false;
    boolean gamepad2_b_pressed = false;
    boolean gamepad2_x_pressed = false;
    boolean gamepad2_y_pressed = false;
    private double LeftMotorPower = 0;
    private double RightMotorPower = 0;

    private double powerNormal = .5;
    private double powerMax = .8;
    private final double DEADBAND_TRIGGER = .1;
    //*********************************************************************************************
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("TeleOp_test", "Initialized");
        RBTChassis.setParentMode(Settings.PARENTMODE.PARENT_MODE_TELE);
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


        //RobotLog.aa(TAGTeleop, "gamepad1 " + RightMotorPower);
        //RobotLog.aa(TAGTeleop, "trigers " + gamepad1.left_trigger);

        // if the driver has any triggers pulled this means H drive only drive the H wheels
        // as straightly as possible
        if (gamepad1.left_trigger > DEADBAND_TRIGGER || gamepad1.right_trigger > DEADBAND_TRIGGER) {
            RBTChassis.subHDrive.cmdTeleop(CommonLogic.joyStickMath(gamepad1.left_trigger),
                    CommonLogic.joyStickMath(gamepad1.right_trigger));
            RBTChassis.cmdTeleOp(0,0);
        }
        // stop the H drive and give joystick values to the other wheels.
        else {
            RBTChassis.subHDrive.cmdStop();
            RBTChassis.cmdTeleOp(CommonLogic.joyStickMath(-gamepad1.left_stick_y),
                    CommonLogic.joyStickMath(-gamepad1.right_stick_y));
        }

       // RBTChassis.subExtender.cmd_stickControl(gamepad2.right_stick_y);

        if (gamepad2.a) {
            //RBTChassis.subExtender.cmd_MoveToStart();
        }
        if (gamepad2.b) {
            //RBTChassis.subExtender.cmd_MoveToPos1();
        }
        if (gamepad2.x) {
            //RBTChassis.subExtender.cmd_MoveToPos3();
        }
        if (gamepad2.y) {
            //RBTChassis.subExtender.cmd_MoveToPos2();
        }

        // Bumpers high and lower Powers for the wheels
        if (gamepad1.left_bumper) {
            //RBTChassis.setMaxPower(powerMax);
        }

        if (gamepad1.right_bumper) {
            //RBTChassis.setMaxPower(powerNormal);
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

