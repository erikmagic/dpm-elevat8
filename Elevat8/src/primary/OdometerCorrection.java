package primary;

import lejos.hardware.sensor.EV3ColorSensor;

/**This class uses the color sensor aiming to the ground to determine black lines on the competition board.
 * Also uses the odometer to know approximately where the robot is in order to update the good axis. Version 1.0 
 * uses 1 color sensor. The 0,0 point is considered to be the upper corner of the starting tile.
 * @author Erik-Olivier Riendeau, 2016
 *@version 1.0
 */
public class OdometerCorrection extends Thread {
	private Odometer odo;
	private ColorSensor correctionSensor;
	private boolean always_on = true;
	double lastX, lastY;
	private long correctionStart, correctionEnd;
	private final double max_corner = 10;
	private int counter_corner = 0;
	private final double tile = 30.48;
	private double small_precision = 3;
	private double big_precision = 28;
	
	/**Constructor, necessitates the odometer to access the position of the robot to know what to correct. 
	 * Also necessitates the Color Sensor to detect black lines and subsequently correct the position
	 * @param odo
	 * @param correctionSensor
	 */
	public OdometerCorrection(Odometer odo, ColorSensor correctionSensor){
		this.odo = odo;
		this.correctionSensor = correctionSensor;
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		while (always_on){
			if (correctionSensor.getValue() < 27){ // detects a black line
				// if it is too close to a corner , do nothing
				for ( int i = 0; i < 4; i++){
					// check if the robot's heading is close to a corner
					if ( odo.getAngle() < (45 + 90*i)+max_corner && odo.getAngle() > (45 +90*i) - max_corner){
						counter_corner++;
						break;
					}
				}
				// in the case in which the robot is sufficiently far from the corners
				if (counter_corner == 0){
					lastY = odo.getY();
					lastX = odo.getX();
					
					// example at 31 -> 31%30.48 = 0.52; 0.52 < small_precision && 0.52 > 0; TRUE -> set odo in Y to 31 -0.52
					if ( lastY%tile < small_precision && lastY%tile > 0 ){
						odo.setY(lastY - lastY%tile);
					}
					// example at 30 -> 30%30.48 = 30; 30 > big_precision && 30 < 30.48 ; TRUE -> set odo in Y to 30 + (30.48-30)
					else if (lastY%tile > big_precision && lastY%tile <tile){
						odo.setY(lastY + (tile-lastY));
					}
					// example at 31 -> 31%30.48 = 0.52; 0.52 < small_precision && 0.52 > 0; TRUE -> set odo in X to 31 -0.52
					else if (lastX%tile < small_precision && lastX%tile > 0 ){
						odo.setX(lastX - lastX%tile);
					}
					// example at 30 -> 30%30.48 = 30; 30 > big_precision && 30 < 30.48 ; TRUE -> set odo in X to 30 + (30.48-30)
					else if (lastX%tile > big_precision && lastX%tile <tile){
						odo.setX(lastX + (tile-lastX));
					}
				}
			}
			counter_corner = 0;
			
			
		}
	}
}
