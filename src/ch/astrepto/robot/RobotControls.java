package ch.astrepto.robot;

import ch.astrepto.robot.capteurs.ColorSensor;
import ch.astrepto.robot.capteurs.ColorSensor2;
import ch.astrepto.robot.moteurs.DirectionMotor;
import ch.astrepto.robot.moteurs.TractionMotor;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Delay;

public class RobotControls {

	private DirectionMotor directionMotor;
	private TractionMotor tractionMotor;
	private ColorSensor colorGauche;
	private ColorSensor2 colorDroite;
	private static float intensity = 0;
	private static float previousSpeed = 0;
	private static float previousTachoCount = 0;
	private static float previousDistance = 0;
	public static int mode;

	public RobotControls() {

		colorGauche = new ColorSensor();
		colorDroite = new ColorSensor2();
		directionMotor = new DirectionMotor();

		previousSpeed = TractionMotor.currentSpeed;
		System.out.println("Placer le robot sur le cercle interne apres la priorit� de droite");
		Button.waitForAnyPress();
		Track.updateTrackInfos();
		tractionMotor = new TractionMotor();

	}

	/**
	 * Gestion du carrefour Une fois le carrefour d�tect�, cette section r�agit en fonction du
	 * c�t� du croisement
	 */
	public void crossroads() {
		// n'est pas mis � la m�me condition juste en dessous pour acc�l�rer le
		// freinage (sinon lent � cause de goTo)
		if (Track.trackPart == -1)
			// arr�te le robot
			tractionMotor.move(false);

		// indique qu'on est en train de passer le croisement
		Track.inCrossroads = true;
		tractionMotor.resetTacho();
		// les roues se remettent droites

		
		  // si on est au croisement � priorit� 
		if (Track.trackPart == 1) {
			directionMotor.goTo(10); 
		 }else { 
			 directionMotor.goTo(0); 
		}
		 
		tractionMotor.move(true);

	}

	/**
	 * Gestion de la d�tection de la fin du carrefour D�tecte la fin du carrefour et maj les
	 * indications de piste
	 */
	public void crossroadsEnd() {
		// on attends de l'avoir pass� pour red�marrer les fonctions de direction
		if (tractionMotor.getTachoCount() >= Track.crossroadsLength / TractionMotor.cmInDegres) {

			float intensityGauche = colorGauche.getIntensity();
			float intensityDroite = colorDroite.getIntensity();

			if (intensityGauche - intensityDroite > 5) {
				Track.inCrossroads = false;
				Track.crossroads = false;
				Track.justAfterCrossroads = true;
				if (Track.trackPart == 1 && Track.trackSide == -1
						|| Track.trackPart == -1 && Track.trackSide == 1) {
					Track.changeTrackSide();
				}
				Track.changeTrackPart();
				tractionMotor.resetTacho();

			} else if (intensityDroite - intensityGauche > 5) {
				Track.inCrossroads = false;
				Track.crossroads = false;
				Track.justAfterCrossroads = true;
				if (Track.trackPart == 1 && Track.trackSide == 1
						|| Track.trackPart == -1 && Track.trackSide == -1) {
					Track.changeTrackSide();
				}
				Track.changeTrackPart();
				tractionMotor.resetTacho();
			}
		}
	}

	/**
	 * Gestion de la direction automatique une fois que la pr�c�dente direction est termin�e, la
	 * nouvelle est d�termin�e en fonction de l'intensit� lumineuse d�tect�e
	 */
	public void updateDirection() {

		// Maj la direction si "le pr�c�dent mvt est fini"
		if (directionMotor.previousMoveComplete()) {
			// l'angle est d�termin� par la situation du robot sur la piste
			int angle = directionMotor.determineAngle(intensity);

			directionMotor.goTo(angle);
		}

	}

	/**
	 * Gestion de la vitesse automatique la vitesse est d�termin�e en fonction de la distance en
	 * cm mesur�e
	 */
	public void updateSpeed() {
		// d�finition de la vitesse
		tractionMotor.setSpeed(TractionMotor.maxSpeed);
	}

	/**
	 * Gestion de la d�tection de l'intensit� lumineuse au sol Rel�ve l'intensit� lumineuse et
	 * d�tecte le croisement
	 */
	public void updateLightIntensity() {
		// Rel�ve la valeur lumineuse actuelle
		intensity = colorDroite.getIntensity();

		// D�tection du carrefour (+3 pour les variations lumineuses)
		if (intensity <= ColorSensor.trackCrossingValue + 1)
			// Indique qu'on est arriv� au carrefour
			Track.crossroads = true;
	}

	/**
	 * Arr�te le robot � la fin
	 */
	public void robotStop() {
		// arret du robot
		tractionMotor.move(false);
		// remet les roues droites
		Delay.msDelay(500);
		directionMotor.goTo(0);
	}

	public void robotStart() {
		tractionMotor.move(true);
	}
}
