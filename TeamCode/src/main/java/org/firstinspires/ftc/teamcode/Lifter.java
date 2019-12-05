package org.firstinspires.ftc.teamcode;

/* LIFTER controls the extending slide on the robot

 */


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorControllerEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.RobotLog;


//@TeleOp(name = "LIFTER", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class Lifter extends BaseHardware {
    //Encoder positions for the LIFTER
    public static final int LIFTER_STEP = 250;
    public static final int LIFTERPOS_TOL = 60;
    public static final double LIFTERPOWER_UP = 1.0;
    public static final double LIFTERPOWER_DOWN = 1.0;
    //public static final double LIFTERPOWER_INIT = -.125;
    public static final double LIFTERStickDeadBand = .2;
    public static final int CLEAR_NUB_TICS = 525;
    private static final String TAGLIFTER = "8492-LIFTER";
    //Named index positions
    public static int PICK_POS = 0;
    public static int CARRY_POS = 1;
    public static int PRE_PICK_POS = 2;
    public static int CLEAR_FOUNDATION_POS = 2;


    private static int LOW_INDEX = 0;
    private static int HIGH_INDEX = 7;
    private static int[] LIFTER_POSITIONS_TICKS = new int[HIGH_INDEX + 1];
    public boolean underLEGControl = false;
    DcMotorControllerEx lftControl = null;
    PIDFCoefficients pidUp = null;
    PIDFCoefficients pidDown = null;
    int motorIndex = 0;
    private Settings.CHASSIS_TYPE chassisType_Current = Settings.CHASSIS_TYPE.CHASSIS_COMPETITION;
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
        //int brickheight = 350;
        //int foundationheight = 240;
        LIFTER_POSITIONS_TICKS[PICK_POS] = -50;  //start pick
        LIFTER_POSITIONS_TICKS[CARRY_POS] = 248; //70;  //carry
        // Pre-Pick pos is the same as CLEAR_FOUNDATION_POS
        LIFTER_POSITIONS_TICKS[CLEAR_FOUNDATION_POS] = 1350;   //level 1
        LIFTER_POSITIONS_TICKS[3] = 2775; //611;  //level 2
        LIFTER_POSITIONS_TICKS[4] = 3995; //900;  //level 3
        LIFTER_POSITIONS_TICKS[5] = 5120; //1175;  //level 4
        LIFTER_POSITIONS_TICKS[6] = 6290; //1475;  //level 5
        LIFTER_POSITIONS_TICKS[7] = 7075; //1695;  //level 6

        CurrentTickCount = LFT1.getCurrentPosition();
        LFT1.setTargetPosition(CurrentTickCount);
        LFT1.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        lftControl = (DcMotorControllerEx) LFT1.getController();

        // get the port number of our configured motor.
        motorIndex = LFT1.getPortNumber();

        // get the PID coefficients for the RUN_USING_ENCODER  modes.
        PIDFCoefficients pidOrig = lftControl.getPIDFCoefficients(motorIndex, DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("LFT1 P", pidOrig.p);
        telemetry.addData("LFT1 I", pidOrig.i);
        telemetry.addData("LFT1 D", pidOrig.d);
        telemetry.addData("LFT1 F", pidOrig.f);

        // Set new values here as needed from Pid_Tuner
        double NEW_P = pidOrig.p;
        double NEW_I = pidOrig.i;
        double NEW_D = pidOrig.d;
        double NEW_F = pidOrig.f;


        // Stock PIDF values from the first time we looked at the pids
        // p = 4.9600067138
        // i = 0.4960002197
        // d = 0.0
        // F = 49.600006103

        // change coefficients.
        //pidUp = new PIDFCoefficients(20.0, 2.1, .1, 65);
        //pidDown = new PIDFCoefficients(20.0, 2.1, .1, 65);

        pidUp = new PIDFCoefficients(NEW_P, NEW_I, NEW_D, NEW_F);
        pidDown = new PIDFCoefficients(NEW_P, NEW_I, NEW_D, NEW_F);

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

        //PIDFCoefficients pidOrig = lftControl.getPIDFCoefficients(motorIndex, DcMotor.RunMode.RUN_USING_ENCODER);

        //telemetry.addData("LFT1 P", pidOrig.p);
        //telemetry.addData("LFT1 I", pidOrig.i);
        //telemetry.addData("LFT1 D", pidOrig.d);
        //telemetry.addData("LFT1 F", pidOrig.f);
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
                lftControl.setPIDFCoefficients(motorIndex, DcMotor.RunMode.RUN_USING_ENCODER, pidDown);
                LFT1.setPower(LIFTERPOWER_DOWN);

            }
            // If needed to go Up go faster than Down
            else {
                LFT1.setPower(LIFTERPOWER_UP);
                lftControl.setPIDFCoefficients(motorIndex, DcMotor.RunMode.RUN_USING_ENCODER, pidUp);
            }
            //Set the motor to hold the new position
            LFT1.setTargetPosition(LIFTER_POSITIONS_TICKS[index]);
        } else {
            telemetry.addData("Lifter Index Out of Range", index);
        }
    }

    //*********************************************************************************************

    public void incPositionIndex() {
        if (underLEGControl) {
            return;
        }
        //The user might have been using stick control reset the index
        CurrentIndex = findNextIndexUP(CurrentTickCount);
        //Make sure that we still have a valid index
        if (CommonLogic.indexCheck(CurrentIndex, LOW_INDEX, HIGH_INDEX - 1)) {
            //If we are in range then dec the index... Else we will move to the current index position
            //if (CommonLogic.inRange(CurrentIndex, LIFTER_POSITIONS_TICKS[CurrentIndex], LIFTERPOS_TOL)) {
            CurrentIndex++;
            //}
        }
        setPosition(CurrentIndex);
    }

    //*********************************************************************************************

    public void decPositionIndex() {
        if (underLEGControl) {
            return;
        }
        //The user might have been using stick control reset the index
        CurrentIndex = findNextIndexDown(CurrentTickCount);
        //Make sure that we still have a valid index
        if (CommonLogic.indexCheck(CurrentIndex, LOW_INDEX + 1, HIGH_INDEX)) {
            //If we are in range then dec the index... Else we will move to the current index position
            //if (CommonLogic.inRange(CurrentIndex, LIFTER_POSITIONS_TICKS[CurrentIndex], LIFTERPOS_TOL)) {
            CurrentIndex--;
            //}
        }
        setPosition(CurrentIndex);
    }

    //*********************************************************************************************

    public void stickControl(double throttle) {


        if (throttle < 0) {
            if ((CurrentTickCount - LIFTER_STEP) > LIFTER_POSITIONS_TICKS[LOW_INDEX]) {
                // update the index so that it displays correctly
                CurrentIndex = findNextIndexDown(CurrentTickCount);
                LFT1.setTargetPosition(CurrentTickCount - LIFTER_STEP);
            }
        } else if (throttle > 0) {
            if ((CurrentTickCount + LIFTER_STEP) < LIFTER_POSITIONS_TICKS[HIGH_INDEX]) {
                // update the index so that it displays correctly
                CurrentIndex = findNextIndexUP(CurrentTickCount);
                LFT1.setTargetPosition(CurrentTickCount + LIFTER_STEP);
            }
        }

    }

    //*********************************************************************************************
    // With the advent of stick control this means the Lifter can be anywhere long its entire range
    // of positions.   This function searches the known positions and sets the index value to the
    // closest one going UP.
    private int findNextIndexUP(int ticks) {

        int retValue = LOW_INDEX;
        for (int i = LOW_INDEX; i <= HIGH_INDEX; i++) {
            retValue = i;
            if (CommonLogic.inRange(ticks, LIFTER_POSITIONS_TICKS[i], LIFTERPOS_TOL)) {
                break;
            } else if (ticks <= (LIFTER_POSITIONS_TICKS[i] - 1)) {
                break;
            }
        }
        return retValue;
    }

    //*********************************************************************************************
    // With the advent of stick control this means the Lifter can be anywhere long its entire range
    // of positions.   This function searches the known positions and sets the index value to the
    // closest one going Down.
    private int findNextIndexDown(int ticks) {

        int retValue = HIGH_INDEX;
        for (int i = HIGH_INDEX; i >= LOW_INDEX; i--) {
            retValue = i;
            if (CommonLogic.inRange(ticks, LIFTER_POSITIONS_TICKS[i], LIFTERPOS_TOL)) {
                break;
            } else if (ticks >= (LIFTER_POSITIONS_TICKS[i])) {
                break;
            }
        }
        return retValue;
    }

    //*********************************************************************************************
    public int clear_tower() {

        int clear_tower_tics = CurrentTickCount + CLEAR_NUB_TICS;

        if (clear_tower_tics > LIFTER_POSITIONS_TICKS[HIGH_INDEX]) {
            clear_tower_tics = LIFTER_POSITIONS_TICKS[HIGH_INDEX];
        }

        LFT1.setTargetPosition(clear_tower_tics);
        return clear_tower_tics;
    }
    //*********************************************************************************************

    public int getIndexTics(int index) {

        if (CommonLogic.indexCheck(index, LOW_INDEX, HIGH_INDEX)) {
            return LIFTER_POSITIONS_TICKS[index];
        } else if (index < LOW_INDEX) {
            return LIFTER_POSITIONS_TICKS[LOW_INDEX];
        } else {
            return LIFTER_POSITIONS_TICKS[HIGH_INDEX];
        }

    }
    //*********************************************************************************************

    public int getPosTics() {
        return CurrentTickCount;
    }

    //*********************************************************************************************
    public void setPosTics(int tics) {
        if ((tics >= LIFTER_POSITIONS_TICKS[0]) && (tics <= LIFTER_POSITIONS_TICKS[HIGH_INDEX])) {
            LFT1.setTargetPosition(tics);
        }

    }
    //*********************************************************************************************

    public void stop() {
        LFT1.setPower(0);
        LFT1.setTargetPosition(CurrentTickCount);
    }
    //*********************************************************************************************

}
