package primary;

import lejos.hardware.Sound;
import lejos.robotics.RegulatedMotor;

/**This thread assumes the robot is holding a block and only has to go to the appropriate zone. After the robot deposits the block in the correct zone, the thread ends and SearchAndMove is resumed.
 * @author Erik-Olivier Riendeau, Qingzhou Yang 2016
 *
 */
public class GoToZone extends Thread {
	
	// ----------------------- fields ----------------------------- //
		private RegulatedMotor leftMotor, rightMotor, clawMotor, elevateMotor;
		private Odometer odo;
		private Navigation nav;
		//be very careful when try to change the constants below
		private int  ROTATIONSPEED = 92 , FORWARDSPEED = 150, ACCELERATION;
		private USSensor sideSensor, frontSensor, heightSensor;
		private volatile static  boolean  complete_stop, thread_on;
		private double FinalX, FinalY;
		private boolean flag = true;
		private double Begheading;
		
		private final double DEG_MIN_ERR = 3, DEG_MAX_ERR = 357, CM_ERR = 0.3, DEG_ERR = 10;
		private static final double BANDCENTER = 15, BANDWIDTH = 3, STOP = 0, STOP_ERROR = 35;
		
		private static final double MAPSIZE = 182.88; 


		/**GoToZone constructor that allows most functionalities to the class ( all motors and ultra sonic sensors access)
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
		public GoToZone(RegulatedMotor leftMotor, RegulatedMotor rightMotor, RegulatedMotor clawMotor, RegulatedMotor elevateMotor, Navigation nav, Odometer odo, int FORWARDSPEED
				, int ROTATIONSPEED, int ACCELERATION, USSensor sideSensor, USSensor frontSensor , USSensor heightSensor, double FinalX, double FinalY){
			
			this.leftMotor = leftMotor;
			this.rightMotor = rightMotor;
			this.clawMotor = clawMotor;
			this.elevateMotor = elevateMotor;
			this.odo = odo;
			this.nav = nav;
			this.frontSensor = frontSensor;
			this.sideSensor = sideSensor;
			this.FinalX = FinalX;
			this.FinalY = FinalY;
//			this.FORWARDSPEED = FORWARDSPEED;
//			this.ROTATIONSPEED = ROTATIONSPEED;
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
					// TODO block has been captured and the robot needs to go to the zone
					double minAng;
					while (Math.abs(FinalX - odo.getX()) > CM_ERR || Math.abs(FinalY - odo.getY()) > CM_ERR) {			
						
						minAng = (Math.atan2(FinalY - odo.getY(), FinalX - odo.getX()))*(180.0/Math.PI);
						if (minAng < 0)
							minAng += 360.0;
						if(minAng > DEG_ERR){
							nav.turnTo(minAng, false);
						}			
						nav.setSpeeds(FORWARDSPEED, FORWARDSPEED);		
						
						if(frontSensor.getValue()<28){						
							Sound.beep();
							nav.turnBy(-80,true);
						if(flag){
							Begheading = odo.getAngle();
							flag = false;	
						}
						
							try{Thread.sleep(1500);}catch(Exception e){}
							
							//perform bangbang
							while(odo.getAngle() > ((Begheading+180)%360+STOP_ERROR) || odo.getAngle() < ((Begheading+180)%360-STOP_ERROR)){
								bangbang();
							}
						Sound.beep();
						}
					}
					elevateMotor.rotate(-280);
					clawMotor.rotate(-130);
					activateSearchAndMove();
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
		/** when the robot successfuly deposits its block in the zone, it can resume the search and move thread
		 * 
		 */
		public void activateSearchAndMove(){
			// pause current thread
			GoToZone.pauseThread();
			// re activate search and move
			SearchAndMove.resumeThread();
		}
		/** if the robot is heading to the zone and encounters an object, dodge this object
		 * 
		 */
		public void activateDodgeBlock(){
			// pause current thread
			GoToZone.pauseThread();
			// re activate dodge block
			DodgeObject.resumeThread();
		}
		
		private void bangbang(){
			double distance = sideSensor.getValue();
			if(distance > 250){
				distance = 250;
			}
			double distError = BANDCENTER - distance;//15- us.getvalue()

			
			if (Math.abs(distError) <= BANDWIDTH) { // Within limits, same speed
				nav.setSpeeds(FORWARDSPEED, FORWARDSPEED);
			}
		
			else if (distError >0) { // Medium close to the wall, move away faster	
				nav.setSpeeds(FORWARDSPEED, ROTATIONSPEED);
			}else{ //Far from wall, move closer
				nav.setSpeeds(ROTATIONSPEED, FORWARDSPEED);

			}
		}
}
