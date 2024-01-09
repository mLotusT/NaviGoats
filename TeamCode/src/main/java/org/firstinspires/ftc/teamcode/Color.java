package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class Color extends LinearOpMode {
    // Define a variable for our color sensor
    ColorSensor color;


    @Override
    public void runOpMode() {
        // Get the color sensor from hardwareMap
        color = hardwareMap.get(ColorSensor.class, "Color");
        
        // Wait for the Play button to be pressed
        waitForStart();

        // While the Op Mode is running, update the telemetry values.
        while (opModeIsActive()) {
            double r = color.red();
            double g = color.green();
            double b = color.blue();
            double Brightness = (0.2126*r + 0.7152*g + 0.0722*b);


            telemetry.addData("Red", r);
            telemetry.addData("Green", g);
            telemetry.addData("Blue", b);
            telemetry.addData("Brightness", Brightness);
            telemetry.update();
        }
    }
}
