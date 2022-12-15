package com.pixel.fire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import static com.pixel.fire.Helper.Constants.PPM;

public class Bullet
{
    //ANIMATION
    private TextureRegion currentFrame;
    Animation<TextureRegion> bulletAnimation;
    float stateTime;
    private static final int SPEED = 900;
    private static Texture texture;
    private static final Array<Rectangle> objects = new Array<>();
    private Rectangle object = null;
    private float x;
    private final float y;
    private final boolean isLeft;
    private boolean collision = false;
    public boolean remove = false;

    public Bullet (Vector2 position, boolean isLeft)
    {
        this.isLeft = isLeft;
        if (isLeft) this.x = position.x * PPM - 35;
        else this.x = position.x * PPM + 5;
        this.y = position.y * PPM;

        if (texture == null)
        {
            texture = new Texture("Sprites/bullet.png");
        }

        for (int i = 0; i < objects.size; i++)
        {
            if (isLeft && objects.get(i).contains(objects.get(i).x,y) && x > objects.get(i).x)
            {
                if (object == null)
                {
                    object = objects.get(i);
                }
                else if (objects.get(i).x > object.x)
                {
                    object = objects.get(i);
                }
                collision = true;
            }
            if (!isLeft && objects.get(i).contains(objects.get(i).x,y) && x < objects.get(i).x)
            {
                if (object == null)
                {
                    object = objects.get(i);
                }
                else if (objects.get(i).x < object.x)
                {
                    object = objects.get(i);
                }
                collision = true;
            }
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
            bulletAnimation = new Animation<>(0.03f, bulletFrames);
        }
        stateTime = 0f;
        currentFrame = bulletAnimation.getKeyFrame(stateTime, false);
    }

    public void update (float deltaTime)
    {
        if (collision)
        {
            checkCollision();
        }

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

    public static void setObjects(Array<PolygonMapObject> polygons)
    {
        for (int i = 0; i < polygons.size; i++)
        {
            objects.add(polygons.get(i).getPolygon().getBoundingRectangle());
        }
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    private void checkCollision()
    {
        if (object.contains(x,y))
        {
            remove = true;
        }
    }
}
