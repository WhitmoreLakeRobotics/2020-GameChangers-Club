package org.firstinspires.ftc.teamcode;

/* LIFTER controls the extending slide on the robot

 */


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;


//@TeleOp(name = "LIFTER", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class Lifter extends BaseHardware {
    private static final String TAGLIFTER = "8492-LIFTER";

    //Encoder positions for the LIFTER
    public static final int LIFTER_STEP = 10;
    public static final int LIFTERPOS_TOL = 15;
    public static final double LIFTERPOWER_UP = 1.0;
    public static final double LIFTERPOWER_DOWN = 1.0;
    public static final double LIFTERPOWER_INIT = -.125;
    public static final double LIFTERStickDeadBand = .2;

    private Settings.CHASSIS_TYPE chassisType_Current = Settings.CHASSIS_TYPE.CHASSIS_COMPETITION;

    private static int LOW_INDEX = 0;
    private static int HIGH_INDEX = 7;
    private static int[] LIFTER_POSITIONS_TICKS = new int[HIGH_INDEX + 1];
    private int CurrentIndex = LOW_INDEX;
    private int CurrentTickCount = 0;

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

        LFT1 = hardwareMap.dcMotor.get("LFT1");
        LFT1.setDirection(DcMotor.Direction.REVERSE);
        LFT1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        RobotLog.aa(TAGLIFTER, "LIFTERPos: " + LFT1);
        //do not know what digital channel is check here for errors ******
        LIFTERTCH = hardwareMap.get(DigitalChannel.class, "lifterTCH");
        LIFTERTCH.setMode(DigitalChannel.Mode.INPUT);
        int brickheight = 350;
        int foundationheight = 226;
        LIFTER_POSITIONS_TICKS[0] = 0;  //start
        LIFTER_POSITIONS_TICKS[1] = 50;  //carry
        LIFTER_POSITIONS_TICKS[2] = foundationheight; //level 1
        LIFTER_POSITIONS_TICKS[3] = foundationheight + (1 * brickheight);  //level 2
        LIFTER_POSITIONS_TICKS[4] = foundationheight + (2 * brickheight); //level 3
        LIFTER_POSITIONS_TICKS[5] = foundationheight + (3 * brickheight);
        ;  //level 4
        LIFTER_POSITIONS_TICKS[6] = foundationheight + (3 * brickheight);  //level 5
        LIFTER_POSITIONS_TICKS[7] = foundationheight + (3 * brickheight);  //level 6

        CurrentTickCount = LFT1.getCurrentPosition();
        LFT1.setTargetPosition(CurrentTickCount);
        LFT1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    //*********************************************************************************************
    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {

    }

    //*********************************************************************************************
    public void setChassisType(Settings.CHASSIS_TYPE ct) {

        chassisType_Current = ct;
    }
    //*********************************************************************************************

    private void initLifterTCH() {
        // if their is a limit switch for zero on the lifter then zero it.

        if (chassisType_Current == Settings.CHASSIS_TYPE.CHASSIS_COMPETITION) {
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
        // else do nothing

    }

    //*********************************************************************************************
    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {

        // this is always called by chassis
        LFT1.setPower(0);

    }

    //*********************************************************************************************

    public void autoStart() {
        // This is only called by chassis when running Auto OpModes
        //initLifterTCH();
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
        telemetry.addData("Lifter-Index", CurrentIndex);
        telemetry.addData("LifterPos-Ticks", CurrentTickCount);
        //telemetry.update();
    }

    //*********************************************************************************************
    // compare tick counts and see if we are in range for that position.
    private boolean testInPosition(int currPos, int desiredPos) {

        return CommonLogic.inRange(currPos, desiredPos, LIFTERPOS_TOL);
    }

    //*********************************************************************************************
    // based on a position index... Are we at that position
    public boolean isInPosition(int index) {
        boolean retValue = false;

        // given an index then see if we are in that position.
        if (CommonLogic.indexCheck(index, LOW_INDEX, HIGH_INDEX)) {
            CurrentTickCount = LFT1.getCurrentPosition();
            retValue = testInPosition(CurrentTickCount, LIFTER_POSITIONS_TICKS[index]);
        }
        return retValue;
    }

    //*********************************************************************************************
    // given an index set the motor to move to that position.
    public void setPosition(int index) {

        // verify that we are not stepping outside of the array
        if (CommonLogic.indexCheck(index, LOW_INDEX, HIGH_INDEX)) {
            // Make sure that we are not already at the requested position
            //if (!isInPosition(index)) {
            // If needed to go Down go slower than Up
            if (CurrentTickCount > LIFTER_POSITIONS_TICKS[index]) {
                LFT1.setPower(LIFTERPOWER_DOWN);
            }
            // If needed to go Up go faster than Down
            else {
                LFT1.setPower(LIFTERPOWER_UP);
            }
            //Set the motor to hold the new position
            LFT1.setTargetPosition(LIFTER_POSITIONS_TICKS[index]);
            //}
        }
    }

    //*********************************************************************************************
    public void incPositionIndex() {
        // only inc the position if we are in the current one

        //CurrentTickCount = LFT1.getCurrentPosition();
        CurrentIndex = findNextIndexUP(CurrentTickCount);
        if (CommonLogic.indexCheck(CurrentIndex, LOW_INDEX, HIGH_INDEX - 1)) {
            telemetry.addData("incPositionIndex", CurrentIndex);
            setPosition(CurrentIndex);
        }
        else {
            int i = CurrentIndex /0;
        }
    }

    //*********************************************************************************************
    public void decPositionIndex() {
        // only dec the position if we are in the current one

        //CurrentTickCount = LFT1.getCurrentPosition();
        CurrentIndex = findNextIndexDown(CurrentTickCount);
        if (CommonLogic.indexCheck(CurrentIndex, LOW_INDEX + 1, HIGH_INDEX)) {
            telemetry.addData("decPositionIndex", CurrentIndex);
            setPosition(CurrentIndex);
        }
        else {
            int i = CurrentIndex /0;
        }
    }

    //*********************************************************************************************

    public void stickControl(double throttle) {
        if (throttle < 0) {
            if ((CurrentTickCount - LIFTER_STEP) > LIFTER_POSITIONS_TICKS[LOW_INDEX]) {
                CurrentIndex = findNextIndexDown(CurrentTickCount);
                LFT1.setTargetPosition(CurrentTickCount - 5);
            }
        } else if (throttle > 0) {
            if ((CurrentTickCount + LIFTER_STEP) < LIFTER_POSITIONS_TICKS[HIGH_INDEX]) {
                CurrentIndex = findNextIndexUP(CurrentTickCount);
                LFT1.setTargetPosition(CurrentTickCount + 5);
            }
        }
    }

    //*********************************************************************************************

    private int findNextIndexUP(int ticks) {
        int retValue = LOW_INDEX;

        for (int i = LOW_INDEX; i < HIGH_INDEX; i++) {
            retValue = i;
            if (ticks < LIFTER_POSITIONS_TICKS[i] + LIFTERPOS_TOL) {
                break;
            }
        }
        return retValue;
    }

    //*********************************************************************************************
    private int findNextIndexDown(int ticks) {

        int retValue = HIGH_INDEX;
        for (int i = HIGH_INDEX; i > LOW_INDEX; i--) {
            retValue = i;
            if (ticks > (LIFTER_POSITIONS_TICKS[i] - LIFTERPOS_TOL)) {
                break;
            }

        }
        return retValue;
    }

    //*********************************************************************************************
    public void stop() {
        LFT1.setPower(0);
        LFT1.setTargetPosition(CurrentTickCount);
    }
    //*********************************************************************************************
}
