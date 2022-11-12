package com.pixel.fire.Objects.Player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

import static com.pixel.fire.Helper.Constants.PPM;

public class Player extends GameEntity{
    Texture playerTexture = new Texture("sprites/Amogus.png");

    public Player(float width, float height, Body body) {
        super(width, height, body); //super - parent class
        this.speed = 4f;
    }

    @Override
    public void update() {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(playerTexture, (body.getPosition().x * PPM) - 24, (body.getPosition().y * PPM) - 24);
    }
}
