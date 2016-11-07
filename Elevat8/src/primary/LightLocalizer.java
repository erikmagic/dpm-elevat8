package primary;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;

/**
 * @author Ken
 *
 */
public class LightLocalizer {
	private Odometer odo;
	private EV3ColorSensor colorSensor;
	private double d = 11.5;	//was 13.25 distance between the light sensor and the center of track
	private double x,y,theta; // values to compute
	
	private RegulatedMotor leftMotor, rightMotor;
	

	public LightLocalizer(Odometer odo, EV3ColorSensor colorSensor,
			RegulatedMotor leftMotor, RegulatedMotor rightMotor) {
		this.odo = odo;
		this.colorSensor = colorSensor;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
	}
	
	public void doLocalization() {
		// array to record data x y theta by the odometer
		double [] pos = new double [3];
		//want to use travalto and turnto method from Navigation class
		Navigation navigator = new Navigation(this.odo);
		int FORWARD_SPEED = Initialization.FORWARDSPEED;
		int ROTATION_SPEED = Initialization.ROTATIONSPEED;
		navigator.turnTo(135,true);	//turn 135 degree, make it face to destination(0.0) 
		odo.setPosition(new double [] {0.0, 0.0, 135}, new boolean [] {false, false, true});
		
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		//move 14 cm to do ls localization
		leftMotor.rotate(convertDistance(Initialization.WHEELRADIUS,11),true);
		rightMotor.rotate(convertDistance(Initialization.WHEELRADIUS,11),false);
		
		//wait a while
		leftMotor.stop();
		rightMotor.stop();
		
		
		double angle[] = new double[4];	//declare an array to store the angles 
		int countgridlines=0;	//number of lines
		while (countgridlines < 4)//start counting lines
		{	
			
			
			
			pos = odo.getPosition();	//get current posistion from odometer
			if (getColorData() < 0.27)	//0.27 by trials
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
			leftMotor.setSpeed((int) ROTATION_SPEED);
			rightMotor.setSpeed((int) ROTATION_SPEED);
			leftMotor.backward();
			rightMotor.forward();
		}
		
		
		// start calculating x, y
		double temp = 0;
		temp = 360 - angle[1] + angle[3];
		y = -d*Math.cos(Math.PI*temp/360);
		temp = Math.abs(angle[0] - angle[2]);
		x= d*Math.cos(Math.PI*temp/360); 
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
		odo.setPosition(new double [] {x, y, 0}, new boolean [] {true, true, false});	
		// travel to origin
		navigator.travelTo(0,0);
		// turn back to 0 direction
		navigator.turnTo(0, true);
	
	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	
	// we use RedMode to detect lines
	private float getColorData() {
		SampleProvider color = colorSensor.getRedMode();
		float[] sample = new float[colorSensor.sampleSize()];
		color.fetchSample(sample, 0);
		return sample[0];
	}

}