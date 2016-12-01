package primary;

import org.freedesktop.dbus.test.profile.Log;

import lejos.robotics.RegulatedMotor;

/** * Navigation class that contains useful methods for controlling the motors attached to the wheels, or any motors.
 * @author Erik-Olivier Riendeau, 2016
 * 
 */
public class Navigation {
	
	// ----------------------- fields ---------------------------- //
	private RegulatedMotor leftMotor, rightMotor;
	private Odometer odo;
	private double WHEELRADIUS, TRACKSIZE;
	private int  ROTATIONSPEED, FORWARDSPEED, ACCELERATION;
	private final double DEG_MIN_ERR = 3, DEG_MAX_ERR = 357, CM_ERR = 0.3, DEG_ERR = 10;
	
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
	 * Travel to desired location , updating the robot's heading as he goes along. 
	 * @param x - in cm
	 * @param y - in cm
	 */
	public void travelTo(double x, double y) {
		double minAng;
		while (Math.abs(x - odo.getX()) > CM_ERR || Math.abs(y - odo.getY()) > CM_ERR) {
			minAng = (Math.atan2(y - odo.getY(), x - odo.getX()))*(180.0/Math.PI);
			if (minAng < 0)
				minAng += 360.0;
			if(minAng > DEG_ERR){
				this.turnTo(minAng, false);
			}			
			this.setSpeeds(FORWARDSPEED, FORWARDSPEED);
		}
		this.setSpeeds(0, 0);
	}
	
	/**
	 * Rotates the robot to a desired angle. Will use the shortest rotation to the desired angle and 
	 * control the motors appropriately. 
	 * @param angle - in degrees from 0 to 360
	 * @param stop - if true, the robot stops after turning
	 */
	public void turnTo(double angle, boolean stop){
		double error = angle - this.odo.getAngle();

		while (Math.abs(error) > DEG_MIN_ERR && Math.abs(error) < DEG_MAX_ERR) {

			error = angle - this.odo.getAngle();
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
	/**Go forwards for a certain distance in centimeters.
	 * @param distance in cm
	 */
	public void goForward(double distance) {
		this.travelTo(Math.cos(this.odo.getAngle()) * distance, Math.sin(this.odo.getAngle()) * distance);
	}
	
	/**Go backwards for a certain distance in centimeters.
	 * @param distance in cm
	 */
	public void goBackwards(double distance){
		this.travelTo(-Math.cos(this.odo.getAngle()) * distance, Math.sin(this.odo.getAngle()) * distance);
	}
	
	/**Private helper method that set the speeds of the motors in rotation/s together.
	 * @param lSpd in rotations/min
	 * @param rSpd in rotations/min
	 */
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
	
	/**Makes the robot turn by a certain angle.
	 * @param angle - in degrees
	 * @param stop - if true, then the robot stops after rotating.
	 */
	public void turnBy(double angle, boolean stop){

		if(this.odo.getAngle()+angle > 0){
		turnTo(this.odo.getAngle()+angle, stop);}
		else{
			turnTo(360+(this.odo.getAngle()+angle), stop);
		}
	}
}
