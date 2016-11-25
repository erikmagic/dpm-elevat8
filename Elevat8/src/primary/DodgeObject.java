package primary;

import lejos.hardware.Sound;
import lejos.robotics.RegulatedMotor;

/**
 * This class dodges a wooden brick in most case by using the front and side
 * sensors. It might occur that the class dodges a styrofoam block if the robot
 * already holds enough blocks and is going back to its zone. This class use the
 * bang bang operatio to dodge blocks. zone.
 * 
 * @author Erik-Olivier Riendeau, 2016
 *
 */
public class DodgeObject extends Thread {

	// ------------------ fields ------------------------ //
	private RegulatedMotor leftMotor, rightMotor;
	private Odometer odo;
	private Navigation nav;
	private int ROTATIONSPEED = 92, FORWARDSPEED = 150, ACCELERATION, STOP = 0;
	private USSensor sideSensor, frontSensor, heightSensor;
	private static volatile boolean complete_stop;
	private static volatile boolean thread_on;
	private static final double BANDCENTER = 15, BANDWIDTH = 3, STOP_ERROR = 35;

	/**
	 * Dodge object constructor that allows most functionalities to the class (
	 * all motors and ultra sonic sensors access)
	 * 
	 * @param leftMotor
	 * @param rightMotor
	 * @param nav
	 * @param odo
	 * @param FORWARDSPEED
	 * @param ROTATIONSPEED
	 * @param ACCELERATION
	 * @param sideSensor
	 * @param frontSensor
	 * @param heightSensor
	 */
	public DodgeObject(RegulatedMotor leftMotor, RegulatedMotor rightMotor, Navigation nav, Odometer odo,
			int FORWARDSPEED, int ROTATIONSPEED, int ACCELERATION, USSensor sideSensor, USSensor frontSensor,
			USSensor heightSensor) {

		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.odo = odo;
		this.nav = nav;
		this.frontSensor = frontSensor;
		this.sideSensor = sideSensor;
		complete_stop = false;
		thread_on = true;
//		this.ROTATIONSPEED = ROTATIONSPEED;
//		this.FORWARDSPEED = FORWARDSPEED;
//		this.ACCELERATION = ACCELERATION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {

		}
		
		while (!complete_stop) {
			while (thread_on) {
				// algorithm
				// MYCODE
				// TODO: implement robustness with front sensor dodging
				double Begheading = odo.getAngle();
				// perform bangbang
				
//				// test, log the result and exit
//				Logger.log("dodge object worked");
//				System.exit(0);


				while(odo.getAngle() > ((Begheading+180)%360+STOP_ERROR) || odo.getAngle() < ((Begheading+180)%360-STOP_ERROR)){
					bangbang();
				}
				nav.setSpeeds(STOP, STOP);
				Sound.beep();
				activateSearchAndMove();

			}
		}
	}

	/**
	 * If the robot's position is between two thresholds, the robot will just
	 * advance. The first threshold is the minimum one and the second threshold
	 * is the maximum one. If the robot's position is less than the minimum
	 * threshold, then the robot will repeatedly turn to increase its position
	 * by getting further from the object the sensor is detecting. Else if the
	 * robot's position is more than the maximum threshold, the robot will
	 * repeatedly turn to lower its position by getting closer to what the
	 * sensor is detecting.
	 * 
	 */
	private void bangbang(){
		double distance = sideSensor.getValue();
		if(distance > 250){
			distance = 250;
		}
		double distError = BANDCENTER - distance;//15- us.getvalue()

		
		if (Math.abs(distError) <= BANDWIDTH) { // Within limits, same speed
			Sound.beep();
			nav.setSpeeds(FORWARDSPEED, FORWARDSPEED);
		}
	
		else if (distError >0) { // Medium close to the wall, move away faster	
			nav.setSpeeds(FORWARDSPEED, ROTATIONSPEED);
		}else{ //Far from wall, move closer
			nav.setSpeeds(ROTATIONSPEED, FORWARDSPEED);

		}
	}

	/**
	 * Pause the thread by deactivating the inner loop
	 * 
	 */
	public static void pauseThread() {
		thread_on = false;
	}

	/**
	 * Resume the thread by re-activating the inner loop
	 * 
	 */
	public static void resumeThread() {
		thread_on = true;
	}

	/**
	 * Terminates everything in the run by stopping the outer loop
	 * 
	 */
	public static void stopThread() {
		complete_stop = true;
	}

	/**
	 * When the robot is looking for blocks and finished dodging a brick, resume
	 * search and move thread
	 * 
	 */
	public static void activateSearchAndMove() {
		// pause current thread
		DodgeObject.pauseThread();
		// activate search and move
		SearchAndMove.resumeThread();
	}

	/**
	 * When the robot is going to the zone and encountered a block, dodge it and
	 * then continue going to the zone
	 * 
	 */
	public static void activateGoToZone() {
		// pause current Thread
		DodgeObject.pauseThread();
		// activate search and move
		SearchAndMove.resumeThread();
	}

}
