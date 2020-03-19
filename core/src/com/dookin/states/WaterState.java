package com.dookin.states;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
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
import com.badlogic.gdx.utils.TimeUtils;
import com.dookin.Asteroid;
import com.dookin.CollisionListener;
import com.dookin.GestureListener;
import com.dookin.Utils;
import com.dookin.managers.GameStateManager;

import java.util.HashMap;

import box2dLight.PointLight;
import box2dLight.RayHandler;


public class WaterState extends GameState{


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
    GlyphLayout layout; //dont do this every frame! Store it as member

    BitmapFont font;
    BitmapFont highlightFont;
    HashMap<Integer, String[]> words = new HashMap<>();
    long time;
    private int currentLongest = 2;

    public WaterState(GameStateManager gsm) {
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
        playerSprite = new Sprite(new Texture("boat.png"));
        planetSprite = new Sprite(new Texture("bplanet.png"));
        sunSprite = new Sprite(new Texture("moon.png"));
        img.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        camera = new OrthographicCamera();
        camera.setToOrtho(false,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		/*  box2d setup */
        gravity = new Vector2(0,-9.8f);
        world = new World(gravity, false);
        b2dr = new Box2DDebugRenderer();
        player = createPlayer(0,0);
        //platform  = createPlatform();
        srend.setAutoShapeType(true);
        //planet = createPlanet(stob2d(new Vector3(0,Gdx.graphics.getHeight(),0)).x,stob2d(new Vector3(0,Gdx.graphics.getHeight(),0)).y ,10f);
        //createPlanet(stob2d(new Vector3(0,Gdx.graphics.getHeight(),0)).x,stob2d(new Vector3(0,Gdx.graphics.getHeight(),0)).y ,10f);
        sun = createPlanet(0,5, 6f);
        sun.setActive(false);
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(.4f);
        myLight = new PointLight(rayHandler, 100, Color.GREEN, 3.0f, 0, 0);
        planetLight = new PointLight(rayHandler, 100, Color.CYAN, 35f, sun.getPosition().x - 2f, sun.getPosition().y);
        planetLight.setXray(false);
        //rayHandler.setBlurNum(2);
        planetLight.setSoft(true);
        //planetLight.setSoft(true);
        //planetLight.setSoftnessLength(90.0f);
        myLight.setSoftnessLength(0.0f);
        //myLight.setXray(true);
        //rayHandler.setCombinedMatrix(camera.combined.scl(Utils.PPM));
        myLight.attachToBody(player);
        cameraUpdate();
        //since camera always follows player, we're drawing the thing on top of the player since player is always in the center of camera
        //testAsteroid = new Asteroid(stob2d(new Vector3(20, 20, 0)), world, 1,5, null, rayHandler);
        //testAsteroid2 = new Asteroid(new Vector3(20,20,0), world, 1,5, null, rayHandler);

        //createPlayer(-20,-20);
        //createTree(5, (float)Math.sqrt(75),0);
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
        Music music = Gdx.audio.newMusic(Gdx.files.internal("dont.mp3"));
        music.setLooping(true);
        //music.play();
        /* Text Stuff */
        font = new BitmapFont();
        highlightFont = new BitmapFont();
        highlightFont.setColor(Color.RED);

        font.setColor(Color.WHITE);
        time = TimeUtils.millis();
        layout = new GlyphLayout();
        words.put(0, new String[]{"tonight", "2"});
        //words.put(1, "tonight");
        words.put(2, new String[]{"i'm gonna have", "3"});
        words.put(3, new String[]{"myself", "5"});
        words.put(5, new String[]{"a really good time", "7"});
        words.put(7, new String[]{"i feel alive", "12"});
        words.put(12, new String[]{"and the world", "15"});
        words.put(15, new String[]{"i'll turn it", "16"});
        words.put(16, new String[]{"inside out", "19"});
        words.put(19, new String[]{"yeah", "20"});
        words.put(20, new String[]{"and floating around", "22"});
        words.put(22, new String[]{"in ecstasy", "25"});
        words.put(25, new String[]{"So don't", "26"});
        words.put(26, new String[]{"stop me", "27"});
        words.put(27, new String[]{"now", "29"});
        words.put(29, new String[]{"don't", "31"});
        words.put(31, new String[]{"stop me", "32"});
        words.put(32, new String[]{"cause i'm having", "33"});

        /* circle test */
        CircleShape circle = new CircleShape();
        circle.setRadius(0.3f);

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        for (int i = 0; i < 500; i++) {
            def.position.set(0,stob2d(new Vector3(i, Gdx.graphics.getHeight() - i,0)).y);
            Body circleBody;
            circleBody = world.createBody(def);
            circleBody.setSleepingAllowed(true);
            //circleBody.setBullet(true);
            circleBody.createFixture(circle, 1.0f);

            PointLight circleLight = new PointLight(rayHandler, 10, new Color(.3f,.25f, .71f,0.9f), 0.7f, 0, 0);
            circleLight.setXray(true);
            circleLight.setSoftnessLength(0.0f);
            circleLight.setSoft(true);
            circleLight.attachToBody(circleBody);
        }

        /*bottom */

        def.type = BodyDef.BodyType.StaticBody;



        /*tells us the scale ratio for screen scale to b2d scale: it's not just 1:PPM! */
        float screenToB2DScaleX = ((float)Gdx.graphics.getWidth()) / (camera.unproject(new Vector3(Gdx.graphics.getWidth(), 0, 0))).scl(1f/Utils.PPM).x;
        float screenToB2DScaleY = -1 * ((float)Gdx.graphics.getHeight()) / (camera.unproject(new Vector3(0, Gdx.graphics.getHeight(), 0))).scl(1f/Utils.PPM).y;
        System.out.println(screenToB2DScaleY);
        //scale might be faster than calling unproject all the time?



        cameraUpdate();
        //looks like unproject assumes the following coordinate system for the screen: far left: x = 0, far right, x=Gdx.graphics.getWidth(), bottom: y=Gdx.graphics.getHeight(), top y=0.
        // also looks like drawing with sprite batches operates at coordinate system similar to box2d and unlike what unproject assumes?
        def.position.set(0,stob2d(new Vector3(0, Gdx.graphics.getHeight(),0)).y);
        PolygonShape platformShape = new PolygonShape();
        platformShape.setAsBox(stob2d(new Vector3(Gdx.graphics.getWidth(),0,0)).x, 1);
        Body platformBody = world.createBody(def);
        platformBody.createFixture(platformShape, 3.0f);

        def.position.set(stob2d(new Vector3(0, 0,0)).x,0);
        platformShape.setAsBox(1,Gdx.graphics.getHeight() / screenToB2DScaleY);
        Body leftBody = world.createBody(def);
        leftBody.createFixture(platformShape, 3.0f);

        def.position.set(stob2d(new Vector3(Gdx.graphics.getWidth(), 0,0)).x, 0);
        platformShape.setAsBox(1,Gdx.graphics.getHeight() / screenToB2DScaleY);
        Body rightBody = world.createBody(def);
        rightBody.createFixture(platformShape, 3.0f);




        b2dr.setDrawAABBs(false);
        b2dr.setDrawContacts(false);
        b2dr.setDrawInactiveBodies(false);
        b2dr.setDrawJoints(true);
        b2dr.setDrawBodies(false);




        //words.put(4, "myself");


    }

    @Override
    public void resize(int w, int h) {
        if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
            System.out.println(Gdx.graphics.getWidth() + " " + Gdx.graphics.getHeight());
            camera.setToOrtho(false,Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
            return;
        }
        System.out.println("RESIZE " + Gdx.graphics.getWidth() + " " + Gdx.graphics.getHeight());
        camera.setToOrtho(false,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void update(float delta) {



//each planet should have an update method with a query AABB for its shit


        //world.QueryAABB(nut, planet.getPosition().x-(r * 2), planet.getPosition().y-(r * 2), planet.getPosition().x+(r * 2), planet.getPosition().y+(r * 2));


        world.step(1/60f, 10, 6);
        //planet.setTransform(0,0, 0);
        cameraUpdate(); //camera's orthographic x and y world coords in pixels are set to player's coords in pixels

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


        //playerSprite.setOriginCenter();
        playerSprite.setSize(Utils.m2p(sz),Utils.m2p(sz));
        //Vector3 test = camera.project(new Vector3((player.getWorldCenter().x), (player.getWorldCenter().y), 0));

        playerSprite.setCenter(Utils.m2p(player.getWorldCenter().x),Utils.m2p(player.getWorldCenter().y+1f));
        playerSprite.setOrigin(playerSprite.getWidth() / 2,playerSprite.getHeight() /2 - Utils.m2p(1));

        //playerSprite.setPosition(Utils.m2p(player.getPosition().x),Utils.m2p(player.getPosition().y));
        playerSprite.setRotation(player.getAngle()* MathUtils.radDeg);


        //sprite batch is drawn in world coords with camera.combined projection matrix?



        sunSprite.setCenter(Utils.m2p(sun.getPosition().x), Utils.m2p(sun.getPosition().y));
        sunSprite.setSize(Utils.m2p(((CircleShape)sun.getFixtureList().get(0).getShape()).getRadius() * 2), Utils.m2p(((CircleShape)sun.getFixtureList().get(0).getShape()).getRadius() * 2));



        sunSprite.draw(batch);
        playerSprite.draw(batch);
        batch.end();


        b2dr.render(world, camera.combined.scl(Utils.PPM)); //scaled by ppm since camera is in pixels and 1 meter is 32 pixels so meters -> pixels *=32 //camera.combined matrix is scaled by ppm


        rayHandler.setCombinedMatrix(camera.combined); //camera combined was already scaled. //not really deprecated since calling setCombinedMatrix(camera) is essentially the same but for some reason breaks
        rayHandler.updateAndRender();

        updateInput(); //unfortunately because of precedence


        /* text stuff */






       // batch.end();



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
            gsm.setState(GameStateManager.state.WATER);
        }
        b2dr.setDrawBodies(Gdx.input.isKeyPressed(Input.Keys.SPACE));

    }

    public Body createPlayer(float x, float y) {
        Body pBody;

        //STEP 1, BODY DEFINITION: position, friction, misc properties
        BodyDef def = new BodyDef(); //body definition, describes physical properties body will have.
        //friction, type of body, etc
        def.type = BodyDef.BodyType.DynamicBody;
        //def.angle = 90 * MathUtils.degRad;
        def.position.set(x,y); //b2d world coords
        //def.fixedRotation = true; //no rotations
        pBody = world.createBody(def);//actually create body.
        //STEP 2, CREATE SHAPE
        PolygonShape shape = new PolygonShape();
        shape.set(new float[]{-2,1,2,1,-1,-0.5f,1,-0.5f});
        //box 2d works in meters. pixels per meter is important
        //shape.set(new float[]{0,0,1,0,0.5f,1});
        //shape.setAsBox(2, 1); //box2d takes height and width from center. so actual width is 16 * 2 = 32
        //STEP 3 ASSIGN FIXTURE TO BODY
        pBody.createFixture(shape, 1f);
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
