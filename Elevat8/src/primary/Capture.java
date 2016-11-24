package primary;

import lejos.robotics.RegulatedMotor;

/**Capture class that captures a styrofoam block once the detect object has detected such a block. The 
 * @author Erik-Olivier Riendeau, 2016
 *
 */
public class Capture extends Thread {
	
	// ----------------------- fields ----------------------------- //
	private RegulatedMotor leftMotor, rightMotor, clawMotor, elevateMotor;
	private Odometer odo;
	private Navigation nav;
	private int  ROTATIONSPEED, FORWARDSPEED, ACCELERATION, frontD, blockCount;
	private USSensor sideSensor, frontSensor, heightSensor;
	private static volatile boolean complete_stop, thread_on;
	
	
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
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {

		}
		
		while(!complete_stop){
			while(thread_on){
				// algorithm
				// describe how the capture works in the class comment
				leftMotor.setSpeed(40);
				rightMotor.setSpeed(40);
				nav.turnTo((odo.getAngle()+90)%360,true);
				frontD = frontSensor.getValue();
//				while(frontD > 10){
//					
					leftMotor.setSpeed(FORWARDSPEED);
					rightMotor.setSpeed(FORWARDSPEED);
					leftMotor.forward();
					rightMotor.forward();
					try{
						Thread.sleep(3000);
					}catch(Exception e){
						
					}
					frontD = frontSensor.getValue();
//				}
				//while(frontD > 3 && frontD < 15){
					leftMotor.setSpeed(0);
					rightMotor.setSpeed(0);
					clawMotor.setSpeed(130);
					clawMotor.rotate(180);
					elevateMotor.rotate(270);
					try{
						Thread.sleep(1000);
					}catch(Exception e){
					}
				
						blockCount++;
						//activateGoToZone();
						//break;
				
					//else{
												//clawMotor.rotate(-110);
						//elevateMotor.rotate(-270);
						//continue;
						//clawMotor.rotate(110);
						//nav.turnTo((odo.getAngle()+45)%360, false);
						//nav.turnTo((odo.getAngle()-45)%360, false);
						//clawMotor.rotate(-110);
					//}
				//}
				
				// test log the result and exit
				Logger.log("capture worked");
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
