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
		System.out.println("Placer le robot sur le cercle interne apres la priorité de droite");
		Button.waitForAnyPress();
		Track.updateTrackInfos();
		tractionMotor = new TractionMotor();

	}

	/**
	 * Gestion du carrefour Une fois le carrefour détecté, cette section réagit en fonction du
	 * côté du croisement
	 */
	public void crossroads() {
		// n'est pas mis à la même condition juste en dessous pour accélérer le
		// freinage (sinon lent à cause de goTo)
		if (Track.trackPart == -1)
			// arrête le robot
			tractionMotor.move(false);

		// indique qu'on est en train de passer le croisement
		Track.inCrossroads = true;
		tractionMotor.resetTacho();
		// les roues se remettent droites

		
		  // si on est au croisement à priorité 
		if (Track.trackPart == 1) {
			directionMotor.goTo(10); 
		 }else { 
			 directionMotor.goTo(0); 
		}
		 
		tractionMotor.move(true);

	}

	/**
	 * Gestion de la détection de la fin du carrefour Détecte la fin du carrefour et maj les
	 * indications de piste
	 */
	public void crossroadsEnd() {
		// on attends de l'avoir passé pour redémarrer les fonctions de direction
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
	 * Gestion de la direction automatique une fois que la précédente direction est terminée, la
	 * nouvelle est déterminée en fonction de l'intensité lumineuse détectée
	 */
	public void updateDirection() {

		// Maj la direction si "le précédent mvt est fini"
		if (directionMotor.previousMoveComplete()) {
			// l'angle est déterminé par la situation du robot sur la piste
			int angle = directionMotor.determineAngle(intensity);

			directionMotor.goTo(angle);
		}

	}

	/**
	 * Gestion de la vitesse automatique la vitesse est déterminée en fonction de la distance en
	 * cm mesurée
	 */
	public void updateSpeed() {
		// définition de la vitesse
		tractionMotor.setSpeed(TractionMotor.maxSpeed);
	}

	/**
	 * Gestion de la détection de l'intensité lumineuse au sol Relève l'intensité lumineuse et
	 * détecte le croisement
	 */
	public void updateLightIntensity() {
		// Relève la valeur lumineuse actuelle
		intensity = colorDroite.getIntensity();

		// Détection du carrefour (+3 pour les variations lumineuses)
		if (intensity <= ColorSensor.trackCrossingValue + 1)
			// Indique qu'on est arrivé au carrefour
			Track.crossroads = true;
	}

	/**
	 * Arrête le robot à la fin
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
