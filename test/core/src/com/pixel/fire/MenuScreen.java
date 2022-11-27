package com.pixel.fire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen extends ScreenAdapter
{
    private Stage stage;
    private Viewport viewport;
    private AssetManager assetManager;
    private Skin skin;
    private Table mainTable;
    private MyGame game;

    public MenuScreen(AssetManager assetManager, MyGame game)
    {
        this.assetManager = assetManager;
        skin = assetManager.get(Assets.SKIN);

        this.game = game;
    }

    @Override
    public void show()
    {
        viewport = new ExtendViewport(700,800);
        stage = new Stage(viewport);

        mainTable = new Table();
        mainTable.setFillParent(true);

        stage.addActor(mainTable);

        addButton("Play").addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                game.setScreen(new GameScreen(game.getCamera()));
            }
        });
        addButton("Options").addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                System.out.println("Options click");
            }
        });
        addButton("Credits").addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                System.out.println("Credits click");
            }
        });
        addButton("Quit").addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Gdx.app.exit();
            }
        });

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(.1f, .15f, .15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize (int width, int height)
    {
        viewport.update(width, height);
    }

    private TextButton addButton(String name)
    {
        TextButton button = new TextButton(name, skin);
        mainTable.add(button).width(700).height(120).padBottom(60);
        mainTable.row();
        return button;
    }
}
