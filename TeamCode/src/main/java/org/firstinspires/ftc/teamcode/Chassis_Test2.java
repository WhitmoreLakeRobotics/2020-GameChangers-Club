
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
public class Chassis_Test2 extends OpMode {

    //for truning this is the tolerance of trun in degrees
    public static final int chassis_GyroHeadingTol = 3;

    public enum ChassisMode {
        STOP,
        DRIVE,
        DRIVEH,
        TURN,
        TELEOP,
        TELEOPH
    }


    public static final int ticsPerRev = 1120;
    public static final double wheelDistPerRev = 4 * 3.14159;
    public static final double gearRatio = 80.0 / 80.0;
    public static final double ticsPerInch = ticsPerRev / wheelDistPerRev / gearRatio;

    public static final int ticsPerRevCoreHex = 288;
    public static final double gearRatioH = 1.0 / 1.0;
    public static final double ticsPerInchH = ticsPerRevCoreHex / wheelDistPerRev / gearRatioH;

    public static final double Chassis_DriveTolerInchesH = .25;
    public static final double Chassis_DriveTolerInches = .25;
    // naj set constant for Gyro KP for driving straight
    public static final double chassis_KPGyroStraight = 0.02;
    public static final double chassis_KPGyroStraightH = 0.1;
    private static final String TAGChassis = "8492-Chassis";
    // The IMU sensor object
    BNO055IMU imu;


    public Extender subExtender = new Extender();

    // naj set constant for turning Tolerance in degrees
    // State used for updating telemetry
    Orientation angles;
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private int initCounter = 0;
    //current mode of operation for Chassis
    private ChassisMode ChassisMode_Current = ChassisMode.STOP;
    private boolean cmdComplete = true;
    private int cmdStartTime_mS = 0;
    private PARENTMODE parentMode_Current = null;
    private DcMotor LDM1 = null;
    private DcMotor LDM2 = null;
    private DcMotor RDM1 = null;
    private DcMotor RDM2 = null;
    private DcMotor HDM1 = null;
    private double TargetMotorPowerLeft = 0.0;
    private double TargetMotorPowerRight = 0.0;
    private double TargetMotorPowerH = 0.0;

    private int TargetHeadingDeg = 0;
    private double TargetDistanceInches = 0.0;
    private double TargetDistanceInchesH = 0.0;

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
        HDM1 = hardwareMap.dcMotor.get("HDM1");


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
        if (HDM1 == null) {
            telemetry.log().add("HDM is null...");
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

        HDM1.setDirection(DcMotor.Direction.FORWARD);
        HDM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        HDM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        HDM1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // Set up the parameters with which we will use our IMU. Note that integration
        // algorithm here just reports accelerations to the logcat log; it doesn't actually
        // provide positional information.
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
        telemetry.addData("Chassis", "Initialized");

        subExtender.telemetry = telemetry;
        subExtender.hardwareMap = hardwareMap;
        subExtender.init();

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
    }

    //*********************************************************************************************
    public void setParentMode(PARENTMODE pm) {
        parentMode_Current = pm;
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

        HDM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        LDM1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        RDM1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        LDM2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        RDM2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        HDM1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    //*********************************************************************************************
    public void DriveServoMotorReset() {

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
    }

    //*********************************************************************************************
    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        subExtender.loop();

        // Do nothing the TELEOP is driven by the TeleOp Loop
        // if (ChassisMode_Current == ChassisMode.TELEOP) {}

        if (ChassisMode_Current == ChassisMode.STOP) {
            doStop();
        }


        //  check mode and do what what ever mode is current
        if (ChassisMode_Current == ChassisMode.DRIVE) {
            doDrive();
        }

        if (ChassisMode_Current == ChassisMode.TURN) {
            doTurn();
        }

        if (ChassisMode_Current == ChassisMode.DRIVEH) {
            // drive and stop on the correct distance
            doDriveH();
        }
        if (ChassisMode_Current == ChassisMode.TELEOPH) {
            // drive with driver controlled distance
            doSteerH();
        }

        // Show the elapsed game time and wheel power.
        // telemetry.addData("Status", "Run Time: " + runtime.toString());

        // RobotLog.aa(TAGChassis,"Stage: "+ CurrentStage );
        RobotLog.aa(TAGChassis, "Runtime: " + runtime.seconds());
        double inchesTraveled = Math.abs(getEncoderInches());
        RobotLog.aa(TAGChassis, "loop targetinches: " + Math.abs(TargetDistanceInches - Chassis_DriveTolerInches));
        RobotLog.aa(TAGChassis, "inchesTraveled: " + inchesTraveled);

    }

    //*********************************************************************************************
    public void doTeleopH(double leftPower, double rightPower) {

        if (ChassisMode_Current != ChassisMode.TELEOPH) {
            ChassisMode_Current = ChassisMode.TELEOPH;
            TargetHeadingDeg = getGyroHeading();
            //TargetDistanceInchesH = 288;
            //DriveMotorEncoderReset();
        }

        double totalPower = leftPower - rightPower;

        RobotLog.aa(TAGChassis, "doTeleopH: leftPower=" + leftPower + " rightPower=" + rightPower);
        telemetry.log().add("totalPower =" + totalPower);
        TargetMotorPowerH = totalPower;

    }

    //*********************************************************************************************
    public void doTeleop(double LDMpower, double RDMpower) {
        ChassisMode_Current = ChassisMode.TELEOP;

        double lPower = capMotorPower(LDMpower);
        double rPower = capMotorPower(RDMpower);
        LDM1.setPower(lPower);
        RDM1.setPower(rPower);
        LDM2.setPower(lPower);
        RDM2.setPower(rPower);
        RobotLog.aa(TAGChassis, "doTeleop: lPower=" + lPower + " rPower=" + rPower);
    }

    //*********************************************************************************************
    public void doStopH() {
        HDM1.setPower(0);
        TargetDistanceInchesH = 0;
        TargetMotorPowerH = 0;
        ChassisMode_Current = ChassisMode.STOP;
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
        doStopH();
        ChassisMode_Current = ChassisMode.STOP;

    }

    //*********************************************************************************************
    private void doDrive() {
        // insert adjustments to drive straight using gyro
        RobotLog.aa(TAGChassis, "curr heading: " + gyroNormalize(getGyroHeading()));
        RobotLog.aa(TAGChassis, "Target: " + TargetHeadingDeg);

        double delta = -deltaHeading(gyroNormalize(getGyroHeading()), TargetHeadingDeg);
        double leftPower = TargetMotorPowerLeft - (delta * chassis_KPGyroStraight);
        double rightPower = TargetMotorPowerRight + (delta * chassis_KPGyroStraight);

        RobotLog.aa(TAGChassis, "delta: " + delta);
        RobotLog.aa(TAGChassis, "leftpower: " + leftPower + " right " + rightPower);

        leftPower = capMotorPower(leftPower);
        rightPower = capMotorPower(rightPower);

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
    private void doSteerH() {
        // insert adjustments to drive straight using gyro for both TeleOp and Auto
        RobotLog.aa(TAGChassis, "curr heading: " + gyroNormalize(getGyroHeading()));
        RobotLog.aa(TAGChassis, "Target: " + TargetHeadingDeg);

        double delta = -deltaHeading(gyroNormalize(getGyroHeading()), TargetHeadingDeg);
        double steeringPower = (delta * chassis_KPGyroStraightH);

        RobotLog.aa(TAGChassis, "delta: " + delta);
        RobotLog.aa(TAGChassis, "steeringpower: " + steeringPower);

        LDM1.setPower(capMotorPower((+1.0 * TargetMotorPowerH) - steeringPower));
        LDM2.setPower(capMotorPower((-1.0 * TargetMotorPowerH) - steeringPower));

        RDM1.setPower(capMotorPower((-1.0 * TargetMotorPowerH) + steeringPower));
        RDM2.setPower(capMotorPower((+1.0 * TargetMotorPowerH) + steeringPower));

        HDM1.setPower(TargetMotorPowerH);
    }

    //*********************************************************************************************
    private void doDriveH() {
        doSteerH();
        //check if we've gone far enough, if so stop and mark task complete
        double inchesTraveled = Math.abs(getEncoderInchesH());
        if (inchesTraveled >= Math.abs(TargetDistanceInches - Chassis_DriveTolerInches)) {
            RobotLog.aa(TAGChassis, "Target Inches: " + Math.abs(TargetDistanceInches - Chassis_DriveTolerInches));
            RobotLog.aa(TAGChassis, "Inches Traveled: " + inchesTraveled);
            cmdComplete = true;
            doStop();
        }
    }    // doDriveH()

    //*********************************************************************************************
    private double capMotorPower(double motorPower) {
        // cap the motor power between -1 and +1.
        double retValue = motorPower;

        if (motorPower > maxPower) {
            retValue = maxPower;
        }
        if (motorPower < -maxPower) {
            retValue = -maxPower;
        }
        return retValue;
    }

    //*********************************************************************************************
    private void doTurn() {
        /*
         *   executes the logic of a single scan of turning the robot to a new heading
         */

        int currHeading = gyroNormalize(getGyroHeading());
        RobotLog.aa(TAGChassis, "Turn currHeading: " + currHeading + " target: " + TargetHeadingDeg);
        RobotLog.aa(TAGChassis, "Runtime: " + runtime.seconds());

        if (gyroInTol(currHeading, TargetHeadingDeg, chassis_GyroHeadingTol)) {
            RobotLog.aa(TAGChassis, "Complete currHeading: " + currHeading);
            //We are there stop
            cmdComplete = true;
            ChassisMode_Current = ChassisMode.STOP;
            doStop();
        }
    }

    //*********************************************************************************************
    public int deltaHeading(int currHeading, int targetHeading) {
        int returnValue = 0;
        if (currHeading >= 0 && targetHeading >= 0) {
            returnValue = targetHeading - currHeading;
        } else if (currHeading >= 0 && targetHeading <= 0) {
            returnValue = targetHeading + currHeading;
        } else if (currHeading <= 0 && targetHeading >= 0) {
            returnValue = -1 * (targetHeading + currHeading);
        } else if (currHeading <= 0 && targetHeading <= 0) {
            returnValue = (targetHeading - currHeading);
        }

        return returnValue;
    }

    //*********************************************************************************************
    // create method to return complete bolean
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
        doDrive();
    }

    //*********************************************************************************************
    // create command to be called from auton to drive straight
    public void cmdDriveH(double DrivePower, int headingDeg, double targetDistanceInchesH) {

        cmdComplete = false;
        if (ChassisMode_Current != ChassisMode.DRIVEH) {
            ChassisMode_Current = ChassisMode.DRIVEH;
        }
        TargetHeadingDeg = headingDeg;
        RobotLog.aa(TAGChassis, "cmdDriveH: " + DrivePower);
        TargetMotorPowerH = DrivePower;
        TargetDistanceInchesH = targetDistanceInchesH;
        TargetDistanceInches = 0;
        DriveMotorEncoderReset();
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
    public double getEncoderInchesH() {
        // create method to get inches driven in auton
        // read the values from the encoders
        // LDM1.getCurrentPosition()
        // convert that to inches
        // by dividing by ticksPerInch

        // average the distance traveled by each wheel to determine the distance travled by the
        // robot


        int totalitics = Math.abs(HDM1.getCurrentPosition());
        double averagetics = totalitics / 1;
        double inches = averagetics / ticsPerInchH;
        return inches;

    }
    //*********************************************************************************************
    // create command to be called from auton to reset encoders at end of auton

    public int getGyroHeading() {
        //Read the gyro and return its reading in degrees

        //this should pull heading angle from onboard IMU Gyro
        //https://ftcforum.usfirst.org/forum/ftc-technology/49904-help-with-rev-expansion-hub-integrated-gyro
        //hint: composeTelemetry() also captures this information below.

        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        //return formatAngle(angles.angleUnit, angles.firstAngle);
        return -1 * (int) (angles.firstAngle);
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
        //  intakeArm.stop();
        // dumpBox.stop();
        //scannerArms.stop();
    }

    //*********************************************************************************************
    public void setMaxPower(double newMax) {

        maxPower = newMax;

    }

    //*********************************************************************************************
    public int gyroNormalize(int heading) {
        // takes the full turns out of heading
        // gives us values from 0 to 180 for the right side of the robot
        // and values from 0 to -179 degrees for the left side of the robot

        int degrees = heading % 360;

        if (degrees > 180) {
            degrees = degrees - 360;
        }

        if (degrees < -179) {
            degrees = degrees + 360;
        }

        return (degrees);
    }

    //*********************************************************************************************
    public boolean gyroInTol(int currHeading, int desiredHeading, int tol) {

        int upperTol = gyroNormalize(desiredHeading + tol);
        int lowerTol = gyroNormalize(desiredHeading - tol);
        int normalCurr = gyroNormalize(currHeading);

        float signumUpperTol = Math.signum(upperTol);
        float signumLowerTol = Math.signum(lowerTol);

        boolean retValue = false;
        // works for all positive numbers direction values
        if (signumUpperTol > 0 && signumLowerTol > 0) {
            if ((normalCurr >= lowerTol) && (normalCurr <= upperTol)) {
                retValue = true;
            }
        }

        // works for negative values
        else if (signumUpperTol < 0 && signumLowerTol < 0) {
            if ((normalCurr >= lowerTol) && (normalCurr <= upperTol)) {
                retValue = true;
            }
        }
        // mixed values -tol to + tol  This happens at 180 degrees
        else if ((signumUpperTol < 0) && (signumLowerTol > 0)) {
            // System.out.println("upperTol " + upperTol + " Current " +
            // normalCurr + " lowerTol " + lowerTol);
            if ((Math.abs(normalCurr) >= Math.abs(lowerTol)) &&
                    (Math.abs(normalCurr) >= Math.abs(upperTol))) {
                retValue = true;
            }

        }
        // mixed values -tol to + tol  This happens at 0 degrees
        else if ((signumUpperTol > 0) && (signumLowerTol < 0)) {
            // System.out.println("upperTol " + upperTol + " Current " +
            // normalCurr + " lowerTol " + lowerTol);
            if ((Math.abs(normalCurr) <= Math.abs(lowerTol)) &&
                    (Math.abs(normalCurr) <= Math.abs(upperTol))) {
                retValue = true;
            }

        }
        return (retValue);
    }  // end gyroInTol()

    //*********************************************************************************************
    void composeTelemetry() {

        // At the beginning of each telemetry update, grab a bunch of data
        // from the IMU that we will then display in separate lines.
        telemetry.addAction(new Runnable() {
            @Override
            public void run() {
                // Acquiring the angles is relatively expensive; we don't want
                // to do that in each of the three items that need that info, as that's
                // three times the necessary expense.
                angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            }
        });

        telemetry.addLine()
                .addData("status", new Func<String>() {
                    @Override
                    public String value() {
                        return imu.getSystemStatus().toShortString();
                    }
                })
                .addData("calib", new Func<String>() {
                    @Override
                    public String value() {
                        return imu.getCalibrationStatus().toString();
                    }
                });

        telemetry.addLine()
                .addData("heading", new Func<String>() {
                    @Override
                    public String value() {
                        return formatAngle(angles.angleUnit, angles.firstAngle);
                    }
                })
                .addData("roll", new Func<String>() {
                    @Override
                    public String value() {
                        return formatAngle(angles.angleUnit, angles.secondAngle);
                    }
                })
                .addData("pitch", new Func<String>() {
                    @Override
                    public String value() {
                        return formatAngle(angles.angleUnit, angles.thirdAngle);
                    }
                });
    }

    String formatAngle(AngleUnit angleUnit, double angle) {
        return formatDegrees(AngleUnit.DEGREES.fromUnit(angleUnit, angle));
    }

    //----------------------------------------------------------------------------------------------
    // Formatting
    //----------------------------------------------------------------------------------------------

    String formatDegrees(double degrees) {
        return String.format(Locale.getDefault(), "%.1f", AngleUnit.DEGREES.normalize(degrees));
    }

    //*********************************************************************************************
    public static enum PARENTMODE {
        PARENT_MODE_AUTO,
        PARENT_MODE_TELE
    }
}
