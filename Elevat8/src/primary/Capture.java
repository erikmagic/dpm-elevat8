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
		this.FORWARDSPEED = FORWARDSPEED;
		complete_stop = false;
		thread_on = true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		leftMotor.setSpeed(60);
		rightMotor.setSpeed(60);
		nav.turnTo((odo.getAngle()+105)%360,true);
		while(!complete_stop){
			while(thread_on){
				// algorithm
				// describe how the capture works in the class comment
				frontD = frontSensor.getValue();
				System.out.println("Initial: "+frontD);
				while(frontD > 5){					
					leftMotor.setSpeed(FORWARDSPEED);
					rightMotor.setSpeed(FORWARDSPEED);
					leftMotor.forward();
					rightMotor.forward();
					try{
						Thread.sleep(1000);
					}catch(Exception e){
						
					}
					frontD = frontSensor.getValue();
					System.out.println("Capture: "+frontD);
					while(frontD > 3 && frontD < 9){
						leftMotor.stop();
						rightMotor.stop();
						clawMotor.setSpeed(130);
						clawMotor.rotate(130);
						elevateMotor.rotate(280);
						System.out.println("Elevate: "+frontSensor.getValue());
						try{
							Thread.sleep(1000);
						}catch(Exception e){
						}
						if(frontSensor.getValue() > (2*frontD) ){
							blockCount++;
							break;
						}
						else{
							clawMotor.rotate(-125);
							elevateMotor.rotate(-280);
							System.out.println("Capture incompleted. Let's try again!");
							break;
						}
					}
					if(blockCount == 1){
						System.out.println("capture worked");
						activateGoToZone();
						break;
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
