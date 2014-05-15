package br.usp.ime.ep2;

public class Constants {

	public static final int RIGHT_ANGLE = 90;

	public static final long MS_PER_SECONDS = 1000 /* milliseconds */;
	public static final long NANOS_PER_SECONDS = 1000 /* nanoseconds */ * MS_PER_SECONDS;

	//The game runs at a maximum velocity of 60FPS
	public static final int MAX_FPS = 60;
	public static final long MAX_MS_PER_FRAME = (long) Math.ceil(1.0/MAX_FPS * MS_PER_SECONDS);

	public static enum Collision {
		NOT_AVAILABLE, WALL_RIGHT_LEFT_SIDE, WALL_TOP_BOTTOM_SIDE, PADDLE_BALL_FROM_RIGHT, PADDLE_BALL_FROM_LEFT, PADDLE_BRICK, LIFE_LOST
	}

	public static enum BallDirection {
		RIGHT_UPWARD, LEFT_UPWARD, RIGHT_DOWNWARD, LEFT_DOWNWARD, UNKNOWN_DIRECTION
	}

	public static enum ScoreMultiplier {
		RESTART_LEVEL, PADDLE_HIT, BRICK_HIT
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

		public static final float[] RAINBOW = {
			0.0f,  0.0f,  0.0f,  1.0f,
			1.0f,  0.0f,  0.0f,  1.0f,
			0.0f,  0.0f,  1.0f,  1.0f,
			0.0f,  1.0f,  0.0f,  1.0f,
		};

	}

}
