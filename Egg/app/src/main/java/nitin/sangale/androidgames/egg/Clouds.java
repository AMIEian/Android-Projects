package nitin.sangale.androidgames.egg;

import java.util.Random;

import com.badlogic.androidgames.framework.DynamicGameObject;

public class Clouds extends DynamicGameObject{
	public static float CLOUD_WIDTH = 190f;
	public static float CLOUD_HIGHT = 112f;
	public static float CLOUD_VELOCITY = 5f;
	
	boolean type;
	
	public Clouds()
	{
		super(160f, 240f, CLOUD_WIDTH, CLOUD_HIGHT);
		Random rand = new Random();
		position.set(320f + rand.nextInt(160), 480f - rand.nextInt(175));
		velocity.set(-CLOUD_VELOCITY/(1 + rand.nextInt(3)), 0);
		type = rand.nextBoolean();
	}
	
	public void update(float deltaTime)
	{
		position.add(velocity.x*deltaTime, 0);
		if(position.x + CLOUD_WIDTH / 2 + 10 < 0)
		{
			Random rand = new Random();
			position.set(320f + rand.nextInt(160), 480f - rand.nextInt(175));
			velocity.set(-CLOUD_VELOCITY/(1 + rand.nextInt(3)), 0);
			type = rand.nextBoolean();
		}
	}
	
}
