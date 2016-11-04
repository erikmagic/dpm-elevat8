package primary;

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
	public void travelTo(){
		// TODO
	}
	/**
	 * Rotates the robot to a desired angle
	 */
	public void rotateTo(){
		// TODO
	}
	/**
	 * Rotates the robot by a desired angle
	 */
	public void rotateBy(){
		// TODO
	}
}
