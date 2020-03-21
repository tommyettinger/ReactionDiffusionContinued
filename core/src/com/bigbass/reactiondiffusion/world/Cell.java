package com.bigbass.reactiondiffusion.world;

public class Cell {
	
	public float a;
	public float b;
	
	public float red;
	public float green;
	public float blue;
	
	public Cell(){
		a = 1;
		b = 0;
		
		updateColor();
	}
	
	public void updateColor(){
		//float val = Math.abs(((a * 2f) - b) / 2f);
		float val = (a + b) * (a - b) * 4f; // more defined edges
		
		if(val < 0){
			val = 0;
		} else if(val > 1){
			val = 1;
		}
		
		red = val;
		green = val;
		blue = val;
	}
}
