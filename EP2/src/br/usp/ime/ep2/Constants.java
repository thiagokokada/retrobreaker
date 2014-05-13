package br.usp.ime.ep2;

public class Constants {

	public static final long MS_PER_SECONDS = 1000 /* milliseconds */;
	public static final long NANOS_PER_SECONDS = 1000 /* nanoseconds */ * MS_PER_SECONDS;

	//The game runs at a maximum velocity of 60FPS
	public static final int MAX_FPS = 60;
	//The game lowest speed is 10FPS
	public static final int MIN_FPS = 5;
	public static final long MAX_MS_PER_FRAME = (long) Math.ceil(1.0/MAX_FPS * MS_PER_SECONDS);
	public static final long MIN_MS_PER_FRAME = (long) Math.ceil(1.0/MIN_FPS * MS_PER_SECONDS);
	
	public static enum Collision {
		NOT_AVAILABLE, WALL_RIGHT_LEFT_SIDE, WALL_TOP_BOTTOM_SIDE
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
