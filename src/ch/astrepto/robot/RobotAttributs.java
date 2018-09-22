package ch.astrepto.robot;

public abstract class RobotAttributs {
	public final static float cmInDegres = 0.037699112f; // pas touche (en fct des roues)
	public final static float wheelSpacing = 9.5f;
	public final static float baseLength = 13f;
	public final static double diametreEngrenage = 1.25f;
	public final static double essieu = 1.7f;
	public final static int maxSpeedBigSide = 550;
	public final static int maxSpeedLittleSide = 400;
	public final static int maxSpeedDirection = 220;
	
	public static int degresCourbureToDegresRoue(double angle) {
		
		angle = 	Math.sin(Math.toRadians((angle)))*360*essieu/(diametreEngrenage*Math.PI);

		
		return (int) angle;
	}
	
	public static double degresRoueToDegresCourbure(double angle) {
		
		angle = Math.toDegrees((Math.asin(((diametreEngrenage * Math.toRadians(angle)) / ( essieu * 2)))));
		
		return angle;
	}
}
