package nitin.sangale.androidgames.briksnsaddle;

public class Ball 
	{
		public static int x, y;
		public int xdir, ydir, saddleoldx, count = 0;
		
		public Ball()
			{
				x = 4;
				y = 11;
				xdir = 1;
				ydir = -1;
			}
		
		public void move()
			{
				x = x + (1*xdir);
				if(x < 1)
					{
						if(Settings.soundEnabled)
							Assets.click1.play(1);
						x = 0;
					}
				if(x > 8)
					{
						if(Settings.soundEnabled)
							Assets.click1.play(1);
						x = 9;
					}
				y = y + (1*ydir);
				if(y < 1)
					{
						if(Settings.soundEnabled)
							Assets.click1.play(1);
						y = 0;
					}
				if(y > 12)
					y = 12;
				changedir();
			}
		
		public void changedir()
			{
				//Change xdir
				if(x < 1)
					xdir = xdir*(-1);
				if(x > 8)
					xdir = xdir*(-1);
				
				//Change ydir
				if(y < 1)
					ydir = ydir*(-1);
				if(y == 10)
					saddleoldx = Saddle.x;
				if(y == 11)
					{
						//Check collision with saddle
						if(x >= Saddle.x && x <= (Saddle.x + 3))
							{
								ydir = ydir*(-1);
								if(saddleoldx > Saddle.x)
									xdir = -1;
								if(saddleoldx < Saddle.x)
									xdir = 1;
								if(Settings.soundEnabled)
		    						Assets.click1.play(1);
							}
						if(x == (Saddle.x - 1) && xdir == 1 && ydir == 1)
							{
								xdir = -1;
								ydir = -1;
							}
						if(x == (Saddle.x + 4) && xdir == -1 && ydir == 1)
							{
								xdir = 1;
								ydir = -1;
							}
					}
				//Check collision with Bricks
				if((x >= 0 && x <= 9) && (y >= 2 && y <= 7))
					{
						//Check horizontal collision
						if(y > 2 && y < 7)
							{
								if(x < 8)
									{
										//Check right brick collision
										if(xdir == 1)
											{
												if(Bricks.getType(x+1,y))
													{
														if(Settings.soundEnabled)
															Assets.music.play(1);
														count++;
														Bricks.setType(x+1, y, false);
														xdir = -1;
													}
											}
									}
								if(x > 1)
									{
										//Check left brick collision
										if(xdir == -1)
											{
												if(Bricks.getType(x-1,y))
													{
														if(Settings.soundEnabled)
															Assets.music.play(1);
														count++;
														Bricks.setType(x-1, y, false);
														xdir = 1;
													}
											}
									}
							}
						
						//Check vertical collision
						if(x > 0 && x < 9)
							{
								if(y > 3)
									{
										//Check top brick collision
										if(ydir == -1)
											{
												if(Bricks.getType(x,y-1))
													{
														if(Settings.soundEnabled)
															Assets.music.play(1);
														count++;
														Bricks.setType(x, y-1, false);
														ydir = 1;
													}
											}
									}
								if(y < 6)
									{
										//Check bottom brick collision
										if(ydir == 1)
											{
												if(Bricks.getType(x,y+1))
													{
														if(Settings.soundEnabled)
															Assets.music.play(1);
														count++;
														Bricks.setType(x, y+1, false);
														ydir = -1;
													}
											}
									}
							}
						
						//Check diagonal collision
						if(xdir == 1)
							{
								//Check top right corner
								if(ydir == -1)
									{
										if(Bricks.getType(x+1, y-1))
											{
												if(Bricks.getType(x, y-1) == false && Bricks.getType(x+1, y) == false)
													{
														if(Settings.soundEnabled)
															Assets.music.play(1);
														count++;
														Bricks.setType(x+1, y-1, false);
														xdir = xdir*(-1);
														ydir = ydir*(-1);
													}
											}
									}
								//Check bottom right corner
								if(ydir == 1)
									{
										if(Bricks.getType(x+1, y+1))
											{
												if(Bricks.getType(x, y+1) == false && Bricks.getType(x+1, y) == false)
													{
														if(Settings.soundEnabled)
															Assets.music.play(1);
														count++;
														Bricks.setType(x+1, y+1, false);
														xdir = xdir*(-1);
														ydir = ydir*(-1);
													}
											}
									}
							}
						if(xdir == -1)
							{
								//Check top left corner
								if(ydir == -1)
									{
										if(Bricks.getType(x-1, y-1))
											{
												if(Bricks.getType(x, y-1) == false && Bricks.getType(x-1, y) == false)
													{
														if(Settings.soundEnabled)
															Assets.music.play(1);
														count++;
														Bricks.setType(x-1, y-1, false);
														xdir = xdir*(-1);
														ydir = ydir*(-1);
													}
											}
									}
								//Check bottom left corner
								if(ydir == 1)
									{
										if(Bricks.getType(x-1, y+1))
											{
												if(Bricks.getType(x, y+1) == false && Bricks.getType(x-1, y) == false)
													{
														if(Settings.soundEnabled)
															Assets.music.play(1);
														count++;
														Bricks.setType(x-1, y+1, false);
														xdir = xdir*(-1);
														ydir = ydir*(-1);
													}
											}
									}
							}
					}
			}
		
		public boolean endGame()
			{
				if(y == 12)
					return true;
				else
					return false;
			}
		
		public int getScore()
			{
				return (count*10);
			}
	}
