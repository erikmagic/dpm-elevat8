package primary;

import lejos.robotics.RegulatedMotor;

/**The localization class localizes the robot in the corresponding corner. To do so, it detects two rising edges and then calculate the angle to rotate in order to be headed to 0 degrees. After 
 * doing so, it moves while looking for black lines around in order to find the position 0, 0 which is at the corner intersection of black lines.
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
	
	/**Localization constructor that allows most functionalities to the class ( all motors and ultra sonic sensors access). Also contains instances of all objects used in the second part of the 
	 * code. The second part consist of going to take styrofoam blocks and putting them in the corresponding zone.
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
	 */
	public Localization(RegulatedMotor leftMotor, RegulatedMotor rightMotor, Odometer odo, Navigation nav,SearchAndMove searchMove, DetectObject detectObject, Capture capture, GoToZone gotozone
			, DodgeObject dodgeObject, int FORWARDSPEED, int ROTATIONSPEED, int ACCELERATION, double WHEELRADIUS, double TRACKSIZE, USSensor frontSensor
			, USSensor sideSensor, USSensor heightSensor, ColorSensor correctionSensor){
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
	/**This method does the actual localization.
	 * 
	 */
	public void localize(){
		
		// TODO localization code
		
		// when localization is done, start Threads to start actually moving the robot
		startUlteriorThreads();
	}
	/**starts thread used in the second part of the lab and pause them right after
	 * 
	 */
	public void startUlteriorThreads(){
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
}
