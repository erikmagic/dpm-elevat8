package primary;

import java.util.ArrayList;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

/**This class uses the sensor sensor or the side sensor , filters its result and return
 * its values.
 * This thread is intended to be runned multiples times with the three UltraSonic sensors
 * @author Erik-Olivier Riendeau, 2016
 * 
 * @version 1.0
 */
public class USSensor extends Thread  {
	
	// ------------------------ fields ------------------------ //
	private EV3UltrasonicSensor sensorSensor;
	private SampleProvider sensorProvider;
	private boolean constant_loop = true;
	private Object lock;
	float[] sensorData;
	private ArrayList<Integer> hold_data, filter_list;
	private int sensorSize, hold_data_size = 400, filter_list_size = 5, distance, answer, temp,filterControl = 0;
	
	
	/**
	 * Constructor for USSensors object, also creates used arguments in the class
	 * @param sensorSensor
	 * 
	 */
	public USSensor(EV3UltrasonicSensor sensorSensor){
			this.sensorSensor = sensorSensor;
			lock = new Object();
			
			// set up sensors
			sensorProvider = sensorSensor.getDistanceMode();
			sensorSize = sensorProvider.sampleSize();
			sensorData = new float[sensorSize];
			// array of fixed size  that is constantly re-used in order to save space
			hold_data = new ArrayList<Integer>(hold_data_size);
			filter_list = new ArrayList<Integer>(filter_list_size);
			
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		
		while(constant_loop){
			
			// fetch data from sensor sensor
//			sensorProvider.fetchSample(sensorData, 0);
//			distance = (int)(sensorData[0]*100);
			
			/*
			// filter implementation
			// if the list reaches the end, keep last elements as new elements and clear the rest
			if (hold_data.size() == 200){
				// keeps the 10 first element of the list ( old's last elements)
				for (int i = 0; i < 10; i++){
					hold_data.set(i, hold_data.get(i+190));
				}
				// clear the rest
				for(int i = 10; i < 200; i++){
					hold_data.remove(i);
				}
			}
			hold_data.add(distance);
			
			if (hold_data.size() > 10){
				synchronized (lock){
					distance = filter(hold_data, hold_data.size()-2); // always two element late for the filter to work
				}
			}
			*/
			
		}
		
	}
	
	
	/**Moving median filter. The filter always recycle the same arraylist of fixed size. It computes the median
	 * of the 2 previous and two next elements for each element in the array. Recycles the arraylist once it is full
	 * to save space. 
	 * @param list
	 * @param index
	 * @return
	 */
	public int filter(ArrayList<Integer> list, int index){
		
		// make sure values above 250 are 250
		if (list.get(index) > 250){
			list.set(index, 250);
		}
		// collect the 2 elements that are under the index and the 2 above
		for (int i = -2; i <= 2; i++){
			filter_list.add(list.get(index + i));
		}
		// sort the newly formed list
		sort_list(filter_list);
		
		// replace the distance value by the median of the list
		answer = filter_list.get(2);
		
		// clears the list
		filter_list.clear();
		
		// returns the distance to be changed in the list ArrayList
		return answer;
	}
	 
	/**Simple bubble sort algorithm, sorts the specified array_list
	 * @param list
	 * 
	 */
	private void sort_list(ArrayList<Integer> list){
		for( int out_loop = 0; out_loop < filter_list_size-2; out_loop++){
			for( int in_loop = out_loop; in_loop< filter_list_size-1; in_loop++){
				if ( list.get(out_loop) > list.get(in_loop+1)){
					temp = list.get(out_loop);
					list.set(out_loop, list.get(in_loop+1));
					list.set(in_loop+1, temp);
					
				}
			}
		}
	}
	
	/** returns the distance value filtered from the sensor
	 * @return distance
	 */
	//NOTE: for now, using a simpe filter for now, since filter not done
	public int getValue(){
		synchronized (lock){
			sensorProvider.fetchSample(sensorData, 0);
			int distance = (int)(sensorData[0]*100);
			if (distance >= 255 && filterControl < 10) {
				// bad value, do not set the distance var, however do increment the
				// filter value
				filterControl++;
			} else if (distance >= 255) {
				// We have repeated large values, so there must actually be nothing
				// there: leave the distance alone
				this.distance = distance;
			} else {
				// distance went below 255: reset filter and leave
				// distance alone.
				filterControl = 0;
				this.distance = distance;
			}
						
				return this.distance;
			//Logger.log("Distance : " + Integer.toString(this.distance));

		}
	}
}

