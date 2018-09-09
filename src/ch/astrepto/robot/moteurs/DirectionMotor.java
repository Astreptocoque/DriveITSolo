package ch.astrepto.robot.moteurs;

import ch.astrepto.robot.Track;
import ch.astrepto.robot.capteurs.ColorSensor;
import ch.astrepto.robot.capteurs.TouchSensorEV3;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class DirectionMotor extends Moteur{
	
	public final static int maxDegree = 120; // de droit à un bord
	public final static int maxAngle = 40; // 132 degrés = 43 degré du cercle

	TouchSensorEV3 directionTouchSensor ;
	
	public DirectionMotor(MoteursTypes type, Port port) {
		super(type, port);
		this.maxSpeed = 200;
		this.motor.setSpeed(this.maxSpeed);
		this.directionTouchSensor = new TouchSensorEV3(SensorPort.S2);
	
		initPosition();
		
	}

	private void initPosition() {
		
		motor.backward();
		// cadrage du moteur, où qu'il soit
		boolean boucle = true;
		int sens = -1;
		boolean firstIteration = true;

		while (boucle) {
			int touch = (int) directionTouchSensor.getValue();
			// si le capteur est pressé
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
	 * @param angleP
	 *                angle auquel on veut se rendre, en degres de roue
	 */
	@Override
	public void goTo(int angle) {
		// arrête le moteur s'il est en train de bouger
		if (motor.isMoving())
			motor.stop();

		float currentAngle = super.getCurrentDegres();

		// transformation de l'angle final en nombre de ° que doit faire le robot
		float angleToDo;

		// si l'angle est supérieure au maximum à gauche
		if (angle < -maxDegree) {
			angleToDo = -maxDegree - currentAngle;
			destinationDegres = -maxDegree;
			// si l'angle est supérieur au max à droite
		} else if (angle > maxDegree) {
			angleToDo = maxDegree - currentAngle;
			destinationDegres = maxDegree;
			// sinon
		} else {
			angleToDo = angle - currentAngle;
			destinationDegres = angle;
		}

		motor.rotate((int)(angleToDo/1f), true);

	}

	/**
	 * Determine l'angle en fonction de la luminosité mesurée et de la position sur la piste
	 * 
	 * @param trackSide
	 *                1 ou -1, grand côté ou petit côté
	 * @param trackPart
	 *                1 ou -1, gauche ou droite
	 * @param intensity
	 *                intensité de la piste précédemment mesurée
	 * @return le nbr de degré de rotation
	 */
	public int angleFunctionOfIntensity(float intensity) {
		int angle = 0;
		// valeur d'angle dans le sens opposé (a pour effet de déplacer le zéro des roues
		// sur le dégradé
		int negativeAngle = 40;

		// on determine la nouvelle valeur de degré à tourner au robot
		if (Track.trackSide == 1 && Track.trackPart == 1) {
			angle = (int) ((((maxDegree + negativeAngle) - (maxDegree + negativeAngle)
					/ (Track.trackMaxValue - Track.trackMinValue)
					* (intensity - Track.trackMinValue)) - negativeAngle) * -1);
		} else if (Track.trackSide == -1 && Track.trackPart == -1) {
			angle = (int) (((maxDegree + negativeAngle)
					/ (Track.trackMaxValue - Track.trackMinValue)
					* (intensity - Track.trackMinValue)) - negativeAngle);
		} else if (Track.trackSide == 1 && Track.trackPart == -1) {
			angle = (int) ((((maxDegree + negativeAngle) - (maxDegree + negativeAngle)
					/ (Track.trackMaxValue - Track.trackMinValue)
					* (intensity - Track.trackMinValue)) - negativeAngle));
		} else if (Track.trackSide == -1 && Track.trackPart == 1) {
			angle = (int) ((((maxDegree + negativeAngle)
					/ (Track.trackMaxValue - Track.trackMinValue)
					* (intensity - Track.trackMinValue)) - negativeAngle) * -1);
		}
		return angle;
	}


	/**
	 * comme le capteur tactile n'est pas pressé exactement au centre, mais un peu avant et de
	 * manière décalée si on vient de par la gauche ou par la droite, il faut ajouter un petit
	 * nbr de rotation pour etre bien au centre. Varie si on vient de la gauche ou la droite.
	 * 
	 * @param sens
	 *                1 ou -1, à gauche ou à droite du centre droit des roues
	 * @return
	 */
	private int positioningAdjustment(int sens) {
		int angle;
		if (sens == 1) {  // gauche
			angle = 50;
		} else {
			angle = -23; // droite
		}
		return angle;
	}
}
