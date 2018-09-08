package ch.astrepto.robot;

import ch.astrepto.robot.capteurs.ColorSensor;
import ch.astrepto.robot.moteurs.TractionMotor;

public class Track {

	// VARIABLES POUR LA SITUATION SUR LA PISTE
	public static int trackSide; // 1 si grand, -1 si petit
	public static int trackPart; // 1 c�t� avec priorit� de droite, -1 c�t� prioritaire
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
	public final static float trackPartLength = (float) (((smallRadius + gradientWidth) * 2 * Math.PI)/TractionMotor.cmInDegres);
	// le bout droit + le 1/4 du petit virage de la piste + une marge de 10, en degr�s
	public final static float overtakingLength = (float) ((crossroadsLength
			+ ((smallRadius + gradientWidth / 2) * 2 * Math.PI) / 4) + 10) / TractionMotor.cmInDegres;

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

	/**
	 * Gestion des informations de la piste Appel�e en d�but de programme, cette m�thode permet
	 * au robot de se situer sur la piste. Le robot doit �tre plac� sur le bleu ext�rieur si sur
	 * la partie 1 de la piste, sur le blanc si sur la partie -1 de la piste
	 * 
	 * @param intensity
	 *                l'intensit� lumineuse mesur�e
	 */
	public static void updateTrackInfos() {
		// valeur 0 = partieHuit, valeur 1 = cotePiste
		Track.trackPart = 1;

		// on commence toujours sur le grand c�t�
		Track.trackSide = -1;
	}

}
