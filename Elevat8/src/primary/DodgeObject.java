package primary;

import lejos.robotics.RegulatedMotor;

/**This class dodges a wooden brick in most case by using the front and side sensors. It migh occur that the class dodges a styrofoam block if the robot already holds some and is going bacl to the
 * zone.
 * @author Erik-Olivier Riendeau, 2016
 *
 */
public class DodgeObject extends Thread {

	// ------------------ fields ------------------------ //
		private RegulatedMotor leftMotor, rightMotor;
		private Odometer odo;
		private Navigation nav;
		private int  ROTATIONSPEED, FORWARDSPEED, ACCELERATION;
		private USSensor sideSensor, frontSensor, heightSensor;
		private static volatile boolean complete_stop;
		private static volatile boolean thread_on;
		
		/**Dodge object constructor that allows most functionalities to the class ( all motors and ultra sonic sensors access)
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
		public DodgeObject(RegulatedMotor leftMotor, RegulatedMotor rightMotor, Navigation nav, Odometer odo, int FORWARDSPEED
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
//						//MY CODE
//						//compute the error from bandCenter
//						int distError = bandCenter-distance;
//						int diff;
//						
//						if (Math.abs(distError) <= bandwidth) { // Case 1: Error in bounds, no adjustment
//							leftMotor.setSpeed(motorStraight);
//							rightMotor.setSpeed(motorStraight);            
//							leftMotor.forward();
//							rightMotor.forward(); 
//						}
//						else if (distError > 25) { // Case 2: Really close to wall, reorientate (go backward)
//							diff=calcProp(distError); // Get correction value and apply
//							leftMotor.setSpeed(motorStop);
//							rightMotor.setSpeed(motorStraight+diff);
//							leftMotor.forward();
//							rightMotor.backward();
//						}
//						else if (distError > 20) { // Case 3: positive error, move away from wall
//							diff=calcProp(distError); // Get correction value and apply
//							leftMotor.setSpeed(motorStraight+diff);
//							rightMotor.setSpeed(motorStraight-diff);
//							leftMotor.forward(); // Hack - leJOS bug
//							rightMotor.forward();
//						}
//						else if (distError > 0) { // Case 3: Too far from wall, move closer
//							diff=calcProp(distError); // Get correction value and apply
//							leftMotor.setSpeed(motorStraight+diff);
//							rightMotor.setSpeed(motorStraight-diff);
//							leftMotor.forward(); 
//							rightMotor.forward();
//						}
//						else if (distError < 0) { // Case 3: negative error, move towards wall
//							diff=calcProp(distError); // Get correction value and apply
//							leftMotor.setSpeed(motorStraight-diff);
//							rightMotor.setSpeed(motorStraight+diff);
//							leftMotor.forward(); // Hack - leJOS bug
//							rightMotor.forward();
//						}				
					// TODO wall following code to dodge a brick
				}
			}
		}
		
//		int calcProp (int diff) {
//			int correction;
//			// PROPORTIONAL: Correction is proportional to magnitude of error
//			if (diff < 0) diff=-diff;
//			correction = (int)(PROPCONST *(double)diff);
//			if (correction >= motorStraight) correction = MAXCORRECTION;
//			return correction;
//			}
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
		
		/**When the robot is looking for blocks and finished dodging a brick, resume search and move thread
		 * 
		 */
		public static void activateSearchAndMove(){
			// pause current thread
			DodgeObject.pauseThread();
			// activate search and move
			SearchAndMove.resumeThread();
		}
		/**When the robot is going to the zone and encountered a block, dodge it and then continue going to the zone
		 * 
		 */
		public static void activateGoToZone(){
			// pause current Thread
			DodgeObject.pauseThread();
			// activate search and move
			SearchAndMove.resumeThread();
		}
		
		
}
