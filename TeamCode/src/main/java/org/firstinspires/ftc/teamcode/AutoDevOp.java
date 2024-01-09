// AutoDevOp includes all the code for automatic movement without human input.
// It determines movement based on color sensors and precision movement.

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@Autonomous
public class AutoDevOp extends LinearOpMode {
    // Declare Motor Variables
    private DcMotor RightMotor;
    private DcMotor LeftMotor;
    private DcMotor ArmMotor1;
    private DcMotor ArmMotor2;
    private Servo ClawServo;

    private IMU Gyro;

    // Setup Color sensor
    ColorSensor ColorSens;

    private enum DIRECTION {STRAIGHT, LEFT, RIGHT};

    private enum COLOR {BLUE, RED, GREY, OTHER}
    private COLOR CurrentAlliance = COLOR.OTHER;
    private void setUp() {
        // Assign Motors to corresponding names in drivers hub
        RightMotor = hardwareMap.get(DcMotor.class, "Right");
        LeftMotor = hardwareMap.get(DcMotor.class, "Left");
        ClawServo = hardwareMap.get(Servo.class, "Claw");
        ArmMotor1 = hardwareMap.get(DcMotor.class, "Arm1");
        ArmMotor2 = hardwareMap.get(DcMotor.class, "Arm2");

        ColorSens = hardwareMap.get(ColorSensor.class, "Color");
        Gyro = hardwareMap.get(IMU.class, "imu");
    }

    private void clawOpen(){
        ClawServo.setPosition(0.25);
    }

    private void clawClose(){
        ClawServo.setPosition(0.95);
    }

    private void turn(double throttle, DIRECTION turningDirection){
        switch (turningDirection){
            case LEFT:
                LeftMotor.setPower(-throttle);
                RightMotor.setPower(-throttle);
                break;
            case RIGHT:
                LeftMotor.setPower(throttle);
                RightMotor.setPower(throttle);
                break;
        }
    }

    // Ensures robot turns correctly using a gyroscope
    private void gyroTurn(double startingYaw, double angleDegrees, DIRECTION turningDirection, double errorThreshold){
        double targetYaw = startingYaw;

        switch (turningDirection){
            case LEFT:
                targetYaw = startingYaw + angleDegrees;
                break;
            case RIGHT:
                targetYaw = startingYaw - angleDegrees;
                break;
        }
        while (opModeIsActive()){
            double currentYaw = Gyro.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
            if (currentYaw > targetYaw + errorThreshold || currentYaw < targetYaw - errorThreshold){
                if (currentYaw > targetYaw + errorThreshold){
                    turn(0.5, DIRECTION.RIGHT);
                }
                else{
                    turn(0.5, DIRECTION.LEFT);
                }
            }
            else{
                break;
            }
        }
        motorStop();
    }

    // Robot automatically moves forward
    private void autoDrive(double squares){
        RightMotor.setPower(-0.5);
        LeftMotor.setPower(0.5);
        sleep((long)(squares*1682));
        motorStop();
    }

    // Keeps the robot moving in a straight line until it sees a recognizedd color
    private COLOR gyroMoveUntilColor(double errorThreshold){
        double startingYaw = Gyro.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);

        COLOR c = COLOR.OTHER;
        while (opModeIsActive()){
            double currentYaw = Gyro.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);

            if (currentYaw > startingYaw + errorThreshold || currentYaw < startingYaw - errorThreshold){
                if (currentYaw > startingYaw + errorThreshold){
                    turn(0.3, DIRECTION.RIGHT);
                }
                else{
                    turn(0.3, DIRECTION.LEFT);
                }
            }else{
                RightMotor.setPower(-0.5);
                LeftMotor.setPower(0.5);
            }

            c = getColor();
            if (c != COLOR.GREY){
                break;
            }
        }
        motorStop();
        return c;
    }

    // Detects what color the sensor is currently getting
    private COLOR getColor(){
        double ColorThreshold = 128;
        double BrightnessThreshold = 100;
        double GreyThreshold = 50;
        double r = ColorSens.red();
        double g = ColorSens.green();
        double b = ColorSens.blue();

        // Brightness check to keep color detecting the same every time
        double Brightness = (0.2126*r + 0.7152*g + 0.0722*b); // see https://stackoverflow.com/questions/596216/formula-to-determine-perceived-brightness-of-rgb-color

        if (Brightness < GreyThreshold){
            return COLOR.GREY;
        }

        if (r > ColorThreshold && Brightness > BrightnessThreshold){
            return COLOR.RED;
        }
        if (b > ColorThreshold && Brightness > BrightnessThreshold)
        {
            return COLOR.BLUE;
        }
        return COLOR.OTHER;
    }

    // Converts color detected into a direction
    // This allows for robot to know correct direction to turn depending on what team they are on
    private DIRECTION colorToDirection(COLOR c){

        switch (c) {
            case BLUE:
                return DIRECTION.LEFT;
            case RED:
                return DIRECTION.RIGHT;
            case OTHER:
                return DIRECTION.RIGHT;
            default:
                // THIS SHOULD NOT OCCUR, IF THIS OCCURS YOU GOOFED UP
                return DIRECTION.STRAIGHT;
        }
    }

    // Turns output variables into easier to use strings
    private String colorToString(COLOR c){ // for finetuning purposes
        switch (c) {
            case BLUE:
                return "BLUE";
            case RED:
                return "RED";
            case GREY:
                return "GREY";
            default:
                return "UNKNOWN COLOR";
        }
    }

    //
    private void motorStop(){
        RightMotor.setPower(0);
        LeftMotor.setPower(0);
    }

    private void RunAutomationOneTime(){
        // Actual Automation Code
        double startYaw = Gyro.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        CurrentAlliance = gyroMoveUntilColor(2); // Drive until  not grey color
        sleep(750);
        autoDrive(0.65);
        sleep(750);
        gyroTurn(startYaw,90, colorToDirection(CurrentAlliance), 0.25);
        sleep(750);
        gyroMoveUntilColor(2);
        // Attempt to park robot
        sleep(750);
        arm();
        autoDrive(0.15);
    }

    // Moves the arm into position
    private void arm(){
        ArmMotor1.setPower(-0.6);
        sleep(550);
        ArmMotor1.setPower(0);
        ArmMotor2.setPower(0.75);
        sleep(700);
        ArmMotor2.setPower(0.35);
        clawOpen();
        ArmMotor1.setPower(0.5);
        sleep(200);
        ArmMotor1.setPower(0);
        clawClose();

        ArmMotor1.setPower(-0.6);
        sleep(500);
        ArmMotor1.setPower(-0.3);
        ArmMotor2.setPower(-0.75);
        sleep(700);
        ArmMotor2.setPower(0);
        ArmMotor1.setPower(0.4);
        sleep(500);
        ArmMotor1.setPower(0);

    }


    // ENTRY POINT
    @Override
    public void runOpMode() {

        setUp(); // Initialize variables.

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        RunAutomationOneTime();
        //turnUsingGyroscope(90, DIRECTION.RIGHT, 5);

    }
}
