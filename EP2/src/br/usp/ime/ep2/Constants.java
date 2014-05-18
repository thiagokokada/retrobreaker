package br.usp.ime.ep2;

public class Constants {

	public static final int RIGHT_ANGLE = 90;
	public static final float ANGLE_OF_REFLECTION_BOUND = 60;

	public static final long MS_PER_SECONDS = 1000 /* milliseconds */;
	public static final long NANOS_PER_SECONDS = 1000 /* nanoseconds */ * MS_PER_SECONDS;

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

	public static enum Lives {
		RESTART_LEVEL, LOST_LIFE;
	}

	public static enum Hit {
		RIGHT_LEFT, TOP_BOTTOM
	}

	public static final class Config {
		public static final long MS_PER_UPDATE = 15 /* milliseconds */;
		public static final int FPS_LIMIT = 0; // Set to 0 to disable it
		public static final float WALL = 0.05f;
		public static final float BALL_INITIAL_POS_X = 0.01f;
		public static final float BALL_INITIAL_POS_Y = 0.05f;
		public static final float PADDLE_INITIAL_POS_Y = -0.7f;
		public static final float BRICKS_INITIAL_POS_Y = 0.4f;
		public static final int NUMBER_OF_LINES_OF_BRICKS = 8;
		public static final int NUMBER_OF_COLUMNS_OF_BRICKS = 11;
		public static final float SPACE_BETWEEN_BRICKS = 0.01f;
		public static final float SCREEN_RATIO = 9.0f/16.0f; // Widescreen (16:9) on portrait
	}
	
	public static final class Scales {
		public static final float BRICK = 0.1f;
		public static final float PADDLE = 0.1f;
		public static final float BALL = 0.1f;
		public static final float PARTICLE = 0.03f;
	}
	
	public static final class Colors {
		static final float RGB_UPPER_BOUND = 255;
		static final float[] GRAY_RGB = {128/RGB_UPPER_BOUND, 128/RGB_UPPER_BOUND, 128/RGB_UPPER_BOUND};
		static final float[] WHITE_RGB = {255/RGB_UPPER_BOUND, 255/RGB_UPPER_BOUND, 255/RGB_UPPER_BOUND};
		static final float[] BLACK_RGB = {0/RGB_UPPER_BOUND, 0/RGB_UPPER_BOUND, 0/RGB_UPPER_BOUND};
		static final float[] RED_RGB = {255/RGB_UPPER_BOUND, 0/RGB_UPPER_BOUND, 0/RGB_UPPER_BOUND};
		static final float[] BLUE_RGB = {0/RGB_UPPER_BOUND, 0/RGB_UPPER_BOUND, 255/RGB_UPPER_BOUND};
		static final float[] GREEN_RGB = {0/RGB_UPPER_BOUND, 255/RGB_UPPER_BOUND, 0/RGB_UPPER_BOUND};

		public static final float[] WHITE = {
			WHITE_RGB[0],  WHITE_RGB[1],  WHITE_RGB[2],  1.0f,	// bottom left
			WHITE_RGB[0],  WHITE_RGB[1],  WHITE_RGB[2],  1.0f,	// top left
			WHITE_RGB[0],  WHITE_RGB[1],  WHITE_RGB[2],  1.0f,	// bottom right
			WHITE_RGB[0],  WHITE_RGB[1],  WHITE_RGB[2],  1.0f,	// top right
		};
		
		public static final float[] GRAY = {
			GRAY_RGB[0],  GRAY_RGB[1],  GRAY_RGB[2],  1.0f,
			GRAY_RGB[0],  GRAY_RGB[1],  GRAY_RGB[2],  1.0f,
			GRAY_RGB[0],  GRAY_RGB[1],  GRAY_RGB[2],  1.0f,
			GRAY_RGB[0],  GRAY_RGB[1],  GRAY_RGB[2],  1.0f,
		};
		
		public static final float[] RED = {
			RED_RGB[0],  RED_RGB[1],  RED_RGB[2],  1.0f,
			RED_RGB[0],  RED_RGB[1],  RED_RGB[2],  1.0f,
			RED_RGB[0],  RED_RGB[1],  RED_RGB[2],  1.0f,
			RED_RGB[0],  RED_RGB[1],  RED_RGB[2],  1.0f,
		};
		
		public static final float[] GREEN = {
			GREEN_RGB[0],  GREEN_RGB[1],  GREEN_RGB[2],  1.0f,
			GREEN_RGB[0],  GREEN_RGB[1],  GREEN_RGB[2],  1.0f,
			GREEN_RGB[0],  GREEN_RGB[1],  GREEN_RGB[2],  1.0f,
			GREEN_RGB[0],  GREEN_RGB[1],  GREEN_RGB[2],  1.0f,
		};

	}

}
