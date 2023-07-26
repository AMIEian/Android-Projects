package nitin.sangale.androidgames.briksnsaddle;

public class World 
	{
    	public Bricks bricks;
    	public Saddle saddle;
    	public Ball ball;
    
    	double tickTime;
    	double tick;
    	boolean gameOver = false;

    	public World(double tickValue) 
    		{
    			bricks = new Bricks();
    			saddle = new Saddle();
    			ball = new Ball();
    			tick = tickValue;
    			tickTime = 0;
    		}

    	public void update(float deltaTime) 
    		{
    			if (ball.endGame())
    				{
    					gameOver = true;
    					return;
    				}

    			tickTime = tickTime + deltaTime;

    			while (tickTime > tick) 
    				{
    					tickTime = tickTime - tick;
    					ball.move();
    				}
            }
    }