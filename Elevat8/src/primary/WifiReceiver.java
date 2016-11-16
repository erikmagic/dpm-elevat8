package primary;


import java.io.IOException;
import java.util.HashMap;

import wifi.WifiConnection;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

public class WifiReceiver {
	
	private static final String SERVER_IP = "192.168.2.13"; // will change on competition day
	private static final int TEAM_NUMBER = 1;

	private static TextLCD LCD = LocalEV3.get().getTextLCD();
	
	public void initiateWifi(){
		
		WifiConnection conn = null;
		
		try {
			System.out.println("Connecting...");
			conn = new WifiConnection(SERVER_IP, TEAM_NUMBER, true);
		} catch (IOException e) {
			System.out.println("Connection failed");
		}
		
		LCD.clear();

		if(conn != null){
		
			HashMap<String, Integer> dataMap = conn.StartData;
			if(dataMap == null){
				System.out.println("Failed to read transmission.");
			}
			else{
				System.out.println("Transmission received.\nReading & transfering data...");
				Initialization.BTN = dataMap.get("BTN"); // [1 - 17]
				Initialization.BSC = dataMap.get("BSC"); // [1 - 4]
				Initialization.CTN = dataMap.get("CTN"); // [1 - 17]
				Initialization.CSC = dataMap.get("CSC"); // [1 - 4]
				Initialization.LRZx = dataMap.get("LRZx"); // [-1 - 10]
				Initialization.LRZy = dataMap.get("LRZy"); // [-1 - 10]
				Initialization.URZx = dataMap.get("URZx"); // [0 - 11]
				Initialization.URZy = dataMap.get("URZy"); // [0 - 11]
				Initialization.LGZx = dataMap.get("LGZx"); // [-1 - 10]
				Initialization.LGZy = dataMap.get("LGZy"); // [-1 - 10]
				Initialization.UGZx = dataMap.get("UGZx"); // [0 - 11]
				Initialization.UGZy = dataMap.get("UGZy"); // [0 - 11]
				System.out.println("Data transfer complete.");
				System.out.println("Initiate localization...");
			}
		}
		
	}
	
}
