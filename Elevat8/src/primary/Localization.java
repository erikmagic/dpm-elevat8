package src.primary;

//s1 light sensor, s4 front us sensor
import lejos.hardware.Button;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;

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
	
	
	private EV3UltrasonicSensor usFront;
	private EV3ColorSensor colorSensorRight;
	
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
	public Localization(RegulatedMotor leftMotor, RegulatedMotor rightMotor, Odometer odo, Navigation nav,EV3UltrasonicSensor usFront, EV3ColorSensor colorSensorRight){
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.odo = odo;
		this.nav = nav;
		this.usFront = usFront;
		this.colorSensorRight = colorSensorRight;
	}
	/**This method does the actual localization.
	 * 
	 */
	public void localize(){
		
		// TODO localization code
		SampleProvider usValue = usFront.getMode("Distance");			// colorValue provides samples from this instance
	
		float[] usData = new float[usValue.sampleSize()];				// colorData is the buffer in which data are returned
		
		//Setup color sensor
		// 1. Create a port object attached to a physical port (done above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize operating mode
		// 4. Create a buffer for the sensor data
		SampleProvider colorValue = colorSensorRight.getMode("Red"); 	//provides samples from this instance
		float[] colorData = new float[colorValue.sampleSize()];			// colorData is the buffer in which data are returned
				

		// perform the ultrasonic localization
		USLocalizer usl = new USLocalizer(this.odo, usValue, usData, USLocalizer.LocalizationType.FALLING_EDGE);
		usl.doLocalization();
		try { Thread.sleep(3000); } catch (InterruptedException e){};
		
		// perform the light sensor localization
		LightLocalizer lsl = new LightLocalizer(this.odo, colorSensorRight, leftMotor, rightMotor);
		lsl.doLocalization();			
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);	
		
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
