package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
// GENERAL TESTING DEV OP
public class TestDriveOp extends LinearOpMode {
    // Declare Motor Variables
    private DcMotor RightMotor;
    private DcMotor LeftMotor;

    private Servo ClawServo;
    private boolean IsClawOpen = false;

    private double RightMotorPower;
    private double LeftMotorPower;


    // Utility functions
    private double min(double a, double b) { // return b if a < b. Otherwise return a.
        if (a < b) {
            return b;
        }
        return a;
    }

    private double max(double a, double b) { // return b if a > b. Otherwise return a.
        if (a > b) {
            return b;
        }
        return a;
    }

    private void handleInput() {
        double y = this.gamepad1.left_stick_y;
        double x = this.gamepad1.left_stick_x;


        //MotorCenterPower = this.gamepad1.left_stick_x; // change this to the right gampad later
        // Differential Steering (Since we only got 2 motors)
        RightMotorPower = max(min(x + y, -1.0), 1.0);
        LeftMotorPower = max(min(x - y, -1.0), 1.0);

    }

    private void setUp() {
        // Assign Motors to corresponding names in drivers hub
        RightMotor = hardwareMap.get(DcMotor.class, "Right");
        LeftMotor = hardwareMap.get(DcMotor.class, "Left");
        ClawServo = hardwareMap.get(Servo.class, "Claw");
    }

    private void drive() {
        handleInput();
        RightMotor.setPower(RightMotorPower); //Needs to be negative because rotates opposite side
        LeftMotor.setPower(LeftMotorPower);
    }

    private void claw(){
        if(gamepad1.x && IsClawOpen) {
            // move to 0 degrees.
            servoTest.setPosition(0);
            IsClawOpen = false;
        } else if (gamepad1.x) {
            // move to 90 degrees.
            servoTest.setPosition(0.5);
            IsClawOpen = true;
        }
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
            claw();
            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}