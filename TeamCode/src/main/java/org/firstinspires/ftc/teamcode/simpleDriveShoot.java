package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="Simple Drive + Shoot [No tuned values] [for testing]", group="TeleOp")
public class simpleDriveShoot extends LinearOpMode {

    DcMotor frontLeft, frontRight, backLeft, backRight, intakeMotor; // Launching motor
    DcMotorEx launchMotor;
    Servo launching;
    Servo stopper;

    CRServo transferServo;

    private static final double DEADZONE = 0.06;
    private static final double SPEED_MULT = 1.0;
    private double servoPos = 0.5;
    private boolean yLast = false;
    private boolean aLast = false;
    private int launchLevel = 0;
    private boolean dpadUpLast = false;
    private boolean dpadDownLast = false;
    private boolean dpadLeftLast = false;
    private boolean dpadRightLast = false;

    // 단계별 속도 (0단계 포함)
    private final double[] LAUNCH_SPEED = {
            0.0,  // 0단계 (정지)
            0.2,  // 1단계
            0.4,  // 2단계
            0.6,  // 3단계
            0.8,  // 4단계
            1.0   // 5단계
    };

    @Override
    public void runOpMode() {

        // Motor mapping
//        frontRight = hardwareMap.get(DcMotor.class, "front_right_motor");
//        frontLeft = hardwareMap.get(DcMotor.class, "front_left_motor");
//        backRight = hardwareMap.get(DcMotor.class, "back_right_motor");
//        backLeft = hardwareMap.get(DcMotor.class, "back_left_motor");
//        intakeMotor = hardwareMap.get(DcMotor.class, "intake");
//        launchMotor = hardwareMap.get(DcMotorEx.class, "flywheel");
        frontRight = hardwareMap.get(DcMotor.class, "1");
        frontLeft = hardwareMap.get(DcMotor.class, "0");
        backRight = hardwareMap.get(DcMotor.class, "2");
        backLeft = hardwareMap.get(DcMotor.class, "3");
        intakeMotor = hardwareMap.get(DcMotor.class, "4");
        launchMotor = hardwareMap.get(DcMotorEx.class, "5");

        launching = hardwareMap.get(Servo.class, "launching");
        stopper = hardwareMap.get(Servo.class, "stopper");
        transferServo = hardwareMap.get(CRServo.class, "transfer_servo");

        // Mode

        launchMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Direction
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        launchMotor.setDirection(DcMotorSimple.Direction.REVERSE);



        // Brake
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        launchMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // 초기 서보 위치
        launching.setPosition(servoPos);

        waitForStart();

        while (opModeIsActive()) {

            /* ================== 드라이브 ================== */
            double y  = -gamepad1.left_stick_y;
            double x  =  gamepad1.left_stick_x;
            double rx =  gamepad1.right_stick_x;
            double intakePower = 0;

            if (Math.abs(y) < DEADZONE) y = 0;
            if (Math.abs(x) < DEADZONE) x = 0;
            if (Math.abs(rx) < DEADZONE) rx = 0;

            x *= 1.1;

            double fl = y + x + rx;
            double bl = y - x + rx;
            double fr = y - x - rx;
            double br = y + x - rx;

            double max = Math.max(1.0,
                    Math.max(Math.abs(fl),
                            Math.max(Math.abs(bl),
                                    Math.max(Math.abs(fr), Math.abs(br)))));

            fl = (fl / max) * SPEED_MULT;
            bl = (bl / max) * SPEED_MULT;
            fr = (fr / max) * SPEED_MULT;
            br = (br / max) * SPEED_MULT;

            if (gamepad1.right_trigger > 0) intakePower = 0.4;
            if (gamepad1.left_trigger  > 0) intakePower = -0.4;

            frontLeft.setPower(fl);
            frontRight.setPower(fr);
            backLeft.setPower(bl);
            backRight.setPower(br);
            intakeMotor.setPower(intakePower);
            transferServo.setPower(intakePower);

            /* ================== Launching 모터 단계 조절 ================== */
            boolean dpadUpNow = gamepad1.dpad_up;
            boolean dpadDownNow = gamepad1.dpad_down;

            if (dpadUpNow && !dpadUpLast) {
                launchLevel++;
            }

            if (dpadDownNow && !dpadDownLast) {
                launchLevel--;
            }

            // 단계 제한 (0~5)
            launchLevel = Math.max(0, Math.min(5, launchLevel));

            // 속도 적용
            launchMotor.setPower(LAUNCH_SPEED[launchLevel]);

            dpadUpLast = dpadUpNow;
            dpadDownLast = dpadDownNow;

            /* ================== 서보 증감 ================== */
            boolean yNow = gamepad1.y;
            boolean aNow = gamepad1.a;

            if (yNow && !yLast) servoPos += 0.1;
            if (aNow && !aLast) servoPos -= 0.1;

            servoPos = Math.max(0.2, Math.min(0.7, servoPos));
            launching.setPosition(servoPos);
            stopper.setPosition(servoPos);

            yLast = yNow;
            aLast = aNow;

            /* Stopper */
            boolean dpadLeftNow = gamepad1.dpad_left;
            boolean dpadRightNow = gamepad1.dpad_right;

            if (dpadLeftNow && !dpadUpLast) {
                stopper.setPosition(stopper.getPosition()+0.1);
            }

            if (dpadRightNow && !dpadDownLast) {
                stopper.setPosition(stopper.getPosition()-0.1);
            }

            dpadLeftLast = dpadLeftNow;
            dpadRightLast = dpadRightNow;

            /* ================== Telemetry ================== */
            telemetry.addData("Launcher Level", launchLevel);
            telemetry.addData("Launcher Power", LAUNCH_SPEED[launchLevel]);
            telemetry.addData("Current Flywheel Speed: ", launchMotor.getVelocity());
            telemetry.addData("Servo Hood Position", servoPos);
            telemetry.addData("Servo Stopper Position", stopper.getPosition());
            telemetry.update();
        }
    }
}