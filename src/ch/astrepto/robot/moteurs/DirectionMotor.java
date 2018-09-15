package ch.astrepto.robot.moteurs;

import ch.astrepto.robot.RobotAttributs;
import ch.astrepto.robot.Track;
import ch.astrepto.robot.capteurs.TouchSensorEV3;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;

public class DirectionMotor extends Moteur {

	public final static int maxDegreeRoue = 120; // de droit � un bord
	public final static double maxDegreCourbureDegres = RobotAttributs.degresRoueToDegresCourbure(maxDegreeRoue);

	private TouchSensorEV3 directionTouchSensor;

	public DirectionMotor(MoteursTypes type, Port port) {
		super(type, port);
		this.maxSpeed = 200;
		this.motor.setSpeed(this.maxSpeed);
		this.directionTouchSensor = new TouchSensorEV3(SensorPort.S2);

		initPosition();
	}

	private void initPosition() {

		motor.backward();
		// cadrage du moteur, o� qu'il soit
		boolean boucle = true;
		int sens = -1;
		boolean firstIteration = true;

		while (boucle) {
			int touch = (int) directionTouchSensor.getValue();
			// si le capteur est press�
			if (touch > 0 && firstIteration) {
				motor.rotate(80);
				motor.backward();
			}

			else if (touch > 0) {
				// si la roue vient de la gauche ou de la droite
				motor.rotate(positioningAdjustment(sens));
				boucle = false;

			} else if (motor.isStalled()) {
				motor.forward();
				sens = 1;
			}
			firstIteration = false;
		}
		motor.resetTachoCount();
	}

	/**
	 * Gestion de la direction des roues avants
	 * 
	 * @param angleP
	 *                angle auquel on veut se rendre, en degres de roue
	 */
	@Override
	public void goTo(double angleCourbure) {
		// arr�te le moteur s'il est en train de bouger
		if (motor.isMoving())
			motor.stop();
		System.out.println(angleCourbure);
		
		double angle =  -RobotAttributs.degresCourbureToDegresRoue(angleCourbure);
		double currentAngle = super.getCurrentDegres();

		// transformation de l'angle final en nombre de � que doit faire le robot
		double angleToDo;

		// si l'angle est sup�rieure au maximum � gauche
		if (angle < -maxDegreeRoue) {
			angleToDo = -maxDegreeRoue - currentAngle;
			destinationDegres = -maxDegreeRoue;
			// si l'angle est sup�rieur au max � droite
		} else if (angle > maxDegreeRoue) {
			angleToDo = maxDegreeRoue - currentAngle;
			destinationDegres = maxDegreeRoue;
			// sinon
		} else {
			angleToDo = angle - currentAngle;
			destinationDegres = (int) angle;
		}

		motor.rotate((int) (angleToDo), true);

	}

	public double angleFunctionOfIntensity(float intensity) {

		double angleCourbure;
		double angleForMaxLum;
		double angleForMinLum;
		int angleCourbureContreDirection = 0;
		
		//en fonction du cot� de la piste
		if(Track.getPart() == 1 && Track.getSide() == 1) {
			angleForMaxLum = -angleCourbureContreDirection;
			angleForMinLum = maxDegreCourbureDegres;
		}else if (Track.getPart() == 1 && Track.getSide() == -1) {
			angleForMaxLum = maxDegreCourbureDegres;
			angleForMinLum = -angleCourbureContreDirection;
		}else if (Track.getPart() == -1 && Track.getSide() == 1) {
			angleForMaxLum = angleCourbureContreDirection;
			angleForMinLum =  -maxDegreCourbureDegres;
		}else {
			angleForMaxLum = -maxDegreCourbureDegres;
			angleForMinLum =  angleCourbureContreDirection;
		}
		
		double a = (angleForMinLum - angleForMaxLum) / (Track.minValue - Track.maxValue);
		double b = angleForMaxLum - (angleForMinLum - angleForMaxLum) / (Track.minValue - Track.maxValue)
				* Track.maxValue;

		angleCourbure = a * intensity + b;
		return angleCourbure;
	}

	/**
	 * comme le capteur tactile n'est pas press� exactement au centre, mais un peu avant et de
	 * mani�re d�cal�e si on vient de par la gauche ou par la droite, il faut ajouter un petit
	 * nbr de rotation pour etre bien au centre. Varie si on vient de la gauche ou la droite.
	 * 
	 * @param sens
	 *                1 ou -1, � gauche ou � droite du centre droit des roues
	 * @return
	 */
	private int positioningAdjustment(int sens) {
		int angle;
		if (sens == 1) { // gauche
			angle = 50;
		} else {
			angle = -23; // droite
		}
		return angle;
	}
	
	public void close() {
		directionTouchSensor.close();
	}
	
	public void waitComplete() {
		motor.waitComplete();
	}
}
