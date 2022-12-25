package com.pixel.fire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixel.fire.Helper.TileMapHelper;
import com.pixel.fire.Objects.Player.Enemy;
import com.pixel.fire.Objects.Player.Player;
import com.pixel.fire.client.Client;
import java.util.ArrayList;
import static com.pixel.fire.Helper.Constants.PPM;

public class GameScreen extends ScreenAdapter
{

    // GAME LOGIC
    private MyGame game; // game object
    private OrthographicCamera camera; // game camera
    private SpriteBatch batch; //render sprites
    private World world; //store box2d bodies
    private Box2DDebugRenderer box2DDebugRenderer; //see box2d without using textures
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer; // arena renderer
    private TileMapHelper tileMapHelper; // class to initialize arena and background
    private AssetManager assetManager; // class to initialize assets and work with them
    private int playerVictories = 0; // Counter of player's victories
    private int enemyVictories = 0; // Counter of enemy's victories

    // PAUSE AND MENU
    private Skin skin; // Style for buttons in pause
    private Table mainTable; // Pause
    private Stage stage; // Stage for pause
    private Viewport viewport; // Viewport for pause
    private final MenuScreen menuScreen; // Main menu
    private Table settingsTable; // Settings
    private Slider musicSlider = null; // Music slider in settings
    private Slider soundSlider = null; // Sound slider in settings
    private boolean paused = false; // Pause boolean
    private boolean isStarted = false; // Is game started
    private int timer; // Bullet timer
    private boolean isReloading; // Is player reloading

    // GAME OBJECTS
    private Player player;
    private Enemy enemy;
    private ArrayList<Bullet> bullets;
    private Array<PolygonMapObject> objects = new Array<PolygonMapObject>(); // All game objects (platforms and decorations)

    // BACKGROUND RENDER
    private Background bg; // Background sprite
    private float bg_x, bg2_x; // Coordinates to render background

    // HP RENDER
    private Texture player_hearts_0, player_hearts_1, player_hearts_2, player_hearts_3;
    private Texture enemy_hearts_0, enemy_hearts_1, enemy_hearts_2, enemy_hearts_3;
    private Texture[] player_wins;
    private Texture[] enemy_wins;
    private Sprite playerHearts;
    private Sprite enemyHearts;
    private Sprite playerWinsSprite;
    private Sprite enemyWinsSprite;

    // SERVER-CLIENT OBJECTS
    private Client client;


    public GameScreen(MyGame game, AssetManager assetManager, MenuScreen menuScreen, Client client)
    {
        this.game = game;
        this.camera = game.getCamera();
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0, -60f), false);
        this.box2DDebugRenderer = new Box2DDebugRenderer();
        this.tileMapHelper = new TileMapHelper(this);
        this.orthogonalTiledMapRenderer = tileMapHelper.setupMap();

        this.assetManager = assetManager;
        this.menuScreen = menuScreen;
        this.skin = assetManager.get(Assets.SKIN);

        this.client = client;

        bullets = new ArrayList<Bullet>();
        Bullet.setObjects(objects);

        this.bg = new Background();

        this.enemy = new Enemy();

        player_hearts_0 = new Texture("Sprites/Player_hearts/0.png");
        player_hearts_1 = new Texture("Sprites/Player_hearts/1.png");
        player_hearts_2 = new Texture("Sprites/Player_hearts/2.png");
        player_hearts_3 = new Texture("Sprites/Player_hearts/3.png");
        playerHearts = new Sprite(player_hearts_3);

        enemy_hearts_0 = new Texture("Sprites/Player_hearts/e0.png");
        enemy_hearts_1 = new Texture("Sprites/Player_hearts/e1.png");
        enemy_hearts_2 = new Texture("Sprites/Player_hearts/e2.png");
        enemy_hearts_3 = new Texture("Sprites/Player_hearts/e3.png");
        enemyHearts = new Sprite(enemy_hearts_3);

        player_wins[0] = new Texture("Sprites"); player_wins[1] = new Texture("Sprites");
        player_wins[2] = new Texture("Sprites"); player_wins[3] = new Texture("Sprites");
        playerWinsSprite = new Sprite(player_wins[0]);

        enemy_wins[0] = new Texture("Sprites"); enemy_wins[1] = new Texture("Sprites");
        enemy_wins[2] = new Texture("Sprites"); enemy_wins[3] = new Texture("Sprites");
        enemyWinsSprite = new Sprite(enemy_wins[0]);
    }

    private void update(float delta)
    {
        timer += delta * 1000;

        if (timer >= 200 && timer <= 215)
        {
            SoundManager.get("reload").play(SoundManager.soundVolume);
        }

        if (timer >= 700)
        {
            isReloading = false;
        }
        else
        {
            isReloading = true;
        }

        bgMove();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
        {
            pause();
            settingsTable.setVisible(false);
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && !paused && !player.isDead && !isReloading)
        {
            SoundManager.get("shot").play(SoundManager.soundVolume);
            bullets.add(new Bullet(player.getBody().getPosition(), player.isLeft(), batch, false));
            timer = 0;
            player.JustShot();
            player.SendPlayerInfo();

        }

        if(enemy.GetJustShot())
        {
            SoundManager.get("shot").play(SoundManager.soundVolume);
            bullets.add(new Bullet(enemy.GetVector(), enemy.IsLeft(), batch, true));
            CheckForRoundWin();
        }

        world.step(1 / 60f, 6, 2);
        cameraUpdate();

        ArrayList<Bullet> bulletsToRemove = new ArrayList<Bullet>();
        for (Bullet bullet : bullets)
        {
            bullet.update(delta);
            if (bullet.remove)
            {
                bulletsToRemove.add(bullet);
            }

            if (Enemy.collider.contains(bullet.getX(), bullet.getY()) && !bullet.isEnemy && bullet.hasHit)
            {
                //enemy.isDead = true;
                bullet.hasHit = false;
                bullet.killPlayer();
                enemy.GetShot();
            }

            if (player.collider.contains(bullet.getX(), bullet.getY()) && bullet.isEnemy && bullet.hasHit)
            {
                bullet.hasHit = false;
                bullet.killPlayer();
                player.GetShot();
            }
        }
        bullets.removeAll(bulletsToRemove);

        player.update();
        enemy.update();

        batch.setProjectionMatrix(camera.combined);
        orthogonalTiledMapRenderer.setView(camera);
        UpdateHealthBars();

        if (musicSlider != null && musicSlider.isDragging())
        {
            SoundManager.musicVolume = musicSlider.getValue();
            SoundManager.updateVolume();
        }

        if (soundSlider != null && soundSlider.isDragging())
        {
            SoundManager.soundVolume = soundSlider.getValue();
            SoundManager.updateVolume();
        }

        if (!enemy.isIdle)
        {
            resetMusic();
            isStarted = true;
        }

        UpdateEnemy();
        CheckForRoundWin();
    }

    private void cameraUpdate() 
    {
        Vector3 position = camera.position;

        if(player.getBody().getPosition().x * PPM >= 135 && player.getBody().getPosition().x * PPM <= 1800) {
            position.x = Math.round(player.getBody().getPosition().x * PPM * 10) / 10f;
        }
        else
        {
            position.x = camera.position.x;
        }

        position.y = Math.round(player.getBody().getPosition().y * PPM * 10) / 10f;

        camera.position.set(position);
        camera.update();
    }

    private void bgMove(){
        if(player.getBody().getPosition().x * PPM >= 135 && player.getBody().getPosition().x * PPM <= 1800) {
            bg_x = bg.bg_main.getX();
            bg2_x = bg.bg_jijka.getX();
            bg.bg_main.setPosition(player.getBody().getPosition().x * PPM / 2f, player.getBody().getPosition().y * PPM / 2f);
            bg.bg_jijka.setPosition(player.getBody().getPosition().x * PPM / 3f, player.getBody().getPosition().y * PPM / 3f);
        }
        else{
            bg.bg_main.setPosition(bg_x, player.getBody().getPosition().y * PPM / 2f);
            bg.bg_jijka.setPosition(bg2_x, player.getBody().getPosition().y * PPM / 3f);
        }
    }

    @Override
    public void render(float delta)
    {
        bg.render(batch);

        orthogonalTiledMapRenderer.render(new int[]{4,5});

        if (!player.isDead)
        {
            player.render(batch);
        }

        enemy.render(batch);
        batch.begin();
        orthogonalTiledMapRenderer.render(new int[]{1, 2, 3});
        batch.draw(playerHearts, camera.position.x - 450, camera.position.y - 250);
        batch.draw(enemyHearts, camera.position.x + 190, camera.position.y - 250);

        if (isReloading) batch.draw(player.reloadFrame, player.getBody().getPosition().x * PPM - 12, player.getBody().getPosition().y * PPM + 15, 25, 25);
        for (Bullet bullet : bullets)
        {
            bullet.render(delta);
        }

        batch.end();
        //box2DDebugRenderer.render(world, camera.combined.scl(PPM));

        if (paused)
        {
            stage.act();
            stage.draw();
        }
        this.update(delta);
    }

    @Override
    public void show()
    {
        SoundManager.get("waitmusic").play(SoundManager.musicVolume).loop(true);
        viewport = new ExtendViewport(700,800);
        stage = new Stage(viewport);

        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setVisible(false);

        settingsTable = new Table();
        settingsTable.setFillParent(true);
        settingsTable.setVisible(false);

        stage.addActor(mainTable);
        stage.addActor(settingsTable);

        Label musicVolume = new Label("Music volume", skin);
        settingsTable.add(musicVolume);
        settingsTable.row();

        musicSlider = (Slider) menuScreen.getSettingsTable().getChild(1);
        settingsTable.add(musicSlider).width(700).height(120).padBottom(60);
        settingsTable.row();

        Label soundVolume = new Label("Sound volume", skin);
        settingsTable.add(soundVolume);
        settingsTable.row();

        soundSlider = (Slider) menuScreen.getSettingsTable().getChild(2);
        settingsTable.add(soundSlider).width(700).height(120).padBottom(60);
        settingsTable.row();

        menuScreen.addButton("Resume", mainTable).addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                pause();
            }
        });
        menuScreen.addButton("Settings", mainTable).addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                mainTable.setVisible(false);
                settingsTable.setVisible(true);
            }
        });
        menuScreen.addButton("Disconnect", mainTable).addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                SoundManager.get("gamemusic").stop();
                pause();
                player.setRandomPosition();
                player.update();
                client.ShutDown();
                menuScreen.isServerStarted = false;
                game.setScreen(menuScreen);
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                Gdx.input.setCursorCatched(false);
            }
        });
        menuScreen.addButton("Quit", mainTable).addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                new Dialog("", skin)
                {
                    {
                        text("Are you sure?");
                        button("Yes", "Yes");
                        button("No", "No");
                    }

                    @Override
                    protected void result(final Object object)
                    {
                        if (object.toString().equals("Yes")) {
                            client.ShutDown();
                            Gdx.app.exit();
                        };
                    }
                }.show(stage);
            }
        });

        menuScreen.addButton("Return", settingsTable).addListener(new ClickListener()
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

    public World getWorld()
    {
        return world;
    }

    public MyGame getGame()
    {
        return game;
    }

    public Player getPlayer()
    {
        return player;
    }

    public void pause()
    {
        if (!paused)
        {
            mainTable.setVisible(true);
            paused = true;
            player.pause(true);
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            Gdx.input.setCursorCatched(false);
        }
        else
        {
            mainTable.setVisible(false);
            paused = false;
            player.pause(false);
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);
            Gdx.input.setCursorCatched(true);
        }
    }

    public void setPlayer(Player player, Rectangle collider)
    {
        this.player = player;
        player.collider = collider;
    }

    public void UpdateEnemy() {
        enemy.setState(client.GetInfo());
        //System.out.println("GAMESCREEN.UPDATEENEMY()" + client.GetInfo());
    }
    public void setObjects(PolygonMapObject polygon)
    {
        objects.add(polygon);
    }
    private void UpdateHealthBars() {
        if(player.GetPlayerLives() == 3) playerHearts.setTexture(player_hearts_3);
        else if(player.GetPlayerLives() == 2) playerHearts.setTexture(player_hearts_2);
        else if(player.GetPlayerLives() == 1) playerHearts.setTexture(player_hearts_1);
        else if(player.GetPlayerLives() == 0) playerHearts.setTexture(player_hearts_0);

        if(enemy.GetEnemyLives() == 3) enemyHearts.setTexture(enemy_hearts_3);
        else if(enemy.GetEnemyLives() == 2) enemyHearts.setTexture(enemy_hearts_2);
        else if(enemy.GetEnemyLives() == 1) enemyHearts.setTexture(enemy_hearts_1);
        else if(enemy.GetEnemyLives() == 0) enemyHearts.setTexture(enemy_hearts_0);

    }

    private void CheckForRoundWin() {
        if(player.GetPlayerLives() == 0) {
            SoundManager.get("death").play(SoundManager.soundVolume);
            enemyVictories++;
            enemyWinsSprite.setTexture(enemy_wins[enemyVictories]);
            ResetRound();
        }
        else if(enemy.GetEnemyLives() == 0) {
            SoundManager.get("death").play(SoundManager.soundVolume);
            playerVictories++;
            playerWinsSprite.setTexture(player_wins[playerVictories]);
            ResetRound();
        }
    }
    private void ResetRound() {
        player.SetPlayerLives();
        enemy.SetEnemyLives();

        player.setRandomPosition();
        enemy.setRandomPosition();

        player.isDead = false; enemy.isDead = false;
    }

    private void resetMusic()
    {
        if (!isStarted)
        {
            SoundManager.get("waitmusic").loop(false).stop();
            SoundManager.get("gamemusic").play(SoundManager.musicVolume).loop(true);
        }
    }

}
