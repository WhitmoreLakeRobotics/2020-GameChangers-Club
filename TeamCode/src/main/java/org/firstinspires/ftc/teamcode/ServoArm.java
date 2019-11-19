package org.firstinspires.ftc.teamcode;

/* controls all actions one Grabber arm

 */


import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


//@TeleOp(name = "IntakeArm", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class ServoArm extends BaseHardware {

    public enum SERVO_ARM_STATES {
        COMMANDED_START,
        START,
        MOVING_START,
        COMMANDED_UP,
        MOVING_UP,
        UP,
        COMMANDED_DOWN,
        MOVING_DOWN,
        DOWN,
        UNKNOWN
    }

    private static final String TAGIntakeArm = "8492-ScannerArm";

    /* Declare OpMode members. */

    private SERVO_ARM_STATES servoArmState_current = SERVO_ARM_STATES.UNKNOWN;

    private ElapsedTime ScannerArmTimer = null;
    public int servoArmMoveTime_mS = 1000;


    // Define the hardware
    private Servo grabSvo = null;
    private double grabSvoPos_start = 0;
    private double grabSvoPos_up = 0;
    private double grabSvoPos_down = 0;


    public void setServo(Servo svro) {
        grabSvo = svro;
    }

    public void setPositions(double startPos, double upPos, double downPos) {
        grabSvoPos_start = startPos;
        grabSvoPos_up = upPos;
        grabSvoPos_down = downPos;
    }

    @Override
    public void init() {

        if (grabSvo == null) {
            telemetry.log().add("GrabSvoR is null...");

        }
        ScannerArmTimer = new ElapsedTime();
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
        servoArmState_current = SERVO_ARM_STATES.DOWN;
        ScannerArmTimer.reset();
        cmd_moveUp();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        switch (servoArmState_current) {

            // We have been commanded to move to the start pos... Set new position and start
            // timer
            case COMMANDED_START: {
                grabSvo.setPosition(grabSvoPos_start);
                servoArmState_current = SERVO_ARM_STATES.MOVING_START;
                ScannerArmTimer.reset();
                break;
            }

            // We are now moving... We will be in this state for a while based on time
            // When the time is up we are considered to be in position.
            case MOVING_START: {
                if (ScannerArmTimer.milliseconds() > servoArmMoveTime_mS) {
                    servoArmState_current = SERVO_ARM_STATES.START;
                }
                break;
            }

            // we are in position... Not much to do here
            case START: {
                servoArmState_current = SERVO_ARM_STATES.START;
                break;
            }


            // We have been commanded to move to the up pos... Set new position and start
            // timer
            case COMMANDED_UP: {
                grabSvo.setPosition(grabSvoPos_up);
                servoArmState_current = SERVO_ARM_STATES.MOVING_UP;
                break;
            }

            // We are now moving... We will be in this state for a while based on time
            // When the time is up we are considered to be in position.
            case MOVING_UP: {
                if (ScannerArmTimer.milliseconds() > servoArmMoveTime_mS) {
                    servoArmState_current = SERVO_ARM_STATES.UP;
                    ScannerArmTimer.reset();
                }
                break;
            }

            case UP: {
                servoArmState_current = SERVO_ARM_STATES.UP;
                break;
            }


            // We have been commanded to move to the down pos... Set new position and start
            // timer
            case COMMANDED_DOWN: {
                grabSvo.setPosition(grabSvoPos_down);
                servoArmState_current = SERVO_ARM_STATES.MOVING_DOWN;
                ScannerArmTimer.reset();
                break;
            }

            // We are now moving... We will be in this state for a while based on time
            // When the time is up we are considered to be in position.
            case MOVING_DOWN: {
                if (ScannerArmTimer.milliseconds() > servoArmMoveTime_mS) {
                    servoArmState_current = SERVO_ARM_STATES.DOWN;
                }
                break;
            }


            case DOWN: {
                servoArmState_current = SERVO_ARM_STATES.DOWN;
                break;
            }

            default: {
                break;
            }
        }
    }

    public boolean atDestination(SERVO_ARM_STATES test_state) {
        return (servoArmState_current == test_state);
    }


    public void cmd_moveDown() {
        servoArmState_current = SERVO_ARM_STATES.COMMANDED_DOWN;
    }


    public void cmd_moveUp() {
        servoArmState_current = SERVO_ARM_STATES.COMMANDED_UP;
    }


    public void cmd_moveStart() {
        servoArmState_current = SERVO_ARM_STATES.COMMANDED_START;
    }

    public boolean getIsDown() {
        return servoArmState_current == SERVO_ARM_STATES.DOWN;
    }

    public boolean getIsUp() {
        return servoArmState_current == SERVO_ARM_STATES.UP;
    }


    public boolean getIsStart() {
        return servoArmState_current == SERVO_ARM_STATES.START;
    }


    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {

    }


}
