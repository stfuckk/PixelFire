package com.pixel.fire.Objects.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import static com.pixel.fire.Helper.Constants.PPM;

public class Player extends GameEntity
{
    //
    private static int FRAME_COLS = 8, FRAME_ROWS = 1;
    //
    private boolean left = false;
    private boolean isGrounded = false;
    private boolean isIdle = true;
    private boolean isJumping = false;
    private boolean isFalling = false;

    private int counter = 0;
    //
    private TextureRegion currentFrame;
    Animation<TextureRegion> runningAnimation;
    Animation<TextureRegion> idleAnimation;
    Animation<TextureRegion> jumpingAnimation;
    float stateTime;
    //
    public Player(float width, float height, Body body) {
        super(width, height, body); //super - parent class
        this.speed = 20f;
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

    @Override
    public void update() {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        checkUserInput();

        //////////////GROUND CHECK///////////////
        if
                ((y >= 400 && y <= 400.5) && (x >= 111 && x <= 656) ||
                (y >= 528 && y <= 528.5) && (x >= 751 && x <= 1168) ||          //1
                (y >= 592 && y <= 592.5) && (x >= 815 && x <= 1104) ||
                (y >= 400 && y <= 400.5) && (x >= 1263 && x <= 1808) ||

                (y >= 784 && y <= 784.5) && (x >= 496 && x <= 720) ||
                (y >= 848  && y <= 848.5) && (x >= 367 && x <= 528) ||        //2
                (y >= 848  && y <= 848.5) && (x >= 879 && x <= 1040) ||
                (y >= 784 && y <= 784.5) && (x >= 1199 && x <= 1423) ||
                (y >= 848  && y <= 848.5) && (x >= 1391 && x <= 1552) ||

                (y >= 976  && y <= 976.5) && (x >= 111 && x <= 272) ||
                (y >= 976  && y <= 976.5) && (x >= 1647 && x <= 1808) ||    //3

                (y >= 1104  && y <= 1104.5) && (x >= 431 && x <= 527) ||
                (y >= 1168  && y <= 1168.5) && (x >= 495 && x <= 848) ||    //4
                (y >= 1104  && y <= 1104.5) && (x >= 1392 && x <= 1488) ||
                (y >= 1168  && y <= 1168.5) && (x >= 1071 && x <= 1424) )

            isGrounded = true;
    }

    @Override
    public void render(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        //idle
        if (isGrounded && isIdle && !isJumping)
            currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        //run
        if(!isIdle && isGrounded && body.getLinearVelocity().y == 0)
            currentFrame = runningAnimation.getKeyFrame(stateTime, true);

        //jump
        if(isJumping || isFalling)
            currentFrame = jumpingAnimation.getKeyFrame(stateTime, false);


        //flip sprite
        if(!currentFrame.isFlipX() && left)
            currentFrame.flip(true, false);

        if(currentFrame.isFlipX() && !left)
            currentFrame.flip(true, false);
        //
        batch.begin();
        batch.draw(currentFrame, (body.getPosition().x * PPM) - 64, (body.getPosition().y * PPM) - 16);
        batch.end();
        //
    }
    private void checkUserInput(){
        velX = 0;

        if(body.getLinearVelocity().y == 0 && isGrounded)
        {
            isJumping = false;
            isFalling = false;
            counter = 0;
        }

        if(isGrounded && body.getLinearVelocity().y < 0)
            isFalling = true;

        if(isGrounded && body.getLinearVelocity().x == 0)
            isIdle = true;

        if(isGrounded && body.getLinearVelocity().y > 0)
            isGrounded = false;

        if(Gdx.input.isKeyPressed(Input.Keys.D)){
            velX = 1;
            left = false;
            isIdle = false;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            velX = -1;
            left = true;
            isIdle = false;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.W) && counter <= 1){
            float force = body.getMass() * 23;
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            body.applyLinearImpulse(new Vector2(0, force), body.getPosition(), true);
            isGrounded = false;
            isIdle = false;
            isJumping = true;
            counter++;
        }

        body.setLinearVelocity(velX * speed, body.getLinearVelocity().y < 25 ?
                body.getLinearVelocity().y : 25);
    }

}
