package com.pixel.fire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import org.w3c.dom.Text;

public class MenuScreen extends ScreenAdapter
{
    private Stage stage;
    private Viewport viewport;
    private AssetManager assetManager;
    private Skin skin;
    private Table mainTable;

    public MenuScreen(AssetManager assetManager)
    {
        this.assetManager = assetManager;
        skin = assetManager.get(Assets.SKIN);
    }

    @Override
    public void show()
    {
        viewport = new ExtendViewport(1280,720);
        stage = new Stage(viewport);

        mainTable = new Table();
        mainTable.setFillParent(true);

        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(.1f, .1f, .15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        addButton("Play");
        addButton("Options");
        addButton("Credits");
        addButton("Quit");
    }

    @Override
    public void resize (int width, int height)
    {
        viewport.update(width, height);
    }

    private TextButton addButton(String name)
    {
        TextButton button = new TextButton(name, skin);
        mainTable.add(button);
        return button;
    }
}
