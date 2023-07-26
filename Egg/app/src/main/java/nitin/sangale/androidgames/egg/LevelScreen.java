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

public class LevelScreen extends GLScreen {
    Camera2D guiCam;
    SpriteBatcher batcher;
    Rectangle kidBounds;
    Rectangle easyBounds;
    Rectangle normalBounds;
    Rectangle hardBounds;
    Vector2 touchPoint;
    List<Clouds> cloudes;
    Crow crows;
    List<Sparrow> sparrows;
    float crowStateTime = 0;
    float sparrowStateTime = 0;

    public LevelScreen(Game game) {
        super(game);
        guiCam = new Camera2D(glGraphics, 320, 480);
        batcher = new SpriteBatcher(glGraphics, 100);
        kidBounds = new Rectangle(112, 480 - 168, 96, 48);
        easyBounds = new Rectangle(96, 480 - 232, 128, 48);
        normalBounds = new Rectangle(64, 480 - 296, 192, 48);
        hardBounds = new Rectangle(96, 480 - 360, 128, 48);
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
                
                if(OverlapTester.pointInRectangle(kidBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new GameScreen(game,1));
                    return;
                }
                if(OverlapTester.pointInRectangle(easyBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new GameScreen(game,2));
                    return;
                }
                if(OverlapTester.pointInRectangle(normalBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new GameScreen(game,3));
                    return;
                }
                if(OverlapTester.pointInRectangle(hardBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new GameScreen(game,4));
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
        batcher.drawSprite(160, 240, 208, 272, Assets.levels);
                
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
