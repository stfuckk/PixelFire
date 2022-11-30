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
    //Texture playerTexture = new Texture("sprites/Amogus.png");
    //
    private static int FRAME_COLS = 8, FRAME_ROWS = 1;
    //
    private boolean left = false;
    private boolean isGrounded = false;
    private boolean isIdle = true;
    //
    private TextureRegion currentFrame;
    Animation<TextureRegion> runningAnimation;
    Animation<TextureRegion> jumpingAnimation;
    Animation<TextureRegion> fallingAnimation;
    Animation<TextureRegion> idleAnimation;
    float stateTime;
    //
    public Player(float width, float height, Body body) {
        super(width, height, body); //super - parent class
        this.speed = 20f;
        Texture runSheet = new Texture("sprites/run.png");
        Texture jumpSheet = new Texture("sprites/jump.png");
        Texture idleSheet = new Texture("sprites/idle.png");
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
         /*
        FRAME_COLS = 5;
        tmp = TextureRegion.split(jumpSheet,
                jumpSheet.getWidth() / FRAME_COLS,
                jumpSheet.getHeight() / FRAME_ROWS);
        TextureRegion[] jumpFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        jumpingAnimation = new Animation<TextureRegion>(0.064f, FramesCycle(tmp, jumpFrames));
         */

        //RUNNING//
        FRAME_COLS = 8;
        tmp = TextureRegion.split(runSheet,
                runSheet.getWidth() / FRAME_COLS,
                runSheet.getHeight() / FRAME_ROWS);
        TextureRegion[] runFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        runningAnimation = new Animation<TextureRegion>(0.064f, FramesCycle(tmp, runFrames));

        //FALLING//
        /*
        FRAME_COLS = 4;
        tmp = TextureRegion.split(fallSheet,
                fallSheet.getWidth() / FRAME_COLS,
                fallSheet.getHeight() / FRAME_ROWS);
        TextureRegion[] fallFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        fallingAnimation = new Animation<TextureRegion>(0.064f, FramesCycle(tmp, fallFrames));
        */
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

        //////////////
        if ((y >= 272 && y <= 272.5) && (x >= 110 && x <= 464) ||
                (y >= 208 && y <= 208.5) && (x >= 495 && x <= 848) ||
                (y >= 272 && y <= 272.5) && (x >= 879 && x <= 1232) ||
                (y >= 464 && y <= 464.5) && (x >= 560 && x <= 785) ||
                (y >= 528  && y <= 528.5) && (x >= 1008 && x <= 1232) ||
                (y >= 528  && y <= 528.5) && (x >= 48 && x <= 272))
            isGrounded = true;
        //System.out.println("idle: " + isIdle + " grounded: " + isGrounded + " fall: " + isFalling);
    }

    @Override
    public void render(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();

        //idle
        if (isGrounded && isIdle)
            currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        //jump
        if (!isGrounded)
            currentFrame = jumpingAnimation.getKeyFrame(stateTime, false);
        //run
        if(!isIdle && isGrounded && body.getLinearVelocity().y == 0)
            currentFrame = runningAnimation.getKeyFrame(stateTime, true);

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


        if(isGrounded && body.getLinearVelocity().x == 0)
            isIdle = true;
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

        if(Gdx.input.isKeyJustPressed(Input.Keys.W) && isGrounded){
            float force = body.getMass() * 23;
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            body.applyLinearImpulse(new Vector2(0, force), body.getPosition(), true);
            isGrounded = false;
            isIdle = false;
            //currentFrame = jumpingAnimation.getKeyFrame(stateTime, false);
        }

        body.setLinearVelocity(velX * speed, body.getLinearVelocity().y < 25 ?
                body.getLinearVelocity().y : 25);
    }

}
