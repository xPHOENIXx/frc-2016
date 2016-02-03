package org.usfirst.frc.team166.robot.subsystems;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team166.robot.PIDSpeedController;
import org.usfirst.frc.team166.robot.Robot;
import org.usfirst.frc.team166.robot.RobotMap;
import org.usfirst.frc.team166.robot.commands.drive.DriveWithJoysticks;

/**
 *
 */
public class Drive extends Subsystem {

	double distancePerPulse = 12 / 56320.0; // this makes perfect cents // no it doesn't it makes 2.1306818181...
	double gyroConstant = -0.3 / 10.0;
	double driveSpeedModifierConstant = .7;
	double gyroVal = 0;

	boolean highGear;
	boolean neutral;

	double highGearValue = 0.0;
	double lowGearValue = 1.0;

	Victor leftTopVictor = new Victor(RobotMap.Pwm.leftTopDrive);
	Victor leftBotVictor = new Victor(RobotMap.Pwm.leftBotDrive);
	Victor rightTopVictor = new Victor(RobotMap.Pwm.rightTopDrive);
	Victor rightBotVictor = new Victor(RobotMap.Pwm.rightBotDrive);

	Servo transmission1Servo = new Servo(RobotMap.Pwm.transmission1ServoPort);
	Servo transmission2Servo = new Servo(RobotMap.Pwm.transmission2ServoPort);// dont be dumb by putting double 1s

	Encoder leftEncoder = new Encoder(RobotMap.Digital.leftEncoderA, RobotMap.Digital.leftEncoderB);// more
	Encoder rightEncoder = new Encoder(RobotMap.Digital.rightEncoderA, RobotMap.Digital.rightEncoderB);

	Encoder leftEncoder1 = new Encoder(RobotMap.Digital.leftEncoder1A, RobotMap.Digital.leftEncoder1B); // delete these
	// later when we have the real robot
	Encoder rightEncoder1 = new Encoder(RobotMap.Digital.rightEncoder1A, RobotMap.Digital.rightEncoder1B);

	PIDSpeedController leftTopPID = new PIDSpeedController(leftEncoder, leftTopVictor, "Drive", "LeftTopPID"); // specify
	PIDSpeedController leftBotPID = new PIDSpeedController(leftEncoder1, leftBotVictor, "Drive", "LeftBotPID");
	PIDSpeedController rightTopPID = new PIDSpeedController(rightEncoder, rightTopVictor, "Drive", "RightTopPID");
	PIDSpeedController rightBotPID = new PIDSpeedController(rightEncoder1, rightBotVictor, "Drive", "RightBotPID");// or
																													// bot
																													// motors

	Gyro gyro = new AnalogGyro(RobotMap.Analog.gyroPort);

	RobotDrive tankDrive = new RobotDrive(leftTopPID, leftBotPID, rightTopPID, rightBotPID);

	// RobotDrive tankDrive = new RobotDrive(leftTopVictor, leftBotVictor, rightTopVictor, rightBotVictor);

	public Drive() {
		leftEncoder.setDistancePerPulse(distancePerPulse);
		rightEncoder.setDistancePerPulse(distancePerPulse);
		leftEncoder.setPIDSourceType(PIDSourceType.kRate);
		rightEncoder.setPIDSourceType(PIDSourceType.kRate);

		leftEncoder1.setDistancePerPulse(distancePerPulse);
		rightEncoder1.setDistancePerPulse(distancePerPulse);
		leftEncoder1.setPIDSourceType(PIDSourceType.kRate); // delete these later
		rightEncoder1.setPIDSourceType(PIDSourceType.kRate);
	}

	public double getGyroOffset() {
		gyroVal = Robot.drive.getGyro() * gyroConstant;
		if (Math.abs(gyroVal) > (1.0 - driveSpeedModifierConstant)) {
			gyroVal = (1.0 - driveSpeedModifierConstant) * Math.abs(gyroVal) / gyroVal; // sets gyroVal to either 1 or
			// -1
		}
		return gyroVal;
	}

	public void driveWithGyro() {
		double rightPower = Robot.oi.getRightYAxis() * driveSpeedModifierConstant;
		double leftPower = Robot.oi.getLeftYAxis() * driveSpeedModifierConstant;
		double power = 0;
		power = (rightPower + leftPower) / 2;
		if (Math.abs(Robot.oi.getRightYAxis()) > .1) {
			tankDrive.tankDrive(power + getGyroOffset(), power - getGyroOffset());
		}
	}

	public void highGear() {
		transmission1Servo.set(highGearValue);
		transmission2Servo.set(highGearValue);
		highGear = true;
		neutral = false;
		leftEncoder.setDistancePerPulse(3 * distancePerPulse);
		rightEncoder.setDistancePerPulse(3 * distancePerPulse);
		leftEncoder1.setDistancePerPulse(3 * distancePerPulse); // delete this later
		rightEncoder1.setDistancePerPulse(3 * distancePerPulse);
	}

	public void lowGear() {
		transmission1Servo.set(lowGearValue);
		transmission2Servo.set(lowGearValue);
		highGear = false;
		neutral = false;
		leftEncoder.setDistancePerPulse(distancePerPulse);
		rightEncoder.setDistancePerPulse(distancePerPulse);
		leftEncoder1.setDistancePerPulse(distancePerPulse); // delete this later
		rightEncoder1.setDistancePerPulse(distancePerPulse); // delete this later
	}

	public void neutral() {
		transmission1Servo.set(0.5);
		transmission2Servo.set(0.5);
		neutral = true;
	}

	public void driveWithJoysticks() {
		// integrate gyro into drive. i.e. correct for imperfect forward motion
		// with a proportional controller
		double rightPower = Robot.oi.getRightYAxis() * driveSpeedModifierConstant;
		boolean areJoysticksSimilar = false;
		if ((Math.abs(Robot.oi.getLeftYAxis()) > .1) || (Math.abs(Robot.oi.getRightYAxis()) > .1)) {

			SmartDashboard.putNumber("Gyro Offset", getGyroOffset());
			SmartDashboard.putNumber("Right Power", rightPower);
			SmartDashboard.putBoolean("areJoysticksSimilar", areJoysticksSimilar);
			tankDrive.tankDrive(Robot.oi.getLeftYAxis(), Robot.oi.getRightYAxis()); // if not trying to go straight, //
			// don't use gyro

		} else {
			stop();
		}
	}

	public void stop() {
		tankDrive.tankDrive(0, 0);
	}

	public void resetGyro() {
		gyro.reset();
	}

	public void resetEncoders() {
		leftEncoder.reset();
		rightEncoder.reset();
	}

	public double getLeftEncoder() {
		SmartDashboard.putNumber("Left Encoder", leftEncoder.getRate());
		return leftEncoder.getRate();
	}

	public double getRightEncoder() {
		SmartDashboard.putNumber("Right Encoder", rightEncoder.getRate());
		return rightEncoder.getRate();
	}

	public double getDistance() {
		return (((getLeftEncoder() + getRightEncoder()) / 2.0) / 1024.0) / 31.4;
	}

	public double getGyro() {
		return gyro.getAngle();
	}

	public void turnAngle(double angle) {
		double power = (angle - getGyro()) / angle;
		if (getGyro() < angle - 7.0) {
			tankDrive.tankDrive(power, -power);

			// rightMotor(-power);
			// leftMotor(power);
		} else if (getGyro() > angle + 7) {
			tankDrive.tankDrive(-power, power);
			// rightMotor(power);
			// leftMotor(-power);
		} else if (getGyro() >= angle - 7 && getGyro() <= angle + 7) {
			tankDrive.tankDrive(0, 0);
		}
	}

	public void driveDistance(double distance) { // inches
		double power = (distance - getDistance()) / distance;
		if (getDistance() <= (Math.PI * distance) - 4) {
			tankDrive.tankDrive(power, power);
		} else {
			tankDrive.tankDrive(0, 0);
		}
	}

	public void driveDirection(double angle, double distance) {
		turnAngle(angle);
		driveDistance(distance);
	}

	public void setPIDConstants() {
		// double p = 1;
		// double i = 2;
		// double d = 0;
		// double f = 1;

		double p = 0.000001;
		double i = 0.000001;
		double d = 0.000001;
		double f = 1;

		leftTopPID.setConstants(p, i, d, f);
		leftBotPID.setConstants(p, i, d, f);
		rightTopPID.setConstants(p, i, d, f);
		rightBotPID.setConstants(p, i, d, f);
	}

	@Override
	public void initDefaultCommand() {
		setDefaultCommand(new DriveWithJoysticks());
	}
}
