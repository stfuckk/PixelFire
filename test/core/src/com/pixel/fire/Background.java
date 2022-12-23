package com.pixel.fire;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Background {
    public static Texture bg_main_texture;
    public static Texture bg_jijka_texture;
    public static Sprite bg_main;
    public static Sprite bg_jijka;

    public Background()
    {
        bg_main_texture = new Texture("background/main.png");
        bg_jijka_texture = new Texture("background/jijka.png");
        bg_main = new Sprite(bg_main_texture);
        bg_jijka = new Sprite(bg_jijka_texture);
    }

    public void render(SpriteBatch batch) {
        batch.begin();
        batch.draw(bg_main, bg_main.getX() - 600, bg_main.getY() - 100, 2304, 1296);
        batch.end();
    }
}
