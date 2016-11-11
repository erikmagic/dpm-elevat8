package primary;

import org.freedesktop.dbus.test.profile.Log;

import lejos.robotics.RegulatedMotor;

/**
 * @author Erik-Olivier Riendeau, 2016
 * Navigation class that contains useful methods for controlling the motors attached to the wheels.
 * 
 */
public class Navigation {
	
	// ----------------------- fields ---------------------------- //
	private RegulatedMotor leftMotor, rightMotor;
	private Odometer odo;
	private double WHEELRADIUS, TRACKSIZE;
	private int  ROTATIONSPEED, FORWARDSPEED, ACCELERATION;
	private final double DEG_MIN_ERR = 3, DEG_MAX_ERR = 357, CM_ERR = 0.3;
	
	// ----------------------- constructor ----------------------- //
	/**
	 * Constructor called in the Main to initialize the object using the parameters in the Main
	 *
	 * @param leftMotor
	 * @param rightMotor
	 * @param odo
	 * @param ROTATIONSPEED
	 * @param FORWARDSPEED
	 * @param ACCELERATION
	 * @param WHEELRADIUS
	 * @param TRACKSIZE
	 */
	public Navigation(RegulatedMotor leftMotor, RegulatedMotor rightMotor, Odometer odo, int ROTATIONSPEED, int FORWARDSPEED
			, int ACCELERATION, double WHEELRADIUS, double TRACKSIZE){
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.odo = odo;
		this.ROTATIONSPEED = ROTATIONSPEED;
		this.FORWARDSPEED = FORWARDSPEED;
		this.ACCELERATION = ACCELERATION;
		this.WHEELRADIUS = WHEELRADIUS;
		this.TRACKSIZE = TRACKSIZE;
	}
	
	/**
	 * Simply goes forwards 
	 */
	public void goForwards(){
		// TODO
	}
	/**
	 * Simply goes backwards
	 */
	public void goBackwards(){
		// TODO
	}
	/**
	 * Travel to desired location , updating the robot's heading as he goes along. 
	 */
	public void travelTo(double x, double y) {
		double minAng;
		while (Math.abs(x - odo.getX()) > CM_ERR || Math.abs(y - odo.getY()) > CM_ERR) {
			minAng = (Math.atan2(y - odo.getY(), x - odo.getX()));
			if (minAng < 0)
				minAng += 360.0;
			this.turnTo(minAng, false);
			this.setSpeeds(FORWARDSPEED, FORWARDSPEED);
			Logger.log(Double.toString(odo.getX()));
		}
		Logger.log(Double.toString(odo.getX()));
		this.setSpeeds(0, 0);
	}
	
	/**
	 * Rotates the robot to a desired angle
	 */
	public void turnTo(double angle, boolean stop){
		double error = angle - this.odo.getAngle();

		while (Math.abs(error) > DEG_MIN_ERR && Math.abs(error) < DEG_MAX_ERR) {

			error = angle - this.odo.getAngle();
			Logger.log(Double.toString(odo.getAngle()));
			if (error < -180.0) {
				this.setSpeeds(-ROTATIONSPEED, ROTATIONSPEED);
			} else if (error < 0.0) {
				this.setSpeeds(ROTATIONSPEED, -ROTATIONSPEED);
			} else if (error > 180.0) {
				this.setSpeeds(ROTATIONSPEED, -ROTATIONSPEED);
			} else {
				this.setSpeeds(-ROTATIONSPEED, ROTATIONSPEED);
			}
		}

		if (stop) {
			this.setSpeeds(0, 0);
}
	}
	/**
	 * Rotates the robot by a desired angle
	 */
	/*
	public void turnBy(double angle, boolean stop){
		double rotation = (angle + odo.getAngle())%360;
		if ( angle > 0){
			while ( odo.getAngle() < rotation){
				rightMotor.forward();
				leftMotor.backward();
			}
		} else {
			while (odo.getAngle() < rotation){
				rightMotor.backward();
				leftMotor.forward();
			}
		} 
		if (stop) {
			this.setSpeeds(0, 0);
		}
	}
	*/
	public void goForward(double distance) {
		this.travelTo(Math.cos(this.odo.getAngle()) * distance, Math.sin(this.odo.getAngle()) * distance);
	}
	
	public void goBackwards(double distance){
		this.travelTo(-Math.cos(this.odo.getAngle()) * distance, Math.cos(this.odo.getAngle()) * distance);
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
	
	private static int convertDistance(double radius, double distance) {
		// ( distance / radius) * (180 / PI) in degrees
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius,  width * angle );
	}
}
