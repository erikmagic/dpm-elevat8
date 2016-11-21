package primary;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.PrintStream;

/**Logger that can be used to save variables or text. Before using for the first time, call setLogWriter to
 * create the file. In the current version only one file can be created because the methods are statics.
 * @author Erik-Olivier Riendeau
 *
 */
public class Logger {
	
	static PrintStream writer = System.out;
	private final static long initialTime = System.currentTimeMillis();
	private static long timestamp;
	
	/**Logs the string in the file with the time. Also skips line. To print other types,
	 * use Double.toString() - by example.
	 * @param message
	 */
	public static void log(String message)
	{
		// prints the current time
		timestamp = (System.currentTimeMillis() - initialTime) ;
		
		writer.println(timestamp +  ":" + message);
		writer.println("\r\n");
	}
	
	
	/**Sets the name of the file to save data, if the file does not exist, throws an exception 
	 * and then creates it.
	 * @param filename
	 * @throws FileNotFoundException
	 */
	public static void setLogWriter(String filename) throws FileNotFoundException
	{
		writer = new PrintStream(new File(filename));
		
	}


}