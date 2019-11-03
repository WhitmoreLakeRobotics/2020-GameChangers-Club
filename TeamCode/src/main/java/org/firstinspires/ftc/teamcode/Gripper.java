package org.firstinspires.ftc.teamcode;

/*
controls all actions the gripper servo
*/


import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


//@TeleOp(name = "IntakeArm", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class Gripper extends BaseHardware {

    private static final String TAGIntakeArm = "8492-GripperServo";

    /* Declare OpMode members. */

    private GRIPPER_STATES GripperState_desired = GRIPPER_STATES.UNKNOWN;
    private GRIPPER_STATES GripperState_current = GRIPPER_STATES.UNKNOWN;

    private ElapsedTime GripperTimer = null;
    private int mSecGripperMoveTime = 1000;

    // Define the hardware
    private Servo gripperSvo = null;

    // set neutral positions for first time running on the robot
    private double gripperSvoPos_open = 0.4;
    private double gripperSvoPos_close = 0.6;
    private double gripperSvoPos_start = gripperSvoPos_open;


    @Override
    public void init() {
        gripperSvo = hardwareMap.servo.get("GripSvo1");
        if (gripperSvo == null) {
            telemetry.log().add("GripSvo1 is null...");
        }
        GripperTimer = new ElapsedTime();
        telemetry.addData("GRIPPER", "Initialized");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {

    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        GripperState_desired = GRIPPER_STATES.OPEN;
        GripperState_current = GRIPPER_STATES.CLOSED;
        GripperTimer.reset();
        cmd_open();

    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        switch (GripperState_current) {

            // The first state in opening process, this should only be for 1 scan of the loop
            case COMMANDED_OPEN: {
                // calls out to the hardware are expensive for both time and CPU of the phone
                // only do it as needed. for servos this is once.
                gripperSvo.setPosition(gripperSvoPos_open);
                GripperTimer.reset();
                GripperState_current = GRIPPER_STATES.OPENING;
                break;
            }

            // The second state in the opening process, this should be for about 1 second of time
            case OPENING: {
                if (GripperTimer.milliseconds() > mSecGripperMoveTime) {
                    GripperState_current = GRIPPER_STATES.OPEN;
                }
                break;
            }
            // We are open and both current and desired are the same value.
            case OPEN: {
                GripperState_current = GRIPPER_STATES.OPEN;
                GripperState_desired = GRIPPER_STATES.OPEN;
                break;
            }


            // The first state in closing process, this should only be for 1 scan of the loop
            case COMMANDED_CLOSED: {
                // calls out to the hardware are expensive for both time and CPU of the phone
                // only do it as needed. for servos this is once.
                gripperSvo.setPosition(gripperSvoPos_close);
                GripperTimer.reset();
                GripperState_current = GRIPPER_STATES.CLOSING;
                break;
            }

            // The second state in the closing process, this should be for about 1 second of time
            case CLOSING: {
                if (GripperTimer.milliseconds() > mSecGripperMoveTime) {
                    GripperState_current = GRIPPER_STATES.CLOSED;
                }
                break;
            }

            // We are closed and both current and desired are the same value.
            case CLOSED: {
                GripperState_current = GRIPPER_STATES.CLOSED;
                GripperState_desired = GRIPPER_STATES.CLOSED;
                break;
            }

            default: {
                break;
            }
        }
    }

    public boolean atDestination(GRIPPER_STATES test_state) {
        return (GripperState_current == test_state);
    }


    public void cmd_open() {

        if (GripperState_current == GRIPPER_STATES.CLOSED) {
            GripperState_current = GRIPPER_STATES.COMMANDED_OPEN;
            GripperState_desired = GRIPPER_STATES.OPEN;
        }
    }


    public void cmd_close() {
        // only issue the command when we are open... prevents extra trips to the hardware
        if (GripperState_current == GRIPPER_STATES.OPEN) {
            GripperState_desired = GRIPPER_STATES.CLOSED;
            GripperState_current = GRIPPER_STATES.COMMANDED_CLOSED;
        }
    }


    public boolean getIsOpen() {
        return GripperState_current == GRIPPER_STATES.OPEN;
    }

    public boolean getIsClosed() {
        return GripperState_current == GRIPPER_STATES.CLOSED;
    }


    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {

    }

    public static enum GRIPPER_STATES {
        COMMANDED_OPEN,
        OPENING,
        OPEN,
        COMMANDED_CLOSED,
        CLOSING,
        CLOSED,
        UNKNOWN
    }
}
