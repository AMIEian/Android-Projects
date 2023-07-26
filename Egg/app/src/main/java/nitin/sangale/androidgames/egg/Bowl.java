package nitin.sangale.androidgames.egg;

import java.util.Random;

import com.badlogic.androidgames.framework.DynamicGameObject;

public class Bowl extends DynamicGameObject{
	
	public static final float BOWL_WIDTH = 1.5f;
	public static final float BOWL_HIGHT = 0.75f;
	
	public static final int BOWL_TYPE_STATIC = 0;
	public static final int BOWL_TYPE_MOVING_HORIZONTAL = 1;
	public static final int BOWL_TYPE_MOVING_DIGONAL = 2;
	
	//public static final int BOWL_STATE_START = 0;
	//public static final int BOWL_STATE_END = 1;
	public static final int BOWL_STATE_EMPTY = 0;
	public static final int BOWL_STATE_BOWL_WITH_EGG = 1;
	
	Random rand;
	
	int type;
	int state;
	
	public Bowl(float x, float y, int type, float vel)
	{
		super(x, y, BOWL_WIDTH, BOWL_HIGHT);
		
		this.type = type;
		
		if(type == BOWL_TYPE_STATIC)
		{
			velocity.x = 0;
			velocity.y = 0;
		}
		
		if(type == BOWL_TYPE_MOVING_HORIZONTAL)
		{
			velocity.x = vel;
			velocity.y = 0;
		}
		
		if(type == BOWL_TYPE_MOVING_DIGONAL)
		{
			velocity.x = vel;
			rand = new Random();
			if(rand.nextBoolean() == true)
				velocity.y = vel/2.5f;
			else
				velocity.y = -vel/2.5f;
		}
	}
	
	public void update(float deltaTime)
	{
		if(type == BOWL_TYPE_MOVING_HORIZONTAL)
		{
			position.add(velocity.x*deltaTime, 0);
			bounds.lowerLeft.set(position).sub(BOWL_WIDTH / 2, BOWL_HIGHT / 2);
			
			if(position.x - 0.3f < BOWL_WIDTH / 2)
			{
				velocity.x = -velocity.x;
				position.x = 0.3f + BOWL_WIDTH / 2;
			}
			if(position.x + 0.3f > World.WORLD_WIDTH - BOWL_WIDTH/2)
			{
				velocity.x = -velocity.x;
				position.x = World.WORLD_WIDTH - 0.3f - BOWL_WIDTH / 2;
			}
		}
		
		if(type == BOWL_TYPE_MOVING_DIGONAL)
		{
			position.add(velocity.x*deltaTime, velocity.y*deltaTime);
			bounds.lowerLeft.set(position).sub(BOWL_WIDTH / 2, BOWL_HIGHT / 2);
			
			if(position.x - 0.3f  < BOWL_WIDTH / 2)
			{
				velocity.x = -velocity.x;
				velocity.y = -velocity.y;
				
				position.x = 0.3f + BOWL_WIDTH / 2;
			}
			if(position.x + 0.3f > World.WORLD_WIDTH - BOWL_WIDTH/2 )
			{
				velocity.x = -velocity.x;
				velocity.y = -velocity.y;
				position.x = World.WORLD_WIDTH - 0.3f - BOWL_WIDTH / 2;
			}
		}
	}
}
