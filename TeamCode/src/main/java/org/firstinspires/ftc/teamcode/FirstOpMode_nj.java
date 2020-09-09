package org.firstinspires.ftc.teamcode;
// This is a comment that this is Your first program.
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;



/**
 * An Op mode is a mode of operation either autonomous or teleop.
  * The op mode name should be unique. It will be the name displayed on the driver station. If
 * multiple op modes have the same name, only one will be available.
 */
@Autonomous(name = "FirstOpMode_nj", group = "Auton")
public class FirstOpMode_nj  extends OpMode {
    /* The Telemetry object that allows us to send and display data on the driver station     */
    public Telemetry telemetry = null;


    /**
     * Hardware Mappings go in this section
     */

    /**
     * User defined init method
     * This method will be called once when the INIT button is pressed.
     */
    public void init() {

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

    }

    /**
     * User defined loop method
     * <p>
     * This method will be called repeatedly in a loop while this op mode is running
     */
    public void loop() {

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
}
