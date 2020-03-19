package com.dookin.managers;

import com.dookin.MindGame;
import com.dookin.states.GameState;
import com.dookin.states.MusicState;
import com.dookin.states.PlayState;
import com.dookin.states.SplashState;
import com.dookin.states.WaterState;

import java.util.Stack;

/**
 * Created by David on 3/23/2018.
 */

public class GameStateManager {
    private final MindGame app;
    private Stack<GameState> states;

    public enum state {
        SPLASH,
        GAME,
        MUSIC,
        WATER,
    }
    public GameStateManager(final MindGame app) {
        this.app = app;
        this.states = new Stack<GameState>();
        this.setState(state.SPLASH);
    }
    public MindGame application() {
        return app;
    }
    public void update(float delta) {
        states.peek().update(delta);
    }
    public void render() {
        states.peek().render();
    }
    public void dispose() {
        for(GameState gs: states) {
            gs.dispose();
        }
        states.clear();
    }
    public void resize(int w, int h) {

        states.peek().resize(w, h);
    }
    public void setState(state state) {
        if (states.size() >= 1) {
            states.pop().dispose();
        }
        states.push(getState(state));

    }
    private GameState getState(state state) {
        switch(state) {

            case SPLASH: {
                return new SplashState(this);
            }

            case GAME: return new PlayState(this);
            case MUSIC: return new MusicState(this);
            case WATER: return new WaterState(this);
        }
        return null;
    }
}
