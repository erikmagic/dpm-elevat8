package primary;

import lejos.robotics.RegulatedMotor;

/**Capture class that captures a styrofoam block once the detect object has detected such a block.
 * @author Erik-Olivier Riendeau, 2016
 *
 */
public class Capture extends Thread {
	
	// ----------------------- fields ----------------------------- //
	private RegulatedMotor leftMotor, rightMotor, clawMotor, elevateMotor;
	private Odometer odo;
	private Navigation nav;
	private int  ROTATIONSPEED, FORWARDSPEED, ACCELERATION;
	private USSensor sideSensor, frontSensor, heightSensor;
	private static volatile boolean complete_stop, thread_on;
	
	public int blockCount;
	
	/** Capture constructor that allows most functionalities to the class ( all motors and ultra sonic sensors access)
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
	public Capture(RegulatedMotor leftMotor, RegulatedMotor rightMotor, RegulatedMotor clawMotor, RegulatedMotor elevateMotor,Navigation nav, Odometer odo, int FORWARDSPEED
			, int ROTATIONSPEED, int ACCELERATION, USSensor sideSensor, USSensor frontSensor , USSensor heightSensor){
		
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.clawMotor = clawMotor;
		this.elevateMotor = elevateMotor;
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
				int frontD;
				nav.turnBy(90,false);
				if(frontSensor.getValue() > 20){
					nav.goForward(10);
				}
				while(frontSensor.getValue() >= 10){
					nav.goForward(2);
					frontD = frontSensor.getValue();
					clawMotor.rotate(90,true);
					if(frontD < frontSensor.getValue()){
						continue;
					}
					else{
						clawMotor.rotate(85);
						elevateMotor.rotate(270);
						if(frontSensor.getValue() > 15){
							activateGoToZone();
							break;
						}
						else{
							clawMotor.rotate(-85);
							elevateMotor.rotate(-270);
							clawMotor.rotate(50);
							nav.turnBy(45, false);
							nav.turnBy(-45, false);
							continue;
						}
					}
				}
				
				
				
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
}
