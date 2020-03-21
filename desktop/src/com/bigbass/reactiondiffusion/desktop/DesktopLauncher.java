package com.bigbass.reactiondiffusion.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.bigbass.reactiondiffusion.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width = 800;
		config.height = 600;
		
		config.resizable = false;
		
		config.vSyncEnabled = false;
		
		config.title = "Reaction Diffusion";
		
		new LwjglApplication(new Main(), config);
	}
}
