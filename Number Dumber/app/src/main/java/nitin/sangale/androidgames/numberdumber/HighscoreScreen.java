package nitin.sangale.androidgames.numberdumber;
import java.util.List;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.Input.TouchEvent;

public class HighscoreScreen extends Screen 
	{
    	String lines[] = new String[9];

    	public HighscoreScreen(Game game) 
    		{
    			super(game);

    			for (int i = 0; i < 9; i++) 
    				{
    					if(Settings.highscores[i] == 0)
    						lines[i] = "" + (i + 1) + ". " + Settings.highscores[i] + Settings.highscores[i] + Settings.highscores[i];
    					else
    						lines[i] = "" + (i + 1) + ". " + Settings.highscores[i];
    				}
    		}

    	@Override public void update(float deltaTime) 
    		{
    			List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
    			game.getInput().getKeyEvents();

    			int len = touchEvents.size();
    			for (int i = 0; i < len; i++) 
    				{
    					TouchEvent event = touchEvents.get(i);
    					if (event.type == TouchEvent.TOUCH_UP) 
    						{
    							if (event.y < 72 && event.x > 260 && event.y > 8) 
    								{
    									if(Settings.soundEnabled)
    										Assets.click.play(1);
    									game.setScreen(new MainMenuScreen(game));
    									return;
    								}
    						}
    				}
    		}

    	@Override public void present(float deltaTime) 
    		{
    			Graphics g = game.getGraphics();

    			g.drawPixmap(Assets.background, 0, 0);
    			g.drawPixmap(Assets.record, 60, 15);

    			int y = 75;
    			for (int i = 0; i < 9; i++) 
    				{
    					g.drawPixmap(Assets.lable,60, y);
    					drawText(g, lines[i], 60 + 90, y);
    					y += 45;
    				}

    			g.drawPixmap(Assets.cancel, 260, 8);
    		}

    	public void drawText(Graphics g, String line, int x, int y) 
    		{
    			int len = line.length();
    			for (int i = 0; i < len; i++) 
    				{
    					char character = line.charAt(i);
    
    					if (character == ' ') 
    						{
    							x += 10;
    							continue;
    						}
    
    					int srcX = 0;
    					int srcWidth = 0;
    					if (character == '.') 
    						{
    							srcX = 200;
    							srcWidth = 10;
    						} 
    					else 
    						{
    							srcX = (character - '0') * 20;
    							srcWidth = 20;
    						}
    
    					g.drawPixmap(Assets.numbers, x, y, srcX, 0, srcWidth, 32);
    					x += srcWidth;
    				}
    		}

    	@Override public void pause() 
    		{

    		}

    	@Override public void resume() 
    		{

    		}

    	@Override public void dispose() 
    		{

    		}
	}