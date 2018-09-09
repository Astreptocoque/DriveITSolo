package ch.astrepto.robot.moteurs;

import ch.astrepto.robot.RobotAttributs;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.robotics.RegulatedMotor;

public class TractionMotor{

	private Moteur motorLeft;
	private Moteur motorRight;
	private RegulatedMotor[] synchro;
	private int speed;

	public TractionMotor(MoteursTypes type, Port portLeft, Port portRight) {
		motorLeft = new Moteur(type, portLeft);
		motorRight = new Moteur(type, portRight);	
		this.speed = 600;

		synchro = new EV3LargeRegulatedMotor[1];
		synchro[0] = motorRight.motor;
		motorLeft.motor.synchronizeWith(synchro);
		motorLeft.motor.setAcceleration(2000);
		motorRight.motor.setAcceleration(2000);

		motorLeft.motor.setSpeed(speed);
		motorRight.motor.setSpeed(speed);
	}

	/**
	 * Règle la vitesse et l'ajuste pour chaque roue de traction en fonction du virage Chaque
	 * partie de la piste a ses réglages. Si le robot va tout droit, quelques soit les réglages,
	 * la vitesse de chaque moteur sera égale
	 * 
	 */
	public void setSpeed(double angleCourbure) {
		
		int speedLeft;
		int speedRight;
		
		if(angleCourbure == 0) {
			speedLeft = speed;
			speedRight = speed;
		}else{
			double radius = RobotAttributs.baseLength/Math.tan(Math.toRadians(angleCourbure));
			double rotationSpeed = speed/radius;
			
			speedLeft = (int) (rotationSpeed*(radius- RobotAttributs.wheelSpacing/2));
			speedRight = (int) (rotationSpeed*(radius+ RobotAttributs.wheelSpacing/2));
		}
		
		// set la vitesse
		motorLeft.motor.setSpeed(speedLeft);
		motorRight.motor.setSpeed(speedRight);
	}


	/**
	 * Gestion du mouvement du véhicule (en marche et à l'arret)
	 * 
	 * @param move
	 *                true pour démarrer, false pour arrêter
	 */
	public void move(boolean move) {
		motorLeft.motor.startSynchronization();

		if (move) {
			motorLeft.motor.backward();
			motorRight.motor.backward();
		} else {
			motorLeft.motor.stop();
			motorRight.motor.stop();
		}

		motorLeft.motor.endSynchronization();
	}

	/**
	 * Réinitialise le tachometre de la traction (roues gauche et droite)
	 */
	public void resetTachoCount() {
		motorLeft.motor.resetTachoCount();
		motorRight.motor.resetTachoCount();
	}

	/**
	 * Mesure la distance parcourue par la traction. La mesure est une moyenne des deux roues
	 * 
	 * @return le nbr de degrés de la traction
	 */
	public float getCurrentDegres() {
		return (float) ((motorLeft.getCurrentDegres()+ motorRight.getCurrentDegres())/ 2 * -1);
	}
}
