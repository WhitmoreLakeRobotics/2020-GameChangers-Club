package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name = "Blue_Rotate_Foundation", group = "Auton")
// @Autonomous(...) is the other common choice
@Disabled
public class Auton_Blue_Rotate_Foundation extends OpMode {


    Chassis RBTChassis = new Chassis();
    private stage currentStage = stage._unknown;
    // declare auton power variables
    private double AUTO_DRIVEPower = .5;
    private double AUTO_DRIVEPower_HI = .90;
    private double AUTO_TURNPower = .4;
    private double AUTO_DRIVEpower_HDrive = 1.0;
    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();

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
        msStuckDetectInit = Settings.msStuckDetectInit;
        msStuckDetectInitLoop = Settings.msStuckDetectInitLoop;
        msStuckDetectStart = Settings.msStuckDetectStart;
        msStuckDetectLoop = Settings.msStuckDetectLoop;
        msStuckDetectStop = Settings.msStuckDetectStop;


        RBTChassis.hardwareMap = hardwareMap;
        RBTChassis.telemetry = telemetry;
        RBTChassis.init();
        telemetry.addData("Blue_Rotate_Foundation", "Initialized");
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

    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        telemetry.addData("Blue_Rotate_Foundation", currentStage);
        RBTChassis.loop();

        //can not do anything until hDrive is zeroed and ready
        if (RBTChassis.subHDrive.getCurrentMode() == HDrive.HDriveMode.Initializing) {
            return;
        }

        // check stage and do what's appropriate
        if (currentStage == stage._unknown) {
            currentStage = stage._00_preStart;
        }

        if (currentStage == stage._00_preStart) {
            currentStage = stage._10_Drive_Out;
        }

        // Estimated Time = 1 sec
        if (currentStage == stage._10_Drive_Out) {
            RBTChassis.cmdDrive(AUTO_DRIVEPower, 0, 23);
            RBTChassis.subHDrive.cmdDrive(AUTO_DRIVEpower_HDrive, 15);
            currentStage = stage._20_Pushers_Down;
        }

        // Estimated Time = 1 sec
        if (currentStage == stage._20_Pushers_Down) {
            if (RBTChassis.getcmdComplete() && RBTChassis.subHDrive.getcmdComplete()) {
                RBTChassis.subPushers.cmdMoveAllDown();
                currentStage = stage._25_Turn_With_Foundation;
            }
        }

        if (currentStage == stage._25_Turn_With_Foundation) {
            if (RBTChassis.subPushers.getIsDown()) {
                RBTChassis.cmdTurn(-1.0, -.25, -90);
                currentStage = stage._35_Stuff_it;
            }
        }

        // Estimated Time = 1 sec
        if (currentStage == stage._30_Drive_Back) {
            if (RBTChassis.getcmdComplete()) {
                RBTChassis.cmdDrive(-AUTO_DRIVEPower_HI, -90, 6);
                RBTChassis.subHDrive.cmdDrive(AUTO_DRIVEpower_HDrive, 22);
                currentStage = stage._35_Stuff_it;
            }
        }

        // Estimated Time = 1 sec
        if (currentStage == stage._35_Stuff_it) {
            if (RBTChassis.getcmdComplete() && RBTChassis.subHDrive.getcmdComplete()) {
                RBTChassis.subHDrive.cmdDrive(AUTO_DRIVEpower_HDrive, 22);
                RBTChassis.cmdDrive(AUTO_DRIVEPower, -90, 12);
                currentStage = stage._40_Pushers_Up;
            }
        }

        // Estimated Time = 1 sec
        if (currentStage == stage._40_Pushers_Up) {
            if (RBTChassis.getcmdComplete() && RBTChassis.subHDrive.getcmdComplete()) {
                RBTChassis.subPushers.cmdMoveAllUp();
                currentStage = stage._50_Drive_2_Line;
            }
        }

        // Estimated Time = 1 sec
        if (currentStage == stage._50_Drive_2_Line) {
            if (RBTChassis.subPushers.getIsUp()) {
                RBTChassis.cmdDrive(-AUTO_DRIVEPower_HI, -90, 39);
                RBTChassis.subHDrive.cmdDrive(-AUTO_DRIVEpower_HDrive, 1);
                currentStage = stage._55_Hug_wall;
            }
        }

        // Estimated Time = 1 sec
        if (currentStage == stage._55_Hug_wall) {
            if (RBTChassis.getcmdComplete() && RBTChassis.subHDrive.getcmdComplete()) {
                RBTChassis.subHDrive.cmdDrive(AUTO_DRIVEpower_HDrive, 2);
                currentStage = stage._60_Finish;
            }
        }

        if (currentStage == stage._60_Finish) {
            if (RBTChassis.subHDrive.getcmdComplete()) {
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

    private enum stage {
        _unknown,
        _00_preStart,
        _05_Shuttle_2_End,
        _10_Drive_Out,
        _20_Pushers_Down,
        _25_Turn_With_Foundation,
        _30_Drive_Back,
        _35_Stuff_it,
        _40_Pushers_Up,
        _50_Drive_2_Line,
        _55_Hug_wall,
        _60_Finish
    }

}
