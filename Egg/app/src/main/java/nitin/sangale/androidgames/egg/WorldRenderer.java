package nitin.sangale.androidgames.egg;

import javax.microedition.khronos.opengles.GL10;

import com.badlogic.androidgames.framework.gl.Animation;
import com.badlogic.androidgames.framework.gl.Camera2D;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.gl.TextureRegion;
import com.badlogic.androidgames.framework.impl.GLGraphics;
import nitin.sangale.androidgames.egg.Assets;

public class WorldRenderer {
    static final float FRUSTUM_WIDTH = 10;
    static final float FRUSTUM_HEIGHT = 12;    
    GLGraphics glGraphics;
    World world;
    Camera2D cam;
    SpriteBatcher batcher;    
    float angle;
    float startStateTime = 0;
    float finishStateTime = 0;
    float lifeStateTime = 0;
    float thunderStateTime = 0;
    float camVelocity = 8f;
    public static boolean camMove = false;
    float camNextPos = 0;
    
    public WorldRenderer(GLGraphics glGraphics, SpriteBatcher batcher, World world) {
        this.glGraphics = glGraphics;
        this.world = world;
        this.cam = new Camera2D(glGraphics, FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        this.batcher = batcher;        
        cam.position.set(5,6);
        camNextPos = 6;
    }
    
    public void render(float deltaTime) {
        if(world.egg.state == Egg.EGG_STATE_REST && world.egg.position.y > cam.position.y + 3 && camMove == false)
        {
        	camMove = true;
        	camNextPos = camNextPos + 8;
        }
        if(cam.position.y < camNextPos && camMove == true)
        {
        	camUpdate(deltaTime);
        }
        else
        {
        	camMove = false;
        	cam.position.y = camNextPos;
        }
        cam.setViewportAndMatrices();
        renderObjects(deltaTime);        
    }
    
    private void camUpdate(float deltaTime)
    {
    	cam.position.add(0, camVelocity*deltaTime);
    }
    
    public void renderObjects(float deltaTime) {
        GL10 gl = glGraphics.getGL();
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        
        batcher.beginBatch(Assets.itemsTexture);
        renderEgg(deltaTime);
        renderBowls();
        renderStart(deltaTime);
        renderFinish(deltaTime);
        renderThunder(deltaTime);
        renderLife(deltaTime);
        batcher.endBatch();
        gl.glDisable(GL10.GL_BLEND);
    }
    
    private void renderEgg(float deltaTime) {   
    	angle = angle + 360*deltaTime;
    	if(angle >= 360)
    		angle = 0;
        if(world.egg.state != Egg.EGG_STATE_REST )
        	batcher.drawSprite(world.egg.position.x, world.egg.position.y, 1f, 1f, angle, Assets.egg);        
    }
    
    private void renderStart(float deltaTime)
    {
    	startStateTime = startStateTime + deltaTime;
    	TextureRegion keyFrame = Assets.startAnim.getKeyFrame(startStateTime, Animation.ANIMATION_LOOPING);
    	Bowl bowl = world.bowls.get(0);
    	batcher.drawSprite(bowl.position.x, bowl.position.y - 1, 3, 0.75f, keyFrame);
    }
    
    private void renderFinish(float deltaTime)
    {
    	finishStateTime = finishStateTime + deltaTime;
    	TextureRegion keyFrame = Assets.finishAnim.getKeyFrame(finishStateTime, Animation.ANIMATION_LOOPING);
    	Bowl bowl = world.bowls.get(45);
    	batcher.drawSprite(bowl.position.x, bowl.position.y + 1, 3, 0.75f, keyFrame);
    }
    
    private void renderThunder(float deltaTime)
    {
    	thunderStateTime = thunderStateTime + deltaTime;
    	TextureRegion keyFrame = Assets.thunderAnim.getKeyFrame(thunderStateTime, Animation.ANIMATION_LOOPING);
    	if(world.egg.state == Egg.EGG_STATE_DEAD)
    		batcher.drawSprite(world.egg.position.x, world.egg.position.y, 1f, 1.5f, keyFrame);
    }
    
    private void renderLife(float deltaTime)
    {
    	if(world.newLife == 1)
    	{
    		lifeStateTime = lifeStateTime + deltaTime;
        	TextureRegion keyFrame = Assets.lifeAnim.getKeyFrame(lifeStateTime, Animation.ANIMATION_NONLOOPING);
    		batcher.drawSprite(world.currentBowl.position.x, world.currentBowl.position.y, 1f, 1f, keyFrame);
    		if(lifeStateTime > 1.6)
    		{
    			lifeStateTime = 0;
    			world.newLife = 0;
    		}
    	}
    }
    private void renderBowls() {
        int len = world.bowls.size();
        for(int i = 0; i < len; i++) {
            Bowl bowl = world.bowls.get(i);
            //if(bowl.state == Bowl.BOWL_STATE_START)
            	//batcher.drawSprite(bowl.position.x, bowl.position.y, 1.5f, 0.75f, Assets.startBowl);
            if(bowl.state == Bowl.BOWL_STATE_EMPTY)
            	batcher.drawSprite(bowl.position.x, bowl.position.y, 2.0f, 0.75f, Assets.bowl);
            if(bowl.state == Bowl.BOWL_STATE_BOWL_WITH_EGG)
            	batcher.drawSprite(bowl.position.x, bowl.position.y, 2.0f, 1.19f, Assets.bowlWithEgg);
            //if(bowl.state == Bowl.BOWL_STATE_END)
            	//batcher.drawSprite(bowl.position.x, bowl.position.y, 56, 28, Assets.nest);
        }
    }
}
