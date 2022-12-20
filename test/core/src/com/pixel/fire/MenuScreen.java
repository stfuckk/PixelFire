package com.pixel.fire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

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
    private final TextListener textListener = new TextListener();
    private TextField textField = null;
    private String ip = "";
    public boolean isServerStarted = false;
    private Sound mainMenuMusic = Gdx.audio.newSound(Gdx.files.internal("audio/baby.wav"));

    public MenuScreen(AssetManager assetManager, MyGame game)
    {
        skin = assetManager.get(Assets.SKIN);

        this.game = game;

        gameScreen = new GameScreen(game, assetManager, this, client);
        mainMenuMusic.play(1.0f);
        long id = mainMenuMusic.play(1.0f);
        mainMenuMusic.setLooping(id, true);
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
        addButton("Connect", playTable).addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if (ip.equals(""))
                {
                    checkTextField();
                }
                else
                {
                    client.StartClient(ip);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (!client.isServerStarted()) {
                        client.ShutDown(); //If server is not started initialize client's suicide
                        Dialog d = new Dialog("Connection error", skin) {
                            {
                                text("Server is not created!");
                                button("OK");
                            }
                        };
                        d.show(stage);
                    } else {
                        playTable.setVisible(false);
                        game.setScreen(gameScreen);
                        gameScreen.getPlayer().SetClient(client);
                    }
                }
            }
        });
        addButton("Create server", playTable).addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if (!isServerStarted) {
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                isServerStarted = true;
                                Server.start();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }.start();
                    Dialog d = new Dialog("Server", skin) {
                        {
                            text("Server has been created!");
                            button("Got it!");
                        }
                    };
                    d.show(stage);
                }
                else
                {
                    Dialog d = new Dialog("Server", skin) {
                        {
                            text("Server already exist");
                            button("OK");
                        }
                    };
                    d.show(stage);
                }
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
        update();

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

    public void checkTextField()
    {
        if (textField == null)
        {
            textField = new TextField("", skin);
            playTable.add(textField);
        }
    }

    private void update()
    {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && textField != null && !textField.getText().equals(""))
        {
            textField.setVisible(false);
            ip = textField.getText();
        }
    }

}

class TextListener implements TextInputListener
{
    private String text;


    @Override
    public void input(String text)
    {
        this.text = text;
    }

    @Override
    public void canceled()
    {
        this.text = "Cancelled";
    }

    public String getText()
    {
        return text;
    }
}
