package ch.astrepto.robot.moteurs;

import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.robotics.RegulatedMotor;

public  class Moteur {

	protected RegulatedMotor motor;
	protected int maxSpeed = 200;
	protected float destinationDegres;
	protected float currentDegres = 0;
	
	public Moteur(MoteursTypes type, Port port) {
		
		switch(type) {
			case EV3Motor:
				motor = new EV3LargeRegulatedMotor(port);
				break;
			case EV3MediumMotor:
				motor = new EV3MediumRegulatedMotor(port);
				break;
			case NXTMotor:
				motor = new NXTRegulatedMotor(port);
				break;
		}
	}
	
	/**
	 * 
	 * @param angle	en degres
	 */
	void goTo(int angle) {
		motor.rotate(angle);
	}
	
	public float getCurrentDegres() {
		return this.motor.getTachoCount();
	}
	
	public float getCurrentRadian() {
		return (float) (Math.PI*this.motor.getTachoCount()/180);
	}
	
	public boolean isPreviousMoveComplete() {
		return ! this.motor.isMoving();
	}
}
