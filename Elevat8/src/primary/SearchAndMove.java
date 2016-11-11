package primary;

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
	private static final double MAPSIZE = 120; 
	private static final float DISTANCE_TOLERANCE = 29, STOP = 0;
	private static final int DIVISION = 4, SCAN_INTERVAL = 10;
	
	
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
		this.ROTATIONSPEED = ROTATIONSPEED;
		this.FORWARDSPEED = FORWARDSPEED;
		this.ACCELERATION = ACCELERATION;
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
				//while the scanner is not scanning anything
				while (!scanning()){
					//depending on where the robot is on the map, it will travel following a certain path please see pathSetter function for detail
					nav.turnTo(pathSetter(odo.getX(),odo.getY()), true);
					nav.goForward(SCAN_INTERVAL);
					//TODO: make sure goFoward is not buggy
				}
				//TODO: what to do when scan scans something: bridge with wall following or object capturing
				
			}
		}
	}
	/**Scanning method: scan the environment for obstacles depending on where the robot is on the map
	 * returns a boolean which indicates if an object has been detected in the scanning process
	 */
	private boolean scanning(){
		//position of scanned object, the heading followed by the distance detected
		//-1 heading indicates no object detected
		double objectPosition [] = {-1,255};
		
		//TODO: update this so it's universal, matter the map size.
		//TODO: also so that it includes the center CASE 4
		//TODO: add more comments
		
		/**the direction which the robot scans varies depending on where the robot is on the map
		 * 1. On the bottom left corner, it scans 90 degrees from y+ to x+ axis
		 * 2. On the bottom middle, it scans 180 degrees from y- to x+ axis
		 * 3. On the bottom right corner, it scans 90 degrees from x- to y+ axis
		 * 4. In the middle, it scans 360 from x+ to x+ axis
		 * 5. On the top left corner, it scans 90 degrees from x+ to y- axis
		 * 6. On the top middle corner, it scans 180 degrees from y- to x+ axis
		 * 7. On the top right corner, it scans 90 degrees from x- to y- axis
		 */
		if (odo.getX()<15 && odo.getY()<60){
			nav.turnTo(0, true);
			while (odo.getAng()<90||odo.getAng()>357){
				//scan rotating left
				nav.setSpeeds(-ROTATIONSPEED, ROTATIONSPEED);
				objectPosition = scanningDataProcessing(objectPosition);
			}
			while (odo.getAng()>1&&odo.getAng()<359){
				nav.setSpeeds(ROTATIONSPEED, -ROTATIONSPEED);
				objectPosition = scanningDataProcessing(objectPosition);
			}
		}
		else if (odo.getX()<30 && odo.getY()<60){
			nav.turnTo(0, true);
			while (odo.getAng()<180||odo.getAng()>357){
				//scan rotating left
				nav.setSpeeds(-ROTATIONSPEED, ROTATIONSPEED);
				objectPosition = scanningDataProcessing(objectPosition);
			}
			while (odo.getAng()>1&&odo.getAng()<359){
				nav.setSpeeds(ROTATIONSPEED, -ROTATIONSPEED);
				objectPosition = scanningDataProcessing(objectPosition);
			}
		}
		//other map position scanning
		else if (odo.getX()>30 && odo.getY()<60){
			nav.turnTo(180, true);
			while (odo.getAng()>90){
				nav.setSpeeds(ROTATIONSPEED, -ROTATIONSPEED);
				objectPosition = scanningDataProcessing(objectPosition);
			}
			while (odo.getAng()<180){
				nav.setSpeeds(-ROTATIONSPEED, ROTATIONSPEED);
				objectPosition = scanningDataProcessing(objectPosition);
			}
			
		}
		else if (odo.getX()>30 && odo.getY()>60){
			nav.turnTo(270, true);
			while (odo.getAng()>180){
				nav.setSpeeds(ROTATIONSPEED, -ROTATIONSPEED);
				objectPosition = scanningDataProcessing(objectPosition);
			}
			while (odo.getAng()<270){
				nav.setSpeeds(-ROTATIONSPEED, ROTATIONSPEED);
				objectPosition = scanningDataProcessing(objectPosition);

			}	
		}
		else {
			nav.turnTo(270, true);
			while (odo.getAng()<358){
				nav.setSpeeds(-ROTATIONSPEED, ROTATIONSPEED);
				objectPosition = scanningDataProcessing(objectPosition);

			}
			while (odo.getAng()>270){
				nav.setSpeeds(ROTATIONSPEED, -ROTATIONSPEED);
				objectPosition = scanningDataProcessing(objectPosition);
			}
		}
		//when scanning is done, check if an object has been detected
		nav.setSpeeds(STOP, STOP);
			if (objectPosition[0] != -1){
				return true;
			}
			else{
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
				position[0] = odo.getAng();
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
		double heading = odo.getAng();
		/**divide the map into squares, DIVISION determines how many square the length of the map is divided into
		 * increase DIVISION in order to make path more precise
		 * 1. Robot  travels on the first diagonal (heading 45 degrees)
		 * 2. Then, travels on the rightmost side of the map downwards (heading 270 degrees)
		 * 3. Then, travels on the second diagonal (heading 135 degrees)
		 * 4. Then, travels on the left most side of the map downwards (heading 270 degrees)
		 * 5. If the robot gets lost and is not onpath, make it travel to the left if is on the right side of the map, vice versa
		 */
		for (int i=0; i<DIVISION-1; i++){
			//Case 1: if the coordinates are on the first diagonal of the map
			if(i*squareSize<x && x<(i+1)*squareSize && i*squareSize<y && y<(i+1)*squareSize){
				heading = 45;
				//TODO: should I also put angles into variables?
			}
			//Case 3: if the coordinates are on the second diagonal of the map
			else if(i*squareSize<x && x<(i+1)*squareSize && ((DIVISION-2)-i)*squareSize<y && y<((DIVISION-1)-i)*squareSize){
				heading = 135; 
			}
			//Case 2: if the coordinates are on the far right side of the map
			else if ((DIVISION-1)*squareSize <x && x< DIVISION*squareSize && (i+1)*squareSize<y && y<(i+2)*squareSize){
				heading = 270;
			}
			//Case 4: if the coordinates are on the far left side of the map
			else if (0 <x && x< squareSize && (i+1)*squareSize<y && y<(i+2)*squareSize){
				heading = 270;
			}
			//Case 5: if none of the cases apply, make robot go back to known path
			//if the robot is on the right part of the map
			else if (x > MAPSIZE/2){
				heading = 180;
			}
			//if the robot is on the left part of the map
			else{
				heading = 0;
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
	public static void activatedDetectObject(){
		// pause the current thread
		SearchAndMove.pauseThread();
		// activate detect object
		DetectObject.resumeThread();
	}
}
