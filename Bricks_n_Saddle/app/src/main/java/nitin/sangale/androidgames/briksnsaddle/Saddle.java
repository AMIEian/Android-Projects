package nitin.sangale.androidgames.briksnsaddle;

public class Saddle 
	{
		public static int x,y;
		public Saddle()
			{
				x = 3;
				y = 12;
			}
		public void moveleft()
			{
				x = x - 1;
				if(x <= 0)
					x = 0;
			}
		public void moveright()
			{
				x = x + 1;
				if(x >= 6)
					x = 6;
			}
	}
