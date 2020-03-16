package com.dookin.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dookin.MindGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//config.width = 1920 ;

		config.width = 1920;
		config.height = 1080;
		//config.height = 1080;
		config.x = 0;
		config.y = 0;
		//config.resizable = false;
		//config.useHDPI= true;
		new LwjglApplication(new MindGame(), config);
	}
}
