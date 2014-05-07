package br.usp.ime.ep2;

import br.usp.ime.ep2.Constants.Colors;

public class Utils {

	public static Brick[][] createLevel (int blocksX, int blocksY, float initialX, float initialY, float spaceX, float spaceY) {
		Brick[][] level = new Brick[blocksX][blocksY];
		
		float newPosX = initialX;
		float newPosY = initialY;
		
		for (int i=0; i<level.length; i++) {
			for (int j=0; j<level[i].length; j++) {
				level[i][j] = new Brick(Colors.RAINBOW, newPosX, newPosY, 0.1f);
				newPosX += spaceX;
			}
			newPosX = initialX;
			newPosY -= spaceY;
		}

		return level;
	}
	
}
