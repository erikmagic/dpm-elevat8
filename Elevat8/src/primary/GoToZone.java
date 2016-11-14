package src.primary;

import lejos.robotics.RegulatedMotor;

/**This thread assumes the robot is holding a block and only has to go to the appropriate zone. After the robot deposits the block in the correct zone, the thread ends and SearchAndMove is resumed.
 * @author Erik-Olivier Riendeau, 2016
 *
 */
public class GoToZone extends Thread {
	
	// ----------------------- fields ----------------------------- //
		private RegulatedMotor leftMotor, rightMotor;
		private Odometer odo;
		private Navigation nav;
		private int  ROTATIONSPEED, FORWARDSPEED, ACCELERATION;
		private USSensor sideSensor, frontSensor, heightSensor;
		private volatile static  boolean  complete_stop, thread_on;
		
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
		public GoToZone(RegulatedMotor leftMotor, RegulatedMotor rightMotor, Navigation nav, Odometer odo, int FORWARDSPEED
				, int ROTATIONSPEED, int ACCELERATION, USSensor sideSensor, USSensor frontSensor , USSensor heightSensor){
			
			this.leftMotor = leftMotor;
			this.rightMotor = rightMotor;
			this.odo = odo;
			this.nav = nav;
			this.frontSensor = frontSensor;
			this.sideSensor = sideSensor;
			complete_stop = false;
			thread_on = true;
		}
		
		public void run(){
			while(!complete_stop){
				while(thread_on){
					// algorithm
					// TODO block has been captured and the robot needs to go to the zone
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
}
