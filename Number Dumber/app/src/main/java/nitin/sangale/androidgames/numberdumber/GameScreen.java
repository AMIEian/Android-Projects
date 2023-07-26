package nitin.sangale.androidgames.numberdumber;

import java.util.List;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.MyInterstitialListener;
import com.badlogic.androidgames.framework.Screen;

public class GameScreen extends Screen 
	{
    	enum GameState 
    		{
    			Ready,
    			Running,
    			Winner
    		}
    
    	GameState state = GameState.Ready;
    	
    	int oldScore = 0, goal;
    	int downX = 0, downY = 0, upX = 0, upY = 0;
    	
    	int goalSequence[][] = {
    								{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0},
    								{1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15, 4, 8, 12, 0},
    								{1, 3, 5, 7, 2, 4, 6, 8, 9, 11, 13, 15, 10, 12, 14, 0},
    								{1, 3, 5, 7, 9, 11, 13, 15, 2, 4, 6, 8, 10, 12, 14, 0},
    								{0, 15, 14, 13, 9, 10, 11, 12, 8, 7, 6, 5, 1, 2, 3, 4},
    								{1, 2, 3, 4, 12, 13, 14, 5, 11, 0, 15, 6, 10, 9, 8, 7},
    								{7, 8, 9, 10, 6, 1, 2, 11, 5, 4, 3, 12, 0, 15, 14, 13},
    								{7, 11, 14, 0, 4, 8, 12, 15, 2, 5, 9, 13, 1, 3, 6, 10},
    								{15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0}
    						   };
    	public static int currentSequence[] = new int[16];
    	
    	String score = "0";
    	
    	boolean validTouch = false, record = false, showAd = false;
    	
    	Tiles tiles = new Tiles();
    	
    	MyInterstitialListener mL;
    	
    	public GameScreen(Game game, int level) 
    		{
    			super(game);
    			mL = (MyInterstitialListener)game;
    		    goal = level;
    			currentSequence[0] = 15;	currentSequence[1] = 4;		currentSequence[2] = 12;	currentSequence[3] = 8;
    			currentSequence[4] = 2;	currentSequence[5] = 14;		currentSequence[6] = 5;	currentSequence[7] = 9;
    			currentSequence[8] = 7;	currentSequence[9] = 10;		currentSequence[10] = 3;	currentSequence[11] = 13;
    			currentSequence[12] = 1;	currentSequence[13] = 11;		currentSequence[14] = 6;	currentSequence[15] = 0;
    		}
    	
    	@Override public void update(float deltaTime) 
    		{
    			List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
    			game.getInput().getKeyEvents();
        
    			if(state == GameState.Ready)
    				updateReady(touchEvents);
    			if(state == GameState.Running)
    				updateRunning(touchEvents);  
    			if(state == GameState.Winner)
    				updateWinner(touchEvents);
    		}
    
    	private void updateReady(List<TouchEvent> touchEvents) 
    		{
    			Graphics g = game.getGraphics();
    			int len = touchEvents.size();
    			for(int i = 0; i < len; i++)
    				{
    					TouchEvent event = touchEvents.get(i);
    					if(event.type == TouchEvent.TOUCH_DOWN)
    						{
    							if(event.x > (g.getWidth()/2) - 100 && event.y > g.getHeight() - 65 && event.x < (g.getWidth()/2) + 100 && event.y < g.getHeight() - 15)
    								{
    									state = GameState.Running;
    									if(Settings.soundEnabled)
    										Assets.click.play(1);
    								}
    						}
    				}
    		}
    
    	private void updateRunning(List<TouchEvent> touchEvents) 
    		{        
    			int len = touchEvents.size();
    			
    			Graphics g = game.getGraphics();
    			
    			for(int i = 0; i < len; i++) 
    				{
    					TouchEvent event = touchEvents.get(i);
    					
    					if(event.type == TouchEvent.TOUCH_DOWN) 
							{
								if(event.x > 10 && event.y > 90 && event.x < 310 && event.y < 390) 
									{
										downX = (event.x - 10)/75;
										downY = (event.y - 90)/75;
										validTouch = true;
									}
								if(event.x > (g.getWidth() - 72) && event.y > 408) 
									{
										if(Settings.soundEnabled)
											Assets.click.play(1);
										state = GameState.Ready;
										return;
									}
								if(event.x > 8 && event.y > 408 && event.x < 72 && event.y < 472)
									{
										if(Settings.soundEnabled)
											Assets.click.play(1);
										mL.showInterstitial();
										game.setScreen(new MainMenuScreen(game));
										return;
									}
							}
    					if(event.type == TouchEvent.TOUCH_DRAGGED) 
    						{
    							if(event.x > 10 && event.y > 90 && event.x < 310 && event.y < 390) 
									{
										upX = (event.x - 10)/75;
										upY = (event.y - 90)/75;
										
										if((upX - downX) == 1 && downY == upY && validTouch)
											{
												if(tiles.moveRight(downX, downY))
													{
														oldScore++;
														score = "" + oldScore;
														validTouch = false;
														if(Settings.soundEnabled)
															Assets.click1.play(1);
														checkFinish();
													}
											}
											
										if((downX - upX) == 1 && downY == upY && validTouch)
											{
												if(tiles.moveLeft(downX, downY))
													{
														oldScore++;
														score = "" + oldScore;
														validTouch = false;
														if(Settings.soundEnabled)
															Assets.click1.play(1);
														checkFinish();
													}
											}
										
										if((downY - upY) == 1 && downX == upX && validTouch)
											{
												if(tiles.moveUp(downX, downY))
													{
														oldScore++;
														score = "" + oldScore;
														validTouch = false;
														if(Settings.soundEnabled)
															Assets.click1.play(1);
														checkFinish();
													}
											}
										
										if((upY - downY) == 1 && downX == upX && validTouch)
											{
												if(tiles.moveDown(downX, downY))
													{
														oldScore++;
														score = "" + oldScore;
														validTouch = false;
														if(Settings.soundEnabled)
															Assets.click1.play(1);
														checkFinish();
													}
											}
									}
    						}
    				}
    		}
    
    	private void checkFinish() 
    		{
				boolean finish = true;
				
    			for(int i = 0; i <= 15; i++)
					{
						if(currentSequence[i] != goalSequence[goal][i])
							finish = false;
					}
    			
    			if(finish)
    				{
    					if(Settings.addScore(goal, oldScore))
    						{
    							record = true;
    							if(Settings.soundEnabled)
    								Assets.recordsound.play(1);
    							Settings.save(game.getFileIO());
    						}
    					else
    						{
    							record = false;
    							if(Settings.soundEnabled)
    								Assets.win.play(1);
    						}
    					state = GameState.Winner;
    				}
    		}

		private void updateWinner(List<TouchEvent> touchEvents) 
			{
				Graphics g = game.getGraphics();
				int len = touchEvents.size();
				for(int i = 0; i < len; i++)
					{
						TouchEvent event = touchEvents.get(i);
						if(event.type == TouchEvent.TOUCH_DOWN)
							{
								if(event.x > 8 && event.y > 404 && event.x < 108 && event.y < 468)
									{
										if(Settings.soundEnabled)
											Assets.click.play(1);
										mL.showInterstitial();
										game.setScreen(new MainMenuScreen(game));
										return;
									}
								if(event.x > g.getWidth() - 108 && event.y > 404 && event.x < g.getWidth() - 8 && event.y < 468)
									{
										if(Settings.soundEnabled)
											Assets.click.play(1);
										mL.showInterstitial();
										game.setScreen(new GoalScreen(game));
										return;
									}
							}
					}
			}
       	
    	@Override public void present(float deltaTime) 
    		{
    			Graphics g = game.getGraphics();
        
    			g.drawPixmap(Assets.background, 0, 0);
    		
    			if(state == GameState.Ready) 
    				drawReadyUI();
    			if(state == GameState.Running)
    				drawRunningUI();
    			if(state == GameState.Winner)
    				drawGameWinnerUI();
      		}
    
    	private void drawReadyUI() 
    		{    			
    			Graphics g = game.getGraphics();
    			int i = 0;
    			g.drawPixmap(Assets.slate, 0, 80);
    			for(int y = 0; y <= 3; y++)
    				{
    					for(int x = 0; x <= 3; x++)
    						{
    							i = 4 * y + x;
    							g.drawPixmap(Assets.number[goalSequence[goal][i]], (x*75) + 10, (y*75) + 90);
    						}
    				}
    			g.drawPixmap(Assets.gotit, (g.getWidth()/2) - 100, (g.getHeight()-65));    			
    		}
    
    	private void drawRunningUI() 
    		{
    			Graphics g = game.getGraphics();
    			int i = 0;
    			g.drawPixmap(Assets.slate, 0, 80);
    			for(int y = 0; y <= 3; y++)
    				{
    					for(int x = 0; x <= 3; x++)
    						{
    						    i = 4 * y + x;
    							g.drawPixmap(Assets.number[currentSequence[i]], (x*75) + 10, (y*75) + 90);
    						}
    				}
    			g.drawPixmap(Assets.goal, g.getWidth()-72, 408);
    			g.drawPixmap(Assets.buttons, 8, 408, 0, 128, 64, 64);
    			g.drawPixmap(Assets.moves, (g.getWidth()/2)-50, 404);
    			drawText(g, score, g.getWidth() / 2 - score.length()*20 / 2, g.getHeight() - 42);                
    		}
    
    	private void drawGameWinnerUI()
    		{
    			Graphics g = game.getGraphics();
    			
    			g.drawPixmap(Assets.slate, 0, 80);
    			
    			if(record)
					g.drawPixmap(Assets.newrecord, 10, 90);
    			else
    				g.drawPixmap(Assets.goalcompleted, 10, 90);
    			
    			g.drawPixmap(Assets.quit, 8, 404);
    			g.drawPixmap(Assets.next, g.getWidth() - 108, 404);
    		}
 
    	public void drawText(Graphics g, String line, int x, int y) 
    		{
    			int len = line.length();
    			for (int i = 0; i < len; i++) 
    				{
    					char character = line.charAt(i);

    					if (character == ' ') 
    						{
    							x += 20;
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