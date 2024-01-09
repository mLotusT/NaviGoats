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

    private DcMotor ArmMotor1;
    private DcMotor ArmMotor2;

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

    private double abs(double a){
        if (a < 0){ // if negative
            return a * -1;
        }
        return a;
    }

    private void setUp() {
        // Assign Motors to corresponding names in drivers hub
        RightMotor = hardwareMap.get(DcMotor.class, "Right");
        LeftMotor = hardwareMap.get(DcMotor.class, "Left");
        ClawServo = hardwareMap.get(Servo.class, "Claw");
        ArmMotor1 = hardwareMap.get(DcMotor.class, "Arm1");
        ArmMotor2 = hardwareMap.get(DcMotor.class, "Arm2");
    }

    private void drive() {

        double y = this.gamepad1.left_stick_y;
        double x = this.gamepad1.left_stick_x;


        //MotorCenterPower = this.gamepad1.left_stick_x; // change this to the right gampad later
        // Differential Steering (Since we only got 2 motors)
        RightMotorPower = max(min(x + y, -1.0), 1.0);
        LeftMotorPower = max(min(x - y, -1.0), 1.0);

        RightMotor.setPower(RightMotorPower);
        LeftMotor.setPower(LeftMotorPower);
    }

    private void claw(){
        double y = gamepad1.right_stick_y;

        ClawServo.setPosition(1-abs(y));
    }

    private void arm(){
        double sensitivityMultiplier = 1.0;
        if (gamepad1.a){ // hold a
            sensitivityMultiplier = 0.75;
        }

        if (gamepad1.left_bumper){
            ArmMotor2.setPower(0.5 * sensitivityMultiplier);
        }
        else{
            ArmMotor2.setPower((-0.5 * sensitivityMultiplier)*gamepad1.left_trigger);

        }

        if (gamepad1.right_bumper){
            ArmMotor1.setPower(-0.65 * sensitivityMultiplier);
        }
        else{
            ArmMotor1.setPower((0.65*sensitivityMultiplier)*gamepad1.right_trigger);

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
            arm();
            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}
