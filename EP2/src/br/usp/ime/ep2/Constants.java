package br.usp.ime.ep2;

public class Constants {

	public static final int RIGHT_ANGLE = 90;

	public static final long MS_PER_SECONDS = 1000 /* milliseconds */;
	public static final long NANOS_PER_SECONDS = 1000 /* nanoseconds */ * MS_PER_SECONDS;

	//The game runs at a maximum velocity of 60FPS
	public static final int MAX_FPS = 60;
	public static final long MAX_MS_PER_FRAME = (long) Math.ceil(1.0/MAX_FPS * MS_PER_SECONDS);

	public static enum Collision {
		NOT_AVAILABLE, WALL_RIGHT_LEFT_SIDE, WALL_TOP_BOTTOM_SIDE, PADDLE_BALL, BRICK_BALL, EX_BRICK_BALL, LIFE_LOST
	}

	public static enum BallDirection {
		RIGHT_UPWARD, LEFT_UPWARD, RIGHT_DOWNWARD, LEFT_DOWNWARD, UNKNOWN_DIRECTION
	}
	
	public static enum Score {
		RESTART_LEVEL, BRICK_HIT, EX_BRICK_HIT
	}

	public static enum ScoreMultiplier {
		RESTART_LEVEL, LOST_LIFE, PADDLE_HIT, BRICK_HIT
	}

	public static enum Lifes {
		RESTART_LEVEL, LOST_LIFE;
	}

	public static enum Hit {
		RIGHT_LEFT, TOP_BOTTOM
	}

	public static final class Config {
		public static final int LIFE_COUNT = 3;
		public static final int HIT_SCORE = 100;
		public static final int MAX_SCORE_MULTIPLIER = 8;
		public static final boolean INVICIBILITY = false;
	}
	
	public static final class Colors {
		static final float RGB_UPPER_BOUND = 255;
		static final float[] GRAY_RGB = {128/RGB_UPPER_BOUND, 128/RGB_UPPER_BOUND, 128/RGB_UPPER_BOUND};
		static final float[] WHITE_RGB = {255/RGB_UPPER_BOUND, 255/RGB_UPPER_BOUND, 255/RGB_UPPER_BOUND};
		static final float[] BLACK_RGB = {0/RGB_UPPER_BOUND, 0/RGB_UPPER_BOUND, 0/RGB_UPPER_BOUND};
		static final float[] RED_RGB = {255/RGB_UPPER_BOUND, 0/RGB_UPPER_BOUND, 0/RGB_UPPER_BOUND};
		static final float[] BLUE_RGB = {0/RGB_UPPER_BOUND, 0/RGB_UPPER_BOUND, 255/RGB_UPPER_BOUND};
		static final float[] GREEN_RGB = {0/RGB_UPPER_BOUND, 255/RGB_UPPER_BOUND, 0/RGB_UPPER_BOUND};

		public static final float[] RAINBOW = {
			BLACK_RGB[0],  BLACK_RGB[1],  BLACK_RGB[2],  1.0f,	// bottom left
			RED_RGB[0],  RED_RGB[1],  RED_RGB[2],  1.0f,		// top left
			BLUE_RGB[0],  BLUE_RGB[1],  BLUE_RGB[2],  1.0f,		// bottom right
			GREEN_RGB[0],  GREEN_RGB[1],  GREEN_RGB[2],  1.0f,	// top right
		};
		
		public static final float[] GRAY_GRADIENT = {
			GRAY_RGB[0],  GRAY_RGB[1],  GRAY_RGB[2],  1.0f,		// bottom left
			WHITE_RGB[0],  WHITE_RGB[1],  WHITE_RGB[2],  1.0f,	// top left
			BLACK_RGB[0],  BLACK_RGB[1],  BLACK_RGB[2],  1.0f,	// bottom right
			GRAY_RGB[0],  GRAY_RGB[1],  GRAY_RGB[2],  1.0f,		// top right
		};
		
		public static final float[] RED_GRADIENT = {
			RED_RGB[0],  RED_RGB[1],  RED_RGB[2],  1.0f,		// bottom left
			WHITE_RGB[0],  WHITE_RGB[1],  WHITE_RGB[2],  1.0f,	// top left
			BLACK_RGB[0],  BLACK_RGB[1],  BLACK_RGB[2],  1.0f,	// bottom right
			RED_RGB[0],  RED_RGB[1],  RED_RGB[2],  1.0f,		// top right
		};

	}

}
