package nitin.sangale.androidgames.egg;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.androidgames.framework.math.OverlapTester;
import com.badlogic.androidgames.framework.math.Vector2;

public class World {
	public interface WorldListener {
		public void life();
		public void thunder();
		public void winner();
		public void end();
	}
	
	public static final float WORLD_WIDTH = 10;
	public static final float WORLD_HEIGHT = 12 * 15;
	public static final int WORLD_STATE_RUNNING = 0;
	public static final int WORLD_STATE_LEVEL_COMPLETE = 1;
	public static final int WORLD_STATE_GAME_OVER = 2;
	public static final Vector2 gravity = new Vector2(0, -13);
	
	public final List<Bowl> bowls;
	public final WorldListener listener;
	public final Random rand;
	
	public int score;
	public int state;
	public int level;
	public int newLife = 0;
	
	Egg egg;
	Bowl currentBowl;
	Bowl nextBowl;
	int bowlCounter;
	int eggCounter = 12;
	
	public World(WorldListener listener,int level) {
		this.bowls = new ArrayList<Bowl>();
		this.listener = listener;
		rand = new Random();
		this.level = level;
		
		generateLevel(level);
		
		bowlCounter = 0;
		this.egg = new Egg(5, 2, bowls.get(bowlCounter));
		currentBowl = bowls.get(bowlCounter);
		nextBowl = bowls.get(bowlCounter+1);
		
		this.score = 0;
		this.state = WORLD_STATE_RUNNING;
	}
	
	public void generateLevel(int level){
		int baseVelocity = 1;
		float previous_velocity = -1.0f;
		float y = 2;
		
		//First Bowl of the game
		Bowl startingBowl = new Bowl(5, y, 0, 0);
		startingBowl.state = Bowl.BOWL_STATE_BOWL_WITH_EGG;
		bowls.add(startingBowl);
		y = y + 4;
		
		//Add next bowls according to level
		if(level == 1)
		{
			//KID LEVEL
			//float y = 6;
			int count = 1;
			int levelVelocity = level*baseVelocity;
			while(count < 45)
			{
				if(rand.nextFloat() > 0.6)
				{
					float velocityDivider = rand.nextInt(levelVelocity) + 1;
					float bowl_velocity = levelVelocity/velocityDivider;
					if(bowl_velocity == previous_velocity)
					{
						bowl_velocity = bowl_velocity + 0.1f;						
					}
					Bowl movingBowl = new Bowl(5,y, Bowl.BOWL_TYPE_MOVING_HORIZONTAL, bowl_velocity);
					movingBowl.state = Bowl.BOWL_STATE_EMPTY;
					bowls.add(movingBowl);
					previous_velocity = bowl_velocity;
				}
				else
				{
					Bowl staticBowl = new Bowl(5, y, Bowl.BOWL_TYPE_STATIC, 0);
					staticBowl.state = Bowl.BOWL_STATE_EMPTY;
					bowls.add(staticBowl);
					previous_velocity = 0.0f;
				}
				count++;
				y = y + 4;
			}
		}
		
		if(level == 2)
		{
			//EASY LEVEL
			//float y = 6;
			int count = 1;
			int levelVelocity = level*baseVelocity;
			while(count < 45)
			{
				if(rand.nextFloat() > 0.4)
				{
					float velocityDivider = rand.nextInt(levelVelocity) + 1;
					float bowl_velocity = levelVelocity/velocityDivider;
					if(bowl_velocity == previous_velocity)
					{
						bowl_velocity = bowl_velocity + 0.1f;						
					}
					Bowl movingBowl = new Bowl(5,y, Bowl.BOWL_TYPE_MOVING_HORIZONTAL, bowl_velocity);
					movingBowl.state = Bowl.BOWL_STATE_EMPTY;
					bowls.add(movingBowl);
					previous_velocity = bowl_velocity;
				}
				else
				{
					Bowl staticBowl = new Bowl(5, y, Bowl.BOWL_TYPE_STATIC, 0);
					staticBowl.state = Bowl.BOWL_STATE_EMPTY;
					bowls.add(staticBowl);
					previous_velocity = 0.0f;
				}
				count++;
				y = y + 4;
			}
		}
		
		if(level == 3)
		{
			//NORMAL LEVEL
			//float y = 6;
			int count = 1;
			int levelVelocity = level * baseVelocity;
			while(count < 45)
			{
				if(rand.nextFloat() > 0.3)
				{
					if(count > 30 && rand.nextFloat() < 0.3)
					{
						float velocityDivider = rand.nextInt(levelVelocity) + 1;
						float bowl_velocity = levelVelocity/velocityDivider;
						if(bowl_velocity == previous_velocity && rand.nextFloat() > 0.2)
						{
							bowl_velocity = bowl_velocity + 0.2f;						
						}
						Bowl movingBowl = new Bowl(5,y, Bowl.BOWL_TYPE_MOVING_DIGONAL, bowl_velocity);
						movingBowl.state = Bowl.BOWL_STATE_EMPTY;
						bowls.add(movingBowl);
						previous_velocity = bowl_velocity;
					}
					else
					{
						float velocityDivider = rand.nextInt(levelVelocity) + 1;
						float bowl_velocity = levelVelocity/velocityDivider;
						if(bowl_velocity == previous_velocity && rand.nextFloat() > 0.1)
						{
							bowl_velocity = bowl_velocity + 0.2f;						
						}
						Bowl movingBowl = new Bowl(5,y, Bowl.BOWL_TYPE_MOVING_HORIZONTAL, bowl_velocity);
						movingBowl.state = Bowl.BOWL_STATE_EMPTY;
						bowls.add(movingBowl);
						previous_velocity = bowl_velocity;
					}
				}
				else
				{
					Bowl staticBowl = new Bowl(5, y, Bowl.BOWL_TYPE_STATIC, 0);
					staticBowl.state = Bowl.BOWL_STATE_EMPTY;
					bowls.add(staticBowl);
					previous_velocity = 0.0f;
				}
				count++;
				y = y + 4;
			}
		}
		
		if(level == 4)
		{
			//HARD LEVEL
			//float y = 6;
			int count = 1;
			int levelVelocity = level * baseVelocity + 2;
			while(count < 45)
			{
				if(rand.nextFloat() > 0.1)
				{
					if(count > 10 && rand.nextFloat() < 0.6)
					{
						float velocityDivider = rand.nextInt(levelVelocity) + 1;
						float bowl_velocity = levelVelocity/velocityDivider;
						if(bowl_velocity == previous_velocity && rand.nextFloat() > 0.2)
						{
							bowl_velocity = bowl_velocity + 0.5f;						
						}
						Bowl movingBowl = new Bowl(5,y, Bowl.BOWL_TYPE_MOVING_DIGONAL, bowl_velocity);
						movingBowl.state = Bowl.BOWL_STATE_EMPTY;
						bowls.add(movingBowl);
						previous_velocity = bowl_velocity;
					}
					else
					{
						float velocityDivider = rand.nextInt(levelVelocity) + 1;
						float bowl_velocity = levelVelocity/velocityDivider;
						if(bowl_velocity != previous_velocity && rand.nextFloat() > 0.3)
						{
							bowl_velocity = previous_velocity;						
						}
						Bowl movingBowl = new Bowl(5,y, Bowl.BOWL_TYPE_MOVING_HORIZONTAL, bowl_velocity);
						movingBowl.state = Bowl.BOWL_STATE_EMPTY;
						bowls.add(movingBowl);
						//previous_velocity = bowl_velocity;
					}
				}
				else
				{
					Bowl staticBowl = new Bowl(5, y, Bowl.BOWL_TYPE_STATIC, 0);
					staticBowl.state = Bowl.BOWL_STATE_EMPTY;
					bowls.add(staticBowl);
					previous_velocity = 0.0f;
				}
				count++;
				y = y + 4;
			}
		}
		
		Bowl endingBowl = new Bowl(5, y, Bowl.BOWL_TYPE_STATIC, 0);
		endingBowl.state = Bowl.BOWL_STATE_EMPTY;
		bowls.add(endingBowl);
	}
	
	public void update(float deltaTime)
	{
		updateEgg(deltaTime);
		updateBowls(deltaTime);
		if(egg.state == Egg.EGG_STATE_FALL)
		{
			checkCollision();
		    //checkLevelComplete();
		    checkEggEnd();
		}
		if(egg.state == Egg.EGG_STATE_DEAD && state != WORLD_STATE_GAME_OVER)
			creatNewEgg();
	}
	
	public void updateEgg(float deltaTime)
	{
		egg.update(deltaTime);
		if(egg.state == Egg.EGG_STATE_REST)
		{
			egg.position.set(currentBowl.position);
			egg.velocity.set(currentBowl.velocity);
		}
	}
	
	public void updateBowls(float deltaTime)
	{
		int len = bowls.size();
		for(int i = 0; i < len; i++)
		{
			Bowl bowl = bowls.get(i);
			bowl.update(deltaTime);
		}
	}
	
	public void checkCollision()
	{
		if(OverlapTester.overlapRectangles(egg.bounds, nextBowl.bounds))
		{
			bowlCounter = bowlCounter + 1;
			egg.state = Egg.EGG_STATE_REST;
			nextBowl.state = Bowl.BOWL_STATE_BOWL_WITH_EGG;
			currentBowl = nextBowl;
			if(bowlCounter < 45)
				nextBowl = bowls.get(bowlCounter+1);
			score = bowlCounter * 10;
			checkLevelComplete();
		}
	}
	
	public void checkLevelComplete()
	{
		if(bowlCounter == 45)
		{
			state = WORLD_STATE_LEVEL_COMPLETE;
		    score = score + level * 100;
		    listener.winner();
		}
	}
	
	public void checkEggEnd()
	{
		if(egg.state == Egg.EGG_STATE_FALL)
		{
			if(egg.position.y < nextBowl.position.y)
			{
				egg.dead();
				eggCounter = eggCounter - 1;
				listener.thunder();
				if(eggCounter == 0)
				{
					state = WORLD_STATE_GAME_OVER;
					listener.end();
				}
			}
		}
	}
	
	public void creatNewEgg()
	{
		if(egg.position.y < currentBowl.position.y - 3)
		{
			egg = new Egg(currentBowl.position.x, currentBowl.position.y, currentBowl);
			currentBowl.state = Bowl.BOWL_STATE_BOWL_WITH_EGG;
			newLife = 1;
			listener.life();
		}
	}
}