package nitin.sangale.androidgames.numberdumber;

import java.util.List;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.Input.TouchEvent;

public class GoalScreen extends Screen 
	{
        public GoalScreen(Game game) 
        	{
        		super(game);
        	}
    
        @Override public void update(float deltaTime) 
    		{
        		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        		game.getInput().getKeyEvents();
        
        		int len = touchEvents.size();
        		for(int i = 0; i < len; i++) 
        			{
        				TouchEvent event = touchEvents.get(i);
        				if(event.type == TouchEvent.TOUCH_DOWN) 
        					{
        						if(event.x > 60 && event.y > 19 && event.x < 260 && event.y < 61 ) 
        							{
        								game.setScreen(new GameScreen(game, 0));
        								if(Settings.soundEnabled)
        									Assets.click.play(1);
        								return;
        							}
        						
        						if(event.x > 60 && event.y > 69 && event.x < 260 && event.y < 111 ) 
    								{
    									game.setScreen(new GameScreen(game, 1));
    									if(Settings.soundEnabled)
    										Assets.click.play(1);
    									return;
    								}
        						
        						if(event.x > 60 && event.y > 119 && event.x < 260 && event.y < 161 ) 
									{
										game.setScreen(new GameScreen(game, 2));
										if(Settings.soundEnabled)
											Assets.click.play(1);
										return;
									}
        						
        						if(event.x > 60 && event.y > 169 && event.x < 260 && event.y < 211 ) 
									{
										game.setScreen(new GameScreen(game, 3));
										if(Settings.soundEnabled)
											Assets.click.play(1);
										return;
									}
        						
        						if(event.x > 60 && event.y > 219 && event.x < 260 && event.y < 261 ) 
									{
										game.setScreen(new GameScreen(game, 4));
										if(Settings.soundEnabled)
											Assets.click.play(1);
										return;
									}
        						
        						if(event.x > 60 && event.y > 269 && event.x < 260 && event.y < 311 ) 
									{
										game.setScreen(new GameScreen(game, 5));
										if(Settings.soundEnabled)
											Assets.click.play(1);
										return;
									}
        						
        						if(event.x > 60 && event.y > 319 && event.x < 260 && event.y < 361 ) 
									{
										game.setScreen(new GameScreen(game, 6));
										if(Settings.soundEnabled)
											Assets.click.play(1);
										return;
									}
        						
        						if(event.x > 60 && event.y > 369 && event.x < 260 && event.y < 411 ) 
									{
										game.setScreen(new GameScreen(game, 7));
										if(Settings.soundEnabled)
											Assets.click.play(1);
										return;
									}
        						
        						if(event.x > 60 && event.y > 419 && event.x < 260 && event.y < 461 ) 
									{
										game.setScreen(new GameScreen(game, 8));
										if(Settings.soundEnabled)
											Assets.click.play(1);
										return;
									}
        					}
        			}
    		}

        @Override public void present(float deltaTime) 
        	{
        		Graphics g = game.getGraphics();      
        		g.drawPixmap(Assets.background, 0, 0);
        		g.drawPixmap(Assets.levels, 60,15);
        	}

        @Override public void pause() 
        	{
        		// TODO Auto-generated method stub

        	}

        @Override public void resume() 
        	{
        		// TODO Auto-generated method stub

        	}

        @Override public void dispose() 
        	{
        		// TODO Auto-generated method stub

        	}

	}
