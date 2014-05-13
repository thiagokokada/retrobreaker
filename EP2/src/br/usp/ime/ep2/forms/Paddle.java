package br.usp.ime.ep2.forms;

public class Paddle extends Quad {
	public static final float[] VERTICES = {
		-1.0f, -0.2f, // bottom left
		-1.0f,  0.2f, // top left
		1.0f, -0.2f, // bottom right
		1.0f,  0.2f, // top right
	};

	public Paddle(float[] colors, float pos_x, float pos_y,
			float scale) {
		super(VERTICES, colors, pos_x, pos_y, scale);
	}
	
}
