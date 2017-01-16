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
	private ArrayList<Double> hold_data, filter_list;
	private int sensorSize, hold_data_size = 400, filter_list_size = 5, temp,filterControl = 0, out;
	private double distance, answer;
	
	
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
			hold_data = new ArrayList<Double>(hold_data_size);
			filter_list = new ArrayList<Double>(filter_list_size);
			
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		
		while(constant_loop){
			
			// fetch data from sensor sensor
			sensorProvider.fetchSample(sensorData, 0);
			distance = (sensorData[0]*100.0);
			
			out = hold_data.size();
			
			if (distance > 250.0) distance = 250;
			
			if ( hold_data.size() < 15){ hold_data.add(distance); }
			
			else {
				hold_data.add(distance);
				distance = filter(hold_data, out-5); // always 5 distances behind 
				hold_data.set(out-5, distance);
				

			}
			

			
		}
		
	}
	
	
	/**Moving median filter. The filter always recycle the same arraylist of fixed size. It computes the median
	 * of the 2 previous and two next elements for each element in the array. Recycles the arraylist once it is full
	 * to save space. 
	 * @param list
	 * @param index
	 * @return
	 */
	public double filter(ArrayList<Double> list, int index) {
		
		// make sure values above 250 are 250
		if (list.get(index) > 250.0) {
			list.set(index, 250.0 );
		}
		// collect the 2 elements that are under the index and the 2 above
		for (int i = -5; i <= 5; i++) {
			filter_list.add(list.get(index + i));
		}
		// sort the newly formed list
		filter_list = quicksort(filter_list);
		
		// replace the distance value by the median of the list
		answer = filter_list.get(5);

		// clears the list
		filter_list.clear();

		// returns the distance to be changed in the list ArrayList
		return answer;
	}
	 
	/**
	 * Simple bubble sort algorithm, sorts the specified array_list
	 * 
	 * @param filter_list2
	 * 
	 */
	private ArrayList<Double> quicksort(ArrayList<Double> list){
		if ( list.size() <= 1) return list;
		else {
			double pivot = list.remove(0);
			ArrayList<Double> list1 = getLessEqualThan(list, pivot);
			ArrayList<Double> list2 = getMoreThan(list, pivot);
			list1 = quicksort(list1);
			list2 = quicksort(list2);
			list1.add(pivot);
			list1.addAll(list2);
			return list1;
		}
	}
	
	private ArrayList<Double> getLessEqualThan(ArrayList<Double> list, double pivot){
		ArrayList<Double> returnedList = new ArrayList<Double>();
		for ( double element : list){
			if (element <= pivot) returnedList.add(element);
		}
		return returnedList;
	}
	private ArrayList<Double> getMoreThan(ArrayList<Double> list, double pivot){
		ArrayList<Double> returnedList = new ArrayList<Double>();
		for( double element: list){
			if (element > pivot) returnedList.add(element);
		}
		return returnedList;
	}
	
	public double getValue(){
		return hold_data.get(out-5);
	}
}

