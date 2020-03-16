package com.dookin;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by David on 3/19/2018.
 */

public class CollisionListener implements ContactListener {
    World world;

    public CollisionListener(World world) {
        this.world = world;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        Body ba = fa.getBody();
        Body bb = fb.getBody();
        if (fa == null || fb == null) {System.out.println("what"); return;}
        //if (fa.getBody().getUserData() == null || fb.getBody().getUserData() == null) {return;}

//in order to avoid an asteroid breaking up from collisions with its own non-threaded parts,we need to use collision filters?
        //((float[])fa.getUserData())[0]=1;
        //((float[])fb.getUserData())[0]=1;
        if (ba.getUserData() instanceof Asteroid && bb.getUserData() instanceof Asteroid) {
            if (((Asteroid) ba.getUserData()) != ((Asteroid) bb.getUserData() )) {
                //if the 2 collided bodies are not part of the same asteroid:
                //thing is, a broken off piece of the original asteroid should be able to break the original
                ((Asteroid) ba.getUserData()).separateChunk(ba);
                ((Asteroid) bb.getUserData()).separateChunk(bb);
            } else {
                //((float[])fa.getUserData())[0]=0;
                //((float[])fb.getUserData())[0]=0;
            }
        }//if only 1 bod is asteroid
        else if (ba.getUserData() instanceof Asteroid) {
            //((Asteroid) ba.getUserData()).separate();
           // System.out.println("shit collidedp1");
            ((Asteroid) ba.getUserData()).separateChunk(ba);
        }
        else if (bb.getUserData() instanceof Asteroid) {
            //((Asteroid) bb.getUserData()).separate();
            //System.out.println("shit collidedp2");
            ((Asteroid) bb.getUserData()).separateChunk(bb);
        }


        //idea: here if normal force from planet(contact force) is < than certain value, turn off the planet gravity force for that object.
        //in nut callbackquery, if instanceof whatever,(i should really make an all encompassing gameobject class :/) if forcevar is false, no force
        //make update method for asteroid, if difference in position from planet is > certain noticeable amount, reactivate force for like 2 steps?

    }

    @Override
    public void endContact(Contact contact) {
        //((float[])contact.getFixtureA().getUserData())[0]=0;
        //((float[])contact.getFixtureA().getUserData())[1]=0;
        //((float[])contact.getFixtureB().getUserData())[0]=0;
        //((float[])contact.getFixtureA().getUserData())[0]=0;

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
