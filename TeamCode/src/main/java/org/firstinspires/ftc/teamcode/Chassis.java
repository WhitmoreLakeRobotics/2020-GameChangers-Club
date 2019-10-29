
package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.Locale;


//@TeleOp_test(name="Basic: Iterative OpMode", group="Iterative Opmode")
//@Disabled
public class Chassis extends OpMode {

    //for truning this is the tolerance of trun in degrees
    public static final int chassis_GyroHeadingTol = 3;

    public enum ChassisMode {
        STOP,
        DRIVE,
        TURN,
        TELEOP,
        UNKNOWN
    }


    public static final int ticsPerRev = Settings.REV_HD_40_MOTOR_TICKS_PER_REV;
    public static final double wheelDistPerRev = 2 * 3.14159;
    public static final double gearRatio = 80.0 / 40.0;
    public static final double ticsPerInch = ticsPerRev / wheelDistPerRev / gearRatio;
    public static final double Chassis_DriveTolerInches = .25;
    // naj set constant for Gyro KP for driving straight
    public static final double chassis_KPGyroStraight = 0.02;
    private static final String TAGChassis = "8492-Chassis";


    public Extender subExtender = new Extender();
    public CommonGyro subGyro = new CommonGyro();
    public HDrive subHDrive = new HDrive();
    public Gripper subGripper = new Gripper();
    public GrabberArms subGrabbers = new GrabberArms();

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private int initCounter = 0;
    //current mode of operation for Chassis
    private ChassisMode ChassisMode_Current = ChassisMode.UNKNOWN;
    private boolean cmdComplete = true;
    private int cmdStartTime_mS = 0;
    private Settings.PARENTMODE parentMode_Current = null;
    private DcMotor LDM1 = null;
    private DcMotor LDM2 = null;
    private DcMotor RDM1 = null;
    private DcMotor RDM2 = null;

    private double TargetMotorPowerLeft = 0.0;
    private double TargetMotorPowerRight = 0.0;

    private int TargetHeadingDeg = 0;
    private double TargetDistanceInches = 0.0;

    private double maxPower = 1.0;

    //*********************************************************************************************
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {

        RDM1 = hardwareMap.dcMotor.get("RDM1");
        LDM1 = hardwareMap.dcMotor.get("LDM1");
        LDM2 = hardwareMap.dcMotor.get("LDM2");
        RDM2 = hardwareMap.dcMotor.get("RDM2");


        if (LDM1 == null) {
            telemetry.log().add("LDM1 is null...");
        }
        if (LDM2 == null) {
            telemetry.log().add("LDM2 is null...");
        }
        if (RDM1 == null) {
            telemetry.log().add("RDM1 is null...");
        }
        if (RDM2 == null) {
            telemetry.log().add("RDM2 is null...");
        }


        LDM1.setDirection(DcMotor.Direction.FORWARD);
        LDM2.setDirection(DcMotor.Direction.FORWARD);
        RDM1.setDirection(DcMotor.Direction.REVERSE);
        RDM2.setDirection(DcMotor.Direction.REVERSE);

        LDM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LDM2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RDM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RDM2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        LDM1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        LDM2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RDM1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RDM2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        LDM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        LDM2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        RDM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        RDM2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);



        subExtender.telemetry = telemetry;
        subExtender.hardwareMap = hardwareMap;
        subExtender.init();

        subGyro.telemetry = telemetry;
        subGyro.hardwareMap = hardwareMap;
        subGyro.init();

        subHDrive.telemetry = telemetry;
        subHDrive.hardwareMap = hardwareMap;
        subHDrive.init();

        subGripper.telemetry = telemetry;
        subGripper.hardwareMap = hardwareMap;
        subGripper.init();

        subGrabbers.telemetry = telemetry;
        subGrabbers.hardwareMap = hardwareMap;
        subGrabbers.init();
        telemetry.addData("Chassis", "Initialized");
        ChassisMode_Current = ChassisMode.STOP;
        runtime.reset();
    }

    //*********************************************************************************************
    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {

        if (runtime.milliseconds() > 1000) {
            initCounter = initCounter + 1;
            telemetry.addData("Chassis init time: ", initCounter);
            telemetry.update();
            runtime.reset();
        }
        subExtender.init_loop();
        subGyro.init_loop();
        subHDrive.init_loop();
        subGripper.init_loop();
        subGrabbers.init_loop();
    }

    //*********************************************************************************************
    public void setParentMode(Settings.PARENTMODE pm) {

        parentMode_Current = pm;
        subHDrive.setParentMode(pm);
        subGyro.setParentMode(pm);
        subExtender.setParentMode(pm);
    }

    //*********************************************************************************************

    private void setChassis (){
        subExtender.setChassisType(Settings.CHASSIS_TYPE.CHASSIS_TEST);
        subHDrive.setChassisType(Settings.CHASSIS_TYPE.CHASSIS_TEST);
    }

    //*********************************************************************************************
    private void setMotorMode(DcMotor.RunMode newMode) {

        LDM1.setMode(newMode);
        RDM1.setMode(newMode);
        LDM2.setMode(newMode);
        RDM2.setMode(newMode);
    }

    //*********************************************************************************************
    public void setMotorMode_RUN_WITHOUT_ENCODER() {
        setMotorMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    //*********************************************************************************************
    public void DriveMotorEncoderReset() {

        LDM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RDM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LDM2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RDM2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        LDM1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        RDM1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        LDM2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        RDM2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
    //*********************************************************************************************
    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        runtime.reset();

        switch (parentMode_Current) {
            case PARENT_MODE_AUTO:
                break;

            case PARENT_MODE_TELE:
                break;
        }
        subExtender.start();
        subGyro.start();
        subHDrive.start();
        //subGripper.start();
        subGrabbers.start();
    }

    //*********************************************************************************************
    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        subExtender.loop();
        subGyro.loop();
        subHDrive.loop();
        subGripper.loop();
        subGrabbers.loop();

        switch (ChassisMode_Current) {

            case STOP:
                doStop();
                break;

            case DRIVE:
                doDrive();
                break;


            case TURN:
                doTurn();
                break;

            case TELEOP:
                doTeleop();
                break;

            default:
                break;


        }
        // Show the elapsed game time and wheel power.
        // telemetry.addData("Status", "Run Time: " + runtime.toString());

        // RobotLog.aa(TAGChassis,"Stage: "+ CurrentStage );
        //RobotLog.aa(TAGChassis, "Runtime: " + runtime.seconds());
        //double inchesTraveled = Math.abs(getEncoderInches());
        //RobotLog.aa(TAGChassis, "loop targetinches: " + Math.abs(TargetDistanceInches - Chassis_DriveTolerInches));
        //RobotLog.aa(TAGChassis, "inchesTraveled: " + inchesTraveled);

    }

    //*********************************************************************************************
    public void doTeleop() {
        ChassisMode_Current = ChassisMode.TELEOP;

        double lPower = CommonLogic.CapMotorPower(TargetMotorPowerLeft, -maxPower, maxPower);
        double rPower = CommonLogic.CapMotorPower(TargetMotorPowerRight, -maxPower, maxPower);
        LDM1.setPower(lPower);
        RDM1.setPower(rPower);
        LDM2.setPower(lPower);
        RDM2.setPower(rPower);
        RobotLog.aa(TAGChassis, "doTeleop: lPower=" + lPower + " rPower=" + rPower);
    }

    //*********************************************************************************************
    private void doStop() {

        RobotLog.aa(TAGChassis, "doStop:");
        TargetMotorPowerLeft = 0;
        TargetMotorPowerRight = 0;
        TargetDistanceInches = 0;
        LDM1.setPower(TargetMotorPowerLeft);
        LDM2.setPower(TargetMotorPowerLeft);
        RDM1.setPower(TargetMotorPowerRight);
        RDM2.setPower(TargetMotorPowerRight);
        ChassisMode_Current = ChassisMode.STOP;

    }

    //*********************************************************************************************
    private void doDrive() {

        // insert adjustments to drive straight using gyro
        RobotLog.aa(TAGChassis, "curr heading: " + subGyro.gyroNormalize(subGyro.getGyroHeading()));
        RobotLog.aa(TAGChassis, "Target: " + TargetHeadingDeg);

        double delta = -subGyro.deltaHeading(subGyro.gyroNormalize(subGyro.getGyroHeading()), TargetHeadingDeg);
        double leftPower = TargetMotorPowerLeft - (delta * chassis_KPGyroStraight);
        double rightPower = TargetMotorPowerRight + (delta * chassis_KPGyroStraight);

        RobotLog.aa(TAGChassis, "delta: " + delta);
        RobotLog.aa(TAGChassis, "leftpower: " + leftPower + " right " + rightPower);

        leftPower = CommonLogic.CapMotorPower(leftPower, - maxPower, maxPower);
        rightPower = CommonLogic.CapMotorPower(rightPower, -maxPower, maxPower);

        LDM1.setPower(leftPower);
        LDM2.setPower(leftPower);
        RDM1.setPower(rightPower);
        RDM2.setPower(rightPower);

        //check if we've gone far enough, if so stop and mark task complete
        double inchesTraveled = Math.abs(getEncoderInches());

        if (inchesTraveled >= Math.abs(TargetDistanceInches - Chassis_DriveTolerInches)) {
            RobotLog.aa(TAGChassis, "Target Inches: " + Math.abs(TargetDistanceInches - Chassis_DriveTolerInches));
            RobotLog.aa(TAGChassis, "Inches Traveled: " + inchesTraveled);
            cmdComplete = true;
            doStop();
        }

    }    // doDrive()

    //*********************************************************************************************
    private void doTurn() {
        /*
         *   executes the logic of a single scan of turning the robot to a new heading
         */

        int currHeading = subGyro.gyroNormalize(subGyro.getGyroHeading());
        RobotLog.aa(TAGChassis, "Turn currHeading: " + currHeading + " target: " + TargetHeadingDeg);
        RobotLog.aa(TAGChassis, "Runtime: " + runtime.seconds());

        if (subGyro.gyroInTol(currHeading, TargetHeadingDeg, chassis_GyroHeadingTol)) {
            RobotLog.aa(TAGChassis, "Complete currHeading: " + currHeading);
            //We are there stop
            cmdComplete = true;
            ChassisMode_Current = ChassisMode.STOP;
            doStop();
        }
    }

    //*********************************************************************************************
    // create method to return complete boolean
    public boolean getcmdComplete() {

        return (cmdComplete);
    }

    //*********************************************************************************************
    // create command to be called from auton to drive straight
    public void cmdDrive(double DrivePower, int headingDeg, double targetDistanceInches) {

        cmdComplete = false;
        if (ChassisMode_Current != ChassisMode.DRIVE) {
            ChassisMode_Current = ChassisMode.DRIVE;
        }
        TargetHeadingDeg = headingDeg;
        RobotLog.aa(TAGChassis, "cmdDrive: " + DrivePower);
        TargetMotorPowerLeft = DrivePower;
        TargetMotorPowerRight = DrivePower;
        TargetDistanceInches = targetDistanceInches;
        DriveMotorEncoderReset();
        ChassisMode_Current = ChassisMode.DRIVE;
        //doDrive();
    }

    //*********************************************************************************************
    public void cmdTurn(double LSpeed, double RSpeed, int headingDeg) {

        //can only be called one time per movement of the chassis
        ChassisMode_Current = ChassisMode.TURN;
        TargetHeadingDeg = headingDeg;
        RobotLog.aa(TAGChassis, "cmdTurn target: " + TargetHeadingDeg);

        LDM1.setPower(LSpeed);
        LDM2.setPower(LSpeed);
        RDM1.setPower(RSpeed);
        RDM2.setPower(RSpeed);
        cmdComplete = false;
        runtime.reset();
        doTurn();
    }

    //*********************************************************************************************

    public void cmdTeleOp (double lSpeed, double rSpeed){
        cmdComplete = false;
        ChassisMode_Current = ChassisMode.TELEOP;
        TargetMotorPowerLeft = lSpeed;
        TargetMotorPowerRight = rSpeed;
    }

    //*********************************************************************************************
    public double getEncoderInches() {
        // create method to get inches driven in auton
        // read the values from the encoders
        // LDM1.getCurrentPosition()
        // convert that to inches
        // by dividing by ticksPerInch

        // average the distance traveled by each wheel to determine the distance travled by the
        // robot


        int totalitics = Math.abs(LDM1.getCurrentPosition()) +
                Math.abs(LDM2.getCurrentPosition()) +
                Math.abs(RDM1.getCurrentPosition()) +
                Math.abs(RDM2.getCurrentPosition());
        double averagetics = totalitics / 4;
        double inches = averagetics / ticsPerInch;


        return inches;

    }

    //*********************************************************************************************
    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        LDM1.setPower(0);
        LDM2.setPower(0);
        RDM1.setPower(0);
        RDM2.setPower(0);
        ChassisMode_Current = ChassisMode.STOP;
        subExtender.stop();
        subGyro.stop();
        subHDrive.stop();
        subGripper.stop();
        subGrabbers.stop();
    }

    //*********************************************************************************************
    public void setMaxPower(double newMax) {
        maxPower = Math.abs(newMax);
    }
    //*********************************************************************************************
}
