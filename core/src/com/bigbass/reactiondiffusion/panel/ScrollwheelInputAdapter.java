package com.bigbass.reactiondiffusion.panel;

import com.badlogic.gdx.InputAdapter;

public abstract class ScrollwheelInputAdapter extends InputAdapter {
	
	@Override
	public abstract boolean scrolled(float amountX, float amountY);
}
