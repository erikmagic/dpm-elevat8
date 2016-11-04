package primary;

import lejos.robotics.RegulatedMotor;

/**Odometer class, takes value from the tachometer and gives a somewhat precise estimation of the current position. 
 * The Odometer works closely with the Odometer Correction class in order to achieve more precise results
 * @author Erik-Olivier Riendeau, 2016
 *
 */
public class Odometer extends Thread {
	
	// ---------------------------------- fields -------------------------------------- //
	private boolean always_on;
	private RegulatedMotor rightMotor, leftMotor;
	private double trackSize, wheelRadius;
	private Object lock;
	private double positionX, positionY, angle;
	/**Odometer constructor
	 * 
	 * @param leftMotor
	 * @param rightMotor
	 * @param trackSize
	 * @param wheelRadius
	 */
	public Odometer(RegulatedMotor leftMotor, RegulatedMotor rightMotor, double trackSize, double wheelRadius){
		lock = new Object();
		always_on = true;
		this.rightMotor = rightMotor;
		this.leftMotor = leftMotor;
		this.trackSize = trackSize;
		this.wheelRadius = wheelRadius;
		
	}
	/**Runnable instance
	 * 
	 */
	public void run(){
		while(always_on){
			// TODO implementation of the odometer
		}
	}
	public double getX(){
		synchronized( lock){
			return positionX;
		}
	}
	public double getY(){
		synchronized(lock){
			return positionY;
		}
	}
	public double getAngle(){
		synchronized(lock){
			return angle;
		}
	}
	public void setX(double x){
		positionX = x;
	}
	public void setY(double y){
		positionY = y;
	}
	public void setAngle(double ang){
		angle = angle;
	}

}
