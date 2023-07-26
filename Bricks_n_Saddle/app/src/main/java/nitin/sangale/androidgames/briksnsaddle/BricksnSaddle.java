package nitin.sangale.androidgames.briksnsaddle;

import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.AndroidGame;

public class BricksnSaddle extends AndroidGame 
	{
		@Override public Screen getStartScreen() 
			{
				return new LoadingScreen(this);
			}
	}