package primary;


import java.io.IOException;
import java.util.HashMap;

import wifi.WifiConnection;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

public class WifiReceiver {
	
	private static final String SERVER_IP = "192.168.2.13";
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

		
	}
	
}
