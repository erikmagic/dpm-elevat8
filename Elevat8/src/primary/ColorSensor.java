package primary;

import java.util.ArrayList;

import lejos.hardware.ev3.EV3;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

/**Color Sensor that uses the redMode to detect black lines on the ground
 * @author Erik-Olivier Riendeau, 2016
 *
 */
public class ColorSensor extends Thread {

	// ----------------------- fields ------------------- //
	private boolean constant_loop = true;
	private Object lock;
	float[] sensorData;
	private EV3ColorSensor colorSensor;
	private SampleProvider redMode;
	private int sensorSize, value;
	
	
	/**
	 * ColorSensor constructor
	 *
	 * @param colorSensor
	 */
	public ColorSensor(EV3ColorSensor colorSensor){
		this.colorSensor = colorSensor;
		redMode = colorSensor.getRedMode();
		sensorSize = redMode.sampleSize();
		sensorData = new float[sensorSize];
		lock = new Object();
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		while(constant_loop){
			redMode.fetchSample(sensorData, 0);
			value = (int) (sensorData[0]*100);
		}
	}
	
	
	
	/**
	 * @param list
	 * @param index
	 * @return
	 */
	public int filter(ArrayList<Integer> list, int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	/**return the value detected by the sensor
	 * @return value detected
	 */
	public int getValue() {
		synchronized(lock){	
			return value;
		}
	}
	
 
}
