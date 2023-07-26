package nitin.sangale.androidgames.numberdumber;

import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.AndroidGame;
public class NumberDumberGame extends AndroidGame 
	{
		@Override public Screen getStartScreen() 
			{
				return new LoadingScreen(this);
			}
	}