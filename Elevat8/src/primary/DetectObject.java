package primary;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.robotics.RegulatedMotor;


/**Goes to an object that has been detected and check whether that object is a wooden block or a styrofoam block. Looks at the values given by the sensor aimed above 5 cm, 
 * if this sensor is still detecting an object when the robot approaches then the robot is close to a brick. In this case, starts DodgeObject and pause this thead. If the 
 * sensor above 5 cm detects nothing, then the robot is close to a styrofoam block. Start Capture and pause this thread.
 * @author Erik-Olivier Riendeau, 2016
 *
 */
public class DetectObject extends Thread {
	
	// ------------------ fields ------------------------ //
	private RegulatedMotor leftMotor, rightMotor;
	private Odometer odo;
	private Navigation nav;
	private int  ROTATIONSPEED, FORWARDSPEED, ACCELERATION;
	private USSensor sideSensor, frontSensor, heightSensor;
	private static volatile boolean complete_stop;
	private static volatile boolean thread_on;
	private static boolean isBlueBlock = false; 
	/**Detect object constructor that allows most functionalities to the class ( all motors and ultra sonic sensors access)
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
	public DetectObject(RegulatedMotor leftMotor, RegulatedMotor rightMotor, Navigation nav, Odometer odo, int FORWARDSPEED
			, int ROTATIONSPEED, int ACCELERATION, USSensor sideSensor, USSensor frontSensor , USSensor heightSensor){
		
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.odo = odo;
		this.nav = nav;
		this.frontSensor = frontSensor;
		this.sideSensor = sideSensor;
		this.heightSensor = heightSensor;
		this.FORWARDSPEED = FORWARDSPEED;
		this.ROTATIONSPEED = ROTATIONSPEED;
		
		
		complete_stop = false;
		thread_on = true;
	}
	
	public void run(){
		while(!complete_stop){
			while(thread_on){
				// algorithm
				// TODO
		//move robot forward to a block till the front distance less than certain distance 
//				try{Thread.sleep(3000);}catch(Exception e){}
				
				while(frontSensor.getValue() > 10){
					leftMotor.setSpeed(FORWARDSPEED);
					rightMotor.setSpeed(FORWARDSPEED);
					
					leftMotor.forward();
					rightMotor.forward();
				}
				leftMotor.stop();
				rightMotor.stop();
//				try{Thread.sleep(3000);}catch(Exception e){}
		//turn the robot cw 90 degrees in order to use side sensors to identify the type of block	
				nav.turnBy(-120,true);
				
		//check if nav.turnBy(-100,true) 		
				try{Thread.sleep(3000);}catch(Exception e){}
				
				if(sideSensor.getValue() < 30 && heightSensor.getValue()>30){
					isBlueBlock = true;
					Sound.twoBeeps();
				}
					else{
							isBlueBlock = false;
							Sound.buzz();
					
					}
				while (Button.waitForAnyPress() != Button.ID_ESCAPE);
				System.exit(0);	
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
	
	/**Activate capture if the robot has detected a styrofoam block and then pause the current thread
	 * 
	 */
	public void activateCapture(){
		// pause current thread
		DetectObject.pauseThread();
		// capture the block
		Capture.resumeThread();
	}
	/** Activates dodge object if the robot has detected a wooden brick and then pause the current thread
	 * 
	 */
	public void activateDodgeObject(){
		// pause current thread
		DetectObject.pauseThread();
		// activate dodge object
		DodgeObject.resumeThread();
		
	}
}
