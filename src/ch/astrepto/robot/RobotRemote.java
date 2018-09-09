package ch.astrepto.robot;

import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.remote.ev3.RemoteRequestEV3;

public class RobotRemote {

	/// permet la connection � la seconde brique
	public  String name = "EV2";
	public  RemoteRequestEV3 brique;

	
	public RobotRemote() {
		/// connection � la brique EV2
		System.out.println("Connection a EV2...");

		try {
			brique = new RemoteRequestEV3(BrickFinder.find(name)[0].getIPAddress());
		} catch (Exception e) {
			Sound.beepSequenceUp();
			System.out.println("La connection a echouee ! Pressez un bouton");
			System.out.println(e);
			Button.waitForAnyPress();
			System.exit(0);
		}

		System.out.println("Connecte a EV2");
	}
	
	public void disConnect() {
		brique.disConnect();
	}
}