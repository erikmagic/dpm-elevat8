package primary;

import java.awt.Button;
import java.io.FileNotFoundException;
import java.util.Timer;


import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.remote.ev3.RemoteEV3;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;


/**Main class that holds major parameters, starts vital threads and initializes important objects. Contains
 * the classes getWIFI that fetches the wifi parameters and startThreads that starts the display, odometer and sensors.
 * Also contains initializeObjects that initializes the timer, the navigation and localization.
 *
 *  
 */
public class Initialization {

	// motors fields
	private RegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);
	private RegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.C);
	//private RegulatedMotor elevateMotor = new EV3LargeRegulatedMotor(MotorPort.D);
	//private RegulatedMotor clawMotor = new EV3LargeRegulatedMotor(MotorPort.A);

	// sensor fields
	private Brick brick = BrickFinder.getDefault();
	private Port s4 = brick.getPort("S4");
	//private Port s3 = brick.getPort("S3");
	//private Port s2 = brick.getPort("S2");
	//private Port s1 = brick.getPort("S1");
	private Port usPort = LocalEV3.get().getPort("S1");
	private EV3ColorSensor colorSensor = new EV3ColorSensor(s4);
	// EV3ColorSensor colorSensorLeft = new EV3ColorSensor(s3); to be added if
	// we get more ports
	//private EV3UltrasonicSensor usSide = new EV3UltrasonicSensor(s2);
	private EV3UltrasonicSensor usFront =  new EV3UltrasonicSensor(usPort);
	//private EV3UltrasonicSensor usDetectObject = new EV3UltrasonicSensor(s3);
	// https://sourceforge.net/p/lejos/wiki/Remote%20access%20to%20an%20EV3/
	// https://lejosnews.wordpress.com/2015/02/11/pan-configuration/ to add a
	// sensor from another brick
	private USSensor sideSensor;
	private USSensor frontSensor;
	private USSensor heightSensor;
	private ColorSensor correctionSensor;

	// timer fields
	//private Timer timer = new Timer();
	double deadline = 5; // time to stop the robot in minutes
	private CustomTimer custom_timer;
	
	// navigation fields
	private Navigation nav;
	
	// odometer fields
	private Odometer odo;
	private OdometerCorrection odoCorrection;
	private LCDInfo lcd;
	
	// localization fields
	private Localization loc;
	
	// search and move fields
	private SearchAndMove searchMove;
	
	// detect objects fields
	private DetectObject detectObject;
	
	// capture fields
	private Capture capture;
	
	// go to zone fields
	private GoToZone gotozone;
	
	// dodge objects fields
	private DodgeObject dodgeObject;
	
	// initialize logger
	
	
	// numerical value fields
	private final double TRACKSIZE = 16.25;// 14.5 -> not enough, 16 -> not enough, 16.5 -> too much from the middle of the left wheel to the middle of the right wheel
	private final double WHEELRADIUS = 2.1;
	private final int ACCELERATION = 500;
	private final int FORWARDSPEED = 185;
	private final int ROTATIONSPEED = 125;
	
	/**Empty constructor
	 * 
	 */
	public Initialization(){
		
	}
	
	/**The initialize method simply starts the methods getWIFI, startThreads and initializeObjects
	 * 
	 */
	public void initialize(){
		// start by getting wifi info to fetch needed parameters for object initializations
		getWIFI();
		// initialize objects used troughout the code
		initializeObjects();
		// start urgent threads, after initialize objects because some of these threads are objects
		startThreads();
		
		// localize the robot once everything has been set up before
		loc.localize();
		System.exit(0);
	}
	
	public void isolation_test(){
		initializeObjects();
		startThreads();
		nav.turnTo(180, true);
		nav.turnTo(360, true);
		nav.turnTo(90, true);
		nav.turnTo(270, true);
		nav.turnTo(0, true);
		lejos.hardware.Button.waitForAnyPress();
		System.exit(0);
	}

	/**
	 * Creates a WIFI object and accesses the object to fetch needed information
	 */
	public void getWIFI() {

	}

	/**
	 * Starts the TIMER, ODOMETER, USSENSORS, COLORSENSORS AND DISPLAY
	 */
	public void startThreads() {
		//custom_timer.startTimer(); // start the actual timer thread
		odo.start();
		//sideSensor.start();
		frontSensor.start();
		//heightSensor.start();
		searchMove.start();
		
	}

	/**
	 * Initializes instances of localization, custom_timer and navigation
	 */
	public void initializeObjects() {
		
		try {
			Logger.setLogWriter("Navigation_data.txt");
		} catch (FileNotFoundException e) {
			System.out.println("error file");
			e.getMessage();
			System.out.println(e);
		} // set up logger
		//custom_timer = new CustomTimer(timer, deadline); // initialize timer object
		odo = new Odometer(leftMotor, rightMotor, TRACKSIZE, WHEELRADIUS);
		lcd = new LCDInfo(odo);
		//sideSensor = new USSensor(usSide);
		frontSensor = new USSensor(usFront);
		//heightSensor = new USSensor(usDetectObject);
		correctionSensor = new ColorSensor(colorSensor);
		//odoCorrection = new OdometerCorrection(odo, correctionSensor);
		nav = new Navigation(leftMotor, rightMotor, odo, ROTATIONSPEED, FORWARDSPEED, ACCELERATION, WHEELRADIUS, TRACKSIZE);
		//capture = new Capture(leftMotor, rightMotor, nav, odo, FORWARDSPEED, ROTATIONSPEED, ACCELERATION, sideSensor, frontSensor, heightSensor);
		//dodgeObject = new DodgeObject(leftMotor, rightMotor, nav, odo, FORWARDSPEED, ROTATIONSPEED, ACCELERATION, sideSensor, frontSensor, heightSensor);
		//detectObject = new DetectObject(leftMotor, rightMotor, nav, odo, FORWARDSPEED, ROTATIONSPEED, ACCELERATION, sideSensor, frontSensor, heightSensor);
		//searchMove = new SearchAndMove(leftMotor, rightMotor, nav, odo, ACCELERATION, FORWARDSPEED, ROTATIONSPEED, sideSensor, frontSensor, heightSensor);
		//gotozone = new GoToZone(leftMotor, rightMotor, nav, odo, FORWARDSPEED, ROTATIONSPEED, ACCELERATION, sideSensor, frontSensor, heightSensor);
		//loc = new Localization(leftMotor, rightMotor, odo, nav, searchMove, detectObject, capture, gotozone, dodgeObject, ROTATIONSPEED, FORWARDSPEED, ACCELERATION, WHEELRADIUS, TRACKSIZE
		//			, frontSensor, sideSensor, heightSensor, correctionSensor);
		loc = new Localization(leftMotor, rightMotor, odo, nav, FORWARDSPEED, ROTATIONSPEED, ACCELERATION, WHEELRADIUS, TRACKSIZE, frontSensor, correctionSensor);
	}
	/** Gets the instance of capture object initialized in the Initialization and Capture thread started in the localization.
	 * Needs to be called after initialize in order to dodge nullPointException. 
	 * @return capture
	 */
	public Capture getCapture(){
		try {
			return capture;
		} catch (NullPointerException e) {
			System.out.println("capture called before object was initialized");
			e.printStackTrace();
		}
		System.exit(1);
		return null;
	}
	
	/**Gets the current instance of SearchAndMove initialized in the Initialization and SearchAndMove thread started in the localization.
	 * Needs to be called after initialize in order to dodge nullPointException.
	 * @return SearchAndMove
	 */
	public SearchAndMove getSearchAndMove(){
		try {
			return searchMove;
		} catch (NullPointerException e) {
			System.out.println("searchMove called before object was initialized");
			e.printStackTrace();
		}
		System.exit(1);
		return null;
	}
	/**Gets the current instance of DetectObject initialized in the Initialization and DetectObject thread started in the localization.
	 * Needs to be called after initialize in order to dodge nullPointException.
	 * @return DetectObject
	 */
	public DetectObject getdetectObject(){
		try {
			return detectObject;
		} catch (NullPointerException e) {
			System.out.println("detectObject called before object was initialized");
			e.printStackTrace();
		}
		System.exit(1);
		return null;
	}
	/**Gets the current instance of GoToZone initialized in the Initialization and GoToZone thread started in the localizatio.
	 * Needs to be called after initialize in order to dodge nullPointException.n
	 * @return GoToZone
	 */
	public GoToZone getGoToZone(){
		try {
			return gotozone;
		} catch (NullPointerException e) {
			System.out.println("go to zone called before object was initialized");
			e.printStackTrace();
		}
		System.exit(1);
		return null;
	}

	
	
}
