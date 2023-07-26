package nitin.sangale.androidgames.egg;

import java.util.Random;

import com.badlogic.androidgames.framework.DynamicGameObject;

public class Crow extends DynamicGameObject{
	public static float CROW_WIDTH = 125f;
	public static float CROW_HIGHT = 61f;
	public static float CROW_VELOCITY = 100f;
	
	public Crow()
	{
		super(160f, 240f, CROW_WIDTH, CROW_HIGHT);
		Random rand = new Random();
		position.set(0 - rand.nextInt(160), 400f - rand.nextInt(175));
		velocity.set(CROW_VELOCITY/(1 + rand.nextInt(3)), 0);
	}
	
	public void update(float deltaTime)
	{
		position.add(velocity.x*deltaTime, 0);
		if(position.x - CROW_WIDTH / 2 - 2500 > 320)
		{
			Random rand = new Random();
			position.set(0 - rand.nextInt(2500), 400f - rand.nextInt(175));
			velocity.set(CROW_VELOCITY/(1 + rand.nextInt(3)), 0);
		}
	}
	
}
