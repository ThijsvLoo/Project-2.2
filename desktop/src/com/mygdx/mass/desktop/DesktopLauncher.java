package com.mygdx.mass.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.mass.MASS;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = MASS.TITLE;
		config.width = 800;
		config.height = 800;
		config.samples = 16; //anti aliasing
		new LwjglApplication(new MASS(), config);
	}
}
