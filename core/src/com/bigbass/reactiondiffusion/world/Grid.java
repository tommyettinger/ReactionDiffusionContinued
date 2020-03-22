package com.bigbass.reactiondiffusion.world;

import com.badlogic.gdx.graphics.Pixmap;

public class Grid {
	
	public static final RandomTangle RAND = new RandomTangle();
	
	public Cell[][] cells;

	public Grid(int width, int height){
		cells = new Cell[width][height];
		for(int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				cells[i][j] = new Cell();
			}
		}
		if(width == height) {
			for (int i = 1; i < width - 1; i++) {
				for (int j = 1; j < height - 1; j++) {
					if ((RAND.nextLong() & 0x3FFL) == 0) {
						cells[i][j].b = cells[width - 1 - i][j].b = cells[i][height - 1 - j].b = cells[width - 1 - i][height - 1 - j].b = cells[j][i].b = cells[width - 1 - j][i].b = cells[j][height - 1 - i].b = cells[width - 1 - j][height - 1 - i].b = 1f;// - (RAND.nextLong() & 0xFFFFFFL) * 0x1p-26f;
					}
				}
			}
		}
//		else {
//			for(int i = 1; i < width - 1; i++) {
//				for(int j = 1; j < height - 1; j++) {
//					if((RAND.nextLong() & 0xFFL) == 0) {
//						cells[i][j].b = 1f;
//					}
//				}
//			}
//		}
	}
	public Grid(Pixmap pm){
		int width = pm.getWidth();
		int height = pm.getHeight();
		cells = new Cell[width][height];
		for(int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				cells[i][j] = new Cell(pm.getPixel(i, j));
			}
		}
		for(int i = 1; i < width - 1; i++) {
			for(int j = 1; j < height - 1; j++) {
				if((RAND.nextLong() & 0xFL) == 0) {
					cells[i][j].b = 1f;
				}
			}
		}
	}
}
