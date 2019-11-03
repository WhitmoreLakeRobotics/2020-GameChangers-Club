package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;


public class HDrive extends BaseHardware {

    //for tuning this is the tolerance of turn in degrees
    public static final int chassis_GyroHeadingTol = 3;

    public enum HDriveMode {
        Stop,
        Drive,
        Unknown,
        TeleOp,
        Idle
    }

    public static final int TICKS_PER_REV = Settings.REV_CORE_HEX_MOTOR_TICKS_PER_REV;
    public static final int HDM2_UP_POS = 0;

    public static final int HDM2_DOWN_POS = (int) (TICKS_PER_REV / 2);
    public static final int HDM2_TOL = 21;

    public static final double wheelDistPerRev = 3.0 * 3.14159;
    public static final double gearRatio = 80 / 80;
    public static final double ticsPerInch = TICKS_PER_REV / wheelDistPerRev / gearRatio;
    public static final double Chassis_DriveTolerInches = .25;
    // naj set constant for Gyro KP for driving straight
    public static final double chassis_KPGyroStraight = 0.02;
    private static final String TAGHDrive = "8492-HDrive";

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private int initCounter = 0;
    //current mode of operation for Chassis
    private HDriveMode HdriveMode_Current = HDriveMode.Unknown;
    private boolean cmdComplete = true;
    private int cmdStartTime_mS = 0;
    private Settings.PARENTMODE parentMode_Current = null;
    private Settings.CHASSIS_TYPE chassistype_Current = null;
    private DcMotor HDM1 = null;
    private DcMotor HDM2 = null;
    private double TargetMotorPowerH = 0;
    private double PrevMotorPowerH = 0;
    private double TargetDistanceInchesH = 0;


    private static double HLiftPower = 1.0;

    private double maxPower = 1.0;

    //*********************************************************************************************
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        HDM1 = hardwareMap.dcMotor.get("HDM1");
        if (HDM1 == null) {
            telemetry.log().add("HDM1 is null...");
        }

        HDM2 = hardwareMap.dcMotor.get("HDM2");
        if (HDM2 == null) {
            telemetry.log().add("HDM2 is null...");
        }

        HDM1.setDirection(DcMotor.Direction.FORWARD);
        HDM1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        HDM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        HDM1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);





        runtime.reset();
        HdriveMode_Current = HDriveMode.Idle;
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

    public void setChassisType(Settings.CHASSIS_TYPE ct) {
        chassistype_Current = ct;
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
                zeroHDrive();

                break;

            case PARENT_MODE_TELE:
                break;

            default:
                break;
        }
    }

    //*********************************************************************************************
    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        switch (HdriveMode_Current) {
            case Stop:
                doStop();
                break;

            case Drive:
                doDrive();
                break;

            case TeleOp:
                doDrive();
                break;

            default:
                break;
        }
    }

    //*********************************************************************************************
    public void cmdTeleop(double leftPower, double rightPower) {

        HdriveMode_Current = HDriveMode.TeleOp;
        double totalPower = leftPower - rightPower;
        totalPower = CommonLogic.CapMotorPower(totalPower, -1.0, 1.0);
        RobotLog.aa(TAGHDrive, "doTeleop: Power=" + totalPower);
        TargetMotorPowerH = totalPower * 1.0;
        telemetry.log().add(String.format("TargetMotorPowerH= %.2f", TargetMotorPowerH));

    }
    //*********************************************************************************************

    public void doStop() {

        RobotLog.aa(TAGHDrive, "doStop:");
        if (HdriveMode_Current == HDriveMode.Stop) {
            TargetDistanceInchesH = 0;
            TargetMotorPowerH = 0;

            // If we were driving then raise the wheels
            HDM2.setTargetPosition(HDM2_UP_POS);
            HDM2.setPower(HLiftPower);

            HDM1.setPower(TargetMotorPowerH);
            PrevMotorPowerH = TargetMotorPowerH;
            HdriveMode_Current = HDriveMode.Idle;

        }
    }


    //*********************************************************************************************

    private void doDrive() {

        // if we were stopped then lower the drive wheels
        if (TargetMotorPowerH != 0) {
            HDM2.setPower(HLiftPower);
            HDM2.setTargetPosition(HDM2_DOWN_POS);
        } else {
            HDM2.setPower(HLiftPower);
            HDM2.setTargetPosition(HDM2_UP_POS);
        }

        if (CommonLogic.inRange(HDM2.getCurrentPosition(), HDM2_DOWN_POS, HDM2_TOL)) {
            // If this is just a change of speed then change the speed
            if (TargetMotorPowerH != PrevMotorPowerH) {
                HDM1.setPower(TargetMotorPowerH);
                PrevMotorPowerH = TargetMotorPowerH;
            }

            //check if we've gone far enough, if so stop and mark task complete
            if (HdriveMode_Current == HDriveMode.Drive) {
                double inchesTraveled = Math.abs(getEncoderInches());
                if (inchesTraveled >= Math.abs(TargetDistanceInchesH - Chassis_DriveTolerInches)) {
                    RobotLog.aa(TAGHDrive, "Target Inches: " + Math.abs(TargetDistanceInchesH - Chassis_DriveTolerInches));
                    RobotLog.aa(TAGHDrive, "Inches Traveled: " + inchesTraveled);
                    cmdComplete = true;
                    doStop();
                }
            }
        }
    }    // doDrive()

    //*********************************************************************************************
    // create method to return complete bolean
    public boolean getcmdComplete() {

        return (cmdComplete);
    }

    //*********************************************************************************************
    // create command to be called from auton to drive straight

    public void cmdDrive(double DrivePower, double targetDistanceInches) {
        cmdComplete = false;
        if (HdriveMode_Current != HDriveMode.Drive) {
            HdriveMode_Current = HDriveMode.Drive;
        }
        RobotLog.aa(TAGHDrive, "cmdDrive: " + DrivePower);
        TargetMotorPowerH = DrivePower;
        TargetDistanceInchesH = targetDistanceInches;
    }

    //*********************************************************************************************

    public void cmdStop() {
        //TargetMotorPowerH = 0;
        HdriveMode_Current = HDriveMode.Stop;
    }

    //*********************************************************************************************
    public double getEncoderInches() {
        // create method to get inches driven in auton
        return Math.abs(HDM1.getCurrentPosition()) / ticsPerInch;
    }

    //*********************************************************************************************
    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        HDM1.setPower(0);
        HdriveMode_Current = HDriveMode.Stop;
        HDM2.setPower(0);
    }

    //*********************************************************************************************
    public void setMaxPower(double newMax) {
        maxPower = Math.abs(newMax);
    }

    //*********************************************************************************************

    private void zeroHDrive () {

        // only reset things if starting in auton... Never reset them in TeleOp
        /*if (parentMode_Current == Settings.PARENTMODE.PARENT_MODE_TELE) {
            // This motor performs the lift of the HDrive
            HDM2.setDirection(DcMotor.Direction.FORWARD);
            HDM2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            HDM2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            HDM2.setTargetPosition(HDM2_UP_POS);
            HDM2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }

         */
        if (parentMode_Current == Settings.PARENTMODE.PARENT_MODE_AUTO  &&
                chassistype_Current == Settings.CHASSIS_TYPE.CHASSIS_COMPETITION) {

        }

    }
}
