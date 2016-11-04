package primary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Logger {
	
	static PrintStream writer = System.out;
	private final static long initialTime = System.currentTimeMillis();
	private static long timestamp;
	
	public static void log(String message)
	{
		// prints the current time
		timestamp = (System.currentTimeMillis() - initialTime) ;
		
		writer.println(timestamp +  ":" + message);
		writer.println("\r\n");
	}
	
	
	public static void setLogWriter(String filename) throws FileNotFoundException
	{
		writer = new PrintStream(new File(filename));
		
	}


}