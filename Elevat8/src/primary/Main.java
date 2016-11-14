package src.primary;

/**Main is almost empty, all the work is done in Initialization, localization and subsequent called classes.
 * @author Erik-Olivier Riendeau, 2016
 *
 */
public class Main {

	/**Main method that only starts the initialization which does all the work.
	 * @param args
	 */
	public static void main(String[] args) {
		Initialization initialize = new Initialization();
		initialize.initialize();
	}

}
