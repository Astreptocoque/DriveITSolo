package ch.astrepto.robot.capteurs;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import ch.astrepto.robot.ConnectionCoffreDeToit;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.hardware.sensor.SensorModes;
import lejos.remote.ev3.RMISampleProvider;
import lejos.remote.ev3.RemoteEV3;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;

public class ColorSensor2 {
	
	private EV3ColorSensor color;
	private SampleProvider colorSensor;
	private float[] sampleColorSensor;
	
	public static final float trackMaxValue = 60-15; // blanc
	public static final float trackMinValue = 4; // bleu foncé
	public static final float trackCrossingValue = 2; // ligne noire
	
	public ColorSensor2(){
		
		//ConnectionCoffreDeToit.brique[1].createSampleProvider("s1", "EV3ColorSensor", "red");
		color = new EV3ColorSensor(ConnectionCoffreDeToit.brique[1].getPort("S1"));
		colorSensor = color.getRedMode();
		sampleColorSensor = new float[colorSensor.sampleSize()];
	}
	
	/**
	 * 
	 * @return la valeur du capteur, entre 0 et 100
	 * @throws RemoteException 
	 */
	public float getIntensity() {
		colorSensor.fetchSample(sampleColorSensor, 0);
		// on change l'échelle de 0 à 1 en 0 à 100
		float intensity = sampleColorSensor[0] * 100;
		
		// ajustement de la valeur et détection év. du croisement
		if (intensity > trackMaxValue)
			intensity = trackMaxValue;
		
		return intensity;
	}
}
