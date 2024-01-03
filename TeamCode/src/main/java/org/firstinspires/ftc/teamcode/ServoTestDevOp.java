package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class ServoTestDevOp extends LinearOpMode {
    // Declare Motor Variables

    Servo servoTest;


    private void handleInput() {
        if(gamepad1.y) {
            // move to 0 degrees.
            servoTest.setPosition(0.5);
        } else if (gamepad1.x || gamepad1.b) {
            // move to 90 degrees.
            servoTest.setPosition(0.75);
        } else if (gamepad1.a) {
            // move to 180 degrees.
            servoTest.setPosition(0.85);
        }
    }

    private void setUp() {
        servoTest = hardwareMap.get(Servo.class, "Claw");
    }


    // ENTRY POINT
    @Override
    public void runOpMode() {

        setUp(); // Initialize variables.

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        while (opModeIsActive()) {

            handleInput();
            telemetry.addData("Servo Position", servoTest.getPosition());
            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}
