package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name = "Teleop", group = "Teleop")
// @TeleOp(...) is the other common choice

public class Teleop extends OpMode {


    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    private double LeftMotorPower = 0;
    private double RightMotorPower = 0;
    private DcMotor LDM1 = null;
    private DcMotor RDM1 = null;
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        LDM1 = hardwareMap.dcMotor.get("LDM1");
        LDM1.setDirection(DcMotor.Direction.FORWARD);
        LDM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        RDM1 = hardwareMap.dcMotor.get("RDM1");
        RDM1.setDirection(DcMotor.Direction.REVERSE);
        RDM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {

    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {


    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        // Setup a variable for each drive wheel to save power level for telemetry
        double leftPower =joyStickMath(-gamepad1.left_stick_y);
        double rightPower = joyStickMath(-gamepad1.right_stick_y);

        RDM1.setPower(rightPower);
        LDM1.setPower(leftPower);




        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftPower, rightPower);
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
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
