package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorControllerEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.RobotLog;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "Tele_Op", group = "TeleOp")
//@Disabled
public class Pid_Tuner extends OpMode {


    private DcMotor motor = null;
    private DcMotorControllerEx motorControl = null;
    private int motorIndex = 0;
    double NEW_P = 2.5;
    double NEW_I = 0.1;
    double NEW_D = 0.2;
    public static final int LIFTERPOS_TOL = 5;
    public static final double LIFTERPOWER_UP = 1.0;
    public static final double LIFTERPOWER_DOWN = 1.0;


    //Named index positions
    public static int PICK_POS = 0;
    public static int CARRY_POS = 1;
    public static int PRE_PICK_POS = 2;

    private static int LOW_INDEX = 0;
    private static int HIGH_INDEX = 8;
    private static int[] LIFTER_POSITIONS_TICKS = new int[HIGH_INDEX + 1];
    private int CurrentIndex = LOW_INDEX;
    private int CurrentTickCount = 0;

    private boolean gp1_prev_a = false;
    private boolean gp1_prev_b = false;
    private boolean gp1_prev_x = false;
    private boolean gp1_prev_y = false;
    private boolean gp1_prev_right_bumper = false;
    private boolean gp1_prev_left_bumper = false;
    private boolean gp1_prev_dpad_up = false;
    private boolean gp1_prev_dpad_down = false;
    private boolean gp1_prev_dpad_left = false;
    private boolean gp1_prev_dpad_right = false;

    private boolean gp2_prev_a = false;
    private boolean gp2_prev_b = false;
    private boolean gp2_prev_x = false;
    private boolean gp2_prev_y = false;
    private boolean gp2_prev_right_bumper = false;
    private boolean gp2_prev_left_bumper = false;
    private boolean gp2_prev_dpad_up = false;
    private boolean gp2_prev_dpad_down = false;
    private boolean gp2_prev_dpad_left = false;
    private boolean gp2_prev_dpad_right = false;

    //*********************************************************************************************
    public void init() {
        motor = hardwareMap.dcMotor.get("LFT1");
        motor.setDirection(DcMotor.Direction.REVERSE);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        DcMotorControllerEx motorControl = (DcMotorControllerEx) motor.getController();


        // get the port number of our configured motor.
        motorIndex = motor.getPortNumber();

        // get the PID coefficients for the RUN_USING_ENCODER  modes.
        PIDFCoefficients pidOrig = motorControl.getPIDFCoefficients(motorIndex, DcMotor.RunMode.RUN_TO_POSITION);

        telemetry.addData("LFT1 P", pidOrig.p);
        telemetry.addData("LFT1 I", pidOrig.i);
        telemetry.addData("LFT1 D", pidOrig.d);
        telemetry.addData("LFT1 F", pidOrig.f);

    }

    //*********************************************************************************************
    public void init_loop() {

    }

    //*********************************************************************************************
    public void start() {
        int brickheight = 300;
        int foundationheight = 226;

        LIFTER_POSITIONS_TICKS[PICK_POS] = 15;  //start pick
        LIFTER_POSITIONS_TICKS[CARRY_POS] = 70;  //carry
        LIFTER_POSITIONS_TICKS[PRE_PICK_POS] = 100;  // Pre-Pick location
        LIFTER_POSITIONS_TICKS[3] = foundationheight; //level 1
        LIFTER_POSITIONS_TICKS[4] = foundationheight + (1 * brickheight);  //level 2
        LIFTER_POSITIONS_TICKS[5] = foundationheight + (2 * brickheight);  //level 3
        LIFTER_POSITIONS_TICKS[6] = foundationheight + (3 * brickheight);  //level 4
        LIFTER_POSITIONS_TICKS[7] = foundationheight + (4 * brickheight);  //level 5
        LIFTER_POSITIONS_TICKS[8] = foundationheight + (5 * brickheight);  //level 6

    }

    //*********************************************************************************************
    public void loop() {

        boolean change = false;

        PIDFCoefficients pidOrig = motorControl.getPIDFCoefficients(motorIndex, DcMotor.RunMode.RUN_TO_POSITION);

        if (CommonLogic.oneShot(gamepad2.dpad_right, gp2_prev_dpad_right)) {
            pidOrig.i = pidOrig.i + .05;
            change = true;
        }

        if (CommonLogic.oneShot(gamepad2.dpad_up, gp2_prev_dpad_up)) {
            pidOrig.p = pidOrig.p + .10;
            change = true;
        }

        if (CommonLogic.oneShot(gamepad2.dpad_left, gp2_prev_dpad_left)) {
            pidOrig.i = pidOrig.i - .05;
            change = true;
        }

        if (CommonLogic.oneShot(gamepad2.dpad_down, gp2_prev_dpad_down)) {
            pidOrig.p = pidOrig.p - .10;
            change = true;
        }

        if (CommonLogic.oneShot(gamepad2.a, gp2_prev_a)) {
            pidOrig.d = pidOrig.d - .05;
            change = true;
        }

        if (CommonLogic.oneShot(gamepad2.y, gp2_prev_y)) {
            pidOrig.d = pidOrig.d + .05;
            change = true;
        }

        if (CommonLogic.oneShot(gamepad2.b, gp2_prev_b)) {
            pidOrig.f = pidOrig.f - .01;
            change = true;
        }

        if (CommonLogic.oneShot(gamepad2.x, gp2_prev_x)) {
            pidOrig.f = pidOrig.f + .01;
            change = true;
        }

        if (change) {
            motorControl.setPIDFCoefficients(motorIndex, DcMotor.RunMode.RUN_TO_POSITION, pidOrig);
        }


        if (CommonLogic.oneShot(gamepad1.a, gp1_prev_a)) {
            decPositionIndex();
        }

        if (CommonLogic.oneShot(gamepad1.y, gp1_prev_y)) {
            incPositionIndex();
        }

        CurrentTickCount = motor.getCurrentPosition();

        telemetry.addData("r", String.format("Idx=%d\tTics=%d", CurrentIndex,  LIFTER_POSITIONS_TICKS[CurrentIndex]));
        telemetry.addData("c", String.format("\tPos-Tics=%d", CurrentTickCount));

        telemetry.addData("MC P", pidOrig.p);
        telemetry.addData("MC I", pidOrig.i);
        telemetry.addData("MC D", pidOrig.d);
        telemetry.addData("MC F", pidOrig.f);

        // Update the previous status for gamepad 1
        gp1_prev_a = gamepad1.a;
        gp1_prev_b = gamepad1.b;
        gp1_prev_x = gamepad1.x;
        gp1_prev_y = gamepad1.y;
        gp1_prev_left_bumper = gamepad1.left_bumper;
        gp1_prev_right_bumper = gamepad1.right_bumper;
        gp1_prev_dpad_down = gamepad1.dpad_down;
        gp1_prev_dpad_left = gamepad1.dpad_left;
        gp1_prev_dpad_up = gamepad1.dpad_up;
        gp1_prev_dpad_right = gamepad1.dpad_right;

        // Update the previous status for gamepad 2
        gp2_prev_a = gamepad2.a;
        gp2_prev_b = gamepad2.b;
        gp2_prev_x = gamepad2.x;
        gp2_prev_y = gamepad2.y;
        gp2_prev_left_bumper = gamepad2.left_bumper;
        gp2_prev_right_bumper = gamepad2.right_bumper;
        gp2_prev_dpad_down = gamepad2.dpad_down;
        gp2_prev_dpad_left = gamepad2.dpad_left;
        gp2_prev_dpad_up = gamepad2.dpad_up;
        gp2_prev_dpad_right = gamepad2.dpad_right;
    }

    //*********************************************************************************************
    public void stop() {

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
                motor.setPower(LIFTERPOWER_DOWN);
            }
            // If needed to go Up go faster than Down
            else {
                motor.setPower(LIFTERPOWER_UP);
            }
            //Set the motor to hold the new position
            motor.setTargetPosition(LIFTER_POSITIONS_TICKS[index]);
        }
        else {
            telemetry.addData("Lifter Index Out of Range",index);
        }
    }

    //*********************************************************************************************

    public void incPositionIndex() {
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
}
