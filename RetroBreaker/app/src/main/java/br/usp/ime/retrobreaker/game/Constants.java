package br.usp.ime.retrobreaker.game;

public class Constants {

	public static final int RIGHT_ANGLE = 90;
	public static final float ANGLE_OF_REFLECTION_BOUND = 60;

	public static final long MS_PER_SECONDS = 1000 /* milliseconds */;
	public static final long NANOS_PER_SECONDS = 1000 /* nanoseconds */ * MS_PER_SECONDS;

	public enum Collision {
		NOT_AVAILABLE, WALL_RIGHT_LEFT_SIDE, WALL_TOP_BOTTOM_SIDE, PADDLE_BALL, BRICK_BALL, EX_BRICK_BALL, LIFE_LOST
	}

	public enum BallDirection {
		RIGHT_UPWARD, LEFT_UPWARD, RIGHT_DOWNWARD, LEFT_DOWNWARD, UPWARD, DOWNWARD, UNKNOWN_DIRECTION
	}
	
	public enum Score {
		RESTART_LEVEL, BRICK_HIT, EX_BRICK_HIT
	}

	public enum ScoreMultiplier {
		RESTART_LEVEL, LOST_LIFE, PADDLE_HIT, BRICK_HIT
	}

	public enum Lives {
		RESTART_LEVEL, LOST_LIFE
	}

	public enum Hit {
		RIGHT_LEFT, TOP_BOTTOM
	}

	public static final class Config {
		public static final int MS_PER_UPDATE = 15 /* milliseconds */;
		public static final int FPS_LIMIT = 0; // Set to 0 to disable it
		public static final int FRAME_SKIP = 5;
		public static final int NUMBER_OF_LINES_OF_BRICKS = 8;
		public static final int NUMBER_OF_COLUMNS_OF_BRICKS = 10;
		public static final int MOBILE_BRICK_SKIP_FRAMES = 3;
		public static final float SCREEN_RATIO = 9.0f/16.0f; // Widescreen (16:9) on portrait
		public static final float WALL = 0.0f;
		public static final float BALL_INITIAL_PREVIOUS_POS_X = 0.25f;
		public static final float BALL_INITIAL_PREVIOUS_POS_Y = 0.4f;
		public static final float BALL_INITIAL_POS_X = 0.2f;
		public static final float BALL_INITIAL_POS_Y = 0.2f;
		public static final float PADDLE_INITIAL_POS_X = 0.0f;
		public static final float PADDLE_INITIAL_POS_Y = -0.7f;
		public static final float BRICKS_INITIAL_POS_X = -0.495f;
		public static final float BRICKS_INITIAL_POS_Y = 0.3f;

		//PROVISIONAL BELOW
		public static final float PAUSE_BUTTON_POS_X = .35f;
		public static final float PAUSE_BUTTON_POS_Y = .55f;
		//PROVISIONAL ABOVE

		public static final float SPACE_BETWEEN_BRICKS = 0.01f;
		public static final Collision[] CONSECUTIVE_COLLISION_DETECTION = {
			Collision.WALL_RIGHT_LEFT_SIDE, Collision.WALL_TOP_BOTTOM_SIDE, Collision.PADDLE_BALL
		};
	}
	
	public static final class Difficult {
		// 0 = Can't die, 1 = Easy, 2 = Normal, 3 = Hard
		public static final boolean[] INVINCIBILITY = { true, false, false, false };
		public static final int[] LIFE_STOCK = { 99, 3, 2, 1 };
		public static final int[] HIT_SCORE = { 0, 50, 100, 150 };
		public static final int[] LIFE_SCORE_BONUS = { 0, 2500, 5000, 15000 };
		public static final int[] MAX_SCORE_MULTIPLIER = { 1, 4, 8, 16 };
		public static final float[] BALL_SPEED = { 0.01f, 0.01f, 0.015f, 0.02f };
		public static final float[] GREY_BRICK_PROB = { 0.1f, 0.15f, 0.25f, 0.35f };
		public static final float[] EX_BRICK_PROB = { 0.1f, 0.15f, 0.1f, 0.05f };
		public static final float[] MOBILE_BRICK_PROB = { 0.1f, 0.0f, 0.05f, 0.1f };
		public static final float[] MOBILE_BRICK_SPEED = { 0.002f, 0.0f, 0.0035f, 0.005f };
	}
	
	public static final class Color {
		static final float RGB_UPPER_BOUND = 255;
		static final float[] GRAY_RGB = {128/RGB_UPPER_BOUND, 128/RGB_UPPER_BOUND, 128/RGB_UPPER_BOUND};
		static final float[] WHITE_RGB = {255/RGB_UPPER_BOUND, 255/RGB_UPPER_BOUND, 255/RGB_UPPER_BOUND};
		static final float[] RED_RGB = {255/RGB_UPPER_BOUND, 0/RGB_UPPER_BOUND, 0/RGB_UPPER_BOUND};
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
