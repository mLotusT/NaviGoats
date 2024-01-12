package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

// FULL POWER TO BOTH MOTORS TO DETERMINE INBALANCE OR NOT
@TeleOp
@Disabled
public class SimpleMotorTestDevOp extends LinearOpMode {
    // Declare Motor Variables
    private DcMotor RightMotor;
    private DcMotor LeftMotor;

    private void setUp() {
        // Assign Motors to corresponding names in drivers hub
        RightMotor = hardwareMap.get(DcMotor.class, "Right");
        LeftMotor = hardwareMap.get(DcMotor.class, "Left");

    }

    private void drive() {
        RightMotor.setPower(1.0);
        LeftMotor.setPower(-1.0);
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
            drive();

            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}
