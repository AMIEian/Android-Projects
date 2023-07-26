package nitin.sangale.androidgames.egg;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.MyInterstitialListener;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.gl.Animation;
import com.badlogic.androidgames.framework.gl.Camera2D;
import com.badlogic.androidgames.framework.gl.FPSCounter;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.gl.TextureRegion;
import com.badlogic.androidgames.framework.impl.GLScreen;
import com.badlogic.androidgames.framework.math.OverlapTester;
import com.badlogic.androidgames.framework.math.Rectangle;
import com.badlogic.androidgames.framework.math.Vector2;
import nitin.sangale.androidgames.egg.WorldRenderer;

import nitin.sangale.androidgames.egg.Assets;
import nitin.sangale.androidgames.egg.World;

import nitin.sangale.androidgames.egg.World.WorldListener;

public class GameScreen extends GLScreen {
    static final int GAME_RUNNING = 1;
    static final int GAME_PAUSED = 2;
    static final int GAME_LEVEL_END = 3;
    static final int GAME_OVER = 4;
  
    int state;
    Camera2D guiCam;
    Vector2 touchPoint;
    SpriteBatcher batcher;    
    Rectangle pauseBounds;
    Rectangle resumeBounds;
    Rectangle quitBounds;
    World world;
    WorldListener worldListener;
    WorldRenderer renderer;    
    Clock clock;
   
    int lastScore;
    int lastEggs;
    String scoreString;    
    String eggString;
    String timeString;
    FPSCounter fpsCounter;
    List<Clouds> cloudes;
    Crow crows;
    List<Sparrow> sparrows;
    float crowStateTime = 0;
    float sparrowStateTime = 0;
    
    MyInterstitialListener mL;
    private boolean showMain = false;
    float timer = 0.0f;
    
    public GameScreen(Game game, int level) {
        super(game);
        mL = (MyInterstitialListener)game;
        state = GAME_RUNNING;
        guiCam = new Camera2D(glGraphics, 320, 480);
        touchPoint = new Vector2();
        batcher = new SpriteBatcher(glGraphics, 1000);
        worldListener = new WorldListener() {
            @Override
            public void life()
            {
            	Assets.playSound(Assets.life);
            }
            @Override
            public void thunder()
            {
            	Assets.playSound(Assets.thunder);
            }
            @Override
            public void winner()
            {
            	Assets.playSound(Assets.winner);
            }
            @Override
            public void end()
            {
            	Assets.playSound(Assets.end);
            }
        };
        world = new World(worldListener, level);
        renderer = new WorldRenderer(glGraphics, batcher, world);
        pauseBounds = new Rectangle(320 - 32, 480 - 32, 32, 32);
        resumeBounds = new Rectangle(192, 480 - 352, 64, 64);
        quitBounds = new Rectangle(64, 480 - 352, 64, 64);
        clock = new Clock();
        lastScore = 0;
        lastEggs = 12;
        scoreString = "Score-0";
        eggString = "Eggs-12";
        fpsCounter = new FPSCounter();
        crows = new Crow();
        
        this.cloudes = new ArrayList<Clouds>();
        this.sparrows = new ArrayList<Sparrow>();
        
        for(int i = 0; i < 2; i++)
        {
        	Clouds cloud = new Clouds();
        	this.cloudes.add(cloud);
        	Sparrow sparrow = new Sparrow();
        	this.sparrows.add(sparrow);
        }
    }
    
    @Override
	public void update(float deltaTime) {
    	
    	int length = cloudes.size();
        for(int i = 0; i < length; i++)
          {
          	cloudes.get(i).update(deltaTime);
          	sparrows.get(i).update(deltaTime);
          }
        crows.update(deltaTime);
        
    	if(deltaTime > 0.1f)
	        deltaTime = 0.1f;
    	if(showMain)
    	{
    		timer = timer + deltaTime;
    		if(timer > 1)
    			game.setScreen(new MainMenuScreen(game));
    	}
	    switch(state) {
	    case GAME_RUNNING:
	        updateRunning(deltaTime);
	        break;
	    case GAME_PAUSED:
	        updatePaused();
	        break;
	    case GAME_LEVEL_END:
	        updateLevelEnd();
	        break;
	    case GAME_OVER:
	        updateGameOver();
	        break;
	    }
	}
    
    private void updateRunning(float deltaTime) {
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	        
	        touchPoint.set(event.x, event.y);
	        guiCam.touchToWorld(touchPoint);
	        
	        if(OverlapTester.pointInRectangle(pauseBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            state = GAME_PAUSED;
	            return;
	        }            
	        
	        else if(world.egg.state == Egg.EGG_STATE_REST && WorldRenderer.camMove == false)
	        {
	        	world.egg.jump();
	        	Assets.playSound(Assets.jumpSound);
	            world.currentBowl.state = Bowl.BOWL_STATE_EMPTY;
	        }
	    }
	    
	    clock.update(deltaTime);
	    world.update(deltaTime);
	    if(world.score != lastScore) {
	        lastScore = world.score;
	        scoreString = "Score-" + lastScore;
	    }
	    if(world.eggCounter != lastEggs)
	    {
	    	lastEggs = world.eggCounter;
	    	eggString = "Eggs-" + lastEggs;
	    }
	    
	    if(world.state == World.WORLD_STATE_LEVEL_COMPLETE) {
	        state = GAME_LEVEL_END;        
	    }
	    
	    if(world.state == World.WORLD_STATE_GAME_OVER) {
	        state = GAME_OVER;
	    }
	}
    
    private void updatePaused() {
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	        
	        touchPoint.set(event.x, event.y);
	        guiCam.touchToWorld(touchPoint);
	        
	        if(OverlapTester.pointInRectangle(resumeBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            state = GAME_RUNNING;
	            return;
	        }
	        
	        if(OverlapTester.pointInRectangle(quitBounds, touchPoint)) {
	            Assets.playSound(Assets.clickSound);
	            showMain = true;
	            timer = 0f;
	            mL.showInterstitial();
	            return;
	        
	        }
	    }
	}
    
    private void updateLevelEnd()
    {
    	List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	        Assets.playSound(Assets.clickSound);
	        showMain = true;
            timer = 0f;
	        mL.showInterstitial();
            return;
	    }
    }
    
    private void updateGameOver()
    {
    	List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	        Assets.playSound(Assets.clickSound);
	        showMain = true;
            timer = 0f;
	        mL.showInterstitial();
            return;
	    }
    }
    
    @Override
    public void present(float deltaTime) {
    	GL10 gl = glGraphics.getGL();
	    gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	    guiCam.setViewportAndMatrices();
        
        gl.glEnable(GL10.GL_TEXTURE_2D);
        
        batcher.beginBatch(Assets.backgroundTexture);
        batcher.drawSprite(160, 240, 320, 480, Assets.background);
        batcher.endBatch();
        
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        
        batcher.beginBatch(Assets.backgroundTexture);
        int length = cloudes.size();
        for(int i = 0; i < length; i++)
        {
        	if(cloudes.get(i).type)
        	{
        		batcher.drawSprite(cloudes.get(i).position.x, cloudes.get(i).position.y, 190, 112, Assets.doubleCloud);
        	}
        	else
        	{
        		batcher.drawSprite(cloudes.get(i).position.x, cloudes.get(i).position.y, 190, 112, Assets.singleCloud);
        	}
        }
        
        crowStateTime = crowStateTime + deltaTime;
    	TextureRegion keyFrame = Assets.crow.getKeyFrame(crowStateTime, Animation.ANIMATION_LOOPING);
    	batcher.drawSprite(crows.position.x, crows.position.y, 125, 61, keyFrame);
        
    	length = sparrows.size();
    	sparrowStateTime = sparrowStateTime + deltaTime;
    	for(int i = 0; i < length; i++)
    	{
    		keyFrame = Assets.sparrow.getKeyFrame(sparrowStateTime, Animation.ANIMATION_LOOPING);
    		batcher.drawSprite(sparrows.get(i).position.x, sparrows.get(i).position.y, 61, 61, keyFrame);
    	}
    	
        batcher.endBatch();
	    
        gl.glDisable(GL10.GL_BLEND);
        
	    renderer.render(deltaTime);
        
        guiCam.setViewportAndMatrices();
	    gl.glEnable(GL10.GL_BLEND);
	    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	    batcher.beginBatch(Assets.itemsTexture);
	    switch(state) {
	    case GAME_RUNNING:
	        presentRunning();
	        break;
	    case GAME_PAUSED:
	        presentPaused();
	        break;
	    case GAME_LEVEL_END:
	        presentLevelEnd();
	        break;
	    case GAME_OVER:
	        presentGameOver();
	        break;
	    }
	    batcher.endBatch();
	    gl.glDisable(GL10.GL_BLEND);
	    
	    fpsCounter.logFrame();
	}
    
    public void presentRunning()
    {
    	batcher.drawSprite(320 - 16, 480 - 16, 32, 32, Assets.closeButton);
    	Assets.font.drawText(batcher, scoreString, 16, 480-20);
    	Assets.font.drawText(batcher, eggString, 16, 480-40);
    	timeString = "Time-" + clock.getTime();
    	Assets.font.drawText(batcher, timeString, 16, 480-60);
    }
    
    private void presentPaused() {
	    batcher.drawSprite(160, 240, 272, 201, Assets.pauseMenu);
	    Assets.font.drawText(batcher, scoreString, 16, 480-20);
    	Assets.font.drawText(batcher, eggString, 16, 480-40);
    	Assets.font.drawText(batcher, timeString, 16, 480-60);
	}
    
    public void presentLevelEnd()
    {
    	batcher.drawSprite(160, 240, 192, 224, Assets.win);
    	Assets.font.drawText(batcher, scoreString, 16, 480-20);
    	Assets.font.drawText(batcher, eggString, 16, 480-40);
    	Assets.font.drawText(batcher, timeString, 16, 480-60);
    }
    
    public void presentGameOver()
    {
    	batcher.drawSprite(160, 240, 192, 224, Assets.gameOver);
    	Assets.font.drawText(batcher, scoreString, 16, 480-20);
    	Assets.font.drawText(batcher, eggString, 16, 480-40);
    	Assets.font.drawText(batcher, timeString, 16, 480-60);
    }
    
    @Override
    public void pause() {
        if(state == GAME_RUNNING)
            state = GAME_PAUSED;
    }

    @Override
    public void resume() {        
    	if(state == GAME_PAUSED)
            state = GAME_RUNNING;
    }

    @Override
    public void dispose() {     
    	
    }
}
