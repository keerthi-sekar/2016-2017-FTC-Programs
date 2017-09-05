package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Teleop: 2016", group="Teleop")  // @Autonomous(...) is the other common choice
@Disabled
public class QL_Teleop extends OpMode
{
    DcMotor up_left;
    DcMotor up_right;
    DcMotor side_left;
    DcMotor side_right;

    @Override
    public void init() {
        up_left = hardwareMap.dcMotor.get("up_left");
        side_right = hardwareMap.dcMotor.get("side_right");
        side_left = hardwareMap.dcMotor.get("side_left");
        up_right = hardwareMap.dcMotor.get("up_right");

        side_left.setDirection(DcMotor.Direction.REVERSE);
        up_right.setDirection(DcMotor.Direction.REVERSE);


        /* eg: Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step (using the FTC Robot Controller app on the phone).
         */
        // leftMotor  = hardwareMap.dcMotor.get("left motor");
        // rightMotor = hardwareMap.dcMotor.get("right motor");

        // eg: Set the drive motor directions:
        // Reverse the motor that runs backwards when connected directly to the battery
        // leftMotor.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        //  rightMotor.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors
        // telemetry.addData("Status", "Initialized");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        gamepad1.right_stick_y = Range.clip(gamepad1.right_stick_y,-1,1);
        gamepad1.right_stick_x = Range.clip(gamepad1.right_stick_x,-1,1); //TEST AND SEE WHATS GOES ON

        up_right.setPower(gamepad1.right_stick_y);
        up_left.setPower(gamepad1.right_stick_y);
        side_left.setPower(gamepad1.right_stick_x);
        side_right.setPower(gamepad1.right_stick_x);

        // eg: Run wheels in tank mode (note: The joystick goes negative when pushed forwards)
        // leftMotor.setPower(-gamepad1.left_stick_y);
        // rightMotor.setPower(-gamepad1.right_stick_y);
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

}
