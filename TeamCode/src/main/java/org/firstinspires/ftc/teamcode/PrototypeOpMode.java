package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.IntakeControl;
import org.firstinspires.ftc.teamcode.mechanisms.TankDrive;

@TeleOp(name="Basic: Omni Linear OpMode", group="Linear OpMode")
public class PrototypeOpMode extends OpMode {

    TankDrive drive = new TankDrive();
    IntakeControl intake = new IntakeControl();
    double throttle, spin;
    double intakePower;

    @Override
    public void init() {
        drive.init(hardwareMap);
        intake.init(hardwareMap);
    }

    @Override
    public void loop() {
        throttle = -gamepad1.left_stick_y;
        spin = gamepad1.left_stick_x;
        intakePower = gamepad1.right_stick_y;

        drive.drive(throttle, spin);
        intake.drive(intakePower);
    }

}
