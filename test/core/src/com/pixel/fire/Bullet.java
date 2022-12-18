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
    // ANIMATION
    private TextureRegion currentFrame;
    Animation<TextureRegion> bulletAnimation;
    float stateTime;

    // BULLET PROPERTIES
    private static final Texture texture = new Texture("Sprites/bullet.png");
    private static final int SPEED = 900;
    private float x;
    private final float y;
    public boolean remove = false; // Bullet will be removed in gameScreen update() method
    public boolean invisible = false; // Bullet will be made invisible
    private final boolean isLeft; // Bullet flies to the left

    // COLLISION CHECK
    private static final Array<Rectangle> objects = new Array<>(); // Array of all map objects
    private Rectangle object = null; // Object that the bullet will collide with
    private boolean collision = false; // Will the bullet collide with any object

    // RENDER
    private final Particles destroyParticle; // Particle of bullet collision
    private final SpriteBatch batch; // Sprite batch that render sprites

    public Bullet (Vector2 position, boolean isLeft, SpriteBatch spriteBatch)
    {
        this.isLeft = isLeft;
        if (isLeft) this.x = position.x * PPM - 35;
        else this.x = position.x * PPM + 5;
        this.y = position.y * PPM;

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
                texture.getHeight());

        TextureRegion[] bulletFrames = new TextureRegion[5];
        int index = 0;
        for (int j = 0; j < 5; j++){
            bulletFrames[index++] = tmp[0][j];
            bulletAnimation = new Animation<>(0.03f, bulletFrames);
        }
        stateTime = 0f;
        currentFrame = bulletAnimation.getKeyFrame(stateTime, false);
        batch = spriteBatch;

        destroyParticle = new Particles(batch, "Sprites/bullet.p", "Sprites/images");
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

    public void render()
    {
        if (!invisible)
        {
            stateTime += Gdx.graphics.getDeltaTime();
            currentFrame = bulletAnimation.getKeyFrame(stateTime, false);
            batch.draw(currentFrame, x, y-10);
        }
        else
        {
            destroyParticle.update(Gdx.graphics.getDeltaTime());
            destroyParticle.render();
        }
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
            if (!invisible) this.startParticle();

            invisible = true;
        }
    }

    private void startParticle()
    {
        destroyParticle.start(x,y);
    }
}
