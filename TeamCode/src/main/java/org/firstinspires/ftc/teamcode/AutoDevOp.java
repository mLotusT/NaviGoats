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

@Autonomous(name="Auto Operation", group = "Competition")
public class AutoDevOp extends LinearOpMode {

    // FINETUINING VARIABLES
    // Drive Variables
    private final double straightDrivingErrorThreshold = 1.5;
    private final double turnDrivingErrorThreshold = 0.125;

    private final double turnSpeed = 0.3;
    private final double straightSpeed = 0.5;

    // Color variables
    private final float minRedHue = 30.0f;
    private final float maxRedHue = 315.0f;
    private final float minBlueHue = 175.0f;
    private final float maxBlueHue = 280.0f;

    private final float MaxColorValue = 700; // adjust this later if needed

    //private final float ColorThreshold = 0.19f;
    //float OpposingColorThreshold = 0.19f;
    private final float SaturationThreshold = 0.40f;
    private final float BrightnessThreshold = 0.19f;
    private final float WhiteThreshold = 0.30f;

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

    private enum COLOR {BLUE, RED, GREY, WHITE, OTHER}

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

    private void turn(DIRECTION turningDirection){
        switch (turningDirection){
            case LEFT:
                LeftMotor.setPower(-turnSpeed);
                RightMotor.setPower(-turnSpeed);
                break;
            case RIGHT:
                LeftMotor.setPower(turnSpeed);
                RightMotor.setPower(turnSpeed);
                break;
        }
    }

    private double calculateTargetYaw(double startingYaw, double angleDegrees, DIRECTION turningDirection){
        double targetYaw = startingYaw;
        switch (turningDirection){
            case LEFT:
                targetYaw += angleDegrees;
                break;
            case RIGHT:
                targetYaw -= angleDegrees;
                break;
        }
        return targetYaw;
    }

    // Ensures robot turns correctly using a gyroscope
    private void gyroTurn(double targetYaw, double errorThreshold){
        while (opModeIsActive()){
            double currentYaw = Gyro.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
            if (currentYaw > targetYaw + errorThreshold || currentYaw < targetYaw - errorThreshold){
                if (currentYaw > targetYaw + errorThreshold){
                    turn(DIRECTION.RIGHT);
                }
                else{
                    turn(DIRECTION.LEFT);
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
    private COLOR gyroMoveUntilColor(double targetYaw, double errorThreshold){

        COLOR c = COLOR.OTHER;
        while (opModeIsActive()){
            double currentYaw = Gyro.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);

            if (currentYaw > targetYaw + errorThreshold || currentYaw < targetYaw - errorThreshold){
                if (currentYaw > targetYaw + errorThreshold){
                    motorStop();
                    sleep(100);
                    turn(DIRECTION.RIGHT);
                }
                else{
                    motorStop();
                    sleep(100);
                    turn(DIRECTION.LEFT);
                }
            }else{
                RightMotor.setPower(-straightSpeed);
                LeftMotor.setPower(straightSpeed);
            }

            c = getColor();
            if (c != COLOR.GREY){
                break;
            }
        }
        motorStop();
        return c;
    }


    private COLOR gyroMoveUntilColor(double errorThreshold){
        return gyroMoveUntilColor(Gyro.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES), errorThreshold);
    }

    // Detects what color the sensor is currently getting
    private COLOR getColor(){
        float[] hsv = new float[3];
        int[] rgb = new int[3];

        int r = ColorSens.red();
        int g = ColorSens.green();
        int b = ColorSens.blue();

        // Convert to percent
        float RedPercantage = Math.min(r/MaxColorValue, 1.0f);
        float GreenPercantage = Math.min(g/MaxColorValue, 1.0f);
        float BluePercantage = Math.min(b/MaxColorValue, 1.0f);

        rgb[0] = (int)(RedPercantage*255);
        rgb[1] = (int)(GreenPercantage*255);
        rgb[2] = (int)(BluePercantage*255);

        android.graphics.Color.RGBToHSV(rgb[0], rgb[1], rgb[2], hsv); // convert rgb to hsv

        if (hsv[1] < SaturationThreshold && hsv[2] > WhiteThreshold){
            return COLOR.WHITE;
        }

        if (hsv[1] < SaturationThreshold){
            return COLOR.GREY;
        }

        /*
                // USE RGB
        if (RedPercantage > ColorThreshold && hsv[2] > BrightnessThreshold){
            return COLOR.RED;
        }
        if (BluePercantage > ColorThreshold && hsv[2] > BrightnessThreshold)
        {
            return COLOR.BLUE;
        }
         */


        // USE HUE
        if ((hsv[0] < minRedHue || hsv[0] > maxRedHue) && hsv[2] > BrightnessThreshold){
            return COLOR.RED;
        }
        if ((hsv[0] > minBlueHue && hsv[0] < maxBlueHue) && hsv[2] > BrightnessThreshold)
        {
            return COLOR.BLUE;
        }

        return COLOR.OTHER;
    }

    // Converts color detected into a direction
    // This allows for robot to know correct direction to turn depending on what team they are on
    private DIRECTION colorToDirection(COLOR c){
        switch (c) {
            case OTHER:
            case BLUE:
                return DIRECTION.LEFT;
            case RED:
            case WHITE:
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
            case WHITE:
                return "WHITE";
            default:
                return "UNKNOWN COLOR";
        }
    }

    //
    private void motorStop(){
        RightMotor.setPower(0);
        LeftMotor.setPower(0);
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

    private void runAutomation(){
        // Actual Automation Code

        Gyro.resetYaw(); // sets yaw to be relative to its current orientation
        telemetry.addData( "Status", "Running");
        double startYaw = Gyro.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        telemetry.addData( "CurrentYaw (should be 0): ", startYaw);
        telemetry.update();
        COLOR currentAlliance = gyroMoveUntilColor(startYaw, straightDrivingErrorThreshold); // Drive until  not grey color, maintain current bearing
        telemetry.addData( "Detected Color: ", colorToString(currentAlliance));
        double targetYaw = calculateTargetYaw(startYaw, 90, colorToDirection(currentAlliance));
        telemetry.addData("New bearing (should be +/- 90): ", targetYaw);
        telemetry.update();
        sleep(500);
        autoDrive(0.75);
        sleep(500);
        gyroTurn(targetYaw, turnDrivingErrorThreshold);
        sleep(500);
        gyroMoveUntilColor(targetYaw,straightDrivingErrorThreshold); // Maintain bearing of calculated of orignal start after 90 degree turn
        sleep(500);
        gyroTurn(targetYaw, turnDrivingErrorThreshold);
        // Attempt to park robot
        sleep(500);
        arm(); // drop pixel
        sleep(500);
        autoDrive(0.15);
    }

    // ENTRY POINT
    @Override
    public void runOpMode() {
        setUp(); // Initialize variables.

        telemetry.addData( "Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        runAutomation();
        /*
        // Color Test Code
        while (opModeIsActive()){
            telemetry.addData("Color: ", colorToString(getColor()));
            telemetry.update();
        }
         */

    }
}
