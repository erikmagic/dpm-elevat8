package primary;

import lejos.robotics.RegulatedMotor;

/**Capture class that captures a Styrofoam block once the detect object has detected such a block.
 * @author Erik-Olivier Riendeau, 2016
 *
 */
public class Capture extends Thread {
	
	// ----------------------- fields ----------------------------- //
	private RegulatedMotor leftMotor, rightMotor, clawMotor, liftMotor;
	private Odometer odo;
	private Navigation nav;
	private int  ROTATIONSPEED, FORWARDSPEED, ACCELERATION;
	private USSensor sideSensor, frontSensor, heightSensor;
	private static volatile boolean complete_stop, thread_on;
	private boolean objectCaptured = false;
	private int blockCount = 0;
	
	/** Capture constructor that allows most functionalities to the class ( all motors and ultrasonic sensors access)
	 * @param leftMotor
	 * @param rightMotor
	 * @param clawMotor
	 * @param nav
	 * @param odo
	 * @param FORWARDSPEED
	 * @param ROTATIONSPEED
	 * @param ACCELERATION
	 * @param sideSensor
	 * @param frontSensor
	 * @param heightSensor
	 */
	public Capture(RegulatedMotor leftMotor, RegulatedMotor rightMotor, RegulatedMotor clawMotor, Navigation nav, Odometer odo, int FORWARDSPEED
			, int ROTATIONSPEED, int ACCELERATION, USSensor sideSensor, USSensor frontSensor , USSensor heightSensor){
		
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.clawMotor = clawMotor;
		//this.liftMotor = liftMotor;
		this.odo = odo;
		this.nav = nav;
		this.frontSensor = frontSensor;
		this.sideSensor = sideSensor;
		complete_stop = false;
		thread_on = true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		while(!complete_stop){
			while(thread_on){
				// algorithm
				// TODO implement an algorithm to capture a block
				/*
				 * STEP 1: update robot orientation and position for capture phase
				 * STEP 2: activate clawMotor to position block
				 * STEP 3: re-update position (relative to the block) to maximize block grip
				 * STEP 4: activate clawMotor to hold block
				 * STEP 5: activate liftMotor to lift block
				 * STEP 6: confirm capture with sensor data
				 * STEP 7: update values (objectCaptured, blockCount, etc.)
				 * STEP 8: start/initiate next phase
				 */
			}
		}
	}
	/**Pause the thread by deactivating the inner loop
	 * 
	 */
	public static void pauseThread(){
		thread_on = false;
	}
	/**Resume the thread by re-activating the inner loop
	 * 
	 */
	public static  void resumeThread(){
		thread_on = true;
	}
	/**Terminates everything in the run by stopping the outer loop
	 * 
	 */
	public static void stopThread(){
		complete_stop = true;
	}
	/**ActivateGoToZone pause the current thread and resumes the thread Go To Zone 
	 * 
	 */
	public void activateGoToZone(){
		// pause current thread
		Capture.pauseThread();
		// activate go to zone 
		GoToZone.resumeThread();
	}
	/** isObjectCaptured confirms that object has been successfully captured
	 * 
	 * @return objectCaptured
	 */
	public boolean isObjectCaptured(){
		return this.objectCaptured;
	}
	/**
	 * getBlockCount getter method to retrieve block count 
	 * @return blockCount
	 */
	public int getBlockCount(){
		return this.blockCount;
	}
}
