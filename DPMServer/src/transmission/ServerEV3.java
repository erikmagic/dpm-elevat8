/*
 * @author Sean Lawlor, Stepan Salenikovich, Francois OD
 * @date November 6, 2013
 * @class ECSE 211 - Design Principle and Methods
 *
 * Modified by F.P. Ferrie, February 28, 2014
 * new parameters for the Winter 2014 competition
 * also fixed nxtComm.search calls as the API changed in 0.9.1
*/

package transmission;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import universal.Universal;
import gui.*;

/*
 * This is the main PC Server function for the NXT communication for the Fall 2012 ECSE 211 Final Project
 */

public class ServerEV3{

	private MainWindow mw;
	private ServerSocket[] serverSockets = new ServerSocket[20];
	private Socket[] connectionSockets = new Socket[20];

	public ServerEV3(MainWindow mw) {
		this.mw = mw;
		
		// initialize one server socket per team
		// port numbers are 2000 + team number (so 2001 through 2020)
		try {
			for(int i = 0; i<serverSockets.length; i++) {
				serverSockets[i] = new ServerSocket(2001+i);
				serverSockets[i].setSoTimeout(10000);
			}
		} catch (IOException e) {
			this.mw.displayOutput("Failed to create server sockets", true);
		}
		
	}

	public int transmit(int[] teamNumbers, char[] roles, HashMap<String,Integer> StartData) {
		switch (Universal.TRANSMIT_RULE) {
		case BOTH:
			//TODO Fix parity checks of data to send
			/*if (teamNumbers.length != roles.length && roles.length != startingCorners.length) {
				this.mw.displayOutput("Length of names, roles, and starting corners don't match", true);
				return -1;
			}*/
			int result = -1;
			// Attempt to open sockets to all targeted clients, unless the socket is already open
			for (int i = 0; i < teamNumbers.length; i ++) {
				this.mw.displayOutput("Attempting to connect to team " + teamNumbers[i] + "...", false);
				try {
					connectionSockets[teamNumbers[i]-1] = serverSockets[teamNumbers[i]-1].accept();
					this.mw.displayOutput("Connected to team " + teamNumbers[i], true);
				} catch (SocketTimeoutException  e) {
					this.mw.displayOutput("Failed to connect to team " + teamNumbers[i] + " (is the EV3 wifi client connected?)", true);					
					//return -1;
				} catch (IOException  e) {
					this.mw.displayOutput("IO Exception!", true);
					return -1;
				}
				//TODO Fix security
				/*
				if(connectionSockets[teamNumbers[i]-1] != null && !connectionSockets[teamNumbers[i]-1].isClosed()) {
					this.mw.displayOutput("Already connected to team " + teamNumbers[i], true);
				} else {
					this.mw.displayOutput("Attempting to connect to team " + teamNumbers[i] + "...", false);
					try {
						connectionSockets[teamNumbers[i]-1] = serverSockets[teamNumbers[i]-1].accept();
						this.mw.displayOutput("Connected to team " + teamNumbers[i], true);
					} catch (SocketTimeoutException  e) {
						this.mw.displayOutput("Failed to connect to team " + teamNumbers[i] + " (is the EV3 wifi client connected?)", true);					
						return -1;
					} catch (IOException  e) {
						this.mw.displayOutput("IO Exception!", true);
						return -1;
					}	
				}
				*/
			}
			// If all sockets are opened, transmit info to all targeted clients
			for (int i = 0; i < teamNumbers.length; i ++) {
				result = sendThroughSocket(connectionSockets[teamNumbers[i]-1], teamNumbers[i], StartData);
				if (result == -1){
					return -1;
				}
			}
			return result;
		case OFFENCE_ONLY:
			//TODO resolve code repetition of socket opening.
			for (int i = 0; i < roles.length; i++){
				if (roles[i] == 'R'){
					try {
						connectionSockets[teamNumbers[i]-1] = serverSockets[teamNumbers[i]-1].accept();
						this.mw.displayOutput("Connected to team " + teamNumbers[i], true);
					} catch (SocketTimeoutException  e) {
						this.mw.displayOutput("Failed to connect to team " + teamNumbers[i] + " (is the EV3 wifi client connected?)", true);					
						return -1;
					} catch (IOException  e) {
						this.mw.displayOutput("IO Exception!", true);
						return -1;
					}
					return sendThroughSocket(connectionSockets[teamNumbers[i]-1], teamNumbers[i], StartData);
				}
			}
			this.mw.displayOutput("No Red player specified to transmit to", true);
			return -1;
		case DEFENCE_ONLY:
			for (int i = 0; i < roles.length; i++){
				if (roles[i] == 'G'){
					try {
						connectionSockets[teamNumbers[i]-1] = serverSockets[teamNumbers[i]-1].accept();
						this.mw.displayOutput("Connected to team " + teamNumbers[i], true);
					} catch (SocketTimeoutException  e) {
						this.mw.displayOutput("Failed to connect to team " + teamNumbers[i] + " (is the EV3 wifi client connected?)", true);					
						return -1;
					} catch (IOException  e) {
						this.mw.displayOutput("IO Exception!", true);
						return -1;
					} catch (SecurityException e) {
						System.out.println(e.getMessage());
						return -1;
					}
					return sendThroughSocket(connectionSockets[teamNumbers[i]-1], teamNumbers[i], StartData);
				}
			}
			this.mw.displayOutput("No Green player specified to transmit to", true);
			return -1;
		default:
			this.mw.displayOutput("Something went wrong detecting the startup mode from Debug class", true);
			return -1;
		}
	}
	
	public int transmit(int[] teamNumbers, HashMap<String,Integer> StartData) {
		int result = -1;
		int check = 0;
		// Attempt to open sockets to all targeted clients, unless the socket is already open
		for (int i = 0; i < teamNumbers.length; i ++) {
			this.mw.displayOutput("Attempting to connect to team " + teamNumbers[i] + "...", false);
			try {
				check = 0;
				connectionSockets[teamNumbers[i]-1] = serverSockets[teamNumbers[i]-1].accept();
				this.mw.displayOutput("Connected to team " + teamNumbers[i], true);
			} catch (SocketTimeoutException  e) {
				this.mw.displayOutput("Failed to connect to team " + teamNumbers[i] + " (is the EV3 wifi client connected?)", true);					
				check = -1;
				//return -1;
			} catch (IOException  e) {
				this.mw.displayOutput("IO Exception!", true);
				//return -1;
				check = -1;
			}
			if (check != -1){
				result = sendThroughSocket(connectionSockets[teamNumbers[i]-1], teamNumbers[i], StartData);
			}
			if (result == -1){
				return -1;
			}
		}
		return -1;
	}

	public int sendThroughSocket(Socket connectionSocket, int teamNumber, HashMap<String,Integer> Data) {
		
		DataOutputStream dos = null;
		
		try {			
			
			while (true) {
				
				// Open data stream to client
				dos = new DataOutputStream(connectionSocket.getOutputStream());				
				
				// Attempt to transmit the data then end the connection 
				this.mw.displayOutput("Sending transmission to team " + teamNumber + "...", false);
				Transmission trans = new Transmission(dos);
				boolean transmission = trans.transfer(Data);
				if (!transmission) {
					this.mw.displayOutput("Transmission to team " + teamNumber + " failed, quitting...", false);
					dos.close();
					connectionSocket.close();
					this.mw.displayOutput("Connection to team " + teamNumber + " terminated", true);
					return -1;
				} else {
					this.mw.displayOutput("Transmission to team " + teamNumber + " successful, quitting...", false);					
					dos.close();
					connectionSocket.close();
					this.mw.displayOutput("Connection to team " + teamNumber + " terminated", true);
					return 0;	
				}
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			this.mw.displayOutput("Failed to provide EV3 name", true);
			return -1;
		} catch (SocketTimeoutException  e) {
			this.mw.displayOutput("Socket timed out (client didn't connect)", true);
			return -1;
		} catch (IOException e) {
			this.mw.displayOutput("IO Exception!", true);
			return -1;
		}
		
	}

}
