package com.pixel.fire.Objects.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import org.w3c.dom.css.Rect;

import static com.pixel.fire.Helper.Constants.PPM;

public class Enemy
{
    //
    private static int FRAME_COLS = 8, FRAME_ROWS = 1;
    //

    public static float x;
    public static float y;
    public static Rectangle collider;
    private boolean left = false;
    private boolean isGrounded = false;
    private boolean isIdle = true;
    private boolean isJumping = false;
    private boolean isFalling = false;
    public boolean isDead = false;
    private static Array<Vector2> spawnpoints = new Array<Vector2>();

    //
    private TextureRegion currentFrame;
    Animation<TextureRegion> runningAnimation;
    Animation<TextureRegion> idleAnimation;
    Animation<TextureRegion> jumpingAnimation;
    float stateTime;
    //

    public Enemy()
    {
        Texture runSheet = new Texture("Sprites/run.png");
        Texture jumpTexture = new Texture("Sprites/jump.png");
        Texture idleSheet = new Texture("Sprites/idle.png");
        //
        TextureRegion[][] tmp;
        //IDLE//
        FRAME_COLS = 5;
        tmp = TextureRegion.split(idleSheet,
                idleSheet.getWidth() / FRAME_COLS,
                idleSheet.getHeight() / FRAME_ROWS);
        TextureRegion[] idleFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        idleAnimation = new Animation<TextureRegion>(0.064f, FramesCycle(tmp, idleFrames));

        //JUMPING//
        FRAME_COLS = 1;
        tmp = TextureRegion.split(jumpTexture,
                jumpTexture.getWidth() / FRAME_COLS,
                jumpTexture.getHeight() / FRAME_ROWS);
        TextureRegion[] jumpFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        jumpingAnimation = new Animation<TextureRegion>(0.064f, FramesCycle(tmp, jumpFrames));

        //RUNNING//
        FRAME_COLS = 8;
        tmp = TextureRegion.split(runSheet,
                runSheet.getWidth() / FRAME_COLS,
                runSheet.getHeight() / FRAME_ROWS);
        TextureRegion[] runFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        runningAnimation = new Animation<TextureRegion>(0.064f, FramesCycle(tmp, runFrames));

        stateTime = 0f;
        currentFrame = idleAnimation.getKeyFrame(stateTime, true);

        spawnpoints.add(new Vector2(10,15));
        spawnpoints.add(new Vector2(30,30));
        spawnpoints.add(new Vector2(15,35));
        spawnpoints.add(new Vector2(5,35));
        spawnpoints.add(new Vector2(45,40));
        spawnpoints.add(new Vector2(45,13));
        spawnpoints.add(new Vector2(30,19));

        x = spawnpoints.get(0).x * PPM;
        y = spawnpoints.get(0).y * PPM;
        isGrounded = true;
    }

    public TextureRegion[] FramesCycle(TextureRegion[][] tmp, TextureRegion[] Frames){
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++){
            for (int j = 0; j < FRAME_COLS; j++){
                Frames[index++] = tmp[i][j];
            }
        }
        return Frames;
    }

    public void update() {
        collider.x = x - collider.width + 16;
        collider.y = y - collider.height + 14;
        //x = body.getPosition().x * PPM;
        //y = body.getPosition().y * PPM;
    }

    public void render(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        //idle
        if (isGrounded && isIdle && !isJumping)
            currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        //run
        if(!isIdle && isGrounded)
            currentFrame = runningAnimation.getKeyFrame(stateTime, true);

        //jump
        if(isJumping || isFalling)
            currentFrame = jumpingAnimation.getKeyFrame(stateTime, true);


        //flip sprite
        if(!currentFrame.isFlipX() && left)
            currentFrame.flip(true, false);

        if(currentFrame.isFlipX() && !left)
            currentFrame.flip(true, false);
        //
        batch.begin();
        batch.draw(currentFrame, x - 64, y - 16);
        batch.end();
        //
    }

    public static void setRectangle(float x, float y, float width, float height)
    {
        collider = new Rectangle(x, y, width, height);
    }
}
