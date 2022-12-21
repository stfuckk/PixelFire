package com.pixel.fire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.pixel.fire.MySound;

public class SoundManager
{
    private static final Array<MySound> sounds = new Array<MySound>();
    public static float volume = 1.0f;

    public static MySound get(String name)
    {
        for (MySound s : sounds)
        {
            if (s.getName().equals(name))
            {
                return s;
            }
        }
        return null;
    }
    public static void add(String name, String path)
    {
        sounds.add(new MySound(name, Gdx.audio.newSound(Gdx.files.internal(path))));
    }

    public static void updateVolume()
    {
        for (MySound s : sounds)
        {
            s.setVolume(volume);
        }
    }
}