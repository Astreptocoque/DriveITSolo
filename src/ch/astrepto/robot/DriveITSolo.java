package ch.astrepto.robot;

import lejos.hardware.Button;

public class DriveITSolo {
	
	public static void main(String[] args) {

		
		// à faire avant de déposer le robot sur la piste
		Button.LEDPattern(6);
		RobotECB rob = new RobotECB();
		System.out.println("Placer le robot sur le cercle externe apres la priorité de droite");
		System.out.println("Appuyer pour demarrer");
		Button.LEDPattern(1);
		rob.touch.waitForPressAndRelease();	
		Button.LEDPattern(0);
		rob.robotStart();
		double speed = 0;

		do {
			
			if (!Track.inCrossroads) {
				float intensity = rob.updateLightIntensity();
				// Détection du carrefour
				if ( intensity<= Track.blackLineValue + 1)
					Track.crossroads = true;
			}

			if (!Track.inCrossroads && !Track.crossroads)
				rob.updateDirection();
			
			if(!Track.crossroads || Track.inCrossroads)
				speed = rob.updateSpeed();

			// entrée dans le croisement
			if (Track.crossroads && !Track.inCrossroads) 
				crossroads(rob);

			// sortie du croisement
			if (Track.inCrossroads) 
				crossroadsEnd(rob);
			
			System.out.println(speed);
			System.out.println("Lol");
			
		} while (!rob.touch.isPressed());
		
	
		end(rob);
	}

	
	/**
	 * Gestion du carrefour Une fois le carrefour détecté, cette section réagit en fonction du
	 * côté du croisement
	 */
	public static void crossroads(RobotECB rob) {

		// indique qu'on est en train de passer le croisement
		rob.tractionMotor.move(false);
		rob.tractionMotor.resetTachoCount();
		Track.inCrossroads = true;

		// les roues se remettent droites
		// correction pour le croisement
		if ((Track.getPart() == 1 && Track.getSide() == -1) || (Track.getPart() == 1 && Track.getSide() == 1)) //|| (Track.getPart() == -1 && Track.getSide() == 1) 
			rob.directionMotor.goTo(-5); 
		 else if((Track.getPart() == -1 && Track.getSide() == -1) )
			 rob. directionMotor.goTo(0); 
		else 
			rob.directionMotor.goTo(0);
		rob.directionMotor.waitComplete();
		rob.tractionMotor.move(true);
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
			
				if((diff > 5 && ((Track.getPart() == 1 && Track.getSide() == -1)
					|| (Track.getPart() == -1 && Track.getSide() == 1)))
					|| (diff < 5 && ((Track.getPart() == 1 && Track.getSide() == 1)
						|| (Track.getPart() == -1 && Track.getSide() == -1)))) {
					Track.changeSide();
				}
			
				Track.changePart();
				rob.tractionMotor.resetTachoCount();
			}
			
			if(Track.getSide() == 1) {
				rob.tractionMotor.setMaxSpeed(RobotAttributs.maxSpeedBigSide);
			}else {
				rob.tractionMotor.setMaxSpeed(RobotAttributs.maxSpeedLittleSide);
			}
		}
	}

	private static void end(RobotECB rob) {
		rob.colorDroite.close();
		rob.colorGauche.close();
		rob.directionMotor.close();
		rob.touch.close();
		rob.directionTouchSensor.close();
	}
	
}