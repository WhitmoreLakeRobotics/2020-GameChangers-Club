package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name = "firstopmode_IND", group = "Auton")
// @Autonomous(...) is the other common choice

public class firstopmode_IND extends OpMode {


    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor LDM1 = null;
    private DcMotor RDM1 = null;

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
        LDM1 = hardwareMap.dcMotor.get("LDM1");
        LDM1.setDirection(DcMotor.Direction.FORWARD);
        LDM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        RDM1 = hardwareMap.dcMotor.get("RDM1");
        RDM1.setDirection(DcMotor.Direction.REVERSE);
        RDM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addData("is_a_tacobella", "Initialized");
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
        Runtime.getRuntime();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        telemetry.addData("is_a_tacobella", "RuNNinG");
        telemetry.addData("Runtime", runtime.milliseconds());
        if (runtime.milliseconds() >=3000) {
            RDM1.setPower(0);
            LDM1.setPower(0);
        }
        else{
            RDM1.setPower(1);
            LDM1.setPower(1);
        }
    }  //  loop

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {

    }
}
