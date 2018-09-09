package br.usp.ime.retrobreaker.forms;

public class Brick extends Quad {

	public static final int BRICK_EXPLOSION_SIZE = 8;
	
	public enum Type {
		NORMAL, EXPLOSIVE, HARD, MOBILE
	}

	private static final float SCALE = 0.1f;
	private static final float[] VERTICES = {
		-0.5f, -0.2f, // bottom left
		-0.5f,  0.2f, // top left
		0.5f, -0.2f, // bottom right
		0.5f,  0.2f, // top right
	};
	
	private int mLives;
	private final Type mType;

	public Brick(float[] colors, float posX, float posY, Type type) {
		super(VERTICES, SCALE, colors, posX, posY);
		mType = type;
		switch (type) {
		case NORMAL:
			mLives = 0;
			break;
		case EXPLOSIVE:
			mLives = 0;
			break;
		case HARD:
			mLives = 1;
			break;
		case MOBILE:
			mLives = 0;
			break;
		}
				
	}
	
	public void decrementLives() {
		mLives--;
	}
	
	public int getLives() {
		return mLives;
	}
	
	public Type getType() {
		return mType;
	}

}
