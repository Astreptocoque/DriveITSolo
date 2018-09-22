package ch.astrepto.robot;

import ch.astrepto.robot.capteurs.ColorSensor;
import ch.astrepto.robot.capteurs.ColorSensorRemote;
import ch.astrepto.robot.capteurs.TouchSensorEV3Remote;
import ch.astrepto.robot.capteurs.TouchSensorNXT;
import ch.astrepto.robot.moteurs.DirectionMotor;
import ch.astrepto.robot.moteurs.MoteursTypes;
import ch.astrepto.robot.moteurs.TractionMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;

public class RobotECB{

	public DirectionMotor directionMotor;
	public TractionMotor tractionMotor;
	public ColorSensor colorGauche;
	public ColorSensorRemote colorDroite;
	public RobotRemote coffre;
	public TouchSensorEV3Remote directionTouchSensor;
	public TouchSensorNXT touch;
	
	// valeurs mesurées
	private float intensity = 0;

	public RobotECB() {

		coffre = new RobotRemote();
		colorGauche = new ColorSensor(SensorPort.S4);
		colorDroite = new ColorSensorRemote(coffre, "S1");
		directionTouchSensor = new TouchSensorEV3Remote(coffre, "S4");
		touch = new TouchSensorNXT(SensorPort.S2);
		directionMotor = new DirectionMotor(MoteursTypes.EV3MediumMotor, MotorPort.A, directionTouchSensor);
		tractionMotor = new TractionMotor(MoteursTypes.EV3Motor, MotorPort.C, MotorPort.B);
	}

	public void updateDirection() {
		// Maj la direction si "le précédent mvt est fini"
		if (directionMotor.isPreviousMoveComplete())
			directionMotor.goTo(directionMotor.angleFunctionOfIntensity(intensity));
	}

	public double updateSpeed() {
		// définition de la vitesse
		return tractionMotor.setSpeed(RobotAttributs.degresRoueToDegresCourbure(directionMotor.getCurrentDegres()));
	}

	public float updateLightIntensity() {
		intensity = colorDroite.getValue();
		return intensity;
	}
	
	public void robotStart() {
		tractionMotor.move(true);
	}
}
