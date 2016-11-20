package primary;
import lejos.hardware.Sound;
/*
 * added gotozonefordemo object in initialization class
 * added gotozonefordemo_test method in initialization class
 * 
 * 
*/
import lejos.robotics.RegulatedMotor;

public class GoToZoneForDemo {
	private RegulatedMotor leftMotor, rightMotor;
	private Odometer odo;
	private double WHEELRADIUS, TRACKSIZE;
	private int  ROTATIONSPEED, FORWARDSPEED, ACCELERATION;
	private USSensor sideSensor, frontSensor, heightSensor;
	private double FinalX, FinalY;
	private Navigation nav;
	private boolean flag = true;
	private double Begheading;
	
	private final double DEG_MIN_ERR = 3, DEG_MAX_ERR = 357, CM_ERR = 0.3, DEG_ERR = 10;
	private static final double BANDCENTER = 15, BANDWIDTH = 3, STOP = 0, STOP_ERROR = 3;

	public GoToZoneForDemo(RegulatedMotor leftMotor, RegulatedMotor rightMotor, Navigation nav,Odometer odo, int FORWARDSPEED, int ROTATIONSPEED
			, int ACCELERATION, double WHEELRADIUS, double TRACKSIZE,USSensor sideSensor, USSensor frontSensor){
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.odo = odo;
		this.ROTATIONSPEED = 80;
		this.FORWARDSPEED = 150;
		this.ACCELERATION = ACCELERATION;
		this.WHEELRADIUS = WHEELRADIUS;
		this.TRACKSIZE = TRACKSIZE;
		this.sideSensor = sideSensor;
		this.frontSensor = frontSensor;
		this.nav = nav;
	}

	public void goToZone(double FinalX, double FinalY){
		double minAng;
		while (Math.abs(FinalX - odo.getX()) > CM_ERR || Math.abs(FinalY - odo.getY()) > CM_ERR) {			
			
			minAng = (Math.atan2(FinalY - odo.getY(), FinalX - odo.getX()))*(180.0/Math.PI);
			if (minAng < 0)
				minAng += 360.0;
			if(minAng > DEG_ERR){
				this.turnTo(minAng, false);
			}			
			this.setSpeeds(FORWARDSPEED, FORWARDSPEED);		
			
			if(frontSensor.getValue()<30){
				nav.turnTo(odo.getAngle()+90,true);
			if(flag){
				Begheading = odo.getAngle();
			flag = false;	
			}
				//perform bangbang
				while(odo.getAngle() > ((Begheading+180)%360+STOP_ERROR) || odo.getAngle() < ((Begheading+180)%360-STOP_ERROR)){
					bangbang();
			}
			Sound.beep();
		}
		nav.setSpeeds(0, 0);
		
		
		}
	}



	private void turnTo(double angle, boolean stop){
		double error = angle - this.odo.getAngle();

		while (Math.abs(error) > DEG_MIN_ERR && Math.abs(error) < DEG_MAX_ERR) {

			error = angle - this.odo.getAngle();
			Logger.log(Double.toString(odo.getAngle()));
			if (error < -180.0) {
				this.setSpeeds(-ROTATIONSPEED, ROTATIONSPEED);
			} else if (error < 0.0) {
				this.setSpeeds(ROTATIONSPEED, -ROTATIONSPEED);
			} else if (error > 180.0) {
				this.setSpeeds(ROTATIONSPEED, -ROTATIONSPEED);
			} else {
				this.setSpeeds(-ROTATIONSPEED, ROTATIONSPEED);
			}
		}


		if (stop) {
			this.setSpeeds(0, 0);
		}
	}


	private void setSpeeds(int lSpd, int rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}


	public void bangbang(){
		double distance = sideSensor.getValue();
		if(distance > 250){
			distance = 250;
		}
		double distError = BANDCENTER - distance;//15- us.getvalue()

		
		if (Math.abs(distError) <= BANDWIDTH) { // Within limits, same speed
			nav.setSpeeds(FORWARDSPEED, FORWARDSPEED);
		}
	
		else if (distError >0) { // Medium close to the wall, move away faster	
			nav.setSpeeds(ROTATIONSPEED, FORWARDSPEED);
		}else{ //Far from wall, move closer
			nav.setSpeeds(FORWARDSPEED, ROTATIONSPEED);

		}
	}
}
