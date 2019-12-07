package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name = "Red_Stone_Foundation2", group = "Auton")
public class Auton_Red_Stone_Move_Foundation2 extends OpMode {


    Chassis RBTChassis = new Chassis();
    private stage currentStage = stage._unknown;
    // declare auton power variables
    private double AUTO_DRIVEPower = .6;
    private double AUTO_DRIVEPower_HI = .90;
    private double AUTO_TURNPower = .4;
    private double AUTO_DRIVEpower_HDrive = -1.0;
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
        telemetry.addData("Red_Stone_Foundation2", "Initialized");
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
            RBTChassis.cmdDrive((AUTO_DRIVEPower * .7), 0, 20);
            RBTChassis.subLifter.setPosition(Lifter.PRE_PICK_POS);
            RBTChassis.subExtender.setPosition(ExtenderMove2Pos.PICK);
            currentStage = stage._10_Drive_Out;
        }

        if (currentStage == stage._10_Drive_Out) {
            if (RBTChassis.getcmdComplete()) {
                RBTChassis.subLeg.pick();
                currentStage = stage._20_Pick_Stone;
            }
        }

        if (currentStage == stage._20_Pick_Stone) {
            if (RBTChassis.subLeg.getcmdComplete()) {
                RBTChassis.cmdDrive(-AUTO_DRIVEPower, 0, 5);
                currentStage = stage._30_Drive_Back;
            }
        }

        if (currentStage == stage._30_Drive_Back) {
            if (RBTChassis.getcmdComplete()) {
                RBTChassis.subHDrive.cmdDrive(AUTO_DRIVEpower_HDrive, 82);
                currentStage = stage._40_Drive_H_UnderBridge;
            }
        }

        if (currentStage == stage._40_Drive_H_UnderBridge) {
            if (RBTChassis.subHDrive.getcmdComplete()) {
                //Also start the lift to the first stone height to clear foundation
                RBTChassis.subLifter.setPosition(Lifter.PRE_PICK_POS);
                currentStage = stage._45_Lifter_up;
            }
        }

        if (currentStage == stage._45_Lifter_up) {
            if (RBTChassis.subLifter.isInPosition(Lifter.PRE_PICK_POS)) {
                currentStage = stage._50_Drive_Forward;
                RBTChassis.cmdDrive(AUTO_DRIVEPower, 0, 9);
                RBTChassis.subExtender.setPosition(ExtenderMove2Pos.PLACE_3);
            }
        }

        if (currentStage == stage._50_Drive_Forward) {
            if (RBTChassis.getcmdComplete() &&
                    RBTChassis.subExtender.isInPosition(ExtenderMove2Pos.PLACE_3)) {
                RBTChassis.subLeg.place();
                RBTChassis.subPushers.cmdMoveAllDown();
                currentStage = stage._70_Place_Stone;
            }
        }

        if (currentStage == stage._70_Place_Stone) {
            if (RBTChassis.subPushers.getIsDown() && RBTChassis.subLeg.getcmdComplete()) {
                RBTChassis.cmdDrive(-AUTO_DRIVEPower, 0, 24);
                currentStage = stage._90_Pull_Back;
            }
        }


        if (currentStage == stage._90_Pull_Back) {
            if (RBTChassis.getcmdComplete()) {
                RBTChassis.subPushers.cmdMoveAllUp();
                currentStage = stage._95_Pushers_Up;
            }
        }

        if (currentStage == stage._95_Pushers_Up) {
            if (RBTChassis.subPushers.getIsUp()) {
                RBTChassis.subHDrive.cmdDrive(-AUTO_DRIVEpower_HDrive, 27);
                currentStage = stage._100_Shuttle_Out;
            }
        }


        if (currentStage == stage._100_Shuttle_Out) {
            if (RBTChassis.subHDrive.getcmdComplete()) {
                RBTChassis.cmdDrive(AUTO_DRIVEPower, 0, 13);
                currentStage = stage._130_Get_In_Lane;
            }
        }
        /*
        if (currentStage == stage._110_Drive_Forward) {
            if (RBTChassis.getcmdComplete()) {
                currentStage = stage._120_Slam_Foundation;
                RBTChassis.subHDrive.cmdDrive(AUTO_DRIVEpower_HDrive, 3);
            }
        }

        if (currentStage == stage._120_Slam_Foundation) {
            if (RBTChassis.subHDrive.getcmdComplete()) {
                RBTChassis.subHDrive.cmdDrive(-AUTO_DRIVEpower_HDrive,2);
                currentStage = stage._125_Clear_Foundation;
            }
        }

        if (currentStage == stage._125_Clear_Foundation) {
            if (RBTChassis.getcmdComplete()) {
                RBTChassis.cmdDrive(AUTO_DRIVEPower, 0, 7);
                currentStage = stage._130_Get_In_Lane;
            }
        }
*/
        if (currentStage == stage._130_Get_In_Lane) {
            if (RBTChassis.getcmdComplete()) {
                RBTChassis.subHDrive.cmdDrive(-AUTO_DRIVEpower_HDrive, 24);
                currentStage = stage._140_Park_On_Line;
            }
        }

        if (currentStage == stage._140_Park_On_Line) {
            if (RBTChassis.subHDrive.getcmdComplete()) {
                RBTChassis.cmdDrive(AUTO_DRIVEPower * .5, 0, 0.5);
                currentStage = stage._150_Finish;
            }
        }

        if (currentStage == stage._150_Finish) {
            if (RBTChassis.getcmdComplete()) {
                stop();
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
        _10_Drive_Out,
        _20_Pick_Stone,
        _30_Drive_Back,
        _40_Drive_H_UnderBridge,
        _45_Lifter_up,
        _50_Drive_Forward,
        _70_Place_Stone,
        _80_Pushers_Down,
        _90_Pull_Back,
        _95_Pushers_Up,
        _100_Shuttle_Out,
        _110_Drive_Forward,
        _120_Slam_Foundation,
        _125_Clear_Foundation,
        _130_Get_In_Lane,
        _140_Park_On_Line,
        _145_Hug_Bridge,
        _150_Finish
    }
}
