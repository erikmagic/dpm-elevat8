package src.primary;

import lejos.robotics.RegulatedMotor;



/**
 * @author Erik-Olivier Riendeau,Qingzhou Yang, 2016
 * Navigation class that contains useful methods for controlling the motors attached to the wheels.
 * 
 */
public class Navigation {
	
	// ----------------------- fields ---------------------------- //
	final static int FAST = 200, SLOW = 140, ACCELERATION = 4000;
	final static double DEG_ERR = 3.0, CM_ERR = 1.0;
	private Odometer odometer;
	private RegulatedMotor leftMotor, rightMotor;
	
	// ----------------------- constructor ----------------------- //
	/**
	 * Constructor called in the Main to initialize the object using the parameters in the Main
	 */
	public Navigation(Odometer odo) {
		this.odometer = odo;
		
		RegulatedMotor[] motors = this.odometer.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];
		
		// set acceleration
		this.leftMotor.setAcceleration(ACCELERATION);
		this.rightMotor.setAcceleration(ACCELERATION);
	}

	/*
	 * Functions to set the motor speeds jointly
	 */
	public void setSpeeds(float lSpd, float rSpd) {
		this.leftMotor.setSpeed((int) lSpd);
		this.rightMotor.setSpeed((int) rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	public void setSpeeds(int lSpd, int rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	/*
	 * Float the two motors jointly
	 */
	public void setFloat() {
		this.leftMotor.stop();
		this.rightMotor.stop();
		this.leftMotor.flt(true);
		this.rightMotor.flt(true);
	}

	
	/**
	 * Simply goes forwards 
	 */
	public void goForward(double distance) {
		this.travelTo(Math.cos(Math.toRadians(this.odometer.getAng())) * distance, Math.cos(Math.toRadians(this.odometer.getAng())) * distance);
	}
	/**
	 * Simply goes backwards
	 */
	public void goBackwards(double distance){
		this.travelTo(-Math.cos(Math.toRadians(this.odometer.getAng())) * distance, Math.cos(Math.toRadians(this.odometer.getAng())) * distance);
	}

	/**
	 * Travel to desired location , updating the robot's heading as he goes along. 
	 */
	public void travelTo(double x, double y) {
		double minAng;
		while (Math.abs(x - odometer.getX()) > CM_ERR || Math.abs(y - odometer.getY()) > CM_ERR) {
			minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / Math.PI);
			if (minAng < 0)
				minAng += 360.0;
			this.turnTo(minAng, false);
			this.setSpeeds(FAST, FAST);
		}
		this.setSpeeds(0, 0);
	}
	/**
	 * Rotates the robot to a desired angle
	 */
	public void turnTo(double angle, boolean stop) {

		double error = angle - this.odometer.getAng();

		while (Math.abs(error) > DEG_ERR) {

			error = angle - this.odometer.getAng();

			if (error < -180.0) {
				this.setSpeeds(-SLOW, SLOW);
			} else if (error < 0.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else if (error > 180.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else {
				this.setSpeeds(-SLOW, SLOW);
			}
		}

		if (stop) {
			this.setSpeeds(0, 0);
		}
	}
	/**
	 * Rotates the robot by a desired angle
	 */
	public void turnBy(double angle, boolean stop){
		double endAngle = this.odometer.getAng() + angle;
		double error = angle;
		while (Math.abs(error) > DEG_ERR) {

			error = endAngle - this.odometer.getAng();

			if (error < -180.0) {
				this.setSpeeds(-SLOW, SLOW);
			} else if (error < 0.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else if (error > 180.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else {
				this.setSpeeds(-SLOW, SLOW);
			}
		}

		if (stop) {
			this.setSpeeds(0, 0);
		}
	}
	
	// calculate degrees of the wheels'rotation when the robot needs to rotate
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	// calculate degrees of the wheels'rotation when the robot is traveling in a straight line
	private static int convertDistance(double radius, double distance) {
		// ( distance / radius) * (180 / PI) in degrees
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
}
