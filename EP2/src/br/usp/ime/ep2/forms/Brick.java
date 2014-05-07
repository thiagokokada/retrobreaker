package br.usp.ime.ep2.forms;

public class Brick extends Quad {
	
	public static final float[] VERTICES = {
		-0.5f, -0.2f, // bottom left
		-0.5f,  0.2f, // top left
		0.5f, -0.2f, // bottom right
		0.5f,  0.2f, // top right
	};

	public Brick(float[] colors, float pos_x, float pos_y,
			float scale) {
		super(VERTICES, colors, pos_x, pos_y, scale);
		// TODO Auto-generated constructor stub
	}

}
