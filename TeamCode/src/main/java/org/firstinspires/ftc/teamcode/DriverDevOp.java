// DriverDevOp includes all the code for manual control by a driver.
// Button Mapping:
//        Left Bumper -> Move small arm up
//        Left Trigger -> Move small arm down
//        Right Bumper -> Move big arm up
//        Right Trigger -> Move big arm down
//        A -> Enable precision arm movement while holding
//        Left Joystick (Directional) -> Moving the robot
//        Right Joystick (Up/Down) -> Opens claw
//        Right Joystick (No Movement) -> Closes claw

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class DriverDevOp extends LinearOpMode {
    // Declare Motor Variables
    private DcMotor RightMotor;
    private DcMotor LeftMotor;

    private DcMotor ArmMotor1;
    private DcMotor ArmMotor2;

    private Servo ClawServo;
    private boolean IsClawOpen = false;

    private double RightMotorPower;
    private double LeftMotorPower;

    // Utility functions
    // Makes it so values cannot go below a value, a
    private double min(double a, double b) { // return b if a < b. Otherwise return a.
        if (a < b) {
            return b;
        }
        return a;
    }

    // Makes it so values cannot go above a value, a
    private double max(double a, double b) { // return b if a > b. Otherwise return a.
        if (a > b) {
            return b;
        }
        return a;
    }

    // Removes negative sign
    private double abs(double a){
        if (a < 0){ // if negative
            return a * -1;
        }
        return a;
    }

    // Assign Motors to corresponding names in driver's hub
    private void setup() {
        RightMotor = hardwareMap.get(DcMotor.class, "Right");
        LeftMotor = hardwareMap.get(DcMotor.class, "Left");
        ClawServo = hardwareMap.get(Servo.class, "Claw");
        ArmMotor1 = hardwareMap.get(DcMotor.class, "Arm1");
        ArmMotor2 = hardwareMap.get(DcMotor.class, "Arm2");
    }

    // Maps left joy stick to moving the robot
    private void drive() {
        double y = this.gamepad1.left_stick_y;
        double x = this.gamepad1.left_stick_x;

        // Differential Steering (Since we only got 2 motors)
        RightMotorPower = max(min(x + y, -1.0), 1.0);
        LeftMotorPower = max(min(x - y, -1.0), 1.0);

        RightMotor.setPower(RightMotorPower);
        LeftMotor.setPower(LeftMotorPower);
    }

    // Maps right joy stick for claw control
    // Up/Down movement opens claw
    // No movement closes claw
    private void claw(){
        double y = gamepad1.right_stick_y;

        ClawServo.setPosition(1-abs(y));
    }

    // Maps bumper controls to arm
    private void arm(){
        double sensitivityMultiplier = 1.0;

        // While holding a, arm controls get more precise
        if (gamepad1.a) { // hold a
            sensitivityMultiplier = 0.75;
        }

        // Maps left bumper into moving small arm up
        if (gamepad1.left_bumper) {
            ArmMotor2.setPower(0.5 * sensitivityMultiplier);
        }

        // Maps left trigger into moving small arm down
        else {
            ArmMotor2.setPower((-0.5 * sensitivityMultiplier)*gamepad1.left_trigger);
        }

        // Maps right bumper into moving big arm up
        if (gamepad1.right_bumper){
            ArmMotor1.setPower(-0.65 * sensitivityMultiplier);
        }

        // Maps right bumper into moving big arm down
        else {
            ArmMotor1.setPower((0.65*sensitivityMultiplier)*gamepad1.right_trigger);
        }
    }

    // ENTRY POINT
    @Override
    public void runOpMode() {

        setup(); // Initialize variables.

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        while (opModeIsActive()) {
            drive();
            claw();
            arm();
            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}
