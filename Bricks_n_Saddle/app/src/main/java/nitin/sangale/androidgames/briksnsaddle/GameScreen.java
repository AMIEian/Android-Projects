package nitin.sangale.androidgames.briksnsaddle;

import java.util.List;

import android.graphics.Color;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.MyInterstitialListener;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.Pixmap;
import com.badlogic.androidgames.framework.Screen;

public class GameScreen extends Screen
	{
    	enum GameState 
    		{
    			Ready,
    			Running,
    			Paused,
    			LevelComplete,
    			GameOver,
    			Winner
    		}
    
    	GameState state = GameState.Ready;
    	World world;
    	int oldScore = 0, totalScore = 0, level = 0, bonus = 100, lifes = 3;
    	String score = "0";
    	
    	MyInterstitialListener mL;
    
    	public GameScreen(Game game) 
    		{
    			super(game);
    			mL = (MyInterstitialListener)game;
    			world = new World(0.30);
    		}
    	

    	@Override public void update(float deltaTime) 
    		{
    			List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
    			game.getInput().getKeyEvents();
        
    			if(state == GameState.Ready)
    				updateReady(touchEvents);
    			if(state == GameState.Running)
    				updateRunning(touchEvents, deltaTime);
    			if(state == GameState.Paused)
    				updatePaused(touchEvents);
    			if(state == GameState.LevelComplete)
    				updateLevelComplete(touchEvents);
    			if(state == GameState.GameOver)
    				updateGameOver(touchEvents);  
    			if(state == GameState.Winner)
    				updateWinner(touchEvents);
    		}
    
    	private void updateReady(List<TouchEvent> touchEvents) 
    		{
    			if(touchEvents.size() > 0)
    				state = GameState.Running;
    		}
    
    	private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) 
    		{        
    			int len = touchEvents.size();
    			for(int i = 0; i < len; i++) 
    				{
    					TouchEvent event = touchEvents.get(i);
    					if(event.type == TouchEvent.TOUCH_UP) 
    						{
    							if(event.x < 64 && event.y < 64) 
    								{
    									if(Settings.soundEnabled)
    										Assets.click.play(1);
    									state = GameState.Paused;
    									return;
    								}
    						}
    					if(event.type == TouchEvent.TOUCH_DOWN) 
    						{
    							if(event.x < 64 && event.y > 416) 
    								{
    									world.saddle.moveleft();
    								}
    							if(event.x > 256 && event.y > 416) 
    								{
    									world.saddle.moveright();
    								}
    						}
    				}
        
    			world.update(deltaTime);
    			if(world.gameOver) 
    				{
    					//if(totalScore == 0)
    						//totalScore = world.ball.getScore();
    					//else
    						//totalScore = totalScore + world.ball.getScore();
    					if(lifes > 1)
    					{
    						lifes = lifes - 1;
    						world = new World(0.30 - (0.05*level));
    						if(Settings.soundEnabled)
    							Assets.live.play(1);
    						totalScore = oldScore;
    						state = GameState.Ready;
    					}
    					else
    					{
    						Settings.addScore(oldScore);
    						Settings.save(game.getFileIO());
    					
    						if(Settings.soundEnabled)
    							Assets.bitten.play(1);
    						state = GameState.GameOver;
    					}
    				}
    			if(oldScore != world.ball.getScore()) 
    				{
    					if(world.ball.getScore() < 280)
    						{
    							oldScore = world.ball.getScore() + totalScore;
    							score = "" + oldScore;
    						}
    					else
							{
								if(level <= 4)
									{
										totalScore = totalScore + world.ball.getScore() + (bonus*level);
										score = "" + totalScore;
										oldScore = 0;
										if(Settings.soundEnabled)
				    						Assets.levelup.play(1);
										state = GameState.LevelComplete;
									}
								else
									{
										totalScore = totalScore + world.ball.getScore()+ (bonus*level);
										Settings.addScore(totalScore);
				    					Settings.save(game.getFileIO());
										oldScore = 0;
										if(Settings.soundEnabled)
											Assets.win.play(1);
										state = GameState.Winner;
									}
							}
    				}
    		}
    
    	private void updatePaused(List<TouchEvent> touchEvents) 
    		{
    			int len = touchEvents.size();
    			for(int i = 0; i < len; i++) 
    				{
    					TouchEvent event = touchEvents.get(i);
    					if(event.type == TouchEvent.TOUCH_UP) 
    						{
    							if(event.x > 80 && event.x <= 240) 
    								{
    									if(event.y > 100 && event.y <= 148) 
    										{
    											if(Settings.soundEnabled)
    												Assets.click.play(1);
    											state = GameState.Running;
    											return;
    										}                    
    									if(event.y > 148 && event.y < 196) 
    										{
    											if(Settings.soundEnabled)
    												Assets.click.play(1);
    											mL.showInterstitial();
    											game.setScreen(new MainMenuScreen(game));                        
    											return;
    										}
    								}
    						}
    				}
    		}
    	
    	private void updateLevelComplete(List<TouchEvent> touchEvents)
    		{
    			if(touchEvents.size() > 0)
    				{
    					//if(Settings.soundEnabled)
    						//Assets.click.play(1);
    					level++;
    					world = new World(0.30 - (0.05*level));
    					state=GameState.Ready;
    				}
    		}
    	private void updateWinner(List<TouchEvent> touchEvents) 
			{
				if(touchEvents.size() > 0)
					{
						if(Settings.soundEnabled)
							Assets.click.play(1);
						mL.showInterstitial();
						game.setScreen(new MainMenuScreen(game));
						return;
					}
			}
    	private void updateGameOver(List<TouchEvent> touchEvents) 
    		{
    			int len = touchEvents.size();
    			for(int i = 0; i < len; i++) 
    				{
    					TouchEvent event = touchEvents.get(i);
    					if(event.type == TouchEvent.TOUCH_UP) 
    						{
    							if(event.x > 256 && event.y > 416 ) 
    								{    									
    									if(Settings.soundEnabled)
    										Assets.click.play(1);
    									mL.showInterstitial();
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
    			
    			drawWorld(world);
    			if(state == GameState.Ready) 
    				drawReadyUI();
    			if(state == GameState.Running)
    				drawRunningUI();
    			if(state == GameState.Paused)
                    drawPausedUI();
    			if(state == GameState.LevelComplete)
    				drawLevelCompleteUI();
    			if(state == GameState.GameOver)
    				drawGameOverUI();
    			if(state == GameState.Winner)
    				drawGameWinnerUI();
        
    			drawText(g, score, g.getWidth() / 2 - score.length()*20 / 2, g.getHeight() - 42);
    		}
    
    	private void drawWorld(World world) 
    		{
    			Graphics g = game.getGraphics();
        
    			Pixmap brickPixmap = Assets.brick;
    			for(int y = 3; y <= 6; y++)
					{
						for(int x = 1; x <= 8; x++)
							{
								if(Bricks.getType(x, y))
									{
										g.drawPixmap(brickPixmap, x*32, y*32);
									}
							}
					}
    			
    			Pixmap ballPixmap = Assets.ball;
    			g.drawPixmap(ballPixmap, (Ball.x)*32, (Ball.y)*32);
    			
    			Pixmap saddlePixmap = Assets.saddle;
    			g.drawPixmap(saddlePixmap, (Saddle.x)*32, (Saddle.y)*32);
    		}
    
    	private void drawReadyUI() 
    		{
    			Graphics g = game.getGraphics();
        
    			g.drawPixmap(Assets.ready, 47, 100);
    			g.drawLine(0, 416, 480, 416, Color.BLACK);
    			for(int i = lifes; i >= 1; i--)
    			{
    				g.drawPixmap(Assets.life, 304 - i*32, 16);
    			}
    		}
    
    	private void drawRunningUI() 
    		{
    			Graphics g = game.getGraphics();

    			g.drawPixmap(Assets.buttons, 0, 0, 64, 128, 64, 64);
    			g.drawLine(0, 416, 480, 416, Color.BLACK);
    			g.drawPixmap(Assets.buttons, 0, 416, 64, 64, 64, 64);
    			g.drawPixmap(Assets.buttons, 256, 416, 0, 64, 64, 64);
    			for(int i = lifes; i >= 1; i--)
    			{
    				g.drawPixmap(Assets.life, 304 - i*32, 16);
    			}
    		}
    
    	private void drawPausedUI() 
    		{
    			Graphics g = game.getGraphics();
        
    			g.drawPixmap(Assets.pause, 80, 100);
    			g.drawLine(0, 416, 480, 416, Color.BLACK);
    			for(int i = lifes; i >= 1; i--)
    			{
    				g.drawPixmap(Assets.life, 304 - i*32, 16);
    			}
    		}
    	
    	private void drawLevelCompleteUI()
    		{
    			Graphics g = game.getGraphics();
    			
    			g.drawPixmap(Assets.level, 0, 0);
    			g.drawLine(0, 416, 480, 416, Color.BLACK);
    		}
    	
    	private void drawGameWinnerUI()
    		{
    			Graphics g = game.getGraphics();
    			
    			g.drawPixmap(Assets.winner, 0, 0);
    			g.drawLine(0, 416, 480, 416, Color.BLACK);
    		}
    	private void drawGameOverUI() 
    		{
    			Graphics g = game.getGraphics();
        
    			g.drawPixmap(Assets.gameOver, 0,0);
    			g.drawLine(0, 416, 480, 416, Color.BLACK);
    			g.drawPixmap(Assets.buttons, 256, 416, 0, 128, 64, 64);
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
    			if(state == GameState.Running)
    				state = GameState.Paused;
        
    			//if(state == GameState.Winner) 
    				//{
    					//Settings.addScore(totalScore);
    					//Settings.save(game.getFileIO());
    				//}
    		}

    	@Override public void resume() 
    		{
        
    		}

    	@Override public void dispose() 
    		{
        
    		}
	}