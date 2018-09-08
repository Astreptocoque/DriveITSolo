package ch.astrepto.robot.moteurs;

import ch.astrepto.robot.Track;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;

public class TractionMotor {

	private EV3LargeRegulatedMotor motorLeft, rightMotor;
	private EV3LargeRegulatedMotor[] synchro;

	private static boolean isMoving = true;
	public final static float maxSpeed = 600f;
	public final static float cmInDegres = 0.037699112f; // pas touche (en fct des roues)
	public final static float wheelSpacing = 9.5f;
	// LIMITES DE DETECTION D'UN VEHICULE
	private final static float lastLimit = 15f; // en dessous, le robot stop
	private final static float secondLimit =25f; // jusqu'ici, le robot garde 75% de sa vitesse
	public final static float firstLimit = 40f; // pass� cette limite, le robot est � plein
							// r�gime
	private final static float speedAtSecondLimit = 3f / 4f; // % de vitesse � la 2�me limite
	public static float currentSpeed;

	public TractionMotor() {
		motorLeft = new EV3LargeRegulatedMotor(MotorPort.C);
		rightMotor = new EV3LargeRegulatedMotor(MotorPort.B);

		synchro = new EV3LargeRegulatedMotor[1];
		synchro[0] = rightMotor;
		motorLeft.synchronizeWith(synchro);
		motorLeft.setAcceleration(2000);
		rightMotor.setAcceleration(2000);

		setSpeed(maxSpeed);
	}

	/**
	 * R�gle la vitesse et l'ajuste pour chaque roue de traction en fonction du virage Chaque
	 * partie de la piste a ses r�glages. Si le robot va tout droit, quelques soit les r�glages,
	 * la vitesse de chaque moteur sera �gale
	 * 
	 * @param vitesseActuelle
	 */
	public void setSpeed(float vitesseActuelle) {

		float speedLeftMotor = 0;
		float speedRightMotor = 0;
		// on determine la nouvelle valeur de degr� � tourner au robot
		// en fonction de l'endroit sur la piste et du nombre de degr� que tourne le robot
		if (Track.trackSide == 1 && Track.trackPart == 1) {
			speedRightMotor = vitesseActuelle;
			// la vitesse en fonction du rayon du centre de la piste
			speedLeftMotor = (Track.largeRadius - wheelSpacing) * vitesseActuelle / Track.largeRadius;
			// puis en fonction du degr� de rotation
			speedLeftMotor = vitesseActuelle - ((vitesseActuelle - speedLeftMotor)
					/ DirectionMotor.maxDegree * DirectionMotor.getCurrentAngle());
		} else if (Track.trackSide == -1 && Track.trackPart == -1) {
			speedLeftMotor = Track.smallRadius * vitesseActuelle / (Track.smallRadius + wheelSpacing);
			speedLeftMotor = vitesseActuelle - ((vitesseActuelle - speedLeftMotor)
					/ DirectionMotor.maxDegree * DirectionMotor.getCurrentAngle());
			speedRightMotor = vitesseActuelle;
		} else if (Track.trackSide == 1 && Track.trackPart == -1) {
			speedLeftMotor = (Track.largeRadius - wheelSpacing) * vitesseActuelle / Track.largeRadius;
			speedLeftMotor = vitesseActuelle - ((vitesseActuelle - speedLeftMotor)
					/ DirectionMotor.maxDegree * DirectionMotor.getCurrentAngle());
			speedRightMotor = vitesseActuelle;
		} else if (Track.trackSide == -1 && Track.trackPart == 1) {
			speedRightMotor = vitesseActuelle;
			speedLeftMotor = Track.smallRadius * vitesseActuelle / (Track.smallRadius + wheelSpacing);
			speedLeftMotor = vitesseActuelle - ((vitesseActuelle - speedLeftMotor)
					/ DirectionMotor.maxDegree * DirectionMotor.getCurrentAngle());

		}

		// set la vitesse
		rightMotor.setSpeed(speedRightMotor);
		motorLeft.setSpeed(speedLeftMotor);

		// met � jour la vitesse actuelle
		currentSpeed = vitesseActuelle;

		if (vitesseActuelle == 0)
			isMoving = false;

		if (vitesseActuelle > 0 && isMoving == false) {
			// on indique que le robot est en marche
			isMoving = true;
			// demarre le robot
			move(true);
		}
	}

	/**
	 * D�termine la vitesse en fonction de la distance mesur�e
	 * 
	 * @param distance
	 *                La distance mesur�e
	 * @return la vitesse
	 */
	public float determineSpeed(float distance) {
		float speed;

		if (distance > firstLimit) {
			speed = maxSpeed;
		} else if (distance <= firstLimit && distance > secondLimit) {
			speed = (speedAtSecondLimit * maxSpeed) + (maxSpeed - (speedAtSecondLimit * maxSpeed))
					/ (firstLimit - secondLimit) * (distance - secondLimit);
		} else if (distance <= secondLimit && distance > lastLimit) {
			speed = (speedAtSecondLimit * maxSpeed) / (secondLimit - lastLimit) * (distance - lastLimit);
		} else {
			speed = 0;
		}

		return speed;
	}

	/**
	 * Gestion du mouvement du v�hicule (en marche et � l'arret)
	 * 
	 * @param move
	 *                true pour d�marrer, false pour arr�ter
	 */
	public void move(boolean move) {
		motorLeft.startSynchronization();

		if (move) {
			motorLeft.backward();
			rightMotor.backward();
		} else {
			motorLeft.stop();
			rightMotor.stop();
		}

		motorLeft.endSynchronization();
	}

	/**
	 * R�initialise le tachometre de la traction (roues gauche et droite)
	 */
	public void resetTacho() {
		motorLeft.resetTachoCount();
		rightMotor.resetTachoCount();
	}

	/**
	 * Mesure la distance parcourue par la traction. La mesure est une moyenne des deux roues
	 * 
	 * @return le nbr de degr�s de la traction
	 */
	public int getTachoCount() {
		return (motorLeft.getTachoCount() + rightMotor.getTachoCount()) / 2 * -1;
	}
}
