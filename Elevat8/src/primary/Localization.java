package primary;

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
	private USSensor frontSensor, sideSensor, heightSensor;
	private ColorSensor correctionSensor;
	private double WHEELRADIUS, TRACKSIZE;
	private int ROTATIONSPEED, FORWARDSPEED, ACCELERATION;
	private SearchAndMove searchMove;
	private DetectObject detectObject;
	private Capture capture;
	private GoToZone gotozone;
	private DodgeObject dodgeObject;
	
	private double angleA, angleB, theta, positionX, positionY;
	private double[] pos = new double[3], angle = new double[4];
	private final int WALL_DISTANCE = 40, NOISE_MARGIN = 20;//10;
	private int countgridlines = 0; 
	private final double COLORSENSOR_TO_CENTER_TRACK = 11.5;
	
	
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
	 * @param FORWARDSPEED
	 * @param ROTATIONSPEED
	 * @param ACCELERATION
	 * @param WHEELRADIUS
	 * @param TRACKSIZE
	 * @param frontSensor
	 * @param sideSensor
	 * @param heightSensor
	 * @param correctionSensor
	 */ /*
	public Localization(RegulatedMotor leftMotor, RegulatedMotor rightMotor, Odometer odo, Navigation nav,
			SearchAndMove searchMove, DetectObject detectObject, Capture capture, GoToZone gotozone,
			DodgeObject dodgeObject, int FORWARDSPEED, int ROTATIONSPEED, int ACCELERATION, double WHEELRADIUS,
			double TRACKSIZE, USSensor frontSensor, USSensor sideSensor, USSensor heightSensor,
			ColorSensor correctionSensor) {

		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.odo = odo;
		this.nav = nav;
		this.frontSensor = frontSensor;
		this.sideSensor = sideSensor;
		this.heightSensor = heightSensor;
		this.correctionSensor = correctionSensor;
		this.ACCELERATION = ACCELERATION;
		this.WHEELRADIUS = WHEELRADIUS;
		this.FORWARDSPEED = FORWARDSPEED;
		this.ROTATIONSPEED = ROTATIONSPEED;
		this.TRACKSIZE = TRACKSIZE;
		this.searchMove = searchMove;
		this.capture = capture;
		this.gotozone = gotozone;
		this.dodgeObject = dodgeObject;
	}
	*/
	public Localization(RegulatedMotor leftMotor, RegulatedMotor rightMotor, Odometer odo, Navigation nav,
			 int FORWARDSPEED, int ROTATIONSPEED, int ACCELERATION, double WHEELRADIUS,
			double TRACKSIZE, USSensor frontSensor, ColorSensor correctionSensor) {

		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.odo = odo;
		this.nav = nav;
		this.frontSensor = frontSensor;
		
		this.correctionSensor = correctionSensor;
		this.ACCELERATION = ACCELERATION;
		this.WHEELRADIUS = WHEELRADIUS;
		this.FORWARDSPEED = FORWARDSPEED;
		this.ROTATIONSPEED = ROTATIONSPEED;
		this.TRACKSIZE = TRACKSIZE;
		
	}
	/**
	 * This method does the actual localization.
	 * 
	 */
	public void localize() {

		// TODO localization code
		// perform the ultrasonic localization
		leftMotor.setSpeed(ROTATIONSPEED);
		rightMotor.setSpeed(ROTATIONSPEED);
		// rotate the robot until it sees no wall
		while (frontSensor.getValue() < WALL_DISTANCE + NOISE_MARGIN) {
			// turn clockwise
			leftMotor.forward();
			rightMotor.backward();
		}

		// keep rotating until the robot sees a wall, then latch the angle
		while (frontSensor.getValue() > WALL_DISTANCE - NOISE_MARGIN) {
			// turn clockwise
			leftMotor.forward();
			rightMotor.backward();
		}
		Sound.beep();
		angleA = odo.getAngle();

		// switch direction and wait until it sees no wall
		while (frontSensor.getValue() < WALL_DISTANCE + NOISE_MARGIN) {
			// turn counterclockwise
			leftMotor.backward();
			rightMotor.forward();
		}

		// keep rotating until the robot sees a wall, then latch the angle
		while (frontSensor.getValue() > WALL_DISTANCE - NOISE_MARGIN) {
			// turn counterclockwise
			leftMotor.backward();
			rightMotor.forward();
		}
		Sound.beep();
		angleB = odo.getAngle();

		// angleA is clockwise from angleB, so assume the average of the
		// angles to the right of angleB is 45 degrees past 'north'

		// calculate theta
		theta = computeAngle(angleA, angleB);
		Sound.buzz();
		// turn to the 0-axis
		nav.turnTo(theta, true);

		// update the odometer position (example to follow:)
		double position[] = {0.0, 0.0, 0.0};
		boolean update[] = {true, true, true};
		odo.setPosition(position, update);
		
		
		// start the light localization
		nav.turnTo(135, true); // turn 135 degrees to make it face to destination ( furthest corner from wall of the tile)
		odo.setPosition(new double [] { 0.0,  0.0, 135.0 }, new boolean[] {false, false, true});
		
		// advance the equivalent of one tracksize towards the corner of the tile
		nav.goForward(TRACKSIZE/3);
		
		// wait a bit
		leftMotor.stop();
		rightMotor.stop();
		
		while (countgridlines < 4)//start counting lines
		{	
			
			
			
			pos = odo.getPosition();	//get current posistion from odometer
			if (correctionSensor.getValue() < 0.27)	//0.27 by trials
			{
				Sound.beep(); 
				angle[countgridlines] = pos[2];	//store current angle
				countgridlines++;	//line detected
				if (countgridlines == 4)	
				{
					//stop both motors
					leftMotor.stop();
					rightMotor.stop();
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
		
		
		// start calculating x, y
		double temp = 0;
		temp = 360 - angle[1] + angle[3];
		positionY = -COLORSENSOR_TO_CENTER_TRACK * Math.cos(Math.PI*temp/360);
		temp = Math.abs(angle[0] - angle[2]);
		positionX= COLORSENSOR_TO_CENTER_TRACK * Math.cos(Math.PI*temp/360); 
		theta = temp / 2 + 90;//90
		pos = odo.getPosition();
		theta = theta + pos[2];
		if (theta>=360)
		{
			theta=theta % 360;
		}
		if (theta<0)
		{	
			theta=360+theta;
		}
		// the x y is according the target origin (0,0)
		odo.setPosition(new double [] {positionX, positionY, 0}, new boolean [] {true, true, false});	
		// travel to origin
		nav.travelTo(0,0);
		// turn back to 0 direction
		nav.turnTo(0, true);
	
		
		// when localization is done, start Threads to start actually moving the
		// robot
		//startUlteriorThreads();
		
	}

	/**
	 * starts thread used in the second part of the lab and pause them right
	 * after
	 * 
	 */
	public void startUlteriorThreads() {
		searchMove.start();
		SearchAndMove.pauseThread();
		detectObject.start();
		DetectObject.pauseThread();
		capture.start();
		Capture.pauseThread();
		gotozone.start();
		GoToZone.pauseThread();
		dodgeObject.start();
		DodgeObject.pauseThread();

	}
	
	/**Computes the angle from the 2 angles latched during the localization to determine
	 * how much should the robot rotate
	 * @param angleA
	 * @param angleB
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
}
