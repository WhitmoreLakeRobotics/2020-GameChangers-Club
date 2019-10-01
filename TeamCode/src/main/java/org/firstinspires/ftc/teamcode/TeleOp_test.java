/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

//package org.firstinspires.ftc.robotcontroller.external.samples;
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.RobotLog;

//@TeleOp_testp(name = "Teleop-TestChassis", group = "TeleOp_test")
@TeleOp(name = "Teleop-TestChassis", group = "TeleOp")
//@Disabled
public class TeleOp_test extends OpMode {
    private static final String TAGTeleop = "8492-Teleop";
    Chassis_Test2 RBTChassis = new Chassis_Test2();
    // Declare OpMode members.
    boolean gamepad2_a_pressed = false;
    boolean gamepad2_b_pressed = false;
    boolean gamepad2_x_pressed = false;
    boolean gamepad2_y_pressed = false;
    private double LeftMotorPower = 0;
    private double RightMotorPower = 0;

    private double powerNormal = .5;


    private double powerMax = 8;
    //*********************************************************************************************
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("TeleOp_test", "Initialized");
        RBTChassis.setParentMode(Chassis_Test2.PARENTMODE.PARENT_MODE_TELE);
        RBTChassis.hardwareMap = hardwareMap;
        RBTChassis.telemetry = telemetry;
        RBTChassis.setMaxPower(powerNormal);
        RBTChassis.init();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).


        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery


    }
    //*********************************************************************************************
    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        RBTChassis.init_loop();
    }

    //*********************************************************************************************
    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        Runtime.getRuntime();
        RBTChassis.start();
        RBTChassis.setMotorMode_RUN_WITHOUT_ENCODER();
    }

    //*********************************************************************************************
    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        RBTChassis.loop();


        RobotLog.aa(TAGTeleop, "gamepad1 " + RightMotorPower);
        RobotLog.aa(TAGTeleop, "trigers " + gamepad1.left_trigger);

        // if the driver has any triggers pulled this means H drive only drive the H wheels
        // as straightly as possible
        if (gamepad1.left_trigger > 0 || gamepad1.right_trigger > 0) {
            RBTChassis.doTeleopH(joystickMath(gamepad1.left_trigger),
                    joystickMath(gamepad1.right_trigger));
        }
        // stop the H drive and give joystick values to the other wheels.
        else {
            RBTChassis.doStopH();
            RBTChassis.doTeleop(joystickMath(-gamepad1.left_stick_y), joystickMath(-gamepad1.right_stick_y));
        }

        RBTChassis.subExtender.cmd_stickControl(gamepad2.right_stick_y);

        if (gamepad2.a) {
            RBTChassis.subExtender.cmd_MoveToStart();
        }
        if (gamepad2.b) {
            RBTChassis.subExtender.cmd_MoveToPos1();
        }
        if (gamepad2.x) {
            RBTChassis.subExtender.cmd_MoveToPos3();
        }
        if (gamepad2.y) {
            RBTChassis.subExtender.cmd_MoveToPos2();
        }

        // Bumpers high and lower Powers for the wheels
        if (gamepad1.left_bumper) {
            RBTChassis.setMaxPower(powerMax);
        }

        if (gamepad1.right_bumper) {
            RBTChassis.setMaxPower(powerNormal);
        }
    }

    //*********************************************************************************************
    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {

        RBTChassis.stop();

    }

    //*********************************************************************************************
    public double joystickMath(double joyValue) {
        int sign = 1;
        double retValue = 0;
        if (joyValue < 0) {
            sign = -1;
        }
        return Math.abs(Math.pow(joyValue, 2)) * sign;
    }
    //*********************************************************************************************
}

