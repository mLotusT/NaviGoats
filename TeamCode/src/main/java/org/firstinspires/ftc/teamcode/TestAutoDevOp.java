package org.firstinspires.ftc.teamcode;

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
    }

    private void clawOpen(){
        ClawServo.setPosition(0.25);
    }

    private void clawClose(){
        ClawServo.setPosition(0.95);
    }




    private void turn(double percantageOfCircle, DIRECTION turningDirection){

        switch (turningDirection){
            case LEFT:
                LeftMotor.setPower(-1.0);
                RightMotor.setPower(-1.0);
                break;
            case RIGHT:
                LeftMotor.setPower(1.0);
                RightMotor.setPower(1.0);
                break;
        }
        // 1.436 seconds for 360 degrees
        sleep((long)(1436*percantageOfCircle));
        motorStop();
    }

    private void pixelDrop(){
        // unwrap arm in preperation to drop pixel
        // TO DO
        clawOpen();
    }

    private void autoDrive(double squares){
        RightMotor.setPower(1.0);
        LeftMotor.setPower(-1.0);
        sleep((long)(squares*570));
        motorStop();
    }

    private COLOR getColor(){
        double ColorThreshold = 128;
        double BrightnessThreshold = 100;
        double GreyThreshold = 75;
        double r = ColorSens.red();
        double g = ColorSens.green();
        double b = ColorSens.blue();

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

    private DIRECTION colorToDirection(COLOR c){

        switch (c) {
            case BLUE:
                return DIRECTION.LEFT;
            case RED:
                return DIRECTION.RIGHT;
            default:
                // THIS SHOULD NOT OCCUR, IF THIS OCCURS YOU GOOFED UP
                return DIRECTION.STRAIGHT;
        }
    }

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

    private void motorStop(){
        RightMotor.setPower(0);
        LeftMotor.setPower(0);
    }

    private void RunAutomationOneTime(){
        // Actual Automation Code
        autoDrive(1.0); // Move 1 square forward
        CurrentAlliance = getColor();
        turn(0.25, colorToDirection(CurrentAlliance));
        while(getColor() != CurrentAlliance){ // TO DO ADD ANOTHER CONDITION IF CERTAIN TIME PASSES TO STOp
            // keep going forward
            RightMotor.setPower(1.0);
            LeftMotor.setPower(-1.0);
        }
        pixelDrop();
        // Attempt to park robot
        autoDrive(0.25);
    }


    // ENTRY POINT
    @Override
    public void runOpMode() {

        setUp(); // Initialize variables.

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        //RunAutomationOneTime();
        while (opModeIsActive()) {


            // Test Code, simply outputs color it thinks its seeing
            telemetry.addData("Color: ", colorToString(getColor()));

            // Test Automation Movement, uncomment when using, spin both ways

            //turn(0.25, DIRECTION.LEFT);
            //turn(0.25, DIRECTION.RIGHT);

            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}
