package com.pixel.fire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import static com.pixel.fire.Helper.Constants.PPM;

public class Bullet
{
    private static final int SPEED = 1000;
    private static Texture texture;
    private float x;
    private final float y;
    private boolean isLeft;
    public boolean remove = false;

    //ANIMATION
    private TextureRegion currentFrame;
    Animation<TextureRegion> bulletAnimation;
    float stateTime;


    public Bullet (Vector2 position, boolean isLeft)
    {
        this.isLeft = isLeft;
        if (isLeft) this.x = position.x * PPM - 35;
        else this.x = position.x * PPM + 20;
        this.y = position.y * PPM;

        if (texture == null)
        {
            texture = new Texture("Sprites/bullet.png");
        }

        //ANIMATION
        TextureRegion[][] tmp;
        tmp = TextureRegion.split(texture,
                texture.getWidth() / 5,
                texture.getHeight() / 1);

        TextureRegion[] bulletFrames = new TextureRegion[5];
        int index = 0;
            for (int j = 0; j < 5; j++){
                bulletFrames[index++] = tmp[0][j];
                bulletAnimation = new Animation<TextureRegion>(0.025f, bulletFrames);
            }
        stateTime = 0f;
        currentFrame = bulletAnimation.getKeyFrame(stateTime, true);
        //
    }

    public void update(float deltaTime)
    {
        if (isLeft) x -= SPEED * deltaTime;
        else x += SPEED * deltaTime;
        if (x > Gdx.graphics.getWidth() * PPM / 12)
        {
            remove = true;
        }
    }

    public void render (SpriteBatch batch)
    {
        stateTime += Gdx.graphics.getDeltaTime();
        currentFrame = bulletAnimation.getKeyFrame(stateTime, false);
        batch.draw(currentFrame, x, y-10);
    }
}
