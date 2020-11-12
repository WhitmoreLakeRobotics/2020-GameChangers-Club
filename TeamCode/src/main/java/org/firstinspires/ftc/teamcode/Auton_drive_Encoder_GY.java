package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name = "Auton_drive_Encoder_GY", group = "Auton")
//@TeleOp(name="FirstOpMode_NAJ", group="Teleop")


public class Auton_drive_Encoder_GY extends OpMode {


    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor LDM1 = null;
    private DcMotor RDM1 = null;
    public static final int WHEEL_SIZE = 4;
    public static final double WHEEL_DIST_PER_REV = WHEEL_SIZE*3.14159;
    public static final int TICS_PER_REV=1120;
    public static final double TICS_PER_INCH=TICS_PER_REV/WHEEL_DIST_PER_REV;
    public double DistanceTarget= 24;
    public double TicTarget = DistanceTarget *TICS_PER_INCH;
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        //----------------------------------------------------------------------------------------------
        // These constants manage the duration we allow for callbacks to user code to run for before
        // such code is considered to be stuck (in an infinite loop, or wherever) and consequently
        // the robot controller application is restarted. They SHOULD NOT be modified except as absolutely
        // necessary as poorly chosen values might inadvertently compromise safety.
        //----------------------------------------------------------------------------------------------
        telemetry.addData("Ms. Jewell Jumpers", "Initialized");

        LDM1 = hardwareMap.dcMotor.get("LDM1");
        LDM1.setDirection(DcMotor.Direction.FORWARD);
        LDM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        LDM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LDM1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        RDM1 = hardwareMap.dcMotor.get("RDM1");
        RDM1.setDirection(DcMotor.Direction.REVERSE);
        RDM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        RDM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RDM1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

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
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        telemetry.addData("RunTime",runtime.milliseconds());
        telemetry.addData("Ms. Jewell Jumpers", "running");
        telemetry.addData("tictarget= " + TicTarget, "curr_tics " + LDM1.getCurrentPosition());

        if  (LDM1.getCurrentPosition()>TicTarget){
            RDM1.setPower(0);
            LDM1.setPower(0);
        }
        else {
            RDM1.setPower(.5);
            LDM1.setPower(.5);}
    }  //  loop

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {

    }
}
