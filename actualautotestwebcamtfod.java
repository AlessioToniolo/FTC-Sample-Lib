package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="Webcam Auto Test Tfod", group="16633")
//@Disabled
public class Auto_Red_Build_Wall_16633_State extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Quad";
    private static final String LABEL_SECOND_ELEMENT = "Single";
    private static final String VUFORIA_KEY =
            " -- YOUR NEW VUFORIA KEY GOES HERE  --- ";
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;
    BNO055IMU imu;
    Orientation angles;
    Acceleration gravity;
    BNO055IMU.Parameters imuParameters;
    imu = hardwareMap.get(BNO055IMU.class, "imu 1");
    imuParameters = new BNO055IMU.Parameters();
    imuParameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
    imuParameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
    imuParameters.loggingEnabled = false;
    imu.initialize(imuParameters);



    /* Declare OpMode members. */
    MaristBaseRobot2019_Quad_14101 robot   = new MaristBaseRobot2019_Quad_14101();   
    private ElapsedTime runtime = new ElapsedTime();
    
    // Variables to control Speed
    double velocity = 1.0; // Default velocity


    @Override
    public void runOpMode() {
        initVuforia();
        initTfod();
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);



        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 1.78 or 16/9).

            // Uncomment the following line if you want to adjust the magnification and/or the aspect ratio of the input images.
            tfod.setZoom(2.5, 1.78);
        }

        tfod.getUpdatedRecognitions();
        telemetry.addData("# Object Detected", tfod.getUpdatedRecognitions());

            if (updatedRecognitions.size() > 0) {
                double ringCount = 0;
                if (/*ring count is 4 (target zone c)*/) {
                    ringCount = 4;
                } else {
                    ringCount = 1;
                    // Target zone is b
                }
                
            } else {
                // Target zone is a
            }
            for (Recognition recognition : tfod.getUpdatedRecognitions()) {
                telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                        recognition.getLeft(), recognition.getTop());
                telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                        recognition.getRight(), recognition.getBottom());
            }
            telemetry.addData(">", "Micah from Weber Robotics");
            telemetry.update();
        }

        waitForStart();
        tfod.shutdown();

        /* Run movement code */
        if (ringCount == 4) {
            targetc();
        } else if (ringCount == 1) {
            targetb();
        } else {
            targeta();
        }

        /*
        * TEST GYRO ROTATIONAL CODE
        */
        right(90);
        gyroRotationHeadingCorrection(90, 0, 'r');

    }
    
    // Function for target zone movements

    public void targeta() {
    }

    public void targetb() {

    }

    public void targetc() {

    }

    // Gyro rotational function
    public void gyroRotationHeadingCorrection(double angleTurnInDegrees, double previousAngleTurnInDegrees, char whichTurn) {
        double newAngles = angles;
        newAngles = newAngles - previousAngleTurnInDegrees;
        if (newAngles > angleTurnInDegrees) {
            double reverseTurn = newAngles - angleTurnInDegrees;
            if (whichTurn == 'r') {
                left(reverseTurn);
            }
            else {
                right(reverseTurn);
            }
        }
        else {
            double inverseTurn = angleTurnInDegrees - newAngles;
            if (whichTurn == 'r') {
                right(inverseTurn);
            }
            else {
                left(inverseTurn);
            }
        }

    }


    // Functions for REACH 2019 based on Python Turtles
    public void forward(double inches)
    {
        robot.driveStraightInches(velocity, inches, 10);
    }
    
    public void right(double degrees)
    {
        robot.pointTurnDegrees(velocity, degrees, 10);
    }
    
    public void left(double degrees)
    {
        degrees = degrees * -1;
        robot.pointTurnDegrees(velocity, degrees, 10);
    }
    
    public void speed(int speed)
    {
        double newSpeed = (double)speed / 10.0;
        velocity = newSpeed;
    }
    
    // Sample Delay Code
    public void delay(double t) { // Imitates the Arduino delay function
        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < t)) {
            telemetry.addData("Path", "Leg 1: %2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }
    }

}
