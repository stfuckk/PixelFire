package com.pixel.fire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Particles
{
    private ParticleEffect particle;
    private SpriteBatch batch;

    public Particles(SpriteBatch spriteBatch, String filePath,String imagesDir)
    {
        batch = spriteBatch;
        particle = new ParticleEffect();
        particle.load(Gdx.files.internal(filePath), Gdx.files.internal(imagesDir));
    }

    public void start(float x, float y)
    {
        particle.getEmitters().first().setPosition(x,y);
        particle.start();
    }

    public void render()
    {
        particle.draw(batch);
    }

    public void update(float delta)
    {
        particle.update(delta);
    }

    /*
    public ParticleEffect getParticle()
    {
        return particle;
    }


    public boolean isComplete()
    {
        return particle.isComplete();
    }

    public void reset()
    {
        particle.reset();
    }
     */


}
