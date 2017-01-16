package primary;

import lejos.hardware.Sound;
import lejos.robotics.RegulatedMotor;

/**Algorithm to move the robot when it did not detect any blocks. Move in a square fashion until blocks are found.
 * Notice: the entire algorithm is subject to drastic change after demo. This is only the prototype.
 * @author Dongwen Wang, Erik-Olivier Riendeau, 2016
 * @version 1.1
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
	private static final double MAPSIZE = 304.8; 
	private static final float DISTANCE_TOLERANCE = 29;
	private static final int DIVISION = 10, SCAN_INTERVAL = 10, DIVISION_FORSCAN = 5, STOP = 0;
	
	
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
	public SearchAndMove(RegulatedMotor leftMotor, RegulatedMotor rightMotor, Navigation nav, Odometer odo, int ACCELERATION
			, int FORWARDSPEED, int ROTATIONSPEED, USSensor sideSensor, USSensor frontSensor , USSensor heightSensor){ // missing board and zones coordinates
		
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.odo = odo;
		this.nav = nav;
		this.frontSensor = frontSensor;
		this.sideSensor = sideSensor;
		this.ROTATIONSPEED = ROTATIONSPEED;
		this.FORWARDSPEED = FORWARDSPEED;
		this.ACCELERATION = ACCELERATION;
		complete_stop = false;
		thread_on = true;
	}
	
	/**Runnable instance of the search and move. Can be paused with pauseThread or stopped with stopThread.
	 * The algorithm cannot feature navigation methods or any methods that take time to complete because
	 * if it is paused or stopped, the effect needs to be immediate.
	 * TODO: what does he mean... cannot use this method without navigation
	 */
	public void run(){
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {

		}
		while(!complete_stop){
			while(thread_on){
				//while the scanner is not scanning anything
				while (!scanning()){
					//depending on where the robot is on the map, it will travel following a certain path please see pathSetter function for detail
//					if (odo.getY() == 0){
//						Sound.beep();
//					}
					nav.turnTo(pathSetter(odo.getX(),odo.getY()), true);
					nav.setSpeeds(FORWARDSPEED, FORWARDSPEED);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
					}
					nav.setSpeeds(STOP, STOP);					//TODO: make sure goFoward is not buggy
				}
				
				//BRIDGE with object recognition
				activatedDetectObject();
				Logger.log("detect object started");;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (thread_on){
					Logger.log("problem system exited after search and move");
					System.exit(0);
				}
			}
		}
	}
	/**Scanning method: scan the environment for obstacles depending on where the robot is on the map
	 * returns a boolean which indicates if an object has been detected in the scanning process
	 */
	private boolean scanning(){
		/*position of scanned object, {heading, distance at which object was detected
		 * -1 heading and 255 distance indicates no object detected
		 */
		double objectPosition [] = {-1,255};
		//this algorithm considers (0,0) to be at the CORNER of the map, not the first block.
		double x = odo.getX();
		double y = odo.getY();

		//TODO: in the future, if code works well etc. clean it up by creating a for function and reduce lines
		//TODO: also, maybe base turning on robot's current angle? something like that, save them
		/*the direction which the robot scans varies depending on where the robot is on the map
		 * 1. On the bottom left corner, it scans 90 degrees from y+ to x+ axis
		 * 2. On the bottom middle side, it scans 180 degrees from x- to x+ axis
		 * 3. On the bottom right corner, it scans 90 degrees from x- to y+ axis
		 * 4. In the middle, it scans 360 from x+ to x+ axis
		 * 5. On the top left corner, it scans 90 degrees from x+ to y- axis
		 * 6. On the top middle side, it scans 180 degrees from x- to x+ axis
		 * 7. On the top right corner, it scans 90 degrees from x- to y- axis
		 * 8. On the right middle side, it scans 180 degrees from y+ to y- axis
		 * 9. On the left middle side, it scans 180 degrees from y+ to y- axis
		 */
		//Tweakable map distance values to fit any map size
		double outerSquare = MAPSIZE/DIVISION_FORSCAN;

		//CASE 1 if robot is on the bottom left corner
		if (x<outerSquare && y<outerSquare){
			nav.turnTo(0, true);
			while (odo.getAngle()<90||odo.getAngle()>357){
				//scan rotating left from 0 to 90 degrees
				nav.setSpeeds(-ROTATIONSPEED, ROTATIONSPEED);
				//compute the position of the object detected if any
				objectPosition = scanningDataProcessing(objectPosition);
			}
		}
		
		//CASE 2 if robot is on the bottom middle
		else if (x<MAPSIZE-outerSquare && y<outerSquare){
			nav.turnTo(0, true);
			while (odo.getAngle()<180||odo.getAngle()>357){
				//scan rotating left from 0 to 180 degrees
				nav.setSpeeds(-ROTATIONSPEED, ROTATIONSPEED);
				//compute the position of the object detected if any
				objectPosition = scanningDataProcessing(objectPosition);
			}
		}
		
		//CASE 3 if robot is on the bottom right 
		else if (x>MAPSIZE-outerSquare && y<outerSquare){
			nav.turnTo(180, true);
			while (odo.getAngle()>90){
				//scan rotating right from 180 to 90 degrees
				nav.setSpeeds(ROTATIONSPEED, -ROTATIONSPEED);
				//compute the position of the object detected if any
				objectPosition = scanningDataProcessing(objectPosition);
			}
			
		}
		//case 6 if robot is on the top middle
		else if (y>MAPSIZE-outerSquare){
			nav.turnTo(180, true);
			while (odo.getAngle()<357){
				//scan rotating left from 180 to around 0
				nav.setSpeeds(-ROTATIONSPEED, ROTATIONSPEED);
				//compute the position of the object detected if any
				objectPosition = scanningDataProcessing(objectPosition);
			}
		}
		
		//CASE 7 if robot is on the top right
		else if (x>MAPSIZE-outerSquare && y>MAPSIZE-outerSquare){
			nav.turnTo(270, true);
			while (odo.getAngle()>180){
				//scan rotating right from 270 to 180 degrees
				nav.setSpeeds(ROTATIONSPEED, -ROTATIONSPEED);
				//compute the position of the object detected if any
				objectPosition = scanningDataProcessing(objectPosition);
			}	
		}
		//CASE 5 if robot is on the top left 
		else if (x<outerSquare && y>MAPSIZE-outerSquare){
			nav.turnTo(270, true);
			while (odo.getAngle()<357){
				//scan rotating left from 270 to around 0
				nav.setSpeeds(-ROTATIONSPEED, ROTATIONSPEED);
				//compute the position of the object detected if any
				objectPosition = scanningDataProcessing(objectPosition);

			}
		}
		//CASE 6 if robot is on the top middle
		else if (y>MAPSIZE-outerSquare){
			nav.turnTo(180, true);
			while (odo.getAngle()<357){
				//scan rotating left from 180 to around 0
				nav.setSpeeds(-ROTATIONSPEED, ROTATIONSPEED);
				//compute the position of the object detected if any
				objectPosition = scanningDataProcessing(objectPosition);
			}
		}
		//CASE 8 if the robot is on the right middle
		else if (x>MAPSIZE-outerSquare && y>outerSquare){
			nav.turnTo(270, true);
			while (odo.getAngle()>90){
				//scan rotating right from 270 to around 90
				nav.setSpeeds(ROTATIONSPEED, -ROTATIONSPEED);
				//compute the position of the object detected if any
				objectPosition = scanningDataProcessing(objectPosition);
			}
		}
		//CASE 9 if the robot is on the left middle
		else if (x<outerSquare && y>outerSquare){
			nav.turnTo(270, true);
			while (odo.getAngle()>90){
				//scan rotating left from 270 to around 90
				nav.setSpeeds(-ROTATIONSPEED, ROTATIONSPEED);
				//compute the position of the object detected if any
				objectPosition = scanningDataProcessing(objectPosition);
			}
		}
		//CASE 4 if the robot is in the middle (inner square)
		else{
			nav.turnTo(0, true);
			while (odo.getAngle()<359){
				//rotate 360 degrees from the left
				nav.setSpeeds(-ROTATIONSPEED, ROTATIONSPEED);
				//compute the position of the object detected if any
				objectPosition = scanningDataProcessing(objectPosition);
			}
		}
		//when scanning is done, check if an object has been detected
		nav.setSpeeds(STOP, STOP);
		if (objectPosition[0] != -1){
			//a header of -1 means that no object has been detected
			nav.turnTo(objectPosition[0], true);
			//turn to heading where obstacle was detected
			Sound.beep();
			Sound.beep();
			Sound.beep();
			Sound.beep();
			Sound.beep();
			return true;
		}
		else{
			//Sound.beep();
			return false;
		}
	}
	/**Scanner helper method: process the scanned data, check if there has been a closer object scanned and outputs the
	 * position of previous object or new object
	 * @param: position
	 */
	private double[] scanningDataProcessing(double[] position){
		double newDist = frontSensor.getValue();
		if (newDist< DISTANCE_TOLERANCE && newDist<position[1]){
				position[1] = newDist;
				position[0] = odo.getAngle();
		}
		return position;
	}
	
	/**Searching helper method: determine the heading of the robot, depending on its position
	 * lead the robot on a set searching path, which goes to each diagonal corners. This path is set to help the robot cover
	 * the most area with the least traveling
	 * @param: x
	 * @param: y
	 */
	private double pathSetter(double x, double y){
		double squareSize = MAPSIZE/DIVISION;
		double heading = odo.getAngle();
		//setup a boolean to check if the angle got sorted in the for loop
		boolean notSorted = true;
		//this algorithm works with (0,0) being the CORNER of the map, not the on the first squares, correct accordingly
		x += 1;
		y += 1;
		/*divide the map into squares, DIVISION determines how many square the length of the map is divided into
		 * increase DIVISION in order to make path more precise
		 * 1. Robot  travels on the first diagonal (heading 45 degrees)
		 * 2. Then, travels on the rightmost side of the map downwards (heading 270 degrees)
		 * 3. Then, travels on the second diagonal (heading 135 degrees)
		 * 4. Then, travels on the left most side of the map downwards (heading 270 degrees)
		 * 5. If the robot gets lost and is not on path, make it travel to the left if is on the right side of the map, vice versa
		 */

		for (int i = 0; i<DIVISION-1; i++){
			//Case 1: if the coordinates are on the first diagonal of the map
			if(i*squareSize <= x && x<(i+1)*squareSize && i*squareSize<=y && y<(i+1)*squareSize){
				Sound.beep();
				heading = 45;
				notSorted = false;
				//continue, don't go through loop again if it go sorted
				continue;
				//TODO: should I also put angles into variables?
			}
			
			//Case 3: if the coordinates are on the second diagonal of the map
			else if(i*squareSize <= x && x<(i+1)*squareSize && ((DIVISION-2)-i)*squareSize<=y && y<((DIVISION-1)-i)*squareSize){
				Sound.beep();
				Sound.beep();
				heading = 135; 
				notSorted = false;
				continue;

			}
			//Case 2: if the coordinates are on the far right side of the map
			else if ((DIVISION-1)*squareSize <=x && x< DIVISION*squareSize && (i+1)*squareSize<=y && y<(i+2)*squareSize){
				Sound.beep();
				Sound.beep();
				Sound.beep();
				heading = 270;
				notSorted = false;
				continue;

			}
			//Case 4: if the coordinates are on the far left side of the map
			else if (0 <=x && x< squareSize && (i+1)*squareSize<=y && y<(i+2)*squareSize){
				Sound.buzz();
				heading = 270;
				notSorted = false;
				continue;
			}
		}
			//if it is not sorted, then it is Case 5
		if (notSorted){
			//TODO: clean up this code
			//Case 5: if none of the cases apply, make robot go back to known path
			//if the robot is on the right part of the map
			if (x > MAPSIZE/2){
				//if the robot is on the bottom right
				Sound.buzz();
				Sound.buzz();
				if (y <= squareSize){
					heading = 0;
				}
				//if the robot
				else if (y >= squareSize*(DIVISION-1)){
					heading = 180;
				}
				
				else {
					heading = 180;
				}
			}
			//if the robot is on the middle left part of the map
			else{
				Sound.buzz();
				Sound.buzz();
				Sound.buzz();
				if (y <= squareSize){
					heading = 0;
				}
				//if the robot
				else if (y >= squareSize*(DIVISION-1)){
					heading = 180;
				}
			
				else{
					heading = 0;
				}
			}
		}
		return heading;
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
	public  void activatedDetectObject(){
		// pause the current thread
		SearchAndMove.pauseThread();
		// activate detect object
		DetectObject.resumeThread();
	}
}