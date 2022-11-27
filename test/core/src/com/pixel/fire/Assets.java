package com.pixel.fire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets
{
    private AssetManager assetManager = new AssetManager();

    public static final AssetDescriptor<Skin> SKIN = new AssetDescriptor<Skin>(Gdx.files.internal("skin/uiskin.json"), Skin.class, new SkinLoader.SkinParameter("uiskin.json"));

    public void loadAll()
    {
        assetManager.load(SKIN);
    }

    public AssetManager getAssetManager()
    {
        return assetManager;
    }
}