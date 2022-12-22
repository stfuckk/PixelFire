package com.pixel.fire;

import com.badlogic.gdx.audio.Sound;

public class MySound
{
    private String name;
    private Sound sound;
    private long id;

    public MySound(String name, Sound sound)
    {
        this.name = name;
        this.sound = sound;
    }

    public String getName()
    {
        return name;
    }

    public MySound play(float volume)
    {
        id = sound.play(volume);
        return this;
    }

    public MySound stop()
    {
        sound.stop();
        return this;
    }

    public MySound loop(boolean loop)
    {
        sound.setLooping(id, loop);
        return this;
    }

    public MySound setVolume(float volume)
    {
        sound.setVolume(id, volume);
        return this;
    }
}
