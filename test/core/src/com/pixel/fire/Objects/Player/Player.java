package com.pixel.fire.Objects.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import static com.pixel.fire.Helper.Constants.PPM;

public class Player extends GameEntity{
    Texture playerTexture = new Texture("sprites/Amogus.png");
    private int jumpCounter;

    public Player(float width, float height, Body body) {
        super(width, height, body); //super - parent class
        this.speed = 8f;
        this.jumpCounter = 0;
    }

    @Override
    public void update() {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        checkUserInput();
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(playerTexture, (body.getPosition().x * PPM) - 24, (body.getPosition().y * PPM) - 24);
    }

    private void checkUserInput(){
        velX = 0;
        if(Gdx.input.isKeyPressed(Input.Keys.D))
            velX = 1;
        if(Gdx.input.isKeyPressed(Input.Keys.A))
            velX = -1;

        if(Gdx.input.isKeyJustPressed(Input.Keys.W) && jumpCounter < 2){
            float force = body.getMass() * 18;
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            body.applyLinearImpulse(new Vector2(0, force), body.getPosition(), true);
            jumpCounter++;
        }
        //reset jump
        if(body.getLinearVelocity().y == 0)
            jumpCounter = 0;
        else if (body.getLinearVelocity().y <= 3 && body.getLinearVelocity().y >= -3 && jumpCounter != 1)
            jumpCounter = 2;

        body.setLinearVelocity(velX * speed, body.getLinearVelocity().y < 25 ?
                body.getLinearVelocity().y : 25);
    }
}
