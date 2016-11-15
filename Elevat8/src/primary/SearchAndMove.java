package primary;

import lejos.robotics.RegulatedMotor;

/**Algorithm to move the robot when it did not detect any blocks. Move in a square fashion until blocks are found.
 * @author Erik-Olivier Riendeau, 2016
 *
 */
public class SearchAndMove extends Thread {
	
	// ---------------------- fields ----------------------- //
	private RegulatedMotor leftMotor, rightMotor;
	private Odometer odo;
	private Navigation nav;
	private int  ROTATIONSPEED, FORWARDSPEED, ACCELERATION;
	private USSensor sideSensor, frontSensor, heightSensor;
	private static volatile boolean complete_stop, thread_on;
	
	/**Search and move constructor that allows most functionalities to the class ( all motors and ultra sonic sensors access)
	 * @param leftMotor
	 * @param rightMotor
	 * @param ACCELERATION
	 * @param FORWARDSIZE
	 * @param ROTATIONSPEED
	 * @param nav
	 * @param odo
	 * @param sideSensor
	 * @param frontSensor
	 * @param heightSensor
	 */
	public SearchAndMove(RegulatedMotor leftMotor, RegulatedMotor rightMotor, Navigation nav, Odometer odo, int FORWARDSPEED
			, int ROTATIONSPEED, int ACCELERATION, USSensor sideSensor, USSensor frontSensor , USSensor heightSensor){ // missing board and zones coordinates
		
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.odo = odo;
		this.nav = nav;
		this.frontSensor = frontSensor;
		this.sideSensor = sideSensor;
		complete_stop = false;
		thread_on = true;
	}
	
	/**Runnable instance of the search and move. Can be paused with pauseThread or stopped with stopThread.
	 * The algorithm cannot feature navigation methods or any methods that take time to complete because
	 * if it is paused or stopped, the effect needs to be immediate.
	 * 
	 */
	public void run(){
		while(!complete_stop){
			while(thread_on){
				// algorithm
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
	public static void resumeThread(){
		thread_on = true;
	}
	/**Terminates everything in the run by stopping the outer loop
	 * 
	 */
	public static void stopThread(){
		complete_stop = true;
	}
	/**Activate detect object if the robot detects any objects and pause current thread
	 * 
	 */
	public static void activatedDetectObject(){
		// pause the current thread
		SearchAndMove.pauseThread();
		// activate detect object
		DetectObject.resumeThread();
	}
}
