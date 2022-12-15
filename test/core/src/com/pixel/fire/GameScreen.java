package com.pixel.fire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixel.fire.Helper.BodyHelperService;
import com.pixel.fire.Helper.TileMapHelper;
import com.pixel.fire.Objects.Player.Player;
import com.pixel.fire.client.Client;
import com.pixel.fire.server.Server;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static com.pixel.fire.Helper.Constants.PPM;

public class GameScreen extends ScreenAdapter {
    private MyGame game;
    private OrthographicCamera camera;
    private SpriteBatch batch; //render sprites
    private World world; //store box2d bodies
    private Box2DDebugRenderer box2DDebugRenderer; //see box2d without using textures
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private OrthogonalTiledMapRenderer orthogonalBackgroundRenderer;
    private TileMapHelper tileMapHelper;
    private AssetManager assetManager;
    private Skin skin;
    private Table mainTable;
    private Stage stage;
    private Viewport viewport;
    private MenuScreen menuScreen;
    //game objects
    private Player player;
    private ArrayList<Bullet> bullets;
    private Array<PolygonMapObject> objects = new Array<PolygonMapObject>();
    private boolean paused = false;

    public GameScreen(MyGame game, AssetManager assetManager, MenuScreen menuScreen) {
        this.game = game;
        this.camera = game.getCamera();
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0, -60f), false);
        this.box2DDebugRenderer = new Box2DDebugRenderer();
        this.tileMapHelper = new TileMapHelper(this);
        this.orthogonalTiledMapRenderer = tileMapHelper.setupMap();
        this.orthogonalBackgroundRenderer = tileMapHelper.setupBackground();

        this.assetManager = assetManager;
        this.menuScreen = menuScreen;

        this.skin = assetManager.get(Assets.SKIN);

        bullets = new ArrayList<Bullet>();
        Bullet.setObjects(objects);
    }

    private void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            pause();
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && !paused) {
            bullets.add(new Bullet(player.getBody().getPosition(), player.isLeft()));
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
        }
        bullets.removeAll(bulletsToRemove);
        player.update();
        batch.setProjectionMatrix(camera.combined);
        orthogonalTiledMapRenderer.setView(camera);
        orthogonalBackgroundRenderer.setView(camera);

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
        orthogonalBackgroundRenderer.render();
        player.render(batch);
        batch.begin();
        for (Bullet bullet : bullets)
        {
            bullet.render(batch);
        }
        batch.end();
        orthogonalTiledMapRenderer.render();

        /*
        ArrayList<Bullet> bulletsToRemove = new ArrayList<Bullet>();
        for (Bullet bullet : bullets)
        {
            bullet.update(delta);
            if (bullet.remove)
            {
                bulletsToRemove.add(bullet);
            }
        }
        bullets.removeAll(bulletsToRemove);
         */
        //render objects
        //player.render(batch);
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
                player.setPosition();
                player.update();
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
                        if (object.toString().equals("Yes")) Gdx.app.exit();
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

    public void pause()
    {
        if (paused == false)
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
