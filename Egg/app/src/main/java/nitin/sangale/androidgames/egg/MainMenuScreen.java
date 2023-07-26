package nitin.sangale.androidgames.egg;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.gl.Animation;
import com.badlogic.androidgames.framework.gl.Camera2D;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.gl.TextureRegion;
import com.badlogic.androidgames.framework.impl.GLScreen;
import com.badlogic.androidgames.framework.math.OverlapTester;
import com.badlogic.androidgames.framework.math.Rectangle;
import com.badlogic.androidgames.framework.math.Vector2;

public class MainMenuScreen extends GLScreen {
    Camera2D guiCam;
    SpriteBatcher batcher;
    Rectangle playBounds;
    Rectangle soundBounds;
    Rectangle helpBounds;
    Rectangle aboutBounds;
    Vector2 touchPoint;
    List<Clouds> cloudes;
    Crow crows;
    List<Sparrow> sparrows;
    float crowStateTime = 0;
    float sparrowStateTime = 0;
    
    public MainMenuScreen(Game game) {
        super(game);
        guiCam = new Camera2D(glGraphics, 320, 480);
        batcher = new SpriteBatcher(glGraphics, 100);
        playBounds = new Rectangle(96, 480 - 168, 128, 48);
        soundBounds = new Rectangle(64, 480 - 232, 192, 48);
        helpBounds = new Rectangle(96, 480 - 298, 128, 48);
        aboutBounds = new Rectangle(80, 480 - 360, 160, 48);
        touchPoint = new Vector2();
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
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);                        
            if(event.type == TouchEvent.TOUCH_UP) {
                touchPoint.set(event.x, event.y);
                guiCam.touchToWorld(touchPoint);
                
                if(OverlapTester.pointInRectangle(playBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new LevelScreen(game));
                    return;
                }
                if(OverlapTester.pointInRectangle(aboutBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new AboutScreen(game));
                    return;
                }
                if(OverlapTester.pointInRectangle(helpBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new HelpScreen(game));
                    return;
                }
                if(OverlapTester.pointInRectangle(soundBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    Settings.soundEnabled = !Settings.soundEnabled;
                    if(Settings.soundEnabled) 
                        Assets.music.play();
                    else
                        Assets.music.pause();
                }
            }
        }
        
        int length = cloudes.size();
        for(int i = 0; i < length; i++)
        {
        	cloudes.get(i).update(deltaTime);
        	sparrows.get(i).update(deltaTime);
        }
        crows.update(deltaTime);
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
        
        batcher.beginBatch(Assets.menuTexture);                 
        
        //batcher.drawSprite(160, 480 - 10 - 71, 274, 142, Assets.logo);
        batcher.drawSprite(160, 240, 208, 272, Assets.mainMenu);
        batcher.drawSprite(160, 272, 160, 40, Settings.soundEnabled?Assets.soundOn:Assets.soundOff);
                
        batcher.endBatch();
        
        gl.glDisable(GL10.GL_BLEND);
    }
    
    @Override
    public void pause() {        
        Settings.save(game.getFileIO());
    }

    @Override
    public void resume() {        
    }       

    @Override
    public void dispose() {        
    }
}
