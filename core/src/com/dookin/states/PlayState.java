package com.dookin.states;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.Array;
import com.dookin.Asteroid;
import com.dookin.CollisionListener;
import com.dookin.GestureListener;
import com.dookin.Utils;
import com.dookin.managers.GameStateManager;

import box2dLight.PointLight;
import box2dLight.RayHandler;


public class PlayState extends GameState{


    SpriteBatch batch;
    Texture img;
     private OrthographicCamera camera; //2d camera
    //private GameStateManager gsm;
    private Box2DDebugRenderer b2dr;
    private World world; //box2d world
    private Body player; //box2d body
    private Vector2 gravity;
    private Body platform;
    private RayHandler rayHandler;
    private PointLight myLight;
    private ShapeRenderer srend = new ShapeRenderer();
    private MouseJointDef mousejd;
    private MouseJoint mousej;
    public static Array<Joint> destroyedJoints = new Array<Joint>();
    public Asteroid testAsteroid, testAsteroid2;
    PolygonSpriteBatch polyBatch = new PolygonSpriteBatch();
    private float srcX = 0;
    private Sprite playerSprite;
    private Sprite planetSprite;
    private Body planet;
    private Body sun;
    private Matrix4 cmAdjusted = new Matrix4();
    private PointLight planetLight;
    private Sprite sunSprite;
    Texture textureSolid;
    Texture textureTree;
    private Sprite ts;

    private PolygonRegion region;

    public PlayState(GameStateManager gsm) {
        super(gsm);
        create();





    }

    private void create() {

        batch = new SpriteBatch();
        polyBatch = new PolygonSpriteBatch(); // To assign at the beginning
        textureSolid = new Texture("tile.jpg");
        textureTree = new Texture("tree.png");

        textureSolid.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        img = new Texture("bg3.jpg");
        playerSprite = new Sprite(new Texture("tnt.png"));
        planetSprite = new Sprite(new Texture("bplanet.png"));
        sunSprite = new Sprite(new Texture("moon.png"));
        img.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        camera = new OrthographicCamera();
        camera.setToOrtho(false,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		/*  box2d setup */
        gravity = new Vector2(0,0);
        world = new World(gravity, false);
        b2dr = new Box2DDebugRenderer();
        player = createPlayer(0,0);
        //platform  = createPlatform();
        srend.setAutoShapeType(true);
        planet = createPlanet(stob2d(new Vector3(0,Gdx.graphics.getHeight(),0)).x,stob2d(new Vector3(0,Gdx.graphics.getHeight(),0)).y ,10f);
        //createPlanet(stob2d(new Vector3(0,Gdx.graphics.getHeight(),0)).x,stob2d(new Vector3(0,Gdx.graphics.getHeight(),0)).y ,10f);
        sun = createPlanet(20,20, 6f);
        sun.setActive(false);
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(.4f);
        myLight = new PointLight(rayHandler, 300, Color.RED, 50/Utils.PPM, 0, 0);
        planetLight = new PointLight(rayHandler, 100, Color.CYAN, 35f, sun.getPosition().x - 2f, sun.getPosition().y);
        planetLight.setXray(true);
        //rayHandler.setBlurNum(2);
        planetLight.setSoft(true);
        //planetLight.setSoft(true);
        //planetLight.setSoftnessLength(90.0f);
        myLight.setSoftnessLength(0.0f);
        //myLight.setXray(true);
        //rayHandler.setCombinedMatrix(camera.combined.scl(Utils.PPM));
        myLight.attachToBody(player);
        b2dr.setDrawBodies(false);
        cameraUpdate();
        //since camera always follows player, we're drawing the thing on top of the player since player is always in the center of camera
        testAsteroid = new Asteroid(stob2d(new Vector3(20, 20, 0)), world, 1,5, null, rayHandler);
        testAsteroid2 = new Asteroid(new Vector3(20,20,0), world, 1,5, null, rayHandler);

        createPlayer(-20,-20);
        createTree(5, (float)Math.sqrt(75),0);
        //Sprite TreeSprite = new Sprite(textureTree);
        //TreeSprite.setOriginCenter();
        //TreeSprite.setOrigin();
        //mousejoint
        mousejd = new MouseJointDef();
        mousejd.bodyA = world.createBody(new BodyDef()); //not actually used
        mousejd.bodyB = player; //one that is actually moving around
        mousejd.collideConnected = true;
        mousejd.maxForce = 100;
        //SETUP PROCESSORS AND LISTENERS//
        world.setContactListener(new CollisionListener(world));
        Gdx.input.setInputProcessor(new GestureListener(world, mousej, mousejd, camera, player));
        region = new PolygonRegion(new TextureRegion(textureSolid), new float[] {0, 0, Utils.m2p(10), Utils.m2p(10), (Utils.m2p(10)), 0}, new short[] {0, 1,2});
        polyBatch = new PolygonSpriteBatch();




    }

    @Override
    public void resize(int w, int h) {
        if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
            System.out.println(Gdx.graphics.getWidth() + " " + Gdx.graphics.getHeight());
            camera.setToOrtho(false,Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
            return;
        }
        System.out.println(Gdx.graphics.getWidth() + " " + Gdx.graphics.getHeight());
        camera.setToOrtho(false,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void update(float delta) {



//each planet should have an update method with a query AABB for its shit
        float r = ((CircleShape)planet.getFixtureList().get(0).getShape()).getRadius();


        //world.QueryAABB(nut, planet.getPosition().x-(r * 2), planet.getPosition().y-(r * 2), planet.getPosition().x+(r * 2), planet.getPosition().y+(r * 2));

        Array<Fixture> fixtures = new Array<Fixture>();
        world.getFixtures(fixtures);
        for (Fixture fix: fixtures) {
            applyGravForceToCenter(fix);
        }
        world.step(1/60f, 10, 6);
        //planet.setTransform(0,0, 0);
        cameraUpdate(); //camera's orthographic x and y world coords in pixels are set to player's coords in pixels
        //camera.combined.scl(1f/Utils.PPM);

        //RAYHANDLER UPDATE GARBAGE: literally magic, i don't know why scaling works
        //camera.combined.scl(Utils.PPM); // i don't know why we scale PPM instead of 1/PPM
        //rayHandler.setCombinedMatrix(camera.combined.cpy().scl(Utils.PPM));
        //rayHandler.setCombinedMatrix(camera);
        //rayHandler.update();
        //camera.combined.scl(1f/Utils.PPM);
        //cameraUpdate();

        objectCleanup();
        //--------------------------

        //combined matrix contains the view matrix(where shit is in your 3d world)
        //as well as the projection matrix(how do we map the shit in the 3d world to a 2d plane(the camera)
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        //update(Gdx.graphics.getDeltaTime()); //UPDATE BY DEFAULT IS CALLED BY THE APPLICATION BEFORE RENDER NO MATTER WHAT

        batch.setProjectionMatrix(camera.combined); //
        batch.begin();

        batch.draw(img, Utils.m2p(-100),Utils.m2p(-100), (int)srcX, 0, img.getWidth() * 5, img.getHeight() * 5);

        //testAsteroid.render(batch);

        Vector2 lvec = new Vector2();
        Vector2 rvec = new Vector2();
        ((PolygonShape)player.getFixtureList().get(0).getShape()).getVertex(0,lvec );
        ((PolygonShape)player.getFixtureList().get(0).getShape()).getVertex(1,rvec );
        float sz = lvec.sub(rvec).len();

        playerSprite.setSize(Utils.m2p(sz),Utils.m2p(sz));
        //Vector3 test = camera.project(new Vector3((player.getWorldCenter().x), (player.getWorldCenter().y), 0));
        playerSprite.setCenter(Utils.m2p(player.getWorldCenter().x),Utils.m2p(player.getWorldCenter().y));
        playerSprite.setOriginCenter();


        playerSprite.setRotation(player.getAngle()* MathUtils.radDeg - 90);
        //sprite batch is drawn in world coords with camera.combined projection matrix?

        planetSprite.setCenter(Utils.m2p(planet.getPosition().x), Utils.m2p(planet.getPosition().y));
        planetSprite.setSize(Utils.m2p(((CircleShape)planet.getFixtureList().get(0).getShape()).getRadius() * 2.1f), Utils.m2p(((CircleShape)planet.getFixtureList().get(0).getShape()).getRadius() * 2.1f));



        sunSprite.setCenter(Utils.m2p(sun.getPosition().x), Utils.m2p(sun.getPosition().y));
        sunSprite.setSize(Utils.m2p(((CircleShape)sun.getFixtureList().get(0).getShape()).getRadius() * 2), Utils.m2p(((CircleShape)sun.getFixtureList().get(0).getShape()).getRadius() * 2));



        sunSprite.draw(batch);
        batch.end();
        PolygonSprite sp = new PolygonSprite(region);
        //sp.setOrigin(Utils.m2p(player.getPosition().x), Utils.m2p(player.getPosition().y));
        //sp.setBounds(10,10,10,10);
        sp.setPosition(Utils.m2p(player.getPosition().x), Utils.m2p(player.getPosition().y));
        sp.setOrigin(0,0);
        sp.setRotation(player.getAngle()* MathUtils.radDeg - 90);
        //sp.setSize(1f,100f);
        polyBatch.setProjectionMatrix(camera.combined);
        polyBatch.setColor(Color.CYAN);
        polyBatch.begin();
        //polyBatch.draw(region,Utils.m2p(player.getWorldCenter().x),Utils.m2p(player.getWorldCenter().y), 500,500);
        //sp.draw(polyBatch);

        testAsteroid.renderPolygon(polyBatch);
        testAsteroid2.renderPolygon(polyBatch);
        polyBatch.end();
        batch.begin();
        playerSprite.draw(batch);
        planetSprite.draw(batch);
        ts.draw(batch);

        float r = ((CircleShape)planet.getFixtureList().get(0).getShape()).getRadius();

        batch.end();


        b2dr.render(world, camera.combined.scl(Utils.PPM)); //scaled by ppm since camera is in pixels and 1 meter is 32 pixels so meters -> pixels *=32 //camera.combined matrix is scaled by ppm



        PolygonSprite poly;


// Creating the color filling (but textures would work the same way)
        //Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        //pix.setColor(0xDEADBEFF); // DE is red, AD is green and BE is blue.
        //pix.fill();




        //srend.setProjectionMatrix(batch.getProjectionMatrix());
        //srend.begin(ShapeRenderer.ShapeType.Line);
        //srend.polygon(testAsteroid.vertices);

        //srend.end();


        rayHandler.setCombinedMatrix(camera.combined); //camera combined was already scaled. //not really deprecated since calling setCombinedMatrix(camera) is essentially the same but for some reason breaks
        rayHandler.updateAndRender();

        updateInput(); //unfortunately because of precedence
        System.out.println(stob2d(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)));

    }

    @Override
    public void dispose() {
        rayHandler.dispose();
        batch.dispose();
        img.dispose();
        world.dispose();
        b2dr.dispose();
    }

    private void objectCleanup() {
        for(Joint a: destroyedJoints) {
            world.destroyJoint(a);
            a = null;
        }
        destroyedJoints.clear();


    }
    private void updateInput() {
        /*this input is only for changing states or gamewide info*/
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            gsm.setState(GameStateManager.state.GAME);
        }
    }

    public Body createPlayer(float x, float y) {
        Body pBody;

        //STEP 1, BODY DEFINITION: position, friction, misc properties
        BodyDef def = new BodyDef(); //body definition, describes physical properties body will have.
        //friction, type of body, etc
        def.type = BodyDef.BodyType.DynamicBody;
        def.angle = 90 * MathUtils.degRad;
        def.position.set(x,y); //b2d world coords
        //def.fixedRotation = true; //no rotations
        pBody = world.createBody(def);//actually create body.
        //STEP 2, CREATE SHAPE
        PolygonShape shape = new PolygonShape();
        //box 2d works in meters. pixels per meter is important
        //shape.set(new float[]{0,0,1,0,0.5f,1});
        shape.setAsBox(Utils.p2m(32.0f/2.0f), Utils.p2m(32.0f/2.0f)); //box2d takes height and width from center. so actual width is 16 * 2 = 32
        //STEP 3 ASSIGN FIXTURE TO BODY
        pBody.createFixture(shape, 3.0f);
        //pBody.getFixtureList().get(0).setRestitution(0); //the fixture is attached to the shape and gives it a density
        //pBody.getFixtureList().get(0).setFriction(0);

        //cleanup
        shape.dispose();

        return pBody;
    }

    private Vector3 stob2d(Vector3 s) {
        //camera.update();
        camera.unproject(s);
        return s.set(Utils.p2m(s.x), Utils.p2m(s.y), 0);
    }

    private Body createPlanet(float x, float y, float radius) {
        Body pBody;
        Body dBody;


        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        //def.angle = 90 * MathUtils.degRad;
        def.position.set(x,y);
        pBody = world.createBody(def);
        //PolygonShape shape = new PolygonShape();
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        pBody.createFixture(shape, 3.0f);
        //pBody.getFixtureList().get(0).setFriction(0.5f);
        //pBody.getFixtureList().get(0).setFriction(0);
        //pBody.getFixtureList().get(0).setRestitution(0);


        def.type = BodyDef.BodyType.DynamicBody;
        dBody = world.createBody(def);

        //PolygonShape shape = new PolygonShape();



        //cleanup
        shape.dispose();
        return pBody;

    }

    private Body createTree(float x, float y, float radius) {
        Body pBody;
        Body dBody;


        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        //def.angle = 90 * MathUtils.degRad;
        def.position.set(x,y);
        pBody = world.createBody(def);
        //PolygonShape shape = new PolygonShape();
        PolygonShape shape = new PolygonShape();

        shape.setAsBox(5,2);
        //x^2+y^2=r^2
        //30^2+10^2=100
        pBody.createFixture(shape, 3.0f);
        //180-(90+tan(len(y)/len(x));


        /*
        circle dy/dx = -x/y
        y=(-x/y) * x + b
        y = -x * x / y + b

        y= slope at the tangent pt * x + b
        pos.y = -pos.x * pos.x / pos.y + b
        pos.y + (pos.x * pos.x / pos.y) = b

        //len 1 = radius
        //len 2 = sqrt((pos.x - 0)^2 + (pos.y-b)^2)
        atan(len2/len1)

         */
        float b =  y + (x * x / y);

        float len1 = 10f; //radius of planet
        float len2 = (float)Math.sqrt((x * x) + (y-b) * (y-b));
        System.out.println(len2);
        float angle = MathUtils.atan2(len2, len1);
        System.out.println("angle!" + (angle * MathUtils.radDeg - 90) );

        pBody.setTransform(x,y,90 * MathUtils.degRad -  angle);
        //pBody.getFixtureList().get(0).setFriction(0.5f);
        //pBody.getFixtureList().get(0).setFriction(0);
        //pBody.getFixtureList().get(0).setRestitution(0);


        def.type = BodyDef.BodyType.DynamicBody;
        dBody = world.createBody(def);

        ts = new Sprite(textureTree);
        ts.setOriginCenter();

        //ts.setOrigin(0,0);
        System.out.println(pBody.getPosition());
        //ts.setBounds(Utils.m2p(pBody.getPosition().x), Utils.m2p(pBody.getPosition().y), Utils.m2p(10),Utils.m2p(10));
        //ts.setPosition(Utils.m2p(pBody.getPosition().x), Utils.m2p(pBody.getPosition().y));
        ts.setSize(Utils.m2p(8), Utils.m2p(8));

        ts.setCenter(Utils.m2p(3*MathUtils.cos(pBody.getAngle()) + pBody.getPosition().x), Utils.m2p(3*MathUtils.sin(pBody.getAngle()) + pBody.getPosition().y));
        //ts.setOrigin(0,0);
        ts.setOriginCenter();
        //ts.setSize(Utils.m2p(10),Utils.m2p(10));


        ts.setRotation(pBody.getAngle()* MathUtils.radDeg - 90);

        //PolygonShape shape = new PolygonShape();



        //cleanup
        shape.dispose();
        return pBody;

    }


    public void cameraUpdate() {
        Vector3 position = camera.position;
        position.x = Math.round(player.getPosition().x * Utils.PPM);
        position.y = Math.round(player.getPosition().y * Utils.PPM);
        camera.position.set(position);
        camera.update();
    }
    public Body createPlatform() {
        return null;
    }

    private Body dplanet;
    private QueryCallback nut = new QueryCallback() {
        @Override
        public boolean reportFixture(Fixture fixture) {
            if (fixture.getBody() == planet || fixture.getBody() == dplanet) {
                return false;
            }
            Vector2 plan2Deb = new Vector2();
            plan2Deb.set(fixture.getBody().getPosition().x - planet.getPosition().x, fixture.getBody().getPosition().y-planet.getPosition().y);
            plan2Deb.scl(-1f);

            float rad = ((CircleShape)planet.getFixtureList().get(0).getShape()).getRadius();
            float dist = Math.abs(plan2Deb.x) + Math.abs(plan2Deb.y);

            plan2Deb.scl((1f/dist)*rad/plan2Deb.len() * 34);
            Float flt = new Float(plan2Deb.x);
            if (flt.isNaN()) {
                System.out.println(fixture.getBody() == dplanet);
            }
            //System.out.println(plan2Deb); //static bodies do not have mass : /

            fixture.getBody().applyForceToCenter(plan2Deb, true);
            //fixture.getBody().applyLinearImpulse(plan2Deb, fixture.getBody().getLocalCenter(), true);

            return true;
        }
    };
    private void applyGravForceToCenter(Fixture fixture) {
        if (fixture.getBody() == planet || fixture.getBody() == dplanet) {
            return;
        }
        if (fixture.getBody().getPosition().x == 0.0f) {
            return;
        }
        Vector2 plan2Deb = new Vector2();
        plan2Deb.set(fixture.getBody().getPosition().x - planet.getPosition().x, fixture.getBody().getPosition().y-planet.getPosition().y);
        plan2Deb.scl(-1f);

        float rad = ((CircleShape)planet.getFixtureList().get(0).getShape()).getRadius();
        float dist = (float)Math.sqrt((plan2Deb.x * plan2Deb.x) + (plan2Deb.y * plan2Deb.y));
        plan2Deb.scl((1f/(dist * dist))*rad * fixture.getBody().getMass() * 10f);
        Float flt = new Float(plan2Deb.x);
        if (flt.isNaN()) {
            System.out.println(fixture.getBody() == dplanet);
        }

        Vector2 plan2DebMax = new Vector2();
        float mindist = rad + fixture.getShape().getRadius();
        plan2DebMax.set(mindist, 0);
        plan2DebMax.scl(-1);
        plan2DebMax.scl((1f/(mindist * mindist))*rad * fixture.getBody().getMass() * 10f);
        fixture.getBody().applyForceToCenter(plan2Deb, true);

        //if gravity vector is basically the max, just cut velocity of the fixture
        //System.out.println(fixture.getBody().getLinearVelocity().len()); //static bodies do not have mass : /
        /*if (fixture.getBody() != player)
        System.out.println(plan2Deb.len() + " " + plan2DebMax.len() + " " + fixture.getShape().getRadius());
        //if (fixture.getUserData() == null || ((boolean)fixture.getUserData() == false && fixture.getBody().getLinearVelocity().len() > 0.001f)) {
        if (Math.abs(plan2Deb.len()/plan2DebMax.len()) <.97f) {
        } else {
            //fixture.getBody().setAngularVelocity(0);
            fixture.getBody().setLinearVelocity(0,0);
        }
        */
        if (fixture.getBody().getAngularVelocity() < 0.5f) {
            fixture.getBody().setAngularVelocity(0);
        }
        //1 approach: store last force, if last force caused almost no change in velocity, don't calculate next force
        //another approach: the force is inversely proportional to how long the fixture is in contact with the planet.
        //if rotation and velocity not affected, stop adding force
       // }
        //fixture.getBody().applyLinearImpulse(plan2Deb, fixture.getBody().getLocalCenter(), true);
    }

}
