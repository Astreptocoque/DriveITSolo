package ch.astrepto.robot;

import ch.astrepto.robot.capteurs.ColorSensor;
import ch.astrepto.robot.moteurs.TractionMotor;

public class Track {

	// VARIABLES POUR LA SITUATION SUR LA PISTE
	public static int trackSide; // 1 si grand, -1 si petit
	public static int trackPart; // 1 côté avec priorité de droite, -1 côté prioritaire
	public final static float smallRadius = 15;
	public final static float largeRadius = 55;
	public final static float gradientWidth = 12;

	// VARIABLES POUR LE CARREFOUR
	public static boolean crossroads = false; // si arrivé au carrrefour
	public static boolean inCrossroads = false; // si en train de passer le carrefour
	// var permettant d'atténuer l'angle détecté juste après le carrefour et au démarrage
	public static boolean justAfterCrossroads = true;

	public final static float crossroadsLength = 50; // en cm
	// longueur minimal d'un côté de la piste
	public final static float trackPartLength = (float) (((smallRadius + gradientWidth) * 2 * Math.PI)/TractionMotor.cmInDegres);
	// le bout droit + le 1/4 du petit virage de la piste + une marge de 10, en degrés
	public final static float overtakingLength = (float) ((crossroadsLength
			+ ((smallRadius + gradientWidth / 2) * 2 * Math.PI) / 4) + 10) / TractionMotor.cmInDegres;

	/**
	 * Change le côté de la piste
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

	/**
	 * Gestion des informations de la piste Appelée en début de programme, cette méthode permet
	 * au robot de se situer sur la piste. Le robot doit être placé sur le bleu extérieur si sur
	 * la partie 1 de la piste, sur le blanc si sur la partie -1 de la piste
	 * 
	 * @param intensity
	 *                l'intensité lumineuse mesurée
	 */
	public static void updateTrackInfos() {
		// valeur 0 = partieHuit, valeur 1 = cotePiste
		Track.trackPart = 1;

		// on commence toujours sur le grand côté
		Track.trackSide = -1;
	}

}
