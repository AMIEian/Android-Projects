package nitin.sangale.androidgames.numberdumber;

public class Tiles 
	{
		public int tile[][] = new int[4][4];
		
		public Tiles()
			{
				tile[0][0] = 15;	tile[1][0] = 4;	tile[2][0] = 12;	tile[3][0] = 8;
				tile[0][1] = 2;	tile[1][1] = 14;	tile[2][1] = 5;	tile[3][1] = 9;
				tile[0][2] = 7;	tile[1][2] = 10;	tile[2][2] = 3;	tile[3][2] = 13;
				tile[0][3] = 1;	tile[1][3] = 11;	tile[2][3] = 6;	tile[3][3] = 0;
			}

		public boolean moveRight(int x, int y)
			{
				if(tile[x+1][y] == 0)
					{
						tile[x+1][y] = tile[x][y];
						tile[x][y] = 0;
						GameScreen.currentSequence[4 * y + x] = 0;
						GameScreen.currentSequence[(4 * y + x) + 1] = tile[x+1][y];
						return true;
					}
				else
					{
						return false;
					}
			}
		
		public boolean moveLeft(int x, int y)
			{
				if(tile[x-1][y] == 0)
					{
						tile[x-1][y] = tile[x][y];
						tile[x][y] = 0;
						GameScreen.currentSequence[4 * y + x] = 0;
						GameScreen.currentSequence[4 * y + x - 1] = tile[x-1][y];
						return true;
					}
				else
					{
						return false;
					}
			}
		
		public boolean moveUp(int x, int y)
			{
				if(tile[x][y-1] == 0)
					{
						tile[x][y-1] = tile[x][y];
						tile[x][y] = 0;
						GameScreen.currentSequence[4 * y + x] = 0;
						GameScreen.currentSequence[4 * (y-1) + x] = tile[x][y-1];
						return true;
					}
				else
					{
						return false;
					}
			}
		
		public boolean moveDown(int x, int y)
			{
				if(tile[x][y+1] == 0)
					{
						tile[x][y+1] = tile[x][y];
						tile[x][y] = 0;
						GameScreen.currentSequence[4 * y + x] = 0;
						GameScreen.currentSequence[4 * (y+1) + x] = tile[x][y+1];
						return true;
					}
				else
					{
						return false;
					}
			}
	}
