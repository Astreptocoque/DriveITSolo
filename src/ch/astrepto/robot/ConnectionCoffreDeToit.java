package ch.astrepto.robot;

import java.io.IOException;

import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.remote.ev3.RemoteRequestEV3;

public class ConnectionCoffreDeToit {

	/// permet la connection à la seconde brique
	public static String[] names = { "EV1", "EV2" };
	public static RemoteRequestEV3[] brique = new RemoteRequestEV3[names.length];

	public static void deconnectionEV2() throws IOException {
		brique[1].disConnect();
	}

	public static void connectionEV2() {
		/// connection à la brique EV2
		LCD.drawString("Connection a EV2...", 0, 0);

		try {
			brique[1] = new RemoteRequestEV3(BrickFinder.find(names[1])[0].getIPAddress());
		} catch (Exception e) {
			LCD.clear();
			Sound.beepSequenceUp();
			LCD.drawString("La connection", 1, 0);
			LCD.drawString("a echouee", 3, 1);
			LCD.drawString("!!!!!!!!", 5, 4);
			LCD.drawString("pressez un bouton", 0, 6);
			System.out.println(e);
			Button.waitForAnyPress();
			System.exit(0);
		}

		LCD.clear();
		LCD.drawString("Connecte a EV2", 0, 0);
	}

}
