package br.usp.ime.ep2;

public class Constants {
	
	public static final int MS_PER_FRAME = 50;
	public static final int NANOS_PER_MS = 1000000;
	
	public static final class Collision {
		public static final int WALL_RIGHT_LEFT_SIDE = 1;
		public static final int WALL_TOP_BOTTOM_SIDE = 2;
	}
	
	public static final class Forms {

		public static final float[] PADDLE = {
				-1.0f, -0.2f, // bottom left
				-1.0f,  0.2f, // top left
				1.0f, -0.2f, // bottom right
				1.0f,  0.2f, // top right
		};
		
		public static final float[] BALL = {
			-0.25f, -0.25f, // bottom left
			-0.25f,  0.25f, // top left
			0.25f, -0.25f, // bottom right
			0.25f,  0.25f, // top right
	};

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
