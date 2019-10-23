package org.firstinspires.ftc.teamcode;

/* controls the combined  actions the Scanner Arms

 */


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


//@TeleOp(name = "GrabberArms", group = "CHASSIS")  // @Autonomous(...) is the other common choice

public class GrabberArms extends BaseHardware {
    private static final String TAGIntakeArm = "8492-GrabberArms";
    private GrabberArm ArmRight = new GrabberArm();
    private GrabberArm ArmLeft = new GrabberArm();

    private double grabSvoRPos_start = .86;
    private double grabSvoRPos_up = .73;
    private double grabSvoRPos_down = .25;

    private double grabSvoLPos_start = .15;
    private double grabSvoLPos_up = .27;
    private double grabSvoLPos_down = .76;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {

        telemetry.addData("GrabberArms", "Initialized");

        /* eg: Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step (using the FTC Robot Controller app on the phone).
         */
        ArmLeft.setServo(hardwareMap.servo.get("GrabSvoL"));
        ArmLeft.setPositions(grabSvoLPos_start, grabSvoLPos_up, grabSvoLPos_down);
        ArmLeft.init();

        ArmRight.setServo(hardwareMap.servo.get("GrabSvoR"));
        ArmRight.setPositions(grabSvoRPos_start, grabSvoRPos_up, grabSvoRPos_down);
        ArmRight.init();

    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        ArmLeft.init_loop();
        ArmRight.init_loop();
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        ArmLeft.start();
        ArmRight.start();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        ArmRight.loop();
        ArmLeft.loop();
    }

    public void cmdMoveAllDown() {
        ArmRight.cmd_moveDown();
        ArmLeft.cmd_moveDown();
    }

    public void cmdMoveAllUp() {
        ArmRight.cmd_moveUp();
        ArmLeft.cmd_moveUp();
    }

    public void cmdMoveUpLeft() {
        ArmLeft.cmd_moveUp();
    }

    public void cmdMoveUpRight() {
        ArmRight.cmd_moveUp();
    }

    public void cmdMoveDownLeft() {
        ArmLeft.cmd_moveDown();
    }

    public void cmdMoveDownRight() {
        ArmRight.cmd_moveDown();
    }

    public void cmdMoveStartLeft() {
        ArmLeft.cmd_moveStart();
    }

    public void cmdMoveStartRight() {
        ArmRight.cmd_moveStart();
    }

    public boolean getIsDown() {
        return (ArmLeft.getIsDown() && ArmRight.getIsDown());
    }

    public boolean getIsDownRight() {
        return ArmRight.getIsDown();
    }

    public boolean getIsDownLeft() {
        return ArmLeft.getIsDown();
    }

    public boolean getIsUp() {
        return (ArmLeft.getIsUp() && ArmRight.getIsUp());
    }

    public boolean getIsUpLeft() {
        return (ArmLeft.getIsUp());
    }

    public boolean getIsUpRight() {
        return (ArmRight.getIsUp());
    }


    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        ArmRight.stop();
        ArmLeft.stop();
    }

}
