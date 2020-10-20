package org.firstinspires.ftc.teamcode;
// This is a comment that this is Your first program.
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;



/**
 * An Op mode is a mode of operation either autonomous or teleop.
  * The op mode name should be unique. It will be the name displayed on the driver station. If
 * multiple op modes have the same name, only one will be available.
 */
@Autonomous(name = "FirstOpMode_mg", group = "Auton")
public class FirstOpMode_mg  extends OpMode {
    /* The Telemetry object that allows us to send and display data on the driver station     */
    public Telemetry telemetry = null;

    //private static final String Constant = "Some value";
    public enum ChassisMode {
        STOP,
        DRIVE,
        TURN,
        TELEOP,
        UNKNOWN
    }
    /**
     * Hardware Mappings go in this section
     */
    private DcMotor LDM1 = null;
    private DcMotor RDM1 = null;
    /**
     * User defined init method
     * This method will be called once when the INIT button is pressed.
     */
    public void init() {
        LDM1 = hardwareMap.dcMotor.get("LDM1");
        LDM1.setDirection(DcMotor.Direction.FORWARD);
        LDM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LDM1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        LDM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        RDM1 = hardwareMap.dcMotor.get("RDM1");
        RDM1.setDirection(DcMotor.Direction.REVERSE);
        RDM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RDM1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RDM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);




        if (LDM1 == null) {
            telemetry.log().add("LDM1 is null...");
        }
        if (RDM1 == null) {
            telemetry.log().add("RDM1 is null...");
        }
    }
    /**
     * User defined init_loop method
     * <p>
     * This method will be called repeatedly when the INIT button is pressed.
     * This method is optional. By default this method takes no action.
     */

    public void init_loop() {

    }
    /**
     * User defined start method.
     * <p>
     * This method will be called once when the PLAY button is first pressed.
     * This method is optional. By default this method takes not action.
     * Example usage: Starting another thread.
     */
    public void start(){
telemetry.addData("first note","Hello world");
    }

    /**
     * User defined loop method
     * <p>
     * This method will be called repeatedly in a loop while this op mode is running
     */
    public void loop() {
    //Drive for 2 secs at 0.25 or 25%
        DriveForwardTime(2,0.25);
    }

    /**
     * User defined stop method
     * <p>
     * This method will be called when this op mode is first disabled
     * <p>
     * The stop method is optional. By default this method takes no action.
     */
    public void stop() {

    }

    public void DriveForwardTime(int Secs, double Powerlevel){
        //drive forward for time specified as parameter
        setMotorMode_RUN_WITHOUT_ENCODER();

            LDM1.setPower(Powerlevel);
            RDM1.setPower(Powerlevel);


    }

    private void setMotorMode(DcMotor.RunMode newMode) {

        LDM1.setMode(newMode);
        RDM1.setMode(newMode);

    }
    //*********************************************************************************************
    public void setMotorMode_RUN_WITHOUT_ENCODER() {
        setMotorMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
    //*********************************************************************************************
    public void setMotorMode_RUN_USING_ENCODER() {
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }


}
