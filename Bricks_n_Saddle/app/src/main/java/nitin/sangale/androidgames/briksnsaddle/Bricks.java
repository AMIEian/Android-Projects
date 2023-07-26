package nitin.sangale.androidgames.briksnsaddle;

public class Bricks 
	{
		public static boolean TYPE [][] = new boolean [8][4];
		public Bricks()
			{
				for(int y = 3; y <= 6; y++)
					{
						for(int x = 1; x <= 8; x++)
							{
								TYPE[x-1][y-3] = true;
							}
					}
				TYPE [0][1] = false;
				TYPE [7][1] = false;
				TYPE [0][2] = false;
				TYPE [7][2] = false;
			}
		
		public static boolean getType(int x, int y)
			{
				int typex, typey;
				typex = x - 1;
				typey = y - 3;
				if((typex >= 0 && typex <= 7) && (typey >= 0 && typey <= 3))
					{
						return TYPE[typex][typey];
					}
				else
					{
						return false;
					}
			}
		
		public static void setType(int x, int y, boolean type)
			{
				int typex, typey;
				typex = x - 1;
				typey = y - 3;
				if((typex >= 0 && typex <= 7) && (typey >= 0 && typey <= 3))
					{
						TYPE[typex][typey] = type;
					}
			}
	}
