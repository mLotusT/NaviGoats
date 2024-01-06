package org.firstinspires.ftc.teamcode;
package org.firstinspires.ftc.teamcode.Sensors;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ColorSensor;

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


    private enum alliance {RED_ALLIANCE, BLUE_ALLIANCE};
    private alliance CurrentAlliance;

    private boolean isGoingLeft = false; // True left, false right



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
        ClawServo.setPosition(0.95);
    }




    private void turn90degrees(boolean isLeft){

    }

    private void turn45degrees(){

    }

    private void autoDrive(double x){
        RightMotor.setPower(1.0);
        LeftMotor.setPower(-1.0);
        sleep((long)(x*570));
        motorStop();
    }

    private void checkColor(){

        if (BLUE){
            isGoingLeft = true;
        }
        else
        if (RED)
        {
            isGoingLeft = false;
        }


    }

    private void motorStop(){
        RightMotor.setPower(0);
        LeftMotor.setPower(0);
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

            autoDrive(2);

            checkColor();
            turn90degrees(isGoingLeft);



            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}
