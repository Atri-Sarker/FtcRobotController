package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.CatapultControl;
import org.firstinspires.ftc.teamcode.mechanisms.IntakeControl;
import org.firstinspires.ftc.teamcode.mechanisms.TankDrive;

@TeleOp(name="Drive + Catapult", group="Linear OpMode")
public class PrototypeOpMode extends OpMode {

    TankDrive drive = new TankDrive();
    CatapultControl catapult = new CatapultControl();
    double throttle, spin;

    @Override
    public void init() {
        drive.init(hardwareMap);
        catapult.init(hardwareMap);
    }

    @Override
    public void loop() {
        throttle = -gamepad1.left_stick_y;
        spin = gamepad1.left_stick_x;

        drive.drive(throttle, spin);
        catapult.handleCatapult(gamepad1.right_bumper);
    }

}
