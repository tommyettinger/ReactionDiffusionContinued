package com.bigbass.reactiondiffusion.world;

import com.badlogic.gdx.math.Vector2;

public class Grid {
	
	public static final RandomTangle RAND = new RandomTangle();

	public Vector2 pos;
	
	public Cell[][] cells;
	
	public Grid(float x, float y, int width, int height){
		pos = new Vector2(x, y);
		
		//Vector2 tmp = new Vector2(width * 0.5f, height * 0.5f);
		
		cells = new Cell[width][height];
		for(int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				cells[i][j] = new Cell();
			}
		}
				/*tmp.set(width * 0.5f, height * 0.5f);
				if(tmp.dst(i, j) < 3){
					cells[i][j].b = 1;
				}*/
		for(int i = 1; i < width - 1; i++){
			for(int j = 1; j < height - 1; j++){
					if((RAND.nextLong() & 0x3FFL) == 0){
						cells[i][j].b = cells[width - 1 - i][j].b =
						cells[i][height - 1 - j].b = cells[width - 1 - i][height - 1 - j].b =
						cells[j][i].b = cells[width - 1 - j][i].b =
						cells[j][height - 1 - i].b = cells[width - 1 - j][height - 1 - i].b =
								1f;// - (RAND.nextLong() & 0xFFFFFFL) * 0x1p-26f;
					}
			}
		}
	}
}
