package org.firstinspires.ftc.teamcode;

/* LIFTER controls the extending slide on the robot
    - locking motors in brake mode for initial hanger
*/


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;


//@TeleOp(name = "LIFTER", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class Lifter extends BaseHardware {

    //Encoder positions for the LIFTER

    public static final int LIFTERPOS_TOL = 40;
    public static final double LIFTERPOWER_UP = .375;
    public static final double LIFTERPOWER_DOWN = -.375;
    public static final double LIFTERPOWER_INIT = -.125;
    public static final double LIFTERStickDeadBand = .2;
    private static final String TAGLIFTER = "8492-LIFTER";
    private Settings.PARENTMODE parentMode_Current = null;

    boolean cmdComplete = false;
    boolean underStickControl = false;

    private static int LOW_INDEX = 0;
    private static int HIGH_INDEX = 8;
    private static int [] LIFTER_POSITIONS_TICKS = new int [HIGH_INDEX];
    private int CurrentIndex = LOW_INDEX;
    private int CurrentTickCount = 0;

    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();

    // declare motors
    private DcMotor LFT1 = null;
    private DigitalChannel LIFTERTCH = null;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {

        // telemetry.addData("Status", "Initialized");

        /* eg: Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step (using the FTC Robot Controller app on the phone).
         */

        LFT1 = hardwareMap.dcMotor.get("EXT1");
        LFT1.setDirection(DcMotor.Direction.REVERSE);
        LFT1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        RobotLog.aa(TAGLIFTER, "LIFTERPos: " + LFT1);
        //do not know what digital channel is check here for errors ******
        LIFTERTCH = hardwareMap.get(DigitalChannel.class, "LIFTERTCH");
        LIFTERTCH.setMode(DigitalChannel.Mode.INPUT);


        LIFTER_POSITIONS_TICKS[0] = 0;
        LIFTER_POSITIONS_TICKS[1] = 100;
        LIFTER_POSITIONS_TICKS[2] = 200;
        LIFTER_POSITIONS_TICKS[3] = 300;
        LIFTER_POSITIONS_TICKS[4] = 400;
        LIFTER_POSITIONS_TICKS[5] = 500;
        LIFTER_POSITIONS_TICKS[6] = 600;
        LIFTER_POSITIONS_TICKS[7] = 700;

    }

    //*********************************************************************************************
    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {

    }

    //*********************************************************************************************
    public void setParentMode(Settings.PARENTMODE pm) {

        parentMode_Current = pm;
    }

    //*********************************************************************************************

    private void initLifterTCH() {
        ElapsedTime runtime = new ElapsedTime();
        runtime.reset();
        LFT1.setPower(LIFTERPOWER_INIT);
        while (LIFTERTCH.getState()) {
            if (runtime.milliseconds() > 1500) {
                break;
            }
        }
        LFT1.setPower(0);
    }

    //*********************************************************************************************
    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {

        // this is always called by chassis
        LFT1.setPower(0);

        switch (parentMode_Current) {
            case PARENT_MODE_AUTO:
                autoStart();
                break;
            case PARENT_MODE_TELE:
                teleStart();
                break;
            default:
                break;
        }
        CurrentTickCount = LFT1.getCurrentPosition();
         }

    //*********************************************************************************************

    public void autoStart() {
        // This is only called by chassis when running Auto OpModes

        initLifterTCH();
        LFT1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LFT1.setTargetPosition(LIFTER_POSITIONS_TICKS[0]);
        LFT1.setMode(DcMotor.RunMode.RUN_TO_POSITION);

    }

    //*********************************************************************************************

    public void teleStart() {

        // This is only called by chassis when running Tele Modes

    }

    //*********************************************************************************************

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        CurrentTickCount = LFT1.getCurrentPosition();
        SetPosition();
    }

    //*********************************************************************************************

    private void SetPosition () {

        if (! LFT1.isBusy()) {

        }

    }
    //*********************************************************************************************

    private boolean testInPosition(int currPos, int desiredPos) {

        boolean retValue = false;

        if (CommonLogic.inRange( currPos, desiredPos, LIFTERPOS_TOL) && LFT1.isBusy() == false){
            cmdComplete = retValue;
        }

        return (retValue);
    }

    //*********************************************************************************************
    public boolean getCommandComplete() {
        return cmdComplete;
    }

    //*********************************************************************************************

    public void incPositionIndex (){

       if (CurrentIndex < HIGH_INDEX) {
           CurrentIndex++;
       }

    }

    public void decPositionIndex () {
        if (CurrentIndex > LOW_INDEX) {
            CurrentIndex--;
        }
    }

    public void stop () {
        LFT1.setPower(0);
    }

}
