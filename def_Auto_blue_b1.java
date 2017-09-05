package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "DEF: BLUE B1", group = "DEF")
//@Disabled
public class def_Auto_blue_b1 extends OpMode {

  //List of Robot State
  private enum State {
    STATE_INITIAL,
    STATE_DRIVE_TO_BEACON_ONE,
    STATE_CHARGE,
    STATE_SHOOT,
    STATE_DETECT_COLOR,
    STATE_SEARCH,
    STATE_PUSH_BUTTON,
    STATE_VERIFY,
    STATE_SCOOT_BACK,
    STATE_WAIT_FIVE, //added 1/9/17 Pause for 5 seconds
    STATE_DRIVE_TO_BEACON_TWO,
    STATE_RESET_ENCODERS,
    STATE_DRIVE_TO_CENTER,
    STATE_STOP,
  }

  DcMotor up_left;
  DcMotor up_right;
  DcMotor side_right;
  DcMotor side_left;
  DcMotor flywheel_motor;
  DcMotor sweeper_motor;

  ColorSensor colorSensor;

  Servo left_drop;
  Servo right_drop;
  Servo left_lift;
  Servo right_lift;

  int upCOUNTS;
  int sideCOUNTS;
  FTCUtils myUtils = new FTCUtils() ;

  private State mRobotState; //Tell current state of Robot
  private boolean mFlywheelOn;
  private boolean mSweeperOn;
  private boolean mGoToCenter;
  private boolean mVerified;
  private boolean mShifted; //distance between b1 and b2 flag
  private double  iSearchTime;
    private double iWaitFive;
  //private double iPushTime;
  private ElapsedTime mStateTime = new ElapsedTime();  // Time into current state

  float hsvValues[] = {0F,0F,0F};

  // values is a reference to the hsvValues array.
  final float values[] = hsvValues;

  // get a reference to the RelativeLayout so we can change the background
  // color of the Robot Controller app to match the hue detected by the RGB sensor.
  //final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(com.qualcomm.ftcrobotcontroller.R.id.RelativeLayout);

  // bPrevState and bCurrState represent the previous and current state of the button.
  boolean bPrevState = false;
  boolean bCurrState = false;

  // bLedOn represents the state of the LED.
  boolean bLedOn = true;


  @Override
  public void init() {
    up_left = hardwareMap.dcMotor.get("up_left");
    side_right = hardwareMap.dcMotor.get("side_right");
    side_left = hardwareMap.dcMotor.get("side_left");
    up_right = hardwareMap.dcMotor.get("up_right");
    flywheel_motor = hardwareMap.dcMotor.get("flywheel_motor");
    sweeper_motor = hardwareMap.dcMotor.get("sweeper_motor");

    colorSensor = hardwareMap.colorSensor.get("colorSensor");

    colorSensor.enableLed(false);

    left_drop = hardwareMap.servo.get("left_drop");
    right_drop = hardwareMap.servo.get("right_drop");
    left_lift = hardwareMap.servo.get("left_lift");
    right_lift = hardwareMap.servo.get("right_lift");

    side_left.setDirection(DcMotor.Direction.REVERSE);
    up_left.setDirection(DcMotor.Direction.REVERSE);

    sweeper_motor.setDirection(DcMotor.Direction.REVERSE);

    up_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    side_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    side_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    up_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    up_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    side_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    side_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    up_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    left_drop.setPosition(.8);
    right_drop.setPosition(0.2);

    left_lift.setPosition(0.8); //(1 = init) to grab ball
    right_lift.setPosition(0); // (0 = init)
  }


  @Override
  public void init_loop() {

  }

  /*
   * This method will be called ONCE when start is pressed
   * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#loop()
   */
  @Override
  public void start(){
    telemetry.addData("Entering", "start");
    //up_left.setTargetPosition(upCOUNTS);
    side_right.setTargetPosition(sideCOUNTS);
    //side_left.setTargetPosition(sideCOUNTS);
    //up_right.setTargetPosition(upCOUNTS);
    telemetry.addData("At", "Run_Position" + upCOUNTS);
    up_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);  //old program is DCMotorController
    side_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    side_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    up_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);

    mFlywheelOn = false;
    mSweeperOn = false;
    mGoToCenter = false;
    mVerified = false;
    mShifted = false; // if true go smaller distance
    iSearchTime = 0;
      iWaitFive = 0;

    newState(State.STATE_INITIAL);

  }

  @Override
  public void loop() {

    switch (mRobotState){

      case STATE_INITIAL:   //stay in this state until both encoders are 0
        telemetry.addData("State", "initial");
        newState(State.STATE_DRIVE_TO_BEACON_ONE);
        break;

      case STATE_DRIVE_TO_BEACON_ONE:
        telemetry.addData("drive_to", "beacon_1");
        int sideCOUNTS = myUtils.getNewCounts(54); //GOES HAND IN HAND WITH the rest of the COUNTS
        //BETWEEN 2 BEACONS IT IS 48"
        up_left.setTargetPosition(sideCOUNTS);
        up_right.setTargetPosition(sideCOUNTS);
        side_right.setTargetPosition(sideCOUNTS);
        side_left.setTargetPosition(sideCOUNTS);
        up_left.setPower(0.8);
        up_right.setPower(0.8);
        side_left.setPower(0.8);
        side_right.setPower(0.8);
        //increased speed from 0.6 to 0.8 1/8/17

        flywheel_motor.setPower(0.8); //CHARGE AT BEGINNING OF PROGRAM

        if (side_right.getCurrentPosition() >= sideCOUNTS){
          up_left.setPower(0);
          up_right.setPower(0);
          side_left.setPower(0);
          side_right.setPower(0);
          newState(State.STATE_SHOOT);
        }
        break;

      case STATE_CHARGE:
        telemetry.addData("STATE", "CHARGE");
        flywheel_motor.setPower(0.8);
        newState(State.STATE_SHOOT);
        break;

      case STATE_SHOOT:
        telemetry.addData("State", "Shoot");
        if (mStateTime.time() > 1.0){
          //wait for 3 secs before powering sweeper motor
          sweeper_motor.setPower(0.8);
          newState(State.STATE_DETECT_COLOR);
        }
        break;

      case STATE_DETECT_COLOR:
        telemetry.addData("detect", "color");
        double wait_time = 3.0; //changed from 4.0 to 3.0 1/8/17
        if (mGoToCenter)
            wait_time = 0.5;

        if (mStateTime.time() > wait_time) {
          sweeper_motor.setPower(0);
          flywheel_motor.setPower(0);
          int iTempCounter = 0 ;
          telemetry.addData("within detect color ", "before loop");
           if(secureBeacon()) {
             iSearchTime = 1;
             telemetry.addData("within RED color ", "Move to PUSH " + iSearchTime);
             newState(State.STATE_PUSH_BUTTON);
           } else {
             iSearchTime = 1;
             telemetry.addData("move Adjacent color ", "Move to STATE_SEARCH");
             newState(State.STATE_SEARCH);
           }
          telemetry.addData("goingto", "STATE SEARCH");
        }
        break;

      case STATE_SEARCH:
        //move 6 in since its blue
        sideCOUNTS = myUtils.getNewCounts(60); //AKA 6"
        telemetry.addData("within STATE_SEARCH ", sideCOUNTS);
        mShifted = true;
        side_left.setTargetPosition(sideCOUNTS);
          side_right.setTargetPosition(sideCOUNTS);
          side_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
          side_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
          // ^ travel 6 in
          up_left.setPower(0);
          up_right.setPower(0);
          telemetry.addData("SIDEpos", "COUNTs", side_left.getCurrentPosition() + " --" + iSearchTime);
          // finished moving 6 in - now press button
          if (side_left.getCurrentPosition() >= sideCOUNTS) {
            side_left.setPower(0);
            side_right.setPower(0);
            telemetry.addData("move", "6in" + sideCOUNTS);

          } else {
            telemetry.addData("runningside", "0.6");
            side_left.setPower(.6);
            side_right.setPower(.6);
          }
          newState(State.STATE_PUSH_BUTTON);
        break;

      case STATE_PUSH_BUTTON:
        telemetry.addData("PUSH Button", "BUTTON running for " + iSearchTime);
           if (mStateTime.time() > iSearchTime){
               telemetry.addData("GO", "Scoot back");

                push_button();
             iSearchTime = 1; //reset iSearchtime back 1 one for B2

               if (!mVerified){
                 // !mVerified => mVerified == false
                 newState(State.STATE_VERIFY);
               } else {
                     newState(State.STATE_SCOOT_BACK);
               }
           }
        break;

      case STATE_SCOOT_BACK:
        if (mStateTime.time() > iSearchTime){
          telemetry.addData("SCOOT BACK", "BACK");
            scoot_back();
            newState(State.STATE_DRIVE_TO_CENTER);
        }
        break;

      case STATE_VERIFY:
        if (mStateTime.time() > iSearchTime){
          telemetry.addData("VERIFY", "COLOR");
          iSearchTime = 1;
          mVerified = true;
          if (secureBeacon()){
            telemetry.addData("within State Verify T", "Drive to Beacon 2");
            mVerified = false;
            newState(State.STATE_SCOOT_BACK);
          } else {
            telemetry.addData("within State Verify F", "STATE PUSH BUTTON");
            scoot_back();
            iSearchTime = 1.0;
            newState(State.STATE_WAIT_FIVE); //CHANGED from PUSH BUTTON to WAIT FIVE 1/11/17
          }
         // newState(State.STATE_DRIVE_TO_BEACON_TWO);
        }
        break;

      case STATE_WAIT_FIVE:
          //wait 5 sec before hitting button again (ONLY IF WE PRESS THE WRONG BUTTON)
        if (mStateTime.time() > 1.0) {
            telemetry.addData("hit wrong color", "WAIT 5 sec");
            iSearchTime = 3; //3 second pause
            up_left.setPower(0);
            up_right.setPower(0);
            side_right.setPower(0);
            side_left.setPower(0);
            newState(State.STATE_PUSH_BUTTON);
        }
        break;

      case STATE_DRIVE_TO_CENTER:
          telemetry.addData("DRIVE", "TO CENTER");
          run_without_Encoders();
          up_left.setPower(-0.8);
          up_right.setPower(-0.8);
          side_left.setPower(0.8);
          side_right.setPower(0.8);
          newState(State.STATE_STOP);
        break;

      case STATE_STOP:
        if (mStateTime.time() > 4.0) {
          telemetry.addData("STATE", "STOP");
          up_right.setPower(0);
          up_left.setPower(0);
          side_right.setPower(0);
          side_left.setPower(0);
        }
        break;
    }

  }


  private void newState (State newState){
    // Reset State time and change to next state
    mStateTime.reset();
    mRobotState = newState;

  }
  private void resetEncoders (){
    up_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    side_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    side_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    up_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
  }
  private void run_to_position_Encoders (){
    up_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    up_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    side_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    side_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
  }

  private void run_without_Encoders (){
    up_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    up_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    side_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    side_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
  }

  private boolean secureBeacon () {
    // DETECT COLOR
    boolean detected = false;
    int THRESHOLD = 1;
    telemetry.addData("Entering", "opMode is Active");
    // convert the RGB values to HSV values.
    Color.RGBToHSV(colorSensor.red() * 8, colorSensor.green() * 8, colorSensor.blue() * 8, hsvValues);

    // send the info back to driver station using telemetry function.
    telemetry.addData("LED", bLedOn ? "On" : "Off");
    telemetry.addData("Clear", colorSensor.alpha());
    telemetry.addData("Red  ", colorSensor.red());
    telemetry.addData("Green", colorSensor.green());
    telemetry.addData("Blue ", colorSensor.blue());
    telemetry.addData("Hue", hsvValues[0]);

    telemetry.addData("within securebeacon - Before", "THRESHOLD");

      if (colorSensor.blue() > THRESHOLD) {
        //RED MOVE FORWARD
        telemetry.addData("COLOR", "RED DETECTED " + colorSensor.red());
        detected = true;
      } else {
        //BLUE TRAVEL 6 INCHES AND THEN PUSH FORWARD (FIRST INT = UP, SECOND INT = SIDE)
        detected = false;
      }
    return detected;
  }

  private void push_button() {
      telemetry.addData("within PUSH BUTTON", "push");
    up_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    up_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    up_left.setPower(0.6); //0.6 original speed
    up_right.setPower(0.6);
  }

  private void scoot_back(){
      telemetry.addData("private", "scoot back");
    up_left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    up_right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    up_left.setPower(-0.6);
    up_right.setPower(-0.6);
  }

}
