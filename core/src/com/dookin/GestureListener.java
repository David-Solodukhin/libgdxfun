package com.dookin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;

/**
 * Created by David on 3/21/2018.
 */

public class GestureListener implements InputProcessor {
    private World world;
    private MouseJoint mousej;
    //private QueryCallback mouseCallBack;
    private Camera camera;
    private Vector3 tmp = new Vector3();
    private Vector2 tmp2 = new Vector2();
    private MouseJointDef mousejd;
    private Body player;


    public GestureListener(World world, MouseJoint mousej, MouseJointDef mousejd, Camera camera, Body player) {
        this.world = world;
        this.mousej = mousej;
        this.mousejd = mousejd;
        this.camera = camera;
        this.player = player;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.W) {

            //impulse of 100 in direction of motion
            player.applyLinearImpulse(new Vector2(10 * MathUtils.cos(player.getAngle()),  10 * MathUtils.sin(player.getAngle())), player.getWorldCenter(), true);
        }
        if (keycode == Input.Keys.S) {


            player.applyLinearImpulse(new Vector2(-10 * MathUtils.cos(player.getAngle()),  -10 * MathUtils.sin(player.getAngle())), player.getWorldCenter(), true);
        }
        if (keycode == Input.Keys.A) {

            player.applyTorque(10, true);
            //player.applyLinearImpulse(new Vector2(-10 * MathUtils.cos(player.getAngle()),  -10 * MathUtils.sin(player.getAngle())), player.getWorldCenter(), true);
        }
        if (keycode == Input.Keys.D) {
            player.applyTorque(-10, true);
            //player.applyLinearImpulse(new Vector2(10 * MathUtils.cos(player.getAngle()),  10 * MathUtils.sin(player.getAngle())), player.getWorldCenter(), true);
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        camera.unproject(tmp.set(screenX, screenY, 0));
        //System.out.println("world space coords: " + test.x/32.0f + " " + test.y/32.0f);
        //tmp contains world coords and so does currentouched
        tmp.set(Utils.p2m(tmp.x), Utils.p2m(tmp.y), 0);
        world.QueryAABB(mouseCallBack, tmp.x, tmp.y, tmp.x, tmp.y);

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (mousej == null) {
            return false;
        }
        world.destroyJoint(mousej);
        mousej = null;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (mousej == null) {
            return false;
        }
        camera.unproject(tmp.set(screenX, screenY, 0));
        tmp.set(Utils.p2m(tmp.x), Utils.p2m(tmp.y),0);
        tmp2.set(tmp.x, tmp.y);
        mousej.setTarget(tmp2);
       //System.out.println("move to here drag: "+ Utils.p2m(tmp.x)+" "+ Utils.p2m(tmp.y));
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }


    /*CALLBACKS */

    private QueryCallback mouseCallBack = new QueryCallback() {
        @Override
        public boolean reportFixture(Fixture fixture) {
            camera.unproject(tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            tmp.set(Utils.p2m(tmp.x), Utils.p2m(tmp.y), 0);



            if (!fixture.testPoint(tmp.x, tmp.y)) {
                return true; //if this is not the fixture we touched, get the next one
            }
            mousejd.bodyB = fixture.getBody();

            mousejd.target.set(tmp.x, tmp.y);
            mousej = (MouseJoint)world.createJoint(mousejd);

            return true;
        }
    };
}
