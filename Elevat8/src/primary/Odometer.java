package primary;

import lejos.robotics.RegulatedMotor;

/**Takes value from the tachometer and gives a somewhat precise estimation of the current position. 
 * The Odometer works closely with the Odometer Correction class in order to achieve more precise results.
 * @author Erik-Olivier Riendeau, 2016
 *
 */
public class Odometer extends Thread {
	
	// ---------------------------------- fields -------------------------------------- //
	private boolean always_on;
	private RegulatedMotor rightMotor, leftMotor;
	private double trackSize, wheelRadius;
	private Object lock;
	private double positionX = 0, positionY = 0, theta = 0, nowTachoL = 0, nowTachoR = 0, previousTachoL = 0, previousTachoR = 0;
	private long updateStart, updateEnd;
	private double displacementLeft, displacementRight, displacement, changeDirection, dX, dY;
	// odometer update period, in ms
		private static final long ODOMETER_PERIOD = 25;
	
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
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		while(always_on){
			// TODO implementation of the odometer
			
			// reset the motor tacho meter
			leftMotor.resetTachoCount();
			rightMotor.resetTachoCount();

			while (true) {
				updateStart = System.currentTimeMillis();
				// put (some of) your odometer code here
				
				
				
				//System.out.println("left tacho value is " + leftMotor.getTachoCount() +" right tacho value is " 
				//		+ rightMotor.getTachoCount());
				
				// measure the progression of each motor
				nowTachoL = leftMotor.getTachoCount();
				nowTachoR = rightMotor.getTachoCount();
				
				// compute distance
				displacementLeft = ((Math.PI)*wheelRadius*(nowTachoL- previousTachoL))/180 ;
				displacementRight = ((Math.PI)*wheelRadius*(nowTachoR- previousTachoR))/180;
				
				// update now tacho reading to previous tacho reading
				previousTachoL = nowTachoL;
				previousTachoR = nowTachoR;
				
				// compute total displacement
				displacement = (displacementLeft + displacementRight)/2;
				
				// compute change in direction the robot is heading to
				changeDirection = (displacementLeft-displacementRight)/(trackSize);
				
				

				synchronized (lock) {
					// don't use the variables x, y, or theta anywhere but here!
					
					// update theta 
					theta -= changeDirection;
					
					// update dX and dY using cos, sin and theta
					dX = displacement*Math.cos(theta);
					dY = displacement*Math.sin(theta);
					
					
					// makes sure that theta is between 0 and 2pi
					theta = fixDegAngle(theta);
					
					// switch default role to
					
					// add the displacement to the last position
					positionX += dX;
					positionY += dY;
					
				}

				// this ensures that the odometer only runs once every period
				updateEnd = System.currentTimeMillis();
				if (updateEnd - updateStart < ODOMETER_PERIOD) {
					try {
						Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
					} catch (InterruptedException e) {
						// there is nothing to be done here because it is not
						// expected that the odometer will be interrupted by
						// another thread
					}
				}
			}
		}
	}
	/**
	 * @return - the most updated position in the x axis in cm.
	 */
	public double getX(){
		synchronized(lock){
			return positionX;
		}
	}
	/**
	 * @return - the most updated position in the y axis in cm.
	 */
	public double getY(){
		synchronized(lock){
			return positionY;
		}
	}
	/**
	 * @return - the most updated angle in degrees.
	 */
	public double getAngle(){
		synchronized(lock){
			return theta * 180/Math.PI;
		}
	}
	/**
	 * @param position - set { position in x in cm, position in y in cm, angle in degrees from 0 to 360 }
	 * @param update - { true - will update the first element of the position array, true - will update the 
	 * second element of the position array, true - will update the third element of the position array }.
	 * If any of these elements are false, it will not update the corresponding position.
	 */
	public void setPosition(double[] position, boolean[] update) {
		synchronized (lock){
			if (update[0]){ 
				positionX = position[0];
			}
			if (update[1]){
				positionY = position[1];
			}
			if (update[2]){
				theta = position[2]*Math.PI/180;
			}
		}
	}

	/**
	 * @return { the position in x in cm, the position in y in cm, the angle in degrees }
	 */
	public double[] getPosition() {
		synchronized (this) {
			return new double[] { positionX, positionY, theta * 180/Math.PI };
		}
	}
	//FOR TESTING
	public double getTrack(){
		synchronized(this){
			return trackSize;
		}
	}
	
	public void setTrack (double added){
		synchronized(this){
			trackSize += added;
		}
	}
//TESTING END
	
	/**
	 * @param x - set the position on the x axis in cm
	 */
	public void setX(double x){
		positionX = x;
	}
	/**
	 * @param y - set the position on the y axis in cm
	 */
	public void setY(double y){
		positionY = y;
	}
	/**
	 * @param ang - set the angle in degrees
	 */
	public void setAngle(double ang){
		theta = ang * 	Math.PI/180;
	}
	
	/**
	 * @param angle - in degrees , can be negative or more than 360
	 * @return fixed angle that include negative angles and angle above 360 degrees.
	 */
	private double fixDegAngle(double angle) {
		if (angle < 0.0)
			angle = 2*Math.PI + (angle % (2*Math.PI));

		return angle % (2*Math.PI);
	}


}