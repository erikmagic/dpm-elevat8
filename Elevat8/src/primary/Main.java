package primary;

import java.io.FileNotFoundException;

/**
 * Main is almost empty, all the work is done in Initialization, localization
 * and subsequent called classes. Main only creates an Initialization object and
 * call initialize on this object.
 * 
 * @author Erik-Olivier Riendeau, 2016
 *
 */
public class Main {

	/**
	 * Main method that only starts the initialization which does all the work.
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 *             for the logger
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// calls initialization in order to be able to call non static methods
		// which is not possible in the main

		Initialization initialize = new Initialization();

		//initialize.initialize();
		// initialize.sensor_test();
		// initialize.isolation_test();
		// initialize.localization_test();
		initialize.gotozone_test();
		//initialize.detection_test();
	}

}
