package nitin.sangale.androidgames.egg;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.gl.Animation;
import com.badlogic.androidgames.framework.gl.Camera2D;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.gl.Texture;
import com.badlogic.androidgames.framework.gl.TextureRegion;
import com.badlogic.androidgames.framework.impl.GLScreen;
import com.badlogic.androidgames.framework.math.OverlapTester;
import com.badlogic.androidgames.framework.math.Rectangle;
import com.badlogic.androidgames.framework.math.Vector2;

public class HelpScreen extends GLScreen {
    Camera2D guiCam;
    SpriteBatcher batcher;
    Rectangle back;
    Vector2 touchPoint;
    Texture helpImage;
    TextureRegion helpRegion;
    List<Clouds> cloudes;
    Crow crows;
    List<Sparrow> sparrows;
    float crowStateTime = 0;
    float sparrowStateTime = 0;
    
    public HelpScreen(Game game) {
        super(game);
        
        guiCam = new Camera2D(glGraphics, 320, 480);
        back = new Rectangle(0, 0, 320, 480);
        touchPoint = new Vector2();
        batcher = new SpriteBatcher(glGraphics, 100);
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
    public void resume() {
        helpImage = new Texture(glGame, "help.png" );
        helpRegion = new TextureRegion(helpImage, 8, 8, 292, 352);
    }
    
    @Override
    public void pause() {
        helpImage.dispose();
    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            touchPoint.set(event.x, event.y);
            guiCam.touchToWorld(touchPoint);
            
            if(event.type == TouchEvent.TOUCH_UP) {
                if(OverlapTester.pointInRectangle(back, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new MainMenuScreen(game));
                    return;
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
        
        batcher.beginBatch(helpImage);
        batcher.drawSprite(160, 240, 292, 352, helpRegion);
        batcher.endBatch();
        
        gl.glDisable(GL10.GL_BLEND);
    }

    @Override
    public void dispose() {
    }
}
