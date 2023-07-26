package nitin.sangale.androidgames.numberdumber;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.badlogic.androidgames.framework.FileIO;

public class Settings 
	{
    	public static boolean soundEnabled = true;
    	public static int[] highscores = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    	public static void load(FileIO files) 
    		{
    			BufferedReader in = null;
    			try 
    				{
    					in = new BufferedReader(new InputStreamReader(files.readFile(".numberdumber")));
    					soundEnabled = Boolean.parseBoolean(in.readLine());
    					for (int i = 0; i < 9; i++) 
    						{
    							highscores[i] = Integer.parseInt(in.readLine());
    						}
    				} 
    			catch (IOException e) 
    				{
    					// :( It's ok we have defaults
    				} 
    			catch (NumberFormatException e) 
    				{
    					// :/ It's ok, defaults save our day
    				} 
    			finally 
    				{
    					try 
    						{
    							if (in != null)
    								in.close();
    						} 
    					catch (IOException e) 
    						{
    						}
    				}
    		}

    	public static void save(FileIO files) 
    		{
    			BufferedWriter out = null;
    			try 
    				{
    					out = new BufferedWriter(new OutputStreamWriter(files.writeFile(".numberdumber")));
    					out.write(Boolean.toString(soundEnabled));
    					out.write("\n");
    					for (int i = 0; i < 9; i++) 
    						{
    							out.write(Integer.toString(highscores[i]));
    							out.write("\n");
    						}

    				} 
    			catch (IOException e) 
    				{
    				} 
    			finally 
    				{
    					try 
    						{
    							if (out != null)
    								out.close();
    						} 
    					catch (IOException e) 
    						{
    						}
    				}
    		}

    	public static boolean addScore(int level, int score) 
    		{
    			if(highscores[level] == 0)
    				{
    					highscores[level] = score;
    					return true;
    				}
    			else
    				{
    					if(highscores[level] > score)
    						{
    							highscores[level] = score;
    							return true;
    						}
    					else
    						{
    							return false;
    						}
    				}
    		}
	}
