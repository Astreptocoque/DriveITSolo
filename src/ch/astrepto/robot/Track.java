package ch.astrepto.robot;

import ch.astrepto.robot.capteurs.ColorSensor;
import ch.astrepto.robot.moteurs.TractionMotor;

public class Track {

	// VARIABLES POUR LA SITUATION SUR LA PISTE
	public static int trackSide = -1; // 1 si grand, -1 si petit
	public static int trackPart = 1; // 1 c�t� avec priorit� de droite, -1 c�t� prioritaire
	public final static float smallRadius = 15;
	public final static float largeRadius = 55;
	public final static float gradientWidth = 12;

	// VARIABLES POUR LE CARREFOUR
	public static boolean crossroads = false; // si arriv� au carrrefour
	public static boolean inCrossroads = false; // si en train de passer le carrefour
	// var permettant d'att�nuer l'angle d�tect� juste apr�s le carrefour et au d�marrage
	public static boolean justAfterCrossroads = true;

	public final static float crossroadsLength = 50; // en cm
	// longueur minimal d'un c�t� de la piste
	public final static float trackPartLength = (float) (((smallRadius + gradientWidth) * 2 * Math.PI)/RobotAttributs.cmInDegres);
	// le bout droit + le 1/4 du petit virage de la piste + une marge de 10, en degr�s
	public final static float overtakingLength = (float) ((crossroadsLength
			+ ((smallRadius + gradientWidth / 2) * 2 * Math.PI) / 4) + 10) / RobotAttributs.cmInDegres;

	public static final int trackMaxValue = 60; // blanc
	public static final int trackMinValue = 4; // bleu fonc�
	public static final int trackCrossingValue = 2; // ligne noire
	
	/**
	 * Change le c�t� de la piste
	 */
	public static void changeTrackSide() {
		trackSide *= -1;
	}

	/**
	 * Change la partie de la piste (du huit)
	 */
	public static void changeTrackPart() {
		trackPart *= -1;
	}
}
