package primary;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.remote.ev3.RemoteEV3;
import lejos.robotics.RegulatedMotor;

/**
 * @author Erik-Olivier Riendeau, 2016
 * Main Class that contains all parameters passed to other Classes and Threads. Also starts the ODOMETER, THE DISPLAY
 * ,THE TIMER, THE USSENSORS AND THE COLORSENSORS. Starts by initializing the WIFI class to fetch important information
 * as parameters for the demo.
 *  
 */
public class Main {

	
	
	/**
	 * @param args 
	 * Main method that executes directly in the EV3
	 */
	public static void main(String[] args) {
		// sets motor to according ports
		RegulatedMotor left = new EV3LargeRegulatedMotor(MotorPort.B);
		RegulatedMotor right = new EV3LargeRegulatedMotor(MotorPort.C);
		RegulatedMotor elevateMotor = new EV3LargeRegulatedMotor(MotorPort.D);
		RegulatedMotor clawMotor = new EV3LargeRegulatedMotor(MotorPort.A);
		
		// set up sensors to ports
		Brick brick = BrickFinder.getDefault();
	    Port s4 = brick.getPort("S4");
	    Port s3 = brick.getPort("S3");
	    Port s2 = brick.getPort("S2");
	    Port s1 = brick.getPort("S1");
	    EV3ColorSensor colorSensorRight = new EV3ColorSensor(s4);
	    EV3ColorSensor colorSensorLeft = new EV3ColorSensor(s3);
	    EV3UltrasonicSensor usSide = new EV3UltrasonicSensor(s2);
	    EV3UltrasonicSensor usFront = new EV3UltrasonicSensor(s1);
	    
		// https://sourceforge.net/p/lejos/wiki/Remote%20access%20to%20an%20EV3/
		// https://lejosnews.wordpress.com/2015/02/11/pan-configuration/ to add a sensor from another brick
		
	    // set up the display
	    
	    
	    
		
	}
	
	/**
	 * Creates a WIFI object and accesses the object to fetch needed information
	 */
	public static void getWIFI(){
		
	}
	
	/**
	 * Starts the TIMER, ODOMETER, USSENSORS, COLORSENSORS AND DISPLAY
	 */
	public static void startThreads(){
		
	}
	/**
	 * Initializes intances of localization and navigation
	 */
	public static void initializeObjects(){
		
	}
}	
