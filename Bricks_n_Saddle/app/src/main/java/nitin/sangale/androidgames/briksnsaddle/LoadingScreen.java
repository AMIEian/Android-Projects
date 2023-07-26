package nitin.sangale.androidgames.briksnsaddle;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Graphics;
import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.Graphics.PixmapFormat;

public class LoadingScreen extends Screen 
	{
    	public LoadingScreen(Game game) 
    		{
    			super(game);
    		}

    	@Override public void update(float deltaTime) 
    		{
    			Graphics g = game.getGraphics();
    			Assets.background = g.newPixmap("background.png", PixmapFormat.RGB565);
    			Assets.logo = g.newPixmap("logo.png", PixmapFormat.ARGB4444);
    			Assets.mainMenu = g.newPixmap("mainmenu.png", PixmapFormat.ARGB4444);
    			Assets.buttons = g.newPixmap("buttons.png", PixmapFormat.ARGB4444);
    			Assets.help1 = g.newPixmap("help1.png", PixmapFormat.ARGB4444);
    			Assets.help2 = g.newPixmap("help2.png", PixmapFormat.ARGB4444);
    			Assets.help3 = g.newPixmap("help3.png", PixmapFormat.ARGB4444);
    			Assets.numbers = g.newPixmap("numbers.png", PixmapFormat.ARGB4444);
    			Assets.ready = g.newPixmap("ready.png", PixmapFormat.ARGB4444);
    			Assets.pause = g.newPixmap("pausemenu.png", PixmapFormat.ARGB4444);
    			Assets.gameOver = g.newPixmap("gameover.png", PixmapFormat.ARGB4444);
    			Assets.brick = g.newPixmap("brick.png", PixmapFormat.ARGB4444);
    			Assets.ball = g.newPixmap("ball.png", PixmapFormat.ARGB4444);
    			Assets.saddle = g.newPixmap("saddle.png", PixmapFormat.ARGB4444);
    			Assets.level = g.newPixmap("level.png", PixmapFormat.ARGB4444);
    			Assets.winner = g.newPixmap("winner.png", PixmapFormat.ARGB4444);
    			Assets.about = g.newPixmap("about.png", PixmapFormat.ARGB4444);
    			Assets.life = g.newPixmap("life.png", PixmapFormat.ARGB4444);
    			
    			Assets.click = game.getAudio().newSound("click.ogg");
    			Assets.bitten = game.getAudio().newSound("bitten.ogg");
    			Assets.click1 = game.getAudio().newSound("click1.ogg");
    			Assets.levelup = game.getAudio().newSound("levelup.ogg");
    			Assets.music = game.getAudio().newSound("music.ogg");
    			Assets.win = game.getAudio().newSound("win.ogg");
    			Assets.live = game.getAudio().newSound("live.ogg");
    			
    			Settings.load(game.getFileIO());
    			
    			Assets.startup = game.getAudio().newMusic("startup.mp3");
    			Assets.startup.setLooping(false);
    			
    			
    			game.setScreen(new MainMenuScreen(game));
    		}

    	@Override public void present(float deltaTime) 
    		{

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