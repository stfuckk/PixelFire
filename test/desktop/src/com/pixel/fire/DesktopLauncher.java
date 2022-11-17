package com.pixel.fire;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;


public class DesktopLauncher {


	public static void main (String[] arg) throws InterruptedException {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setIdleFPS(60);
		config.setForegroundFPS(60);
		config.useVsync(false);
		config.setTitle("Pixel Fire");
		config.setResizable(false);
		config.setWindowedMode(960,640);

		new Lwjgl3Application(new MyGame(), config);

		//Gdx.app.setLogLevel(Application.LOG_DEBUG);



	}
}
