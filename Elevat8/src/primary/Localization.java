package primary;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.robotics.RegulatedMotor;

/**
 * The localization class localizes the robot in the corresponding corner. To do
 * so, it detects two rising edges and then calculate the angle to rotate in
 * order to be headed to 0 degrees. After doing so, it moves while looking for
 * black lines around in order to find the position 0, 0 which is at the corner
 * intersection of black lines.
 * 
 * @author Erik-Olivier Riendeau, 2016
 *
 */
public class Localization {
	// ------------------------- fields ----------------------------- //
	private RegulatedMotor leftMotor, rightMotor;
	private Odometer odo;
	private Navigation nav;
	private USSensor frontSensor;
	private ColorSensor correctionSensor;
	private double WHEELRADIUS;
	private int ROTATIONSPEED, FORWARDSPEED, STOP = 0;
	private SearchAndMove searchMove;
	private DetectObject detectObject;
	private Capture capture;
	private GoToZone gotozone;
	private DodgeObject dodgeObject;
	
	private double angleA, angleB;
	private double[] pos = new double[3], angle = new double[4];
	private final int WALL_DISTANCE = 40;
	private int countgridlines = 0; 
	private final double COLORSENSOR_TO_CENTER_TRACK = 8.5;//8.5
	
	
	/**
	 * Localization constructor that allows most functionalities to the class (
	 * all motors and ultra sonic sensors access). Also contains instances of
	 * all objects used in the second part of the code. The second part consist
	 * of going to take styrofoam blocks and putting them in the corresponding
	 * zone.
	 * 
	 * @param leftMotor
	 * @param rightMotor
	 * @param odo
	 * @param nav
	 * @param searchMove
	 * @param detectObject
	 * @param capture
	 * @param gotozone
	 * @param dodgeObject
	 * @param FORWARDSPEED - in degrees/cm
	 * @param ROTATIONSPEED - in degrees/cm
	 * @param ACCELERATION - in degrees/cm^2
	 * @param WHEELRADIUS - in cm
	 * @param TRACKSIZE - in cm
	 * @param frontSensor - 
	 * @param sideSensor
	 * @param heightSensor
	 * @param correctionSensor
	 */ 
	
	public Localization(RegulatedMotor leftMotor, RegulatedMotor rightMotor, Odometer odo, Navigation nav,
			SearchAndMove searchMove, DetectObject detectObject, Capture capture, GoToZone gotozone,
			DodgeObject dodgeObject, int FORWARDSPEED, int ROTATIONSPEED,  double WHEELRADIUS,
			double TRACKSIZE, USSensor frontSensor, ColorSensor correctionSensor) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.odo = odo;
		this.nav = nav;
		this.frontSensor = frontSensor;
		this.correctionSensor = correctionSensor;
		this.WHEELRADIUS = WHEELRADIUS;
		this.FORWARDSPEED = FORWARDSPEED;
		this.ROTATIONSPEED = ROTATIONSPEED;
		this.searchMove = searchMove;
		this.capture = capture;
		this.gotozone = gotozone;
		this.dodgeObject = dodgeObject;
		this.detectObject = detectObject;
		
	}
	
	

	/**
	 * This method does the actual localization. The localization chosen if falling edge. The robot thus rotates until the front sensor does not detect a wall anymore.
	 * Once done, the robot latches this angle and keep rotating until it does detect the other wall. It then latches this angle and calculate its initial orientation.
	 * After, the robot heads to an angle of 0 degrees. Finally, the robot turns on itself to detect the four grid lines and determine its initial X and Y. The robot
	 * then proceeds to go to the edge of the tile with the same orientation. The values of the odometer are updated depending on which corner the robot is.
	 * 
	 */
	public void localize() {
		

		while (frontSensor.getValue() <= WALL_DISTANCE){				
			nav.setSpeeds(ROTATIONSPEED, -ROTATIONSPEED);
		}
		Sound.beep();
		// keep rotating until the robot sees a wall, then latch the angle
		while (frontSensor.getValue() > WALL_DISTANCE){				
			nav.setSpeeds(ROTATIONSPEED, -ROTATIONSPEED);
		}
		angleA = odo.getAngle();
		Sound.beep();

		// rotate in the same direction until the robot does not see a wall, then latch the angle
		while (frontSensor.getValue() <= WALL_DISTANCE){				
			nav.setSpeeds(ROTATIONSPEED, -ROTATIONSPEED);
		}
		Sound.beep();

		nav.setSpeeds(STOP, STOP);
		angleB = odo.getAngle();
		

		// angleA is clockwise from angleB, so assume the average of the
		// angles to the right of angleB is 45 degrees past 'north'


		if (angleA>angleB){
			pos[2] = 210.0 - (angleA-angleB)/2;
		}
		else {
			pos[2] = 30.0 - (angleA-angleB)/2;
		}
		
		// update the odometer position (example to follow:)
		odo.setPosition(pos, new boolean [] {true, true, true});



		nav.turnTo(0, true);
		
		// start the light localization
		nav.turnTo(45, true); // turn 135 degrees to make it face to destination ( furthest corner from wall of the tile)

		leftMotor.setSpeed(FORWARDSPEED);
		rightMotor.setSpeed(FORWARDSPEED);
		
		leftMotor.rotate(convertDistance(WHEELRADIUS,11),true);
		rightMotor.rotate(convertDistance(WHEELRADIUS,11),false);
		
		
		while (countgridlines < 4)//start counting lines
		{	
			//get current posistion from odometer**********************getPosition()problem
			if (correctionSensor.getValue() < 50)	//0.27 by trials
			{
				Sound.beep(); 
				angle[countgridlines] = odo.getAngle();	//store current angle
				countgridlines++;	//line detected
				if (countgridlines == 4)	
				{
					//stop both motors
					nav.setSpeeds(STOP, STOP);
					Sound.beep();
					//break the loop when 4 lines are detected
					break; 
				}
			} 
			//rotate the robot counter-clockwise
			leftMotor.setSpeed( ROTATIONSPEED);
			rightMotor.setSpeed( ROTATIONSPEED);
			leftMotor.backward();
			rightMotor.forward();
		}
		double angleYn = angle[0], angleXn = angle [1], angleYp = angle[2], angleXp = angle[3];
		double thetaY = (360.0 - angleYn) + angleYp;
		double thetaX = angleXn-angleXp;
		double x = -COLORSENSOR_TO_CENTER_TRACK*Math.cos(Math.toRadians(thetaY)/2.0);
		double y = -COLORSENSOR_TO_CENTER_TRACK*Math.cos(Math.toRadians(thetaX)/2.0);
		double angleCorrY = -360+angleYn+thetaY/2.0;
			
		//update position
		pos[0] = x;
		pos[1] = y;
		pos[2] = angleCorrY + odo.getAngle();
		
		// update the odometer position (example to follow:)
		odo.setPosition(pos, new boolean [] {true, true, true});
	
		nav.travelTo(0,0);
		nav.turnTo(0, true);
		Sound.beep();
		Sound.beep();
		// wait a bit
		try { Thread.sleep(3000); } catch (InterruptedException e){};

		
		
		int startingCorner;
		if ( Initialization.BTN == 1) startingCorner = Initialization.BSC;
		else { startingCorner = Initialization.CSC; }
		
		if ( startingCorner == 0){
			startingCorner = 5;
		}
		odo.setPosition(Initialization.initialPosition[startingCorner-1], new boolean[] {true, true, true});

		
		// when localization is done, start Threads to start actually moving the
		// robot
		Sound.twoBeeps();
		Sound.twoBeeps();
		Sound.twoBeeps();
		startUlteriorThreads();
		activateSearchAndMove();
	}

	/**Starts ulterior threads  SearchAndMove, DetectObjects, Capture, GoToZone and DodgeObject.
	 *  Also catches exception and write them in the logger file.
	 * 
	 */
	public void startUlteriorThreads() {
		Logger.log("search and move started");
		try {
			searchMove.start();
		} catch (NullPointerException e) {
			Logger.log("error searchMove");
		}
		Logger.log("search and move paused");
		try {
			SearchAndMove.pauseThread();
		} catch (Exception e) {
			Logger.log("pausing searchmove failed");

		}
		Logger.log("detect object started");
		try {
			detectObject.start();
		} catch (Exception e) {
			Logger.log("detect object failed");

		}
		Logger.log("detectObject paused");
		try {
			DetectObject.pauseThread();
		} catch (Exception e) {
			Logger.log("pausing detect object failed");

		}
		Logger.log("capture started");
		try {
			capture.start();
		} catch (Exception e) {
			Logger.log("capture failed");

		}
		Logger.log("capture paused");
		try {
			Capture.pauseThread();
		} catch (Exception e) {
			Logger.log("pausing capture failed");

		}
		Logger.log("gotozone started");
		try {
			gotozone.start();
		} catch (Exception e) {
			Logger.log("gotozone failed");
		}
		Logger.log("gotozone paused");
		try {
			GoToZone.pauseThread();
		} catch (Exception e) {
			Logger.log("pausing gotozone failed");

		}
		Logger.log("dodgeobject started");
		try {
			dodgeObject.start();
		} catch (Exception e) {
			Logger.log("dodge object failed");

		}
		try {
			DodgeObject.pauseThread();
		} catch (Exception e) {
			Logger.log("pausing dodge object failed");

		}
		Logger.log("dodgeobject paused");
	}
	
	/**Computes the angle from the 2 angles latched during the localization to determine
	 * how much should the robot rotate
	 * @param angleA - in degrees
	 * @param angleB - in degrees
	 * @return the average of the two + either 45 or 225 depending on which direction the robot
	 * is rotating
	 */
	private double computeAngle(double angleA, double angleB){
		if(angleA > angleB){
			double theta = ((angleA + angleB) / 2 + 45) % 360;//45
			Sound.beep();
			Sound.beep();
			return theta;
		}
		else {
			double theta = ((angleA + angleB) / 2 + 225) % 360;	//225
			Sound.beep();
			return theta;
		}
	}
	/**Computes the degrees the wheel should return to travel a certain distance depending on the wheel's radius.
	 * @param radius of the wheel in cm
	 * @param distance to travel in cm
	 * @return the angle the motor should rotate in degrees
	 */
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	
	/**Resumes the thread Search and Move. 
	 * 
	 */
	private void activateSearchAndMove(){
		SearchAndMove.resumeThread();
	}
	
}