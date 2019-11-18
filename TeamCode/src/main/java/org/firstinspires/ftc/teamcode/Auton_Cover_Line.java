package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name = "Cover_Line", group = "Auton")
// @Autonomous(...) is the other common choice

public class Auton_Cover_Line extends OpMode {


    private static enum stage {
        _unknown,
        _00_preStart,
        _10_Drive_Out,
        _20_Finish
    }

    Chassis RBTChassis = new Chassis();

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
        telemetry.addData("Auton_Drive_Straight", "Initialized");
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
        runtime.reset();
        RBTChassis.start();
        //RBTChassis.setMotorMode_RUN_WITHOUT_ENCODER();

    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        telemetry.addData("Auton_Drive_Straight", currentStage);
        RBTChassis.loop();


        //can not do anything until hDrive is zeroed and ready
        if (RBTChassis.subHDrive.getCurrentMode() == HDrive.HDriveMode.Initializing) {
            return;
        }
        // check stage and do what's appropriate
        if (currentStage == stage._unknown) {
            currentStage = stage._00_preStart;
        }

        // delay until covering the line
        if (runtime.seconds() < 22){
            return;
        }

        if (currentStage == stage._00_preStart) {
            currentStage = stage._10_Drive_Out;
        }

        if (currentStage == stage._10_Drive_Out) {
            RBTChassis.cmdDrive(AUTO_DRIVEPower, 0, 12);
            currentStage = stage._20_Finish;
        }

        if (currentStage == stage._20_Finish){
            if (RBTChassis.getcmdComplete()){
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
