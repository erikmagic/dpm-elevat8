package primary;

import java.util.Timer;
import java.util.TimerTask;

import lejos.hardware.Sound;

/**
 * Timer Thread class that starts a timer and ends all Thread after a certain time limit
 * @author Erik-Olivier Riendeau, 2016
 * 
 */
public class CustomTimer extends Thread{
	
	// ----------------------- fields ----------------------- //
	private Timer timer;
	private double limit;
	
	/**
	 * Timer Constructor, calls LimitClass() after desired amount of timer
	 * @param timer
	 * @param limit 
	 * 
	 */
	public CustomTimer(Timer timer, double limit){
		this.timer = timer;
		this.limit = limit;
	}
	public void startTimer(){
		timer.schedule(new LimitClass(), (long) (limit*1000)); // calls the the task LimitClass after desired amount of time for execution
	}
	
	/**
	 * Thread timer that is called from the Timer, this class only pauses all threads before System.exit
	 * @author Erik-Olivier Riendeau, 2016
	 *  
	 */
	private class LimitClass extends TimerTask{
		public void run(){
			Sound.twoBeeps();
			// add all threads to stop
			
			//
			
			System.exit(0);
		}
	}
}
