package com.dookin.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dookin.MindGame;
import com.dookin.managers.GameStateManager;

/**
 * Created by David on 3/23/2018.
 */

public abstract class GameState {
    protected GameStateManager gsm;
    protected MindGame app;
    protected OrthographicCamera camera;
    protected SpriteBatch batch;

    protected GameState(GameStateManager gsm) {
        this.gsm = gsm;
        this.app = gsm.application();
        batch = app.getBatch();
        camera = app.getCamera();

    }

    public abstract void resize(int w,int h);
    public abstract void update(float delta);
    public abstract void render();
    public abstract void dispose();

}
