package primary;

import lejos.hardware.Button;
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
//	private Port s4 = brick.getPort("S4");
	private Port s3 = brick.getPort("S3");
	private Port s2 = brick.getPort("S2");
	//private Port s1 = brick.getPort("S1");
	private Port usPort = LocalEV3.get().getPort("S1");
	private Port lightPort = LocalEV3.get().getPort("S4");
	private EV3ColorSensor colorSensor = new EV3ColorSensor(lightPort);
	// EV3ColorSensor colorSensorLeft = new EV3ColorSensor(s3); to be added if
	// we get more ports
	private EV3UltrasonicSensor usSide = new EV3UltrasonicSensor(s2);//bottom
	private EV3UltrasonicSensor usDetectObject = new EV3UltrasonicSensor(s3);//top
	private EV3UltrasonicSensor usFront =  new EV3UltrasonicSensor(usPort);
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
	
	//button choice
	int buttonChoice;

	
	// initialize logger
	
	
	// numerical value fields
	private final double TRACKSIZE = 16.35; // 14.5 -> not enough, 16 -> not enough, 16.5 -> too much from the middle of the left wheel to the middle of the right wheel
	private final double WHEELRADIUS = 2.1;
	private final int ACCELERATION = 500;
	private final int FORWARDSPEED = 185;
	private final int ROTATIONSPEED = 185;
	
	// WIFI data variables
	public static int BTN, BSC, CTN, CSC, LRZx, LRZy, URZx, URZy, LGZx, LGZy, UGZx, UGZy;
	/* RANGES:
	 * BTN -> [1 - 17]
	 * BSC -> [1 - 4]
	 * CTN -> [1 - 17]
	 * CSC -> [1 - 4]
	 * LRZx -> [-1 - 10]
	 * LRZy -> [-1 - 10]
	 * URZx -> [0 - 11]
	 * URZy -> [0 - 11]
	 * LGZx -> [-1 - 10]
	 * LGZy -> [-1 - 10]
	 * UGZx -> [0 - 11]
	 * UGZy -> [0 - 11]
	 * 
	 * Assumptions: 
	 * LRZx < URZx & LGZx < UGZx
	 * LRZy < URZy & LGZy < UGZy
	 */
	
	// corner coordinates
	private final double[][] X = {{0,0},{304.8,0},{304.8,304.8},{0,304.8}};
	
	/**Empty constructor
	 * 
	 */
	public Initialization(){
		
	}
	
	/**The initialize method simply starts the methods getWIFI, startThreads and initializeObjects
	 * @throws FileNotFoundException 
	 * 
	 */
	public void initialize() throws FileNotFoundException{
		// start by getting wifi info to fetch needed parameters for object initializations
		//getWIFI();
		// initialize objects used troughout the code
		initializeObjects();
		// start urgent threads, after initialize objects because some of these threads are objects
		startThreads();
		
		// localize the robot once everything has been set up before
		//searchMove.start();
		loc.localize();
		while(true){
			searchMove.start();
			detectObject.start();
		}
		//System.exit(0);
	}
	
	public void isolation_test() throws FileNotFoundException{
		initializeObjects();
		startThreads();
//		nav.turnTo(180, true);
//		nav.turnTo(360, true);
		//TODO: PLEASE DO NOT REMOVE THIS CODE, might be useful later
		while(true){
			buttonChoice = Button.waitForAnyPress();
			while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);
			if (buttonChoice == Button.ID_LEFT){
				odo.setTrack(0.1);
			}
			else if (buttonChoice == Button.ID_RIGHT){
				odo.setTrack(-0.1);
			}
			nav.turnTo(90, true);
			Button.waitForAnyPress();
			nav.turnTo(0, true);
		}
//		nav.turnTo(270, true);
//		nav.turnTo(0, true);
		//System.exit(0);
	}
	public void sensor_test() throws FileNotFoundException{
		initializeObjects();
		startThreads();
		for (int i = 0; i < 100 ; i++){
			frontSensor.getValue();	
		}
		lejos.hardware.Button.waitForAnyPress();
		System.exit(0);
	}
	/**
	 * Creates a WIFI object and accesses the object to fetch needed information
	 */
	public void getWIFI() {
		WifiReceiver receiver = new WifiReceiver();
		receiver.initiateWifi();
	}

	/**
	 * Starts the TIMER, ODOMETER, USSENSORS, COLORSENSORS AND DISPLAY
	 */
	public void startThreads() {
		//custom_timer.startTimer(); // start the actual timer thread
		odo.start();
		sideSensor.start();
		frontSensor.start();
		heightSensor.start();		
	}

	/**
	 * Initializes instances of localization, custom_timer and navigation
	 * @throws FileNotFoundException 
	 */
	public void initializeObjects() throws FileNotFoundException {
		
		Logger.setLogWriter("sensor_data.txt");
		 // set up logger
		//custom_timer = new CustomTimer(timer, deadline); // initialize timer object
		odo = new Odometer(leftMotor, rightMotor, TRACKSIZE, WHEELRADIUS);
		//Odometer odo = new Odometer(leftMotor, rightMotor, 30, true);

		lcd = new LCDInfo(odo);
		sideSensor = new USSensor(usSide);
		heightSensor = new USSensor(usDetectObject);
		frontSensor = new USSensor(usFront);
		correctionSensor = new ColorSensor(colorSensor);
		//odoCorrection = new OdometerCorrection(odo, correctionSensor);
		//nav = new Navigation(odo);
		nav = new Navigation(leftMotor, rightMotor, odo, ROTATIONSPEED, FORWARDSPEED, ACCELERATION, WHEELRADIUS, TRACKSIZE);
		//capture = new Capture(leftMotor, rightMotor, nav, odo, FORWARDSPEED, ROTATIONSPEED, ACCELERATION, sideSensor, frontSensor, heightSensor);
		//dodgeObject = new DodgeObject(leftMotor, rightMotor, nav, odo, FORWARDSPEED, ROTATIONSPEED, ACCELERATION, sideSensor, frontSensor, heightSensor);
		detectObject = new DetectObject(leftMotor, rightMotor, nav, odo, FORWARDSPEED, ROTATIONSPEED, ACCELERATION, sideSensor, frontSensor, heightSensor);
		searchMove = new SearchAndMove(leftMotor, rightMotor, nav, odo, ACCELERATION, FORWARDSPEED, ROTATIONSPEED, sideSensor, frontSensor, heightSensor);
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