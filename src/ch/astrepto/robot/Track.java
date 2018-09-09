package ch.astrepto.robot;

public abstract class Track {

	// VARIABLES POUR LA SITUATION SUR LA PISTE
	private static int side = -1; // 1 si grand, -1 si petit
	private static int part = 1; // 1 c�t� avec priorit� de droite, -1 c�t� prioritaire
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

	public static final int maxValue = 60; // blanc
	public static final int minValue = 4; // bleu fonc�
	public static final int blackLineValue = 2; // ligne noire
	
	public static void changeSide() {
		side *= -1;
	}

	public static void changePart() {
		part *= -1;
	}
	
	public static int getSide() {
		return side;
	}
	
	public static int getPart() {
		return part;
	}
}
