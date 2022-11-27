package com.pixel.fire;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.pixel.fire.client.Client;

import java.awt.*;

public class MyGame extends Game {

	public static MyGame INSTANCE;
	private int widthScreen, heightScreen;
	private OrthographicCamera orthographicCamera;

	public MyGame()
	{
		INSTANCE = this;
	}

	@Override
	public void create () {
		Assets assets = new Assets();
		assets.loadAll();
		assets.getAssetManager().finishLoading();
		this.widthScreen = Gdx.graphics.getWidth();
		this.heightScreen = Gdx.graphics.getHeight();
		this.orthographicCamera = new OrthographicCamera();
		this.orthographicCamera.setToOrtho(false, widthScreen, heightScreen);
		this.orthographicCamera.zoom -= 0.25f;
		setScreen(new MenuScreen(assets.getAssetManager()));
		//setScreen(new GameScreen(orthographicCamera));
		Client clientThread = new Client();
		clientThread.StartClient();
	}
}
