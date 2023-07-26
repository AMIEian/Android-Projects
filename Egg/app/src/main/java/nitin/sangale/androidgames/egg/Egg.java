package nitin.sangale.androidgames.egg;

import com.badlogic.androidgames.framework.DynamicGameObject;

public class Egg extends DynamicGameObject{
	public static final float EGG_WIDTH = 0.5f;
	public static final float EGG_HIGHT = 0.8f;
	
	public static final int EGG_STATE_REST = 0;
	public static final int EGG_STATE_JUMP = 1;
	public static final int EGG_STATE_FALL = 2;
	public static final int EGG_STATE_DEAD = 3;
	
	public static final float EGG_JUMP_VELOCITY = 12;
	
	int state;
	
	public Egg(float x, float y, Bowl bowl)
	{
		super(x, y, EGG_WIDTH, EGG_HIGHT);
		this.velocity.x = bowl.velocity.x;
		this.velocity.y = bowl.velocity.y;
		state = EGG_STATE_REST;
	}
	
	public void update(float deltaTime)
	{
		if(state == EGG_STATE_JUMP || state == EGG_STATE_DEAD || state == EGG_STATE_FALL)
		{
			velocity.add(World.gravity.x*deltaTime, World.gravity.y*deltaTime);
			position.add(velocity.x*deltaTime, velocity.y*deltaTime);
			bounds.lowerLeft.set(position).sub(EGG_WIDTH/2, EGG_HIGHT/2);
			
			if(velocity.y < 0 && state == EGG_STATE_JUMP)
			{
				state = EGG_STATE_FALL;
			}
		}
		else
		{
			position.add(velocity.x*deltaTime, velocity.y*deltaTime);
			bounds.lowerLeft.set(position).sub(EGG_WIDTH/2, EGG_HIGHT/2);
		}
	}
	
	public void jump()
	{
		velocity.set(0, EGG_JUMP_VELOCITY);
		state = EGG_STATE_JUMP;
	}
	
	public void dead()
	{
		velocity.set(0, 0);
		state = EGG_STATE_DEAD;
	}
}
