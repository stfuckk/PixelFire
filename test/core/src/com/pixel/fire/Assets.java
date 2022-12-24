package com.pixel.fire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets
{
    private AssetManager assetManager = new AssetManager();
    public static final AssetDescriptor<Skin> SKIN = new AssetDescriptor<>("skin/neon-ui.json", Skin.class, new SkinLoader.SkinParameter("skin/neon-ui.atlas"));
    public static final Texture texture_background = new Texture(Gdx.files.internal("Sprites/background.png"));
    public static final Sprite background = new Sprite(texture_background);
    public static final Texture texture_gray = new Texture(Gdx.files.internal("Sprites/background1.png"));
    public static final Sprite gray = new Sprite(texture_gray);

    public void loadAll()
    {
        assetManager.load(SKIN);
    }

    public AssetManager getAssetManager()
    {
        return assetManager;
    }
}