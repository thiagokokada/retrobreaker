package br.usp.ime.retrobreaker.forms;

public class Paddle extends Quad {

	private static final float SCALE = 0.1f;
	private static final float[] VERTICES = {
		-1.0f, -0.2f, // bottom left
		-1.0f,  0.2f, // top left
		1.0f, -0.2f, // bottom right
		1.0f,  0.2f, // top right
	};

	public Paddle(float[] colors, float posX, float posY) {
		super(VERTICES, SCALE, colors, posX , posY);
	}
	
}
