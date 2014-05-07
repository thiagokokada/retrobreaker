package br.usp.ime.ep2;

import br.usp.ime.ep2.Constants.Colors;
import br.usp.ime.ep2.Constants.Forms;

public class Utils {

	public static Quad[][] createLevel (int blocksX, int blocksY, float initialX, float initialY, float spaceX, float spaceY) {
		Quad[][] level = new Quad[blocksX][blocksY];
		
		float newPosX = initialX;
		float newPosY = initialY;
		
		for (int i=0; i<level.length; i++) {
			for (int j=0; j<level[i].length; j++) {
				level[i][j] = new Quad(Forms.BLOCK, Colors.RAINBOW, newPosX, newPosY, 0.1f);
				newPosX += spaceX;
			}
			newPosX = initialX;
			newPosY -= spaceY;
		}

		return level;
	}
	
}
