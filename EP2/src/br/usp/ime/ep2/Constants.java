package br.usp.ime.ep2;

public class Constants {

	public static final long MS_PER_SECONDS = 1000 /* milliseconds */;
	public static final long NANOS_PER_SECONDS = 1000 /* nanoseconds */ * MS_PER_SECONDS;

	//Thanks to VSync
	public static final double ANDROID_FPS_LIMIT = 60.0f;
	//~60FPS, double arithmetics is too unreliable so we round it.
	public static final double FPS_LIMIT = Math.floor((1.0/60.0) * MS_PER_SECONDS);
	
	public static final class Colors {
		
		public static final float[] RAINBOW = {
				0.0f,  0.0f,  0.0f,  1.0f,
				1.0f,  0.0f,  0.0f,  1.0f,
				0.0f,  0.0f,  1.0f,  1.0f,
				0.0f,  1.0f,  0.0f,  1.0f,
		};
		
	}

}
