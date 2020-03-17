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
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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
import com.badlogic.gdx.utils.TimeUtils;
import com.dookin.Asteroid;
import com.dookin.CollisionListener;
import com.dookin.GestureListener;
import com.dookin.Utils;
import com.dookin.Word;
import com.dookin.managers.GameStateManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;

import box2dLight.PointLight;
import box2dLight.RayHandler;


public class MusicState extends GameState{


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
    float parallax = 0;
    float counter = 0;
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
    FreeTypeFontGenerator generator2 = new FreeTypeFontGenerator(Gdx.files.internal("malibu.ttf"));
    ArrayList<Word> liveWords;

    GlyphLayout layout; //dont do this every frame! Store it as member

    BitmapFont font;
    BitmapFont highlightFont;
    HashMap<Integer, String[]> words = new HashMap<>();
    long time;
    private int currentLongest = 2;

    public MusicState(GameStateManager gsm) {
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
        img.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
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
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(.4f);
        planetLight = new PointLight(rayHandler, 100, Color.CYAN, 35f, 0,0);
        planetLight.setXray(true);
        //rayHandler.setBlurNum(2);
        //planetLight.setSoft(true);
        Music music = Gdx.audio.newMusic(Gdx.files.internal("dont.mp3"));
        music.setLooping(true);
        music.play();
        /* Text Stuff */
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 25;
        font = generator.generateFont(parameter);
        parameter.size = 40;
        highlightFont = generator2.generateFont(parameter);
        highlightFont.setColor(Color.CYAN);
        liveWords = new ArrayList<>();
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










        //words.put(4, "myself");


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

        if (counter == 0) {
            Vector3 pos;
            pos = camera.unproject(new Vector3((float)Math.random() * Gdx.graphics.getWidth() /2, (float)Math.random() * Gdx.graphics.getHeight() /2, 0f));
            System.out.println(pos);
            Iterator<Integer> it = words.keySet().iterator();
            int random = (int)(Math.random() * words.keySet().size());
            int key = 0;
            while (random != 0 && it.hasNext()) {
                key = (Integer)it.next();
                random--;
            }

            liveWords.add(new Word(words.get(key)[0], pos.x, pos.y));
        }
        counter+=0.1f;
        if (counter >= 30) {
            counter = 0;
        }
        cameraUpdate();

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        //update(Gdx.graphics.getDeltaTime()); //UPDATE BY DEFAULT IS CALLED BY THE APPLICATION BEFORE RENDER NO MATTER WHAT

        batch.setProjectionMatrix(camera.combined); //
        batch.begin();

        batch.draw(img, Utils.m2p(-100  ),Utils.m2p(-100 ), 0, 0, img.getWidth() * 10, img.getHeight() * 10);
        parallax +=0.01f;
        batch.end();

        batch.setProjectionMatrix(camera.combined); //
        batch.begin();
        for (Word word: liveWords) {
            word.render(highlightFont, batch);
        }
        batch.end();


        b2dr.render(world, camera.combined.scl(Utils.PPM)); //scaled by ppm since camera is in pixels and 1 meter is 32 pixels so meters -> pixels *=32 //camera.combined matrix is scaled by ppm


        rayHandler.setCombinedMatrix(camera.combined); //camera combined was already scaled. //not really deprecated since calling setCombinedMatrix(camera) is essentially the same but for some reason breaks
        rayHandler.updateAndRender();

        cameraUpdate();
        updateInput(); //unfortunately because of precedence
        //System.out.println(stob2d(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)));

        /* text stuff */










        batch.setProjectionMatrix(camera.projection); //static on screen

        //System.out.println(time);
        int timeSince = (int)(TimeUtils.timeSinceMillis((time)) / 1000F);
        //System.out.println((int)timeSince);
        float currentStart = 0;
        //layout.setText(font, words.getOrDefault(timeSince, ""));
        //font.draw(batch, layout, currentStart, Gdx.graphics.getHeight()/2 - 10);
        int lineLength = 0;
        String line = "";

        for (int i = timeSince - 5; i < timeSince + 5; i++) {
            String[] tmp = words.get(i);
            if (tmp != null && (timeSince >= i && timeSince < Integer.parseInt(tmp[1]) /*|| i > timeSince*/)) {
                layout.setText(font, tmp[0]);
                lineLength += layout.width + 5;
            }
        }
        for (int i = timeSince - 5/*currentLongest*/; i < timeSince + 5; i++) { //every frame, loop through 5 second time period, if there's a word in that time period s.t. the current time is in it's range, draw it red, otherwise, draw the word white
            String[] tmpWord = words.get(i);
            //System.out.println("current time: " + timeSince + " i: " + i + " " + "should be lowest i: " + Math.abs(timeSince - currentLongest) + " currentLongest: " + currentLongest + " " + (tmpWord == null ? "null" : tmpWord[0]));

            if (tmpWord == null) {
                continue;
            }
            currentLongest = Math.max(currentLongest, Math.abs(Integer.parseInt(tmpWord[1]) - i));

            if (timeSince >= i && timeSince < Integer.parseInt(tmpWord[1])) {
                layout.setText(highlightFont, tmpWord[0]);
                batch.begin();

                highlightFont.draw(batch, layout,  -(lineLength)/2 + currentStart , 10);
                batch.end();
                currentStart += layout.width + 5;

            } else {
               /* if (i > timeSince) {
                    layout.setText(font, tmpWord[0]);
                    batch.begin();
                    font.draw(batch, layout,  -(lineLength)/2 + currentStart, -100);
                    batch.end();
                    currentStart += layout.width + 5;

                }*/

            }
        }




       // batch.end();



    }
    public void generateWord() {
        //generate random word that runs across the screen
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
        //position.x = Math.round(camera.position);
        //position.y = Math.round(0 * Utils.PPM);
        camera.position.sub(0.1f,0.1f,0);
        //camera.position.set(position);
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
