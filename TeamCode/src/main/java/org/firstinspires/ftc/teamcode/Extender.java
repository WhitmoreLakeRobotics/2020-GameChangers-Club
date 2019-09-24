package org.firstinspires.ftc.teamcode;

/* Hanger controls all actions involving hanging from the Lander:
    - locking motors in break mode for initial hanger
    - moving the latch mechanism
    - lowering the chassis to the floor
    - retracking the hanger into the chassis
    - reversing this for the end game

 */


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;


//@TeleOp(name = "Extender", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class Extender extends BaseHardware {
    //Encoder positions for the EXTENDER
    public static final int RESTMODE = 0;
    public static final int EXTENDERPOS_RETRACTED = 0;

    public static final int EXTENDERPOS_TOL = 40;
    public static final int EXTENDERPOS_EXNTENDED = 925;  //measured on robot on Oct 11, 2018
    public static final double EXTENDERPOWER_EXTEND = 1;
    public static final double EXTENDERPOWER_RETRACT = -1;
    private static final String TAGExtender = "8492-Extender";
    double EXTENDERPOWER_current = 0;
    boolean cmdComplete = false;
    boolean underStickControl = false;
    int extenderPosition_CURRENT = EXTENDERPOS_RETRACTED;
    int extenderPosition_Start_Pos =0;
    int extenderPosition_Pos1 =720;
    int extenderPosition_pos2 =1440;
    int extenderPosition_pos3 =2160;

    /*    public static final int ticsPerRev = 1100;
        public static final double wheelDistPerRev = 4 * 3.14159;
        public static final double gearRatio = 80 / 80;
        public static final double ticsPerInch = ticsPerRev / wheelDistPerRev / gearRatio;
    */
    // This is the current tick counts of the Hanger
    // This is the commanded tick counts of the Hanger
    double ExtenderStickDeadBand = .2;
    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();

    //set the HANGER powers... We will need different speeds for up and down.
    private ExtenderStates intakeArm = null;
    private double initMotorPower = 0;
    private double currentMotorpower = 0.5;

// Boolean to check if movement complete


// boolean to check if auton or stick control


    // declare motors
    private DcMotor EXT1 = null;
    private DigitalChannel extenderTCH = null;


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
        RobotLog.aa(TAGExtender, "extenderPos: " + EXT1);
//do not know what digital channel is check here for errors ******
        extenderTCH = hardwareMap.get(DigitalChannel.class, "extenderTCH");
        extenderTCH.setMode(DigitalChannel.Mode.INPUT);


    }

    public void extenderMotorEncoderReset() {


        EXT1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        EXT1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        //initPowerHang();
    }

    private void initPowerHang() {
        // If the robot needs help to hang this will give a little bit of motor power to help
        // hold the robot in the 18 inch cube.

        double newMotorPower = 0;
        if (extenderTCH.getState()) {
            newMotorPower = initMotorPower;
            //initMotorPower = initMotorPower + (HANGERPOWER_EXTEND * .01);
            //newMotorPower = initMotorPower - (EXTENDERPOWER_EXTEND * .01); + (EXTENDERPOWER_RETRACT * .01);
        } else {
            if (newMotorPower < 0) {
                newMotorPower = 0;
            }
        }

        if (newMotorPower != initMotorPower) {
            telemetry.addData("initExtenderPower", newMotorPower);
            initMotorPower = newMotorPower;
            EXT1.setPower(initMotorPower);
        }

    }

    private void initHangerTCH() {
        ElapsedTime runtime = new ElapsedTime();
        runtime.reset();
        //runtime.startTime();

        EXT1.setPower(EXTENDERPOWER_RETRACT);
        while (extenderTCH.getState()) {
            if (runtime.milliseconds() > 2000) {
                break;
            }
        }
        EXT1.setPower(0);

    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        // this is always called by chassis
        EXT1.setPower(0);

    }

    public void autoStart() {
        // This is only called by chassis when running Auto OpModes
        initExtenderTCH();
        EXT1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        EXT1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
    private void initExtenderTCH(){}

    public void teleStart() {
        // This is only called by chassis when running Tele Modes

    }


    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        telemetry.addData("ExtenderPos",  extenderPosition_CURRENT);
        RobotLog.aa(TAGExtender, "ExtenderPos: " + extenderPosition_CURRENT);

//check if under stick control [must create process (public void ...) first]
        if (!underStickControl) {
            testInPosition();
        }

        SetMotorPower(EXTENDERPOWER_current);


    }



    private void SetMotorPower(double newMotorPower) {
        //set the motors for the HANGER to the new power only after
        // Safety checks to prevent too low or too high
        extenderPosition_CURRENT = EXT1.getCurrentPosition();
        double newPower = newMotorPower;
        // make sure that we do not attempt to move less than RETRACT limit

        RobotLog.aa(TAGExtender, "Curr Position: " + extenderPosition_CURRENT);
        RobotLog.aa(TAGExtender, "set pwr : " + newPower);

        //if were within start tolerance, stop
        if ((extenderPosition_CURRENT <= (extenderPosition_Start_Pos + EXTENDERPOS_TOL)) && (newPower < 0)) {
            newPower = 0;
        }

        // if on the switch and we are trying to retract set newPower to stop value
        if (!extenderTCH.getState() && (newPower < 0)) {
            newPower = 0;
        }

        // make sure that we do stop at possition 1
        if ((extenderPosition_CURRENT >= (extenderPosition_Pos1 - EXTENDERPOS_TOL)) && (newPower > 0)) {
            newPower = 0;

        }
        // make sure that we do stop at possition 1
        if ((extenderPosition_CURRENT <= (extenderPosition_Pos1 - EXTENDERPOS_TOL)) && (newPower < 0)) {
            newPower = 0;
        }
        // make sure that we do stop at possition 2
        if ((extenderPosition_CURRENT >= (extenderPosition_pos2 - EXTENDERPOS_TOL)) && (newPower > 0)) {
            newPower = 0;

        }
        // make sure that we do stop at possition 2
        if ((extenderPosition_CURRENT <= (extenderPosition_pos2 - EXTENDERPOS_TOL)) && (newPower < 0)) {
            newPower = 0;
        }
        // make sure that we do stop at possition 3
        if ((extenderPosition_CURRENT >= (extenderPosition_pos3 - EXTENDERPOS_TOL)) && (newPower > 0)) {
            newPower = 0;

        }
        // make sure that we do stop at possition 3
        if ((extenderPosition_CURRENT <= (extenderPosition_pos3 - EXTENDERPOS_TOL)) && (newPower < 0)) {
            newPower = 0;
        }
            //only set the power to the hardware when it is being changed.
        if (newPower != EXTENDERPOWER_current) {
            EXTENDERPOWER_current = newPower;
            EXT1.setPower(EXTENDERPOWER_current);
        }
    }

    private void testInPosition() {
        // tests if we are in position and stop if we are;

    }


    //driver is using stick control for Hanger
    public void cmdStickControl(double stickPos) {

        if (Math.abs(stickPos) < ExtenderStickDeadBand) {
            if (underStickControl) {
                EXTENDERPOWER_current = 0;
            }
            // we are inside the deadband do nothing.
            underStickControl = false;
            return;
        } else {
            underStickControl = true;
            cmdComplete = false;
            double currPower = stickPos;

            //limit the power of the stick
            if (stickPos > EXTENDERPOWER_EXTEND) {
                currPower = EXTENDERPOWER_EXTEND;
            }

            //limit the power of the stick
            if (stickPos < EXTENDERPOWER_RETRACT) {
                currPower = EXTENDERPOWER_RETRACT;
            }

            EXTENDERPOWER_current = currPower;
        }

    }


    // somebody pressed a button or ran Auton to send command to move to a given location.
    // create new process
    public void cmd_MoveToTarget(int TargetTicks) {
        int PostionNew = TargetTicks;
        //Do not move below BOTTOM
        RobotLog.aa(TAGExtender, "move to target: " + TargetTicks);
        if (PostionNew <= EXTENDERPOS_TOL + EXTENDERPOS_RETRACTED) {
            PostionNew = EXTENDERPOS_RETRACTED;


        }
        //Do not move above MAX
        if (PostionNew >= EXTENDERPOS_TOL + EXTENDERPOS_EXNTENDED) {
            PostionNew = EXTENDERPOS_EXNTENDED;
        }


        //we are higher than we want to be and
        //not already at the bottom.
        if ((PostionNew <= extenderPosition_CURRENT + EXTENDERPOS_TOL) && (EXTENDERPOS_RETRACTED < extenderPosition_CURRENT)) {
            EXTENDERPOWER_current = EXTENDERPOWER_RETRACT;
            cmdComplete = false;
            underStickControl = false;
        }

        //We are lower than we want to be and not already at the top
        //not already at the bottom.
        if ((PostionNew >= extenderPosition_CURRENT + EXTENDERPOS_TOL) && (EXTENDERPOS_EXNTENDED > extenderPosition_CURRENT)) {
            EXTENDERPOWER_current = EXTENDERPOWER_EXTEND;
            cmdComplete = false;
            underStickControl = false;

            //We need to go down to target
        }
        RobotLog.aa(TAGExtender, "MTT end Target: " + TargetTicks + " cur: " + extenderPosition_CURRENT + " Tol: " + EXTENDERPOS_TOL + "Ext: " + EXTENDERPOS_EXNTENDED);


    }  // cmd_MoveToTarget

    public boolean isExtended() {
        return ((extenderPosition_CURRENT > (EXTENDERPOS_EXNTENDED - EXTENDERPOS_TOL)) &&
                extenderTCH.getState());

    }

    public boolean isRetracted() {
        // Need to add the switch to this method
        return ((extenderPosition_CURRENT < (EXTENDERPOS_RETRACTED + (int)(EXTENDERPOS_TOL * 1.00))) ||
                (!extenderTCH.getState()));

    }


    public int getHangerPos() {
        return extenderPosition_CURRENT;
    }


    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        SetMotorPower(0);

    }
    public enum ExtenderStates{
        START_POS,
        RET_TO_START_POS,
        EXT_TO_POS1,
        RET_TO_POS1,
        POS1,
        EXT_TO_POS2,
        RET_TO_POS2,
        POS2,
        EXT_TO_POS3,
        RET_TO_POS3,
        POS3,
        UNKNOWN,

    }

}
