package com.pixel.fire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixel.fire.client.Client;
import com.pixel.fire.server.Server;

public class MenuScreen extends ScreenAdapter
{
    private Stage stage;
    private Viewport viewport;
    private final Skin skin;
    private final GameScreen gameScreen;
    private Table mainTable;
    private Table playTable;
    private Table settingsTable;
    private final MyGame game;

    private Client client = new Client();

    public MenuScreen(AssetManager assetManager, MyGame game)
    {
        skin = assetManager.get(Assets.SKIN);

        this.game = game;

        gameScreen = new GameScreen(game, assetManager, this, client);
    }

    @Override
    public void show()
    {
        viewport = new ExtendViewport(700,800);
        stage = new Stage(viewport);

        mainTable = new Table();
        mainTable.setFillParent(true);

        playTable = new Table();
        playTable.setFillParent(true);
        playTable.setVisible(false);

        settingsTable = new Table();
        settingsTable.setFillParent(true);
        settingsTable.setVisible(false);

        stage.addActor(mainTable);
        stage.addActor(playTable);

        addButton("Play", mainTable).addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                mainTable.setVisible(false);
                playTable.setVisible(true);
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
        addButton("Credits", mainTable).addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Dialog d = new Dialog("Credits", skin)
                {
                    {
                        text("This beatiful game was created by these dudes:\nKolmogorov Danil Pavlovich aka Denio1337\nKozlov Denis Valeryevich aka PIDOR\nStepanyuk Dmitriy Aleksandrovich aka X3");
                        button("OK");
                    }
                };
                d.show(stage);
                //System.out.println("Credits click");
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
        addButton("Test", mainTable).addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                mainTable.setVisible(false);
                gameScreen.getPlayer().setRandomPosition();
                game.setScreen(gameScreen);
            }
        });
        addButton("Connect", playTable).addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                client = new Client();
                client.StartClient();
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
                if (!client.isServerStarted())
                {
                    client.ShutDown(); //If server is not started initialize client's suicide
                    Dialog d = new Dialog("Connection error", skin)
                    {
                        {
                            text("Server is not created!");
                            button("OK");
                        }
                    };
                    d.show(stage);
                }
                else
                {
                    playTable.setVisible(false);
                    game.setScreen(gameScreen);
                    //gameScreen.getPlayer();
                }
            }
        });
        addButton("Create server", playTable).addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                    new Thread(){@Override public void run()
                    {
                        try
                        {
                            Server.start();
                        } catch (InterruptedException e)
                        {
                            throw new RuntimeException(e);
                        }
                    }}.start();
                    Dialog d = new Dialog("Server", skin) {
                        {
                            text("Server has been created!");
                            button("Got it!");
                        }
                    }; d.show(stage);
                    /*
                    Client clientThread = new Client();
                    clientThread.StartClient();
                    playTable.setVisible(false);
                    game.setScreen(gameScreen);
                    */
            }
        });
        addButton("Return", playTable).addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                playTable.setVisible(false);
                mainTable.setVisible(true);
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

    public TextButton addButton(String name, Table table)
    {
        TextButton button = new TextButton(name, skin);
        table.add(button).width(700).height(120).padBottom(60);
        table.row();
        return button;
    }

}
