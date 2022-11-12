package com.pixel.fire;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.pixel.fire.MyGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setIdleFPS(60);
		config.useVsync(true);
		config.setTitle("Pixel Fire");
		config.setResizable(false);
		config.setWindowedMode(960,640);


		new Lwjgl3Application(new MyGame(), config);
	}
}
