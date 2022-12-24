package com.pixel.fire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixel.fire.client.Client;
import com.pixel.fire.server.Server;
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
    private TextField textField = null;
    private String ip = "";
    public boolean isServerStarted = false;

    private final Slider musicSlider;
    private final Slider soundSlider;
    private final CheckBox fullscreenMode;

    private SpriteBatch batch;

    public MenuScreen(AssetManager assetManager, MyGame game)
    {
        skin = assetManager.get(Assets.SKIN);

        this.game = game;

        gameScreen = new GameScreen(game, assetManager, this, client);

        musicSlider = new Slider(0, 1, 0.01f, false, skin);
        musicSlider.setValue(SoundManager.musicVolume);

        soundSlider = new Slider(0, 1, 0.01f, false, skin);
        soundSlider.setValue(SoundManager.soundVolume);

        fullscreenMode = new CheckBox("Fullscreen mode", skin);

        batch = new SpriteBatch();
    }
    @Override
    public void show()
    {
        SoundManager.get("mainmenumusic").play(SoundManager.musicVolume).loop(true);
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
        stage.addActor(settingsTable);

        Label musicVolume = new Label("Music volume", skin);
        settingsTable.add(musicVolume);
        settingsTable.row();

        settingsTable.add(musicSlider).width(700).height(120).padBottom(60);
        settingsTable.row();

        Label soundVolume = new Label("Sound volume", skin);
        settingsTable.add(soundVolume);
        settingsTable.row();

        settingsTable.add(soundSlider).width(700).height(120).padBottom(60);
        settingsTable.row();

        settingsTable.add(fullscreenMode).width(700).height(120).padBottom(60);
        settingsTable.row();

        addButton("Play", mainTable).addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                mainTable.setVisible(false);
                playTable.setVisible(true);
            }
        });
        addButton("Settings", mainTable).addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                mainTable.setVisible(false);
                settingsTable.setVisible(true);
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
                        text("This beatiful game was created by these dudes:\nKolmogorov Danil Pavlovich aka Denio1337\nKozlov Denis Valeryevich aka stfuckk\nStepanyuk Dmitriy Aleksandrovich aka X3");
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
                if (textField != null)
                {
                    playTable.getChild(3).setVisible(true);
                    textField.setVisible(true);
                }
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
                    }
                    else
                    {
                        playTable.setVisible(false);
                        game.setScreen(gameScreen);
                        gameScreen.getPlayer().SetClient(client);
                        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);
                        Gdx.input.setCursorCatched(true);
                        SoundManager.get("mainmenumusic").stop();
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
                if (textField != null)
                {
                    textField.setVisible(false);
                    playTable.getChild(3).setVisible(false);
                }
                playTable.setVisible(false);
                mainTable.setVisible(true);
            }
        });
        addButton("Return", settingsTable).addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                settingsTable.setVisible(false);
                mainTable.setVisible(true);
            }
        });
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta)
    {
        batch.begin();
        batch.draw(Assets.background, 0, 0, 1280, 720);
        batch.end();
        update();

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
        table.add(button).width(600).height(120).padBottom(60);
        table.row();
        return button;
    }

    public void checkTextField()
    {
        if (textField == null)
        {
            Label ipInput = new Label("Enter ip:", skin);
            playTable.add(ipInput);
            playTable.row();
            textField = new TextField("", skin);
            playTable.add(textField);
            playTable.row();
        }
    }

    public Table getSettingsTable()
    {
        return settingsTable;
    }

    private void update()
    {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && textField != null && !textField.getText().equals(""))
        {
            textField.setVisible(false);
            playTable.getChild(3).setVisible(false);
            ip = textField.getText();
        }

        if (musicSlider.isDragging())
        {
            SoundManager.musicVolume = musicSlider.getValue();
            SoundManager.updateVolume();
        }

        if (soundSlider.isDragging())
        {
            SoundManager.soundVolume = soundSlider.getValue();
            SoundManager.updateVolume();
        }

        if (fullscreenMode.isChecked())
        {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
        else
        {
            Gdx.graphics.setWindowedMode(1280,720);
        }
    }

}
