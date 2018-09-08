package ch.astrepto.robot.moteurs;

import ch.astrepto.robot.Track;
import ch.astrepto.robot.capteurs.ColorSensor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;

public class DirectionMotor {

	private EV3MediumRegulatedMotor directionMotor;

	private SampleProvider directionTouchSensor;
	private float[] sampleDirectionTouchSensor;

	private final static int maxSpeed = 400;
	public final static int maxDegree = 120; // de droit � un bord
	public final static int maxAngle = 40; // 132 degr�s = 43 degr� du cercle
	public final static double wheelBase = 13d; // 13 cm, l'empattement (espace entre roues
							// arri�res et avant)
	private static int currentAngleDestination;

	public DirectionMotor() {
		directionMotor = new EV3MediumRegulatedMotor(MotorPort.A);
		directionTouchSensor = new EV3TouchSensor(SensorPort.S2).getTouchMode();
		sampleDirectionTouchSensor = new float[directionTouchSensor.sampleSize()];
		directionMotor.setSpeed(maxSpeed);

		// cadrage du moteur, o� qu'il soit
		directionMotor.backward();
		boolean boucle = true;
		int sens = -1;
		boolean firstIteration = true;

		while (boucle) {
			directionTouchSensor.fetchSample(sampleDirectionTouchSensor, 0);

			// si le capteur est press�
			if (sampleDirectionTouchSensor[0] == 1 && firstIteration) {
				directionMotor.rotate(80);
				directionMotor.backward();
			}

			else if (sampleDirectionTouchSensor[0] == 1) {
				// si la roue vient de la gauche ou de la droite
				directionMotor.rotate(positioningAdjustment(sens));
				boucle = false;

			} else if (directionMotor.isStalled()) {
				directionMotor.forward();
				sens = 1;
			}
			firstIteration = false;
		}
		directionMotor.resetTachoCount();
	}

	/**
	 * Gestion de la direction des roues avants
	 * @param angleP
	 *                angle auquel on veut se rendre
	 */
	public void goTo(int angleP) {
		// arr�te le moteur s'il est en train de bouger
		if (directionMotor.isMoving())
			directionMotor.stop();

		int currentAngle = directionMotor.getTachoCount();

		// transformation de l'angle final en nombre de � que doit faire le robot
		int angle;

		// si l'angle est sup�rieure au maximum � gauche
		if (angleP < -maxDegree) {
			angle = -maxDegree - currentAngle;
			currentAngleDestination = -maxDegree;
			// si l'angle est sup�rieur au max � droite
		} else if (angleP > maxDegree) {
			angle = maxDegree - currentAngle;
			currentAngleDestination = maxDegree;
			// sinon
		} else {
			angle = angleP - currentAngle;
			currentAngleDestination = angleP;
		}

		directionMotor.rotate((int)(angle/1f), true);

	}

	/**
	 * Determine l'angle en fonction de la luminosit� mesur�e et de la position sur la piste
	 * 
	 * @param trackSide
	 *                1 ou -1, grand c�t� ou petit c�t�
	 * @param trackPart
	 *                1 ou -1, gauche ou droite
	 * @param intensity
	 *                intensit� de la piste pr�c�demment mesur�e
	 * @return le nbr de degr� de rotation
	 */
	public int determineAngle(float intensity) {
		int angle = 0;
		// valeur d'angle dans le sens oppos� (a pour effet de d�placer le z�ro des roues
		// sur le d�grad�
		int negativeAngle = 40;

		// on determine la nouvelle valeur de degr� � tourner au robot
		if (Track.trackSide == 1 && Track.trackPart == 1) {
			angle = (int) ((((maxDegree + negativeAngle) - (maxDegree + negativeAngle)
					/ (ColorSensor.trackMaxValue - ColorSensor.trackMinValue)
					* (intensity - ColorSensor.trackMinValue)) - negativeAngle) * -1);
		} else if (Track.trackSide == -1 && Track.trackPart == -1) {
			angle = (int) (((maxDegree + negativeAngle)
					/ (ColorSensor.trackMaxValue - ColorSensor.trackMinValue)
					* (intensity - ColorSensor.trackMinValue)) - negativeAngle);
		} else if (Track.trackSide == 1 && Track.trackPart == -1) {
			angle = (int) ((((maxDegree + negativeAngle) - (maxDegree + negativeAngle)
					/ (ColorSensor.trackMaxValue - ColorSensor.trackMinValue)
					* (intensity - ColorSensor.trackMinValue)) - negativeAngle));
		} else if (Track.trackSide == -1 && Track.trackPart == 1) {
			angle = (int) ((((maxDegree + negativeAngle)
					/ (ColorSensor.trackMaxValue - ColorSensor.trackMinValue)
					* (intensity - ColorSensor.trackMinValue)) - negativeAngle) * -1);
		}
		return angle;
	}

	/**
	 *  
	 * @return vrai si le moteur a fini son pr�c�dent mouvement
	 */
	public boolean previousMoveComplete() {
		return !directionMotor.isMoving();
	}

	/**
	 * ATTENTION : ne renvoi pas l'angle actuel du robot mais l'angle final.
	 * @return le degr� du robot
	 */
	public static int getCurrentAngle() {
		return currentAngleDestination;
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
		if (sens == 1) {  // gauche
			angle = 50;
		} else {
			angle = -23; // droite
		}
		return angle;
	}
}
