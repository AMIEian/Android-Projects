package nitin.sangale.androidgames.numberdumber;

import java.util.List;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.Input.TouchEvent;

public class HelpScreen3 extends Screen 
	{
        public HelpScreen3(Game game) 
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
        				if(event.type == TouchEvent.TOUCH_UP) 
        					{
        						if(event.x > 248 && event.y > 408 ) 
        							{
        								game.setScreen(new MainMenuScreen(game));
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
        		g.drawPixmap(Assets.help3, 0, 0);
        		g.drawPixmap(Assets.buttons, 248, 408, 0, 128, 64, 64);
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
