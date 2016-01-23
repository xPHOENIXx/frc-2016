package org.usfirst.frc.team166.robot;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into to a variable name. This provides
 * flexibility changing wiring, makes checking the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {

	// Joysticks
	public static final int CopilotJoystickNumber = 2;

	// PWMs

	public static final int LeftShooterMotor = 4;
	public static final int RightShooterMotor = 5;
	public static final int ShooterAngleMotor = 6;

	public static class Encoder {
		public static final int ShooterLeftChannelA = 12;
		public static final int ShooterLeftChannelB = 13;
		public static final int ShooterRightChannelA = 14;
		public static final int ShooterRightChannelB = 15;
	}

	public static final int ShooterPotAngle = 16;

	// For example to map the left and right motors, you could define the
	// following variables to use with your drivetrain subsystem.
	// public static int leftMotor = 1;
	// public static int rightMotor = 2;

	// If you are using multiple modules, make sure to define both the port
	// number and the module. For example you with a rangefinder:
	// public static int rangefinderPort = 1;
	// public static int rangefinderModule = 1;
}
