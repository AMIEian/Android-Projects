package nitin.sangale.androidgames.egg;

import java.util.Random;

import com.badlogic.androidgames.framework.DynamicGameObject;

public class Sparrow extends DynamicGameObject{
	public static float SPARROW_WIDTH = 61f;
	public static float SPARROW_HIGHT = 61f;
	public static float SPARROW_VELOCITY = 150f;
	
	public Sparrow()
	{
		super(160f, 240f, SPARROW_WIDTH, SPARROW_HIGHT);
		Random rand = new Random();
		position.set(320 + rand.nextInt(160), 350f - rand.nextInt(175));
		velocity.set(-SPARROW_VELOCITY/(1 + rand.nextInt(3)), 0);
	}
	
	public void update(float deltaTime)
	{
		position.add(velocity.x*deltaTime, 0);
		if(position.x + SPARROW_WIDTH / 2 + 1800 < 0)
		{
			Random rand = new Random();
			position.set(320 + rand.nextInt(1800), 350f - rand.nextInt(175));
			velocity.set(-SPARROW_VELOCITY/(1 + rand.nextInt(3)), 0);
		}
	}
	
}
