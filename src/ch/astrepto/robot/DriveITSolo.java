package ch.astrepto.robot;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class DriveITSolo {

	public static void main(String[] args) {

		ConnectionCoffreDeToit.connectionEV2();
		// à faire avant de déposer le robot sur la piste
		RobotControls rob = new RobotControls();
		displayStart();
		rob.robotStart();

		boolean boucle = true;

		do {
			// GESTION DU RELEVE LUMINEUX DE LA PISTE
			// Est maj si pas "en train de passer le carrefour" et si pas
			// "initialisation d'un
			// dépassement"
			if (!Track.inCrossroads) {
				rob.updateLightIntensity();
			}

			// GESTION DE LA DIRECTION AUTOMATIQUE
			// Est maj si pas "en train de passer le crossroads", si pas "arrivé au
			// crossroads",
			// si pas "initialisation d'un dépassement" et si "en train de suivre la
			// piste"
			if (!Track.inCrossroads && !Track.crossroads) {
				// si verifiyFreeWay est vrai, l'ultrason ne tourne pas avec les
				// roues

				rob.updateDirection();

			}
			// GESTION DE LA VITESSE AUTOMATIQUE
			// Est maj si pas "intialisation d'un dépassement" et si pas "vérification
			// peut dépasser")
			
			rob.updateSpeed();

			// GESTION DE L'ARRIVEE AU CROISEMENT
			// Est maj si "arrivé au crossroads" mais pas "en train de passer le
			// crossroads"
			if (Track.crossroads && !Track.inCrossroads) {
				rob.crossroads();
			}

			// GESTION A L'INTERIEUR DU CROISEMENT
			// Est maj si "en train de passer le crossroads"
			if (Track.inCrossroads) {
				// on attends de l'avoir passé pour redémarrer les fonctions de
				// direction
				rob.crossroadsEnd();
			}

		} while (boucle);

		rob.robotStop();

	}

	private static void displayStart() {
		// écran blanc
		LCD.clear();
		for (int i = 0; i < 128; i++) {
			drawLine(1, 178, 0, i, 1);
		}

		drawArrow(-1, 20, 66, 61, 0);
		drawArrow(1, 20, 106, 61, 0);
		for (int i = 61; i < 101; i++) {
			drawLine(1, 40, 66, i, 0);
		}

		drawArrow(-1, 20, 68, 63, 1);
		drawArrow(1, 20, 108, 63, 1);
		for (int i = 63; i < 103; i++) {
			drawLine(1, 40, 69, i, 1);
		}

		drawArrow(-1, 20, 70, 65, 0);
		drawArrow(1, 20, 110, 65, 0);
		for (int i = 65; i < 105; i++) {
			drawLine(1, 40, 71, i, 0);
		}
		LCD.drawString("......START.......", 0, 1, true);

		LCD.drawString("ENTER", 7, 5, false);

		Button.ENTER.waitForPress();
		LCD.clear();
	}

	private static void drawArrow(int direction, int size, int x, int y, int color) {
		int xi = 0;
		int yj = 0;
		int i = 1;
		while (yj != size * 2) {

			xi = 0;
			do {
				if (direction == -1)
					LCD.setPixel(x - xi, y + yj, color);
				else
					LCD.setPixel(x + xi, y + yj, color);
				xi += 1;
			} while (xi != i);

			yj += 1;

			if (yj > size)
				i -= 1;
			else
				i += 1;
		}
	}

	private static void drawLine(int direction, int size, int x, int y, int color) {

		if (direction == 1) {
			int xi = 0;
			while (xi != size) {
				LCD.setPixel(x + xi, y, color);
				xi += 1;
			}
		} else {
			int yj = 0;

			while (yj != size) {
				LCD.setPixel(x, y + yj, color);
				yj += 1;
			}
		}
	}
}
