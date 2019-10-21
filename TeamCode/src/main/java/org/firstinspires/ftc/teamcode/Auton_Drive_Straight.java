package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name = "Auton_Gold_Depot_Crater", group = "Auton")
// @Autonomous(...) is the other common choice

public class Auton_Drive_Straight extends OpMode {


    private static enum stage {
        _unknown,
        _00_preStart,
        _10_crawlOut,
        _20_DriveStraight,
        _30_crawlBack,
        _40_DriveStraightBack,
        _50_Done
    }

    Chassis_Test RBTChassis = new Chassis_Test();

    private stage currentStage = stage._unknown;

    // declare auton power variables
    private double AUTO_DRIVEPower = .5;
    private double AUTO_DRIVEPower_HI = .75;
    private double AUTO_TURNPower = .4;
    private double AUTO_DRIVEpower_HDrive = 1.0;

    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Auton_Depot_Crater", "Initialized");
        RBTChassis.setParentMode(Settings.PARENTMODE.PARENT_MODE_AUTO);
        RBTChassis.hardwareMap = hardwareMap;
        RBTChassis.telemetry = telemetry;
        RBTChassis.init();
        msStuckDetectStart = 10000;

        // initialize chassis with hardware map
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        // initialize chassis
        RBTChassis.init_loop();

    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        // initialize chassis
        Runtime.getRuntime();
        RBTChassis.start();
        //RBTChassis.setMotorMode_RUN_WITHOUT_ENCODER();

    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        telemetry.addData("Auton_Depot_Cater", currentStage);
        RBTChassis.loop();

        // check stage and do what's appropriate
        if (currentStage == stage._unknown) {
            currentStage = stage._00_preStart;
        }


        if (currentStage == stage._10_crawlOut) {
            RBTChassis.subHDrive.cmdDrive(AUTO_DRIVEpower_HDrive, 15);
            currentStage = stage._20_DriveStraight;
        }

        if (currentStage == stage._20_DriveStraight) {
            if (RBTChassis.subHDrive.getcmdComplete()) {
                RBTChassis.cmdDrive(AUTO_DRIVEPower, 0, 24.0);
                currentStage = stage._30_crawlBack;
            }

        }

        if (currentStage == stage._30_crawlBack) {
            if (RBTChassis.subHDrive.getcmdComplete()) {
                RBTChassis.subHDrive.cmdDrive(-AUTO_DRIVEpower_HDrive, 15);
                currentStage = stage._40_DriveStraightBack;
            }
        }


        if (currentStage == stage._40_DriveStraightBack) {
            if (RBTChassis.subHDrive.getcmdComplete()) {
                RBTChassis.cmdDrive(-AUTO_DRIVEPower, 0,24);
                currentStage = stage._50_Done;
            }
        }

        if (currentStage == stage._50_Done){
            if (RBTChassis.getcmdComplete()) {
                RBTChassis.stop();
            }
        }

    }  //  loop

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        RBTChassis.stop();
    }

}