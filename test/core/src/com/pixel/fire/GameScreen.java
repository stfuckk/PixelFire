package com.pixel.fire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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

public class GameScreen extends ScreenAdapter {

    // GAME LOGIC
    private MyGame game; // game object
    private OrthographicCamera camera; // game camera
    private SpriteBatch batch; //render sprites
    private World world; //store box2d bodies
    private Box2DDebugRenderer box2DDebugRenderer; //see box2d without using textures
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer; // arena renderer
    private TileMapHelper tileMapHelper; // class to initalize arena and background
    private AssetManager assetManager; // class to initialize assets and work with them

    // PAUSE AND MENU
    private Skin skin; // Style for buttons in pause
    private Table mainTable; // Pause
    private Stage stage; // Stage for pause
    private Viewport viewport; // Viewport for pause
    private MenuScreen menuScreen; // Main menu
    private boolean paused = false; // Pause boolean

    // GAME OBJECTS
    private Player player;
    private Enemy enemy;
    private ArrayList<Bullet> bullets;
    private Array<PolygonMapObject> objects = new Array<PolygonMapObject>();
    // SERVER-CLIENT OBJECTS
    private Client client;

    public GameScreen(MyGame game, AssetManager assetManager, MenuScreen menuScreen, Client client) {
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

        this.enemy = new Enemy();

    }

    private void update(float delta)
    {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
        {
            pause();
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && !paused)
        {
            bullets.add(new Bullet(player.getBody().getPosition(), player.isLeft(), batch));
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
            //System.out.println("bullet x: " + bullet.getX()+ " player x: " + enemy.collider.x);
            //System.out.println("bullet y: " + bullet.getY() + " player y: " + enemy.collider.y);
            if (Enemy.collider.contains(bullet.getX(), bullet.getY()))
            {
                enemy.isDead = true;
                bullet.killPlayer();
            }
        }
        bullets.removeAll(bulletsToRemove);
        player.update();
        enemy.update();
        batch.setProjectionMatrix(camera.combined);
        orthogonalTiledMapRenderer.setView(camera);
    }

    private void cameraUpdate() {
        Vector3 position = camera.position;
        position.x = Math.round(player.getBody().getPosition().x * PPM * 10) / 10f;
        position.y = Math.round(player.getBody().getPosition().y * PPM * 10) / 10f;
        camera.position.set(position);
        camera.update();
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        orthogonalTiledMapRenderer.render(new int[]{2});
        player.render(batch);
        //orthogonalTiledMapRenderer.render();
        enemy.render(batch);
        batch.begin();
        orthogonalTiledMapRenderer.render(new int[]{1, 3});
        for (Bullet bullet : bullets)
        {
            bullet.render(delta);
        }
        batch.end();

        //render objects
        //player.render(batch);
        box2DDebugRenderer.render(world, camera.combined.scl(PPM));

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
        viewport = new ExtendViewport(700,800);
        stage = new Stage(viewport);

        mainTable = new Table();
        mainTable.setFillParent(true);

        stage.addActor(mainTable);
        mainTable.setVisible(false);

        menuScreen.addButton("Resume", mainTable).addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                pause();
            }
        });
        menuScreen.addButton("Options", mainTable).addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                System.out.println("Options click");
            }
        });
        menuScreen.addButton("Disconnect", mainTable).addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                pause();
                player.setRandomPosition();
                player.update();
                client.ShutDown();
                game.setScreen(menuScreen);
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
            return;
        }
        else
        {
            mainTable.setVisible(false);
            paused = false;
            player.pause(false);
        }
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public void setObjects(PolygonMapObject polygon)
    {
        objects.add(polygon);
    }


}
