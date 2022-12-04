package com.pixel.fire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixel.fire.Helper.TileMapHelper;
import com.pixel.fire.Objects.Player.Player;
import com.pixel.fire.client.Client;
import com.pixel.fire.server.Server;

import static com.pixel.fire.Helper.Constants.PPM;

public class GameScreen extends ScreenAdapter
{
    private MyGame game;
    private OrthographicCamera camera;
    private SpriteBatch batch; //render sprites
    private World world; //store box2d bodies
    private Box2DDebugRenderer box2DDebugRenderer; //see box2d without using textures

    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private TileMapHelper tileMapHelper;
    private AssetManager assetManager;
    private PauseScreen pausescreen;
    private MenuScreen menuScreen;
    //game objects
    private Player player;
    private boolean paused = false;

    public GameScreen(MyGame game, AssetManager assetManager){
        this.game = game;
        this.camera = game.getCamera();
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0,-60f), false);
        this.box2DDebugRenderer = new Box2DDebugRenderer();
        this.assetManager = assetManager;
        this.tileMapHelper = new TileMapHelper(this);
        this.orthogonalTiledMapRenderer = tileMapHelper.setupMap();
        this.menuScreen = new MenuScreen(assetManager, game);
        this.pausescreen = new PauseScreen(assetManager, this, menuScreen);
    }

    private void update()
    {
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
        {
            game.setScreen(pausescreen);
            paused = true;
        }
        world.step(1/60f, 6, 2);
        cameraUpdate();
        player.update();

        batch.setProjectionMatrix(camera.combined);
        orthogonalTiledMapRenderer.setView(camera);

    }

    private void cameraUpdate(){
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
        player.render(batch);
        orthogonalTiledMapRenderer.render();
        batch.begin();
        //render objects
        //player.render(batch);
        batch.end();
        box2DDebugRenderer.render(world, camera.combined.scl(PPM));
        this.update();
    }

    public World getWorld() {
        return world;
    }

    public MyGame getGame()
    {
        return game;
    }

    public void setUnpaused()
    {
        paused = false;
    }

    public void setPlayer(Player player){
        this.player = player;
    }
}
