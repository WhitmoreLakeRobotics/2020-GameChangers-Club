package org.firstinspires.ftc.teamcode;

/* Extener controls the extending slide on the robot

 */


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.RobotLog;


//@TeleOp(name = "LIFTER", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class ExtenderMove2Pos extends BaseHardware {
    private static final String TAGLIFTER = "8492-Extener2Pos";

    //Encoder positions for the Extender
    public static final int EXTENDER_STEP = 25;
    public static final int EXTENDER_POS_TOL = 15;
    public static final double LIFTERPOWER_UP = 1.0;
    public static final double LIFTERPOWER_DOWN = 1.0;
    public static final double LIFTERPOWER_INIT = -.125;
    public static final double LIFTERStickDeadBand = .2;

    private Settings.CHASSIS_TYPE chassisType_Current = Settings.CHASSIS_TYPE.CHASSIS_COMPETITION;


    public static int HOME = 0;
    public static int PICK =1;
    public static int PLACE_1=2;
    public static int PLACE_2=3;
    public static int PLACE_3=4;
    public static int OUT=5;


    private static int LOW_INDEX = 0;
    private static int HIGH_INDEX = 5;
    private static int[] EXTENDER_POSITIONS_TICKS = new int[HIGH_INDEX + 1];
    private int CurrentIndex = LOW_INDEX;
    private int CurrentTickCount = 0;

    // declare motors
    private DcMotor EXT1 = null;
    private DigitalChannel extenderTCH = null;
    public boolean underLEGControl = false;

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

        EXT1 = hardwareMap.dcMotor.get("EXT1");
        EXT1.setDirection(DcMotor.Direction.REVERSE);
        EXT1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        RobotLog.aa(TAGLIFTER, "extenderPos: " + EXT1);
        //do not know what digital channel is check here for errors ******
        extenderTCH = hardwareMap.get(DigitalChannel.class, "extenderTCH");
        extenderTCH.setMode(DigitalChannel.Mode.INPUT);

        EXTENDER_POSITIONS_TICKS[HOME] = 0;  //start
        EXTENDER_POSITIONS_TICKS[PICK] = 185;
        EXTENDER_POSITIONS_TICKS[PLACE_1] = 240;
        EXTENDER_POSITIONS_TICKS[PLACE_2] = 425;
        EXTENDER_POSITIONS_TICKS[PLACE_3] = 620;
        EXTENDER_POSITIONS_TICKS[OUT] = 625;

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
    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        // this is always called by chassis
        EXT1.setPower(LIFTERPOWER_UP);


        //If we are on the test chassis autostart
        if (chassisType_Current == Settings.CHASSIS_TYPE.CHASSIS_TEST) {
            resetEncoders();
        }
        else if (! extenderTCH.getState()){
            // we are on the switch go ahead and reset them
            resetEncoders();
        }
        else {
            // We are not on the switch go with current values in the encoders
            CurrentTickCount = EXT1.getCurrentPosition();
            EXT1.setTargetPosition(CurrentTickCount);
            EXT1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
    }

    //*********************************************************************************************

    public void resetEncoders() {
        EXT1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        EXT1.setTargetPosition(EXTENDER_POSITIONS_TICKS[0]);
        EXT1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
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

        CurrentTickCount = EXT1.getCurrentPosition();
        telemetry.addData("Extender-Index", CurrentIndex);
        telemetry.addData("ExtenderPos-Ticks", CurrentTickCount);
    }

    //*********************************************************************************************
    // compare tick counts and see if we are in range for that position.
    private boolean testInPosition(int currPos, int desiredPos) {

        return CommonLogic.inRange(currPos, desiredPos, EXTENDER_POS_TOL);
    }

    //*********************************************************************************************
    // based on a position index... Are we at that position
    public boolean isInPosition(int index) {
        boolean retValue = false;

        // given an index then see if we are in that position.
        if (CommonLogic.indexCheck(index, LOW_INDEX, HIGH_INDEX)) {
            CurrentTickCount = EXT1.getCurrentPosition();
            retValue = testInPosition(CurrentTickCount, EXTENDER_POSITIONS_TICKS[index]);
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
            if (CurrentTickCount > EXTENDER_POSITIONS_TICKS[index]) {
                EXT1.setPower(LIFTERPOWER_DOWN);
            }
            // If needed to go Up go faster than Down
            else {
                EXT1.setPower(LIFTERPOWER_UP);
            }
            //Set the motor to hold the new position
            EXT1.setTargetPosition(EXTENDER_POSITIONS_TICKS[index]);
        }
        else {
            telemetry.addData("Extender Index Out of Range",index);
        }
    }

    //*********************************************************************************************

    public void incPositionIndex() {
        //The user might have been using stick control reset the index
        CurrentIndex = findNextIndexUP(CurrentTickCount);
        //Make sure that we still have a valid index
        if (CommonLogic.indexCheck(CurrentIndex, LOW_INDEX, HIGH_INDEX - 1)) {
            //If we are in range then dec the index... Else we will move to the current index position
            //if (CommonLogic.inRange(CurrentIndex, EXTENDER_POSITIONS_TICKS[CurrentIndex], EXTENDER_POS_TOL)) {
                CurrentIndex++;
            //}
        }
        setPosition(CurrentIndex);
    }

    //*********************************************************************************************

    public void decPositionIndex() {
        //The user might have been using stick control reset the index
        CurrentIndex = findNextIndexDown(CurrentTickCount);
        //Make sure that we still have a valid index
        if (CommonLogic.indexCheck(CurrentIndex, LOW_INDEX + 1, HIGH_INDEX)) {
            //If we are in range then dec the index... Else we will move to the current index position
            //if (CommonLogic.inRange(CurrentIndex, EXTENDER_POSITIONS_TICKS[CurrentIndex], EXTENDER_POS_TOL)) {
                CurrentIndex--;
            //}
        }
        setPosition(CurrentIndex);
    }

    //*********************************************************************************************

    public void stickControl(double throttle) {


        if (throttle < 0) {
            if ((CurrentTickCount - EXTENDER_STEP) > EXTENDER_POSITIONS_TICKS[LOW_INDEX]) {
                // update the index so that it displays correctly
                CurrentIndex = findNextIndexDown(CurrentTickCount);
                EXT1.setTargetPosition(CurrentTickCount - EXTENDER_STEP);
            }
        } else if (throttle > 0) {
            if ((CurrentTickCount + EXTENDER_STEP) < EXTENDER_POSITIONS_TICKS[HIGH_INDEX]) {
                // update the index so that it displays correctly
                CurrentIndex = findNextIndexUP(CurrentTickCount);
                EXT1.setTargetPosition(CurrentTickCount + EXTENDER_STEP);
            }
        }

    }

    //*********************************************************************************************
    // With the advent of stick control this means the Extender can be anywhere long its entire range
    // of positions.   This function searches the known positions and sets the index value to the
    // closest one going Up.
    private int findNextIndexUP(int ticks) {

        int retValue = LOW_INDEX;
        for (int i = LOW_INDEX; i <= HIGH_INDEX; i++) {
            retValue = i;
            if (CommonLogic.inRange(ticks, EXTENDER_POSITIONS_TICKS[i], EXTENDER_POS_TOL)) {
                break;
            } else if (ticks <= EXTENDER_POSITIONS_TICKS[i] - 1) {
                break;
            }
        }
        return retValue;
    }

    //*********************************************************************************************
    // With the advent of stick control this means the Extender can be anywhere long its entire range
    // of positions.   This function searches the known positions and sets the index value to the
    // closest one going Up.
    private int findNextIndexDown(int ticks) {

        int retValue = HIGH_INDEX;
        for (int i = HIGH_INDEX; i >= LOW_INDEX; i--) {
            retValue = i;
            if (CommonLogic.inRange(ticks, EXTENDER_POSITIONS_TICKS[i], EXTENDER_POS_TOL)) {
                break;
            } else if (ticks >= (EXTENDER_POSITIONS_TICKS[i])) {
                break;
            }
        }
        return retValue;
    }

    //*********************************************************************************************

    public int getIndexTics(int index) {

        if (CommonLogic.indexCheck(index, LOW_INDEX, HIGH_INDEX)) {
            return EXTENDER_POSITIONS_TICKS[index];
        } else if (index < LOW_INDEX) {
            return EXTENDER_POSITIONS_TICKS[LOW_INDEX];
        } else {
            return EXTENDER_POSITIONS_TICKS[HIGH_INDEX];
        }

    }
    //*********************************************************************************************

    public int getPosTics() {
        return CurrentTickCount;
    }

    //*********************************************************************************************

    public void stop() {
        EXT1.setPower(0);
        EXT1.setTargetPosition(CurrentTickCount);
    }
    //*********************************************************************************************
}
