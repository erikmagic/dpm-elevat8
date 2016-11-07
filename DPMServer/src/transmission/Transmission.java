/*
* @author Sean Lawlor, Stepan Salenikovich, Francois OD
* @date November 6, 2013
* @class ECSE 211 - Design Principle and Methods
* 
* Modified by F.P. Ferrie, February 28, 2014
* new parameters for the Winter 2014 competition
*/
package transmission;

import java.io.*;
import java.util.HashMap;

public class Transmission {

	private DataOutputStream dos;
	
	// store the output stream to write to the channel in the default constructor
	public Transmission(DataOutputStream dos) {
		this.dos = dos;
	}
	
	// transmit the data specified, and return true if data transmitted successfully, otherwise false which signifies and error	
	public boolean transfer(HashMap<String,Integer> Data) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(dos);
			System.out.println("OOS created");
			oos.writeObject(Data);
			System.out.println("Object written");
			
			return true;
		} catch (IOException e){
			return false;
		}
	}
	
}
