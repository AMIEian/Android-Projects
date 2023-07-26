package nitin.sangale.androidgames.egg;

public class Clock {
	//private int hours;
	private int minutes;
	private int seconds;
	private float stateTime;
	public Clock()
	{
		//hours = 0;
		minutes = 0;
		seconds = 0;
		stateTime = 0;
	}
	
	public void update(float deltaTime)
	{
		stateTime = stateTime + deltaTime;
		if(stateTime >= 1)
		{
			stateTime = 0;
			seconds = seconds + 1;
			if(seconds == 60)
			{
				seconds = 0;
				minutes = minutes + 1;
				//if(minutes == 60)
				//{
					//minutes = 0;
					//hours = hours + 1;
					//if(hours == 99)
						//hours = 0;
				//}
			}
			
		}
	}
	
	public String getTime()
	{
		String time = new String();
		time = minutes+":"+seconds;
		return time;
	}
}
