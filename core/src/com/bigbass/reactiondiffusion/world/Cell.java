package com.bigbass.reactiondiffusion.world;

import com.badlogic.gdx.math.MathUtils;

public class Cell {
	
	public float[] a;
	public float[] b;
	
	public float red;
	public float green;
	public float blue;

	public Cell(){
		a = new float[] {1f, 1f, 1f};
		b = new float[]{0f, 0f, 0f};
		updateColor();
	}

	public Cell(int rgba8888){
		a = new float[] {(rgba8888 >>> 24) / 255f, (rgba8888 >>> 16 & 0xFF) / 255f, (rgba8888 >>> 8 & 0xFF) / 255f};
		b = new float[] {0.05f * a[0], 0.05f * a[1], 0.05f * a[2]};
		updateColor();
	}
	
	public void updateColor(){
		//float val = Math.abs(((a * 2f) - b) / 2f);

		// more defined edges
//		final float val = MathUtils.clamp((a[0] + b) * (a[0] - b) * 4f, 0f, 1f);
		
		red   = 1f - MathUtils.clamp((a[0] + b[0]) * (a[0] - b[0]), 0f, 1f);
		green = 1f - MathUtils.clamp((a[1] + b[1]) * (a[1] - b[1]), 0f, 1f);
		blue  = 1f - MathUtils.clamp((a[2] + b[2]) * (a[2] - b[2]), 0f, 1f);

//		red = MathUtils.clamp((red + b) * (red - b) * 4f, 0f, 1f);
//		green = MathUtils.clamp((green + b) * (green - b) * 4f, 0f, 1f);
//		blue = MathUtils.clamp((blue + b) * (blue - b) * 4f, 0f, 1f);

	}
}
