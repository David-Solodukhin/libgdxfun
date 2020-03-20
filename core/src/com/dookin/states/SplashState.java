package com.dookin.states;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.dookin.managers.GameStateManager;

/**
 * Created by David on 3/23/2018.
 */

public class SplashState extends GameState{
    SpriteBatch batch;
    Texture img;
    private OrthographicCamera camera;
    ShapeRenderer sr;

    public SplashState(GameStateManager gsm) {
        super(gsm);
        camera = new OrthographicCamera();
        camera.setToOrtho(false,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sr = new ShapeRenderer();
        sr.setAutoShapeType(true);
        sr.setProjectionMatrix(camera.combined);
    }

    @Override
    public void resize(int w, int h) {
        //System.out.println(w + " " + h);
        //System.out.println(Gdx.graphics.getWidth() + " " + Gdx.graphics.getHeight());
        //System.out.println("---------"); //how do the chain of resizes work???   first MindGame class detects resize, then for some reason, all states resize w/2 h/2??

        camera.setToOrtho(false,w, h);

        camera.update();
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            gsm.setState(GameStateManager.state.GAME);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            gsm.setState(GameStateManager.state.MUSIC);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            gsm.setState(GameStateManager.state.WATER);
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sr.setColor(Color.BLACK);
        sr.begin();
        //if i set the projection matrix to camera.combined, does this mean that world coords tell where to draw stuff and that i need to convert screen coords to world coords in order to draw correctly????
        //what are world coordinates, when setToOrtho is called shouldn't world coords stay constant and only projection change?

        /*
        Gdx.input.getX,Y returns screen coordinates with the screen coordinate system having 0,0 in the top left and y+ going down. This is expected input into camera.unproject
        If camera.setToOrtho(screenWidth, screenHeight) is set, then the camera's 'world' position is centered at the center of the screen with 0,0 camera position being bottom left.
        */
        sr.setProjectionMatrix(camera.combined); //if camera changes, need to change sr projection matrix accordingly!
        for (int i = 0; i < GameStateManager.state.values().length; i++) {
            sr.box((i * 150),0, 0,100,100,1 );
            if (Gdx.input.isTouched()) {
                Vector3 coords = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(),0));
                //System.out.println(coords);
                if (coords.x > (i * 150) && coords.x < ((i * 150) + 100) && coords.y > (0) && coords.y < 100) {
                    System.out.println("touched square: " + i);
                    gsm.setState(GameStateManager.state.values()[i]);
                }
            }



        }
        sr.end();

    }

    @Override
    public void dispose() {

    }
}
