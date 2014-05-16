package brickExplosion;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import br.usp.ime.ep2.Constants.Collision;
import br.usp.ime.ep2.Constants.Config;
import br.usp.ime.ep2.Game;
import br.usp.ime.ep2.forms.Quad;

/**
 * @author impaler
 *
 */
public class Particle extends Quad {
	
	public static final float scale = 0.03f;
	
	public static final float[] VERTICES = {
		-0.25f, -0.25f, // bottom left
		-0.25f,  0.25f, // top left
		0.25f, -0.25f, // bottom right
		0.25f,  0.25f, // top right
	};
	
	public static final int STATE_ALIVE = 0;	// particle is alive
	public static final int STATE_DEAD = 1;		// particle is dead
	
	public static final int DEFAULT_LIFETIME 	= 20;	// play with this
	public static final int MAX_DIMENSION		= 5;	// the maximum width or height
	public static final float MAX_SPEED			= ((VERTICES[3] - VERTICES[1])*scale)*5;	// maximum speed (per update)
//	public static final float MAX_SPEED			= 10;
	
	private int state;			// particle is alive or dead
	private float widht;		// width of the particle
	private float height;		// height of the particle
//	private float x, y;			// horizontal and vertical position
	private double xv, yv;		// vertical and horizontal velocity
	private int age;			// current age of the particle
	private int lifetime;		// particle dies when it reaches this value
//	private int color;			// the color of the particle
//	private Paint paint;		// internal use to avoid instantiation
	
	private float[] colors;
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public float getWidht() {
		return widht;
	}

	public void setWidht(float widht) {
		this.widht = widht;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

//	public float getX() {
//		return x;
//	}
//
//	public void setX(float x) {
//		mPosX = x;
//	}
//
//	public float getY() {
//		return y;
//	}
//
//	public void setY(float y) {
//		mPosY = y;
//	}

	public double getXv() {
		return xv;
	}

	public void setXv(double xv) {
		this.xv = xv;
	}

	public double getYv() {
		return yv;
	}

	public void setYv(double yv) {
		this.yv = yv;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getLifetime() {
		return lifetime;
	}

	public void setLifetime(int lifetime) {
		this.lifetime = lifetime;
	}

//	public int getColor() {
//		return color;
//	}
//
//	public void setColor(int color) {
//		this.color = color;
//	}
	
	// helper methods -------------------------
	public boolean isAlive() {
		return this.state == STATE_ALIVE;
	}
	public boolean isDead() {
		return this.state == STATE_DEAD;
	}

	public Particle(float[] colors, float pos_x, float pos_y, float scale) {
		super(VERTICES, colors, pos_x, pos_y, scale);
		mPosX = pos_x;
		mPosY = pos_y;
		this.state = Particle.STATE_ALIVE;
//		this.widht = rndInt(1, MAX_DIMENSION);
//		this.height = this.widht;
		this.lifetime = DEFAULT_LIFETIME;
		this.age = 0;
		this.xv = (rndDbl(0, MAX_SPEED * 2) - MAX_SPEED);
		this.yv = (rndDbl(0, MAX_SPEED * 2) - MAX_SPEED);
		// smoothing out the diagonal speed
		if (xv * xv + yv * yv > MAX_SPEED * MAX_SPEED) {
			xv *= 0.7;
			yv *= 0.7;
		}
//		this.color = Color.argb(255, rndInt(0, 255), rndInt(0, 255), rndInt(0, 255));
//		this.paint = new Paint(this.color);
		this.colors = colors;
	}
	
	/**
	 * Resets the particle
	 * @param x
	 * @param y
	 */
	public void reset(float x, float y) {
		this.state = Particle.STATE_ALIVE;
		mPosX = x;
		mPosY = y;
		this.age = 0;
	}

	// Return an integer that ranges from min inclusive to max inclusive.
	static int rndInt(int min, int max) {
		return (int) (min + Math.random() * (max - min + 1));
	}

	static double rndDbl(double min, double max) {
		return min + (max - min) * Math.random();
	}
	
	public void update() {
		if (this.state != STATE_DEAD) {
			mPosX += this.xv;
			mPosY += this.yv;
			
//			// extract alpha
//			int a = this.color >>> 24;
//			a -= 2;								// fade by 5
//			if (a <= 0) {						// if reached transparency kill the particle
//				this.state = STATE_DEAD;
//			} else {
//				this.color = (this.color & 0x00ffffff) + (a << 24);		// set the new alpha
//				this.paint.setAlpha(a);
//				this.age++;						// increase the age of the particle
//			}
			this.age++;						// increase the age of the particle
			if (this.age >= this.lifetime) {	// reached the end if its life
				this.state = STATE_DEAD;
			}
			
		}
	}
	
	public void update2(/*Rect container*/) {		
		// update with collision
		if (this.isAlive()) {
			if (mPosX <= Game.sScreenLowerX || mPosX >= Game.sScreenHigherX - getWidth()) {
				this.xv *= -1;
			}
			// Bottom is 480 and top is 0 !!!
			if (mPosY <= Game.sScreenHigherY || mPosY >= Game.sScreenLowerY - getHeight()) {
				this.yv *= -1;
			}
		}
		update();
	}

//	public void draw(Canvas canvas) {
//		paint.setColor(this.color);
//		canvas.drawRect(mPosX, mPosY, mPosX + this.widht, mPosY + this.height, paint);
//	}

}