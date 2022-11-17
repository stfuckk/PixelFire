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

public class Player extends GameEntity{
    //Texture playerTexture = new Texture("sprites/Amogus.png");
    //
    private static int FRAME_COLS = 8, FRAME_ROWS = 1;
    //
    //private int jumpCounter;
    private boolean left = false;
    private boolean isGrounded = false;
    private boolean isFalling = false;
    //
    private TextureRegion currentFrame;
    Animation<TextureRegion> runningAnimation;
    Animation<TextureRegion> jumpingAnimation;
    Animation<TextureRegion> fallingAnimation;
    float stateTime;
    //
    public Player(float width, float height, Body body) {
        super(width, height, body); //super - parent class
        this.speed = 20f;
        Texture runSheet = new Texture("sprites/run.png");
        Texture jumpSheet = new Texture("sprites/jump.png");
        Texture fallSheet = new Texture("sprites/fall.png");
        //this.jumpCounter = 0;
        //
        TextureRegion[][] tmp;
        //JUMPING//
        FRAME_COLS = 5;
        tmp = TextureRegion.split(jumpSheet,
                jumpSheet.getWidth() / FRAME_COLS,
                jumpSheet.getHeight() / FRAME_ROWS);
        TextureRegion[] jumpFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        jumpingAnimation = new Animation<TextureRegion>(1f, FramesCycle(tmp, jumpFrames));

        //RUNNING//
        FRAME_COLS = 8;
        tmp = TextureRegion.split(runSheet,
                runSheet.getWidth() / FRAME_COLS,
                runSheet.getHeight() / FRAME_ROWS);
        TextureRegion[] runFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        runningAnimation = new Animation<TextureRegion>(0.064f, FramesCycle(tmp, runFrames));



        //FALLING//
        FRAME_COLS = 4;
        tmp = TextureRegion.split(fallSheet,
                fallSheet.getWidth() / FRAME_COLS,
                fallSheet.getHeight() / FRAME_ROWS);
        TextureRegion[] fallFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        fallingAnimation = new Animation<TextureRegion>(0.064f, FramesCycle(tmp, fallFrames));

        stateTime = 0f;
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
        System.out.println(left);
        checkUserInput();

        //////////////
        if ((y >= 272 && y <= 272.5) && (x >= 110 && x <= 464) ||
                (y >= 208 && y <= 208.5) && (x >= 495 && x <= 848) ||
                (y >= 272 && y <= 272.5) && (x >= 879 && x <= 1232) ||
                (y >= 464 && y <= 464.5) && (x >= 560 && x <= 785) ||
                (y >= 528  && y <= 528.5) && (x >= 1008 && x <= 1232) ||
                (y >= 528  && y <= 528.5) && (x >= 48 && x <= 272))
            isGrounded = true;
    }

    @Override
    public void render(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        if (isFalling)
            currentFrame = fallingAnimation.getKeyFrame(stateTime, true);
        if (!isGrounded && !isFalling)
            currentFrame = jumpingAnimation.getKeyFrame(stateTime, true);
        if (velX != 0 && isGrounded)
            currentFrame = runningAnimation.getKeyFrame(stateTime, true);
        if(!currentFrame.isFlipX() && left)
            currentFrame.flip(true, false);
        if(currentFrame.isFlipX() && !left)
            currentFrame.flip(true, false);

        batch.begin();
        batch.draw(currentFrame, (body.getPosition().x * PPM) - 64, (body.getPosition().y * PPM) - 16);
        batch.end();
        //
    }
    private void checkUserInput(){
        velX = 0;
        if(!isGrounded && body.getLinearVelocity().y <= 0)
            isFalling = true;
        if(Gdx.input.isKeyPressed(Input.Keys.D)){
            velX = 1;
            left = false;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            velX = -1;
            left = true;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.W) && isGrounded){
            float force = body.getMass() * 30;
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            body.applyLinearImpulse(new Vector2(0, force), body.getPosition(), true);
            isGrounded = false;
        }

        body.setLinearVelocity(velX * speed, body.getLinearVelocity().y < 30 ?
                body.getLinearVelocity().y : 30);
    }

}
