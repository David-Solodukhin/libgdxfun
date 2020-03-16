package com.dookin;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dookin.managers.GameStateManager;

public class MindGame extends ApplicationAdapter {
	private OrthographicCamera camera; //2d camera
	private GameStateManager gsm;
	SpriteBatch batch;

	@Override
	public void create () {
		gsm = new GameStateManager(this);
		//gsm.setState(GameStateManager.state.SPLASH);


		//myLight.attachToBody(player);
	}



	@Override
	public void render () {
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render();



	}

	@Override
	public void resize(int width, int height) {
		gsm.resize((int)(width/2f),(int) (height/2f));
		 //????
	}
	@Override
	public void dispose () {
		gsm.dispose();

	}




	public void update(float delta) {
		gsm.update(delta);

	}











	public OrthographicCamera getCamera() {
		return camera;
	}
	public SpriteBatch getBatch() {
		return batch;
	}


}
