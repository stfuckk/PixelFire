package com.pixel.fire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import static com.pixel.fire.Helper.Constants.PPM;

public class Bullet
{
    private static final int SPEED = 300;
    private static Texture texture;
    private static Rectangle[] objects = new Rectangle[15];
    private float x;
    private final float y;
    private final boolean isLeft;
    private boolean collision = false;
    private int index;
    public boolean remove = false;

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

        for (int i = 0; i < objects.length; i++)
        {
            if (objects[i].contains(objects[i].x,y))
            {
                System.out.println("dababy");
                index = i;
                collision = true;
                break;
            }
        }
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
        batch.draw(texture, x, y);
    }

    public static void setObjects(PolygonMapObject[] polygons)
    {
        for (int i = 0; i < polygons.length; i++)
        {
            objects[i] = polygons[i].getPolygon().getBoundingRectangle();
        }
    }

    private void checkCollision()
    {
        if (objects[index].contains(x,y))
        {
            remove = true;
        }
    }
}
