package org.firstinspires.ftc.teamcode;

/* controls all actions one Grabber arm

 */


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


//@TeleOp(name = "IntakeArm", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class GrabberArm extends BaseHardware {

    private static final String TAGIntakeArm = "8492-ScannerArm";

    /* Declare OpMode members. */

    private GRABBER_ARM_STATES GrabberArmState_desired = GRABBER_ARM_STATES.UNKNOWN;
    private GRABBER_ARM_STATES GrabberArmState_current = GRABBER_ARM_STATES.UNKNOWN;

    private ElapsedTime ScannerArmTimer = null;
    private int ScannerArmMoveTime = 1250;

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
        GrabberArmState_desired = GRABBER_ARM_STATES.UP;
        GrabberArmState_current = GRABBER_ARM_STATES.UNKNOWN;
        ScannerArmTimer.reset();
        cmd_moveUp();

    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        if (GrabberArmState_current != GrabberArmState_desired) {

            switch (GrabberArmState_desired) {
                case MOVING_DOWN: {
                    GrabberArmState_current = GRABBER_ARM_STATES.MOVING_DOWN;
                    GrabberArmState_desired = GRABBER_ARM_STATES.DOWN;
                    ScannerArmTimer.reset();
                    grabSvo.setPosition(grabSvoPos_down);
                    break;
                }

                case MOVING_START: {
                    GrabberArmState_current = GRABBER_ARM_STATES.MOVING_DOWN;
                    GrabberArmState_desired = GRABBER_ARM_STATES.START;
                    ScannerArmTimer.reset();
                    grabSvo.setPosition(grabSvoPos_start);
                    break;
                }

                case MOVING_UP: {
                    GrabberArmState_current = GRABBER_ARM_STATES.MOVING_UP;
                    GrabberArmState_desired = GRABBER_ARM_STATES.UP;
                    ScannerArmTimer.reset();
                    grabSvo.setPosition(grabSvoPos_up);
                    break;
                }

                case UP: {
                    if (ScannerArmTimer.milliseconds() > ScannerArmMoveTime) {
                        GrabberArmState_current = GRABBER_ARM_STATES.UP;
                    }
                    break;
                }
                case DOWN: {
                    if (ScannerArmTimer.milliseconds() > ScannerArmMoveTime) {
                        GrabberArmState_current = GRABBER_ARM_STATES.DOWN;
                    }
                    break;
                }

                case START: {
                    if (ScannerArmTimer.milliseconds() > ScannerArmMoveTime) {
                        GrabberArmState_current = GRABBER_ARM_STATES.START;
                        //GrabberArmState_desired = GRABBER_ARM_STATES.MOVING_UP;
                    }
                    break;
                }

                default: {
                    break;
                }
            }
        }
    }

    public boolean atDestination(GRABBER_ARM_STATES test_state) {
        return (GrabberArmState_current == test_state);
    }


    public void cmd_moveDown() {
        GrabberArmState_desired = GRABBER_ARM_STATES.MOVING_DOWN;
    }


    public void cmd_moveUp() {
        GrabberArmState_desired = GRABBER_ARM_STATES.MOVING_UP;
    }



    public void cmd_moveStart() {
        GrabberArmState_desired = GRABBER_ARM_STATES.MOVING_START;
    }

    public boolean getIsDown() {
        return GrabberArmState_current == GRABBER_ARM_STATES.DOWN;
    }

    public boolean getIsUp() {
        return GrabberArmState_current == GRABBER_ARM_STATES.UP;
    }


    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {

    }

    public static enum GRABBER_ARM_STATES {
        START,
        MOVING_START,
        UP,
        MOVING_DOWN,
        DOWN,
        MOVING_UP,
        UNKNOWN
    }
}
