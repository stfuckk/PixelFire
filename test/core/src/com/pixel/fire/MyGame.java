package com.pixel.fire;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

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
		this.widthScreen = Gdx.graphics.getWidth();

		this.heightScreen = Gdx.graphics.getHeight();
		this.orthographicCamera = new OrthographicCamera();
		this.orthographicCamera.setToOrtho(false, widthScreen, heightScreen);
		this.orthographicCamera.zoom -= 0.3f;
		//Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());

		Assets assets = new Assets();
		assets.loadAll();
		assets.getAssetManager().finishLoading();

		SoundManager.add("mainmenumusic", "audio/mainmenumusic.wav"); //
		SoundManager.add("shot", "audio/shot.wav"); //
		SoundManager.add("death", "audio/death.wav"); //
		SoundManager.add("jump", "audio/jump.wav"); //
		SoundManager.add("win", "audio/win.wav");
		SoundManager.add("bulletcollision", "audio/bulletcollision.wav"); //
		SoundManager.add("footstep", "audio/footstep.wav");
		SoundManager.add("gamemusic", "audio/gamemusic.wav");

		setScreen(new MenuScreen(assets.getAssetManager(), INSTANCE));
	}

	public OrthographicCamera getCamera()
	{
		return orthographicCamera;
	}
}
