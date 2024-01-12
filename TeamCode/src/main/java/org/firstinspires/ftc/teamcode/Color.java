package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;

@TeleOp(name="Color", group="Test")
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
        float[] hsv = new float[3];
        int[] rgb = new int[3];
        while (opModeIsActive()) {
            float MaxColorValue = 700; // adjust this later if needed

            int r = color.red();
            int g = color.green();
            int b = color.blue();

            // Convert to percent
            float RedPercantage = Math.min(r/MaxColorValue, 1.0f);
            float GreenPercantage = Math.min(g/MaxColorValue, 1.0f);
            float BluePercantage = Math.min(b/MaxColorValue, 1.0f);

            rgb[0] = (int)(RedPercantage*255);
            rgb[1] = (int)(GreenPercantage*255);
            rgb[2] = (int)(BluePercantage*255);


            //double Brightness1 = (0.2126*RedPercantage + 0.7152*GreenPercantage + 0.0722*BluePercantage);
            //double Brightness2 = 0.299 * RedPercantage + 0.587 * GreenPercantage + 0.114 * BluePercantage;
            //double Brightness3 = Math.sqrt(Brightness2);
            android.graphics.Color.RGBToHSV(rgb[0], rgb[1], rgb[2], hsv); // convert rgb to hsv
            telemetry.addData("Red", r);
            telemetry.addData("Green", g);
            telemetry.addData("Blue", b);
            telemetry.addData("Red%", RedPercantage);
            telemetry.addData("Green%", GreenPercantage);
            telemetry.addData("Blue%", BluePercantage);
            telemetry.addData("h%", hsv[0]);
            telemetry.addData("s%", hsv[1]);
            telemetry.addData("v%", hsv[2]);
            //telemetry.addData("Brightness1", Brightness1);
            //telemetry.addData("Brightness2", Brightness2);
            //telemetry.addData("Brightness3", Brightness3);

            float ColorThreshold = 0.19f;
            //float OpposingColorThreshold = 0.19f;
            float SaturationThreshold = 0.40f;
            float BrightnessThreshold = 0.19f;
            float WhiteThreshold = 0.40f;

            if (hsv[1] < SaturationThreshold){
                telemetry.addData("GUESSING COLOR: ", "GREY");
            }

            if (hsv[1] < SaturationThreshold && hsv[2] > WhiteThreshold){
                telemetry.addData("GUESSING COLOR: ", "WHITE");
            }

            if (RedPercantage > ColorThreshold && hsv[2] > BrightnessThreshold){
                telemetry.addData("GUESSING COLOR: ", "RED");
            }
            if (BluePercantage > ColorThreshold && hsv[2] > BrightnessThreshold)
            {
                telemetry.addData("GUESSING COLOR: ", "BLUE");
            }else{
                telemetry.addData("GUESSING COLOR: ", "NO CLUE");
            }


            telemetry.update();
        }
    }
}
