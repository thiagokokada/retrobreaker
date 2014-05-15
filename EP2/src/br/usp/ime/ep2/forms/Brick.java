package br.usp.ime.ep2.forms;

public class Brick extends Quad {
	
	public static final float GRAY_BRICK_PROBABILITY = 0.2f;
	public static final int GRAY_LIVES = 1;
	public static final int NORMAL_LIVES = 0;

	public static final float[] VERTICES = {
		-0.5f, -0.2f, // bottom left
		-0.5f,  0.2f, // top left
		0.5f, -0.2f, // bottom right
		0.5f,  0.2f, // top right
	};
	
	private int mLives;

	public Brick(float[] colors, float pos_x, float pos_y,
			float scale, int lives) {
		super(VERTICES, colors, pos_x, pos_y, scale);
		mLives = lives;
	}
	
	public void decrementLives() {
		mLives--;
	}
	
	public int getLives() {
		return mLives;
	}

}
