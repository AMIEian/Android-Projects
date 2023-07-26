package nitin.sangale.androidgames.numberdumber;

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
    			
    			Assets.number[0] = g.newPixmap("0.png", PixmapFormat.ARGB4444);
    			Assets.number[1] = g.newPixmap("1.png", PixmapFormat.ARGB4444);
    			Assets.number[2] = g.newPixmap("2.png", PixmapFormat.ARGB4444);
    			Assets.number[3] = g.newPixmap("3.png", PixmapFormat.ARGB4444);
    			Assets.number[4] = g.newPixmap("4.png", PixmapFormat.ARGB4444);
    			Assets.number[5] = g.newPixmap("5.png", PixmapFormat.ARGB4444);
    			Assets.number[6] = g.newPixmap("6.png", PixmapFormat.ARGB4444);
    			Assets.number[7] = g.newPixmap("7.png", PixmapFormat.ARGB4444);
    			Assets.number[8] = g.newPixmap("8.png", PixmapFormat.ARGB4444);
    			Assets.number[9] = g.newPixmap("9.png", PixmapFormat.ARGB4444);
    			Assets.number[10] = g.newPixmap("10.png", PixmapFormat.ARGB4444);
    			Assets.number[11] = g.newPixmap("11.png", PixmapFormat.ARGB4444);
    			Assets.number[12] = g.newPixmap("12.png", PixmapFormat.ARGB4444);
    			Assets.number[13] = g.newPixmap("13.png", PixmapFormat.ARGB4444);
    			Assets.number[14] = g.newPixmap("14.png", PixmapFormat.ARGB4444);
    			Assets.number[15] = g.newPixmap("15.png", PixmapFormat.ARGB4444);
    			
    			Assets.background = g.newPixmap("background.png", PixmapFormat.ARGB4444);
    			Assets.logo = g.newPixmap("logo.png", PixmapFormat.ARGB4444);
    			Assets.mainMenu = g.newPixmap("mainmenu.png", PixmapFormat.ARGB4444);
    			Assets.buttons = g.newPixmap("buttons.png", PixmapFormat.ARGB4444);
    			Assets.help1 = g.newPixmap("help1.png", PixmapFormat.ARGB4444);
    			Assets.help2 = g.newPixmap("help2.png", PixmapFormat.ARGB4444);
    			Assets.help3 = g.newPixmap("help3.png", PixmapFormat.ARGB4444);
    			Assets.numbers = g.newPixmap("numbers.png", PixmapFormat.ARGB4444);
    			Assets.lable = g.newPixmap("lable.png", PixmapFormat.ARGB4444);
    			Assets.levels = g.newPixmap("levels.png", PixmapFormat.ARGB4444);
    			Assets.gotit = g.newPixmap("gotit.png", PixmapFormat.ARGB4444);
    			Assets.slate = g.newPixmap("slate.png", PixmapFormat.ARGB4444);
    			Assets.goal = g.newPixmap("goal.png", PixmapFormat.ARGB4444);
    			Assets.about = g.newPixmap("about.png", PixmapFormat.ARGB4444);
    			Assets.cancel = g.newPixmap("cancel.png", PixmapFormat.ARGB4444);
    			Assets.moves = g.newPixmap("moves.png", PixmapFormat.ARGB4444);
    			Assets.goalcompleted = g.newPixmap("goalcompleted.png", PixmapFormat.ARGB4444);
    			Assets.next = g.newPixmap("next.png", PixmapFormat.ARGB4444);
    			Assets.newrecord = g.newPixmap("newrecord.png", PixmapFormat.ARGB4444);
    			Assets.quit = g.newPixmap("quit.png", PixmapFormat.ARGB4444);
    			Assets.record = g.newPixmap("records.png", PixmapFormat.ARGB4444);

    			Assets.click = game.getAudio().newSound("click.ogg");
    			Assets.click1 = game.getAudio().newSound("click1.ogg");
    			Assets.win = game.getAudio().newSound("win.ogg");
    			Assets.recordsound = game.getAudio().newSound("recod.ogg");
    			
    			Settings.load(game.getFileIO());
    			
    			Assets.startup = game.getAudio().newMusic("startup.ogg");
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