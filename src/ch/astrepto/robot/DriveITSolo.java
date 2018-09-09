package ch.astrepto.robot;

import lejos.hardware.Button;

public class DriveITSolo {
	
	public static void main(String[] args) {

		
		// à faire avant de déposer le robot sur la piste
		RobotECB rob = new RobotECB();
		System.out.println("Placer le robot sur le cercle interne apres la priorité de droite");
		Button.waitForAnyPress();		
		System.out.flush();
		Dessin.displayStart();
		rob.robotStart();

		do {
			
			if (!Track.inCrossroads) {
				float intensity = rob.updateLightIntensity();
				// Détection du carrefour
				if ( intensity<= Track.trackCrossingValue + 1)
					Track.crossroads = true;
			}

			if (!Track.inCrossroads && !Track.crossroads)
				rob.updateDirection();
			
			rob.updateSpeed();

			// entrée dans le croisement
			if (Track.crossroads && !Track.inCrossroads) 
				crossroads(rob);

			// sortie du croisement
			if (Track.inCrossroads) 
				crossroadsEnd(rob);


		} while (!Button.ESCAPE.isDown());
		
		end(rob);
	}

	
	/**
	 * Gestion du carrefour Une fois le carrefour détecté, cette section réagit en fonction du
	 * côté du croisement
	 */
	public static void crossroads(RobotECB rob) {

		// indique qu'on est en train de passer le croisement
		Track.inCrossroads = true;
		rob.tractionMotor.resetTachoCount();
		
		// les roues se remettent droites
		// correction pour le croisement
		if (Track.trackPart == 1 && Track.trackSide == -1) 
			rob.directionMotor.goTo(10); 
		 else if(Track.trackPart == -1 && Track.trackSide == -1)
			 rob. directionMotor.goTo(-10); 
		else 
			rob.directionMotor.goTo(0);
	}

	/**
	 * Gestion de la détection de la fin du carrefour Détecte la fin du carrefour et maj les
	 * indications de piste
	 */
	public static void crossroadsEnd(RobotECB rob) {
		// on attends de l'avoir passé pour redémarrer les fonctions de direction
		if (rob.tractionMotor.getCurrentDegres() >= Track.crossroadsLength / RobotAttributs.cmInDegres) {

			int intensityGauche = (int) rob.colorGauche.getValue();
			int intensityDroite = (int) rob.colorDroite.getValue();

			int diff = intensityGauche - intensityDroite;
			
			if(Math.abs(diff) > 5) {
				Track.inCrossroads = false;
				Track.crossroads = false;
				Track.justAfterCrossroads = true;
			
				if((diff > 5 && ((Track.trackPart == 1 && Track.trackSide == -1)
					|| (Track.trackPart == -1 && Track.trackSide == 1)))
					|| (diff < 5 && ((Track.trackPart == 1 && Track.trackSide == 1)
						|| (Track.trackPart == -1 && Track.trackSide == -1)))) {
					Track.changeTrackSide();
				}
			
				Track.changeTrackPart();
				rob.tractionMotor.resetTachoCount();
			}
		}
	}

	private static void end(RobotECB rob) {
		rob.colorDroite.close();
		rob.colorGauche.close();
		rob.coffre.disConnect();
	}
	
}