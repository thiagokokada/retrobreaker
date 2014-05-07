package br.usp.ime.ep2;

public class Constants {
	
	public static final int MS_PER_FRAME = 50;
	public static final int NANOS_PER_MS = 1000000;
	
	public static final class Collision {
		
		public static final int WALL_RIGHT_LEFT_SIDE = 1;
		public static final int WALL_TOP_BOTTOM_SIDE = 2;

	}
	
	public static final class Colors {
		
		public static final float[] RAINBOW = {
				0.0f,  0.0f,  0.0f,  1.0f,
				1.0f,  0.0f,  0.0f,  1.0f,
				0.0f,  0.0f,  1.0f,  1.0f,
				0.0f,  1.0f,  0.0f,  1.0f,
		};
		
	}

}
