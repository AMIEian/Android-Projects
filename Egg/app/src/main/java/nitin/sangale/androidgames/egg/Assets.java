package nitin.sangale.androidgames.egg;

import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.Sound;
import com.badlogic.androidgames.framework.gl.Animation;
import com.badlogic.androidgames.framework.gl.Font;
import com.badlogic.androidgames.framework.gl.Texture;
import com.badlogic.androidgames.framework.gl.TextureRegion;
import com.badlogic.androidgames.framework.impl.GLGame;

public class Assets {
    public static Texture backgroundTexture;
    public static TextureRegion background;
    public static TextureRegion singleCloud;
    public static TextureRegion doubleCloud;
    public static Animation sparrow;
    public static Animation crow;
    
    public static Texture menuTexture;
    public static TextureRegion mainMenu;
    public static TextureRegion levels;
    public static TextureRegion pauseMenu;
    public static TextureRegion soundOn;
    public static TextureRegion soundOff;
    
    public static Texture itemsTexture;            
    public static Font font;
    public static TextureRegion bowl;
    public static Animation lifeAnim;
    public static Animation thunderAnim;
    public static Animation startAnim;
    public static Animation finishAnim;
    public static TextureRegion bowlWithEgg;
    public static TextureRegion egg;
    public static TextureRegion gameOver;
    public static TextureRegion win;
    public static TextureRegion closeButton;
    
    //public static Texture helpTexture;
    //public static TextureRegion help;
    
    public static Music music;
    public static Sound jumpSound;
    public static Sound clickSound;
    public static Sound life;
    public static Sound thunder;
    public static Sound winner;
    public static Sound end;
    
    public static void load(GLGame game) {
        backgroundTexture = new Texture(game, "background.png");
        background = new TextureRegion(backgroundTexture, 0, 0, 320, 480);
        doubleCloud = new TextureRegion(backgroundTexture, 322, 0, 190, 112);
        singleCloud = new TextureRegion(backgroundTexture, 322, 128, 190, 112);
        sparrow = new Animation(0.1f,
								new TextureRegion(backgroundTexture, 323, 241, 61, 61),
								new TextureRegion(backgroundTexture, 386, 241, 61, 61),
								new TextureRegion(backgroundTexture, 449, 241, 61, 61));
        crow = new Animation(0.2f,
							 new TextureRegion(backgroundTexture, 323, 304, 125, 61),
							 new TextureRegion(backgroundTexture, 323, 367, 125, 61));
        
        menuTexture = new Texture(game, "menu.png");
        mainMenu = new TextureRegion(menuTexture, 8, 8, 208, 272);
        levels = new TextureRegion(menuTexture, 224, 8, 208, 272);
        soundOn = new TextureRegion(menuTexture, 16, 300, 160, 40);
        soundOff = new TextureRegion(menuTexture, 16, 364, 160, 40);
        
        itemsTexture = new Texture(game, "items.png");        
        font = new Font(itemsTexture, 8, 8, 16, 16, 20);
        bowl = new TextureRegion(itemsTexture, 4, 140, 184, 72);
        //bowl = new TextureRegion(itemsTexture, 8, 166, 48, 20);
        lifeAnim = new Animation(0.2f,
        					 new TextureRegion(itemsTexture, 384, 0, 64, 64),
        					 new TextureRegion(itemsTexture, 448, 0, 64, 64),
        					 new TextureRegion(itemsTexture, 384, 0, 64, 64),
        					 new TextureRegion(itemsTexture, 448, 0, 64, 64),
        					 new TextureRegion(itemsTexture, 384, 0, 64, 64),
        					 new TextureRegion(itemsTexture, 448, 0, 64, 64),
        					 new TextureRegion(itemsTexture, 384, 0, 64, 64),
        					 new TextureRegion(itemsTexture, 448, 0, 64, 64));
        thunderAnim = new Animation(0.2f,
        							new TextureRegion(itemsTexture, 288, 0, 32, 64),
        							new TextureRegion(itemsTexture, 320, 0, 32, 64));
        startAnim = new Animation(0.5f,
        						  new TextureRegion(itemsTexture, 0, 448, 128, 32),
        						  new TextureRegion(itemsTexture, 128, 448, 128, 32));
        finishAnim = new Animation(0.5f,
        						   new TextureRegion(itemsTexture, 128, 448, 128, 32),
        						   new TextureRegion(itemsTexture, 256, 448, 128, 32));
        bowlWithEgg = new TextureRegion(itemsTexture, 288, 98, 192, 114);
        egg = new TextureRegion(itemsTexture, 262, 162, 20, 28);
        gameOver = new TextureRegion(itemsTexture, 0, 248, 128, 144);
        win = new TextureRegion(itemsTexture, 128, 248, 136, 144);
        pauseMenu = new TextureRegion(menuTexture, 264, 248, 242, 164);
        closeButton = new TextureRegion(itemsTexture, 448, 448, 32, 32);
        
        //helpTexture = new Texture(game, "help.png");
        //help = new TextureRegion(helpTexture, 8, 8, 294, 354);
        
        music = game.getAudio().newMusic("music.mp3");
        music.setLooping(true);
        music.setVolume(0.5f);
        if(Settings.soundEnabled)
            music.play();
        jumpSound = game.getAudio().newSound("jump.ogg");
        clickSound = game.getAudio().newSound("click.ogg"); 
        life = game.getAudio().newSound("life.ogg");
        thunder = game.getAudio().newSound("thunder.ogg");
        winner = game.getAudio().newSound("win.ogg");
        end = game.getAudio().newSound("end.ogg");
    }       
    
    public static void reload() {
        backgroundTexture.reload();
        itemsTexture.reload();
        menuTexture.reload();
        if(Settings.soundEnabled)
            music.play();
    }
    
    public static void playSound(Sound sound) {
        if(Settings.soundEnabled)
            sound.play(1);
    }
}
