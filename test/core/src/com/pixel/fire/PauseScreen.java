package com.pixel.fire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

public class PauseScreen extends ScreenAdapter
{
    private Stage stage;
    private Viewport viewport;
    private AssetManager assetManager;
    private Skin skin;
    private Table mainTable;
    private GameScreen gameScreen;

    private MenuScreen menuScreen;
    private MyGame game;

    public PauseScreen(AssetManager assetManager, GameScreen gameScreen, MenuScreen menuScreen)
    {
        this.assetManager = assetManager;
        skin = assetManager.get(Assets.SKIN);

        this.gameScreen = gameScreen;
        this.game = gameScreen.getGame();

        this.menuScreen = menuScreen;
    }

    @Override
    public void show()
    {
        viewport = new ExtendViewport(700,800);
        stage = new Stage(viewport);

        mainTable = new Table();
        mainTable.setFillParent(true);

        stage.addActor(mainTable);

        addButton("Resume", mainTable).addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                mainTable.setVisible(false);
                game.setScreen(gameScreen);
            }
        });
        addButton("Options", mainTable).addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                System.out.println("Options click");
            }
        });
        addButton("Disconnect", mainTable).addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                game.setScreen(menuScreen);
            }
        });
        addButton("Quit", mainTable).addListener(new ClickListener()
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
        this.update();
        Gdx.gl.glClearColor(.1f, .15f, .15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    private void update()
    {
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
        {
            mainTable.setVisible(false);
            gameScreen.setUnpaused();
            game.setScreen(gameScreen);
        }
    }

    @Override
    public void resize (int width, int height)
    {
        viewport.update(width, height);
    }

    private TextButton addButton(String name, Table table)
    {
        TextButton button = new TextButton(name, skin);
        table.add(button).width(700).height(120).padBottom(60);
        table.row();
        return button;
    }
}
