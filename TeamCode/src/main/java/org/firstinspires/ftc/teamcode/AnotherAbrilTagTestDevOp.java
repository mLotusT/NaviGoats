package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@TeleOp
public class AnotherAbrilTagTestDevOp extends LinearOpMode {
    private double BearingParameter = 10.0; // Will drive forwards +/- of this var
    private double StoppingDistance = 5.0;

    private double MotorDirection = 1.0; // No idea which way the robot is going. so if its going away from abriltag then just change this to -1.0

    private VisionPortal vision;
    private AprilTagProcessor abril;

    private DcMotor RightMotor;
    private DcMotor LeftMotor;


    private void setUp() {

        RightMotor = hardwareMap.get(DcMotor.class, "Right");
        LeftMotor = hardwareMap.get(DcMotor.class, "Left");

        abril = AprilTagProcessor.easyCreateWithDefaults();
        // IF RUN TIME ERROR, THATS BECAUSE YOU DIDNT SET UP CONFIG FOR WEBCAM IN DRIVERS HUB
        vision = VisionPortal.easyCreateWithDefaults(hardwareMap.get(WebcamName.class, "Webcam 1"), abril);

        motorStop();
    }

    private void motorDrive(){
        RightMotor.setPower(1.0 * MotorDirection);
        LeftMotor.setPower(-1.0 * MotorDirection);
    }

    private void motorStop(){
        RightMotor.setPower(0);
        LeftMotor.setPower(0);
    }
    @Override
    public void runOpMode(){

        setUp();

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        while (opModeIsActive()) {
            List<AprilTagDetection> currentDetections = abril.getDetections();
            AprilTagDetection detection = currentDetections.get(0);
            telemetry.addData("# AprilTags Detected", currentDetections.size());

            if (currentDetections.size() != 0){
                // For simplicity sake just use the first item
                if (detection.metadata != null){
                    telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
                    telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                    telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                    telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));

                    if (detection.ftcPose.bearing > -BearingParameter && detection.ftcPose.bearing < BearingParameter){

                        if (detection.ftcPose.range > StoppingDistance){
                            motorDrive();
                        }
                        else{
                            motorStop();;
                        }

                    }
                }
                else{
                    telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
                    telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
                }
            }
            else{
                motorStop();
            }

        }
    }
}
