package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class TestAutoDevOp extends LinearOpMode {
    // Declare Motor Variables
    private DcMotor RightMotor;
    private DcMotor LeftMotor;
    private DcMotor ArmMotor1;
    private DcMotor ArmMotor2;
    private Servo ClawServo;
    private double RightMotorPower;
    private double LeftMotorPower;

    private enum ArmStatus {NEUTRAL, DROP, RETRACT};
    private ArmStatus CurrentArmStatus = ArmStatus.RETRACT;


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



    private void clawOpen(){
        ClawServo.setPosition(0.5);
    }

    private void clawClose(){
        ClawServo.setPosition(0.85);
    }


    /*
        private void arm(){

        ArmMotor1.setPower(gamepad1.right_stick_y*0.4);


        if (gamepad1.left_bumper){
            ArmMotor2.setPower(-0.25);
        }
        else{
            ArmMotor2.setPower(gamepad1.left_trigger*0.5);
        }
    }


     */

    // Make assumptions that arm is starting from retracted state.
    private void armRetract(){
        switch (CurrentArmStatus){
            case DROP:
                break;
            case NEUTRAL:
                break;
            default:
                break;
        }
        CurrentArmStatus = ArmStatus.RETRACT;
    }

    private void armNeutral(){
        switch (CurrentArmStatus){
            case DROP:
                break;
            case RETRACT:
                break;
            default:
                break;
        }
        CurrentArmStatus = ArmStatus.NEUTRAL;
    }

    private void armDrop(){
        switch (CurrentArmStatus){
            case NEUTRAL:
                break;
            case RETRACT:
                break;
            default:
                break;
        }
        CurrentArmStatus = ArmStatus.DROP;
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

            if (gamepad1.y){

            }


            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}
