package nitin.sangale.hanumanaradhana;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class Index extends Activity {
	MediaPlayer mediaPlayer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);
		//mediaPlayer=MediaPlayer.create(this,R.raw.shankh);
	}

	public void LoadChalisa(View view)
		{
			//Play Ghanti
			mediaPlayer=MediaPlayer.create(this,R.raw.ghanta);
			mediaPlayer.start();
			while(mediaPlayer.isPlaying());
			mediaPlayer.stop();
			mediaPlayer.release();
			
			Intent i = new Intent(this, Book.class);
			// Pass all data
			i.putExtra("START", 1);
			i.putExtra("END", 12);
			// Open SingleItemView.java Activity
			startActivity(i);
		}
	
	public void LoadAshtak(View view)
		{
			//Play Ghanti
			mediaPlayer=MediaPlayer.create(this,R.raw.ghanta);
			mediaPlayer.start();
			while(mediaPlayer.isPlaying());
			mediaPlayer.stop();
			mediaPlayer.release();
			
			Intent i = new Intent(this, Book.class);
			// Pass all data
			i.putExtra("START", 13);
			i.putExtra("END", 20);
			// Open SingleItemView.java Activity
			startActivity(i);
		}
	
	public void LoadBan(View view)
		{
			//Play Ghanti
			mediaPlayer=MediaPlayer.create(this,R.raw.ghanta);
			mediaPlayer.start();
			while(mediaPlayer.isPlaying());
			mediaPlayer.stop();
			mediaPlayer.release();
			
			Intent i = new Intent(this, Book.class);
			// Pass all data
			i.putExtra("START", 21);
			i.putExtra("END", 29);
			// Open SingleItemView.java Activity
			startActivity(i);
		}
	
	public void LoadStavan(View view)
		{
			//Play Ghanti
			mediaPlayer=MediaPlayer.create(this,R.raw.ghanta);
			mediaPlayer.start();
			while(mediaPlayer.isPlaying());
			mediaPlayer.stop();
			mediaPlayer.release();
		
			Intent i = new Intent(this, Book.class);
			// Pass all data
			i.putExtra("START", 30);
			i.putExtra("END", 32);
			// Open SingleItemView.java Activity
			startActivity(i);
		}
	
	public void LoadStuti(View view)
		{
			//Play Ghanti
			mediaPlayer=MediaPlayer.create(this,R.raw.ghanta);
			mediaPlayer.start();
			while(mediaPlayer.isPlaying());
			mediaPlayer.stop();
			mediaPlayer.release();
			
			Intent i = new Intent(this, Book.class);
			// Pass all data
			i.putExtra("START", 36);
			i.putExtra("END", 39);
			// Open SingleItemView.java Activity
			startActivity(i);
		}
	
	public void LoadAarti(View view)
		{
			//Play Ghanti
			mediaPlayer=MediaPlayer.create(this,R.raw.ghanta);
			mediaPlayer.start();
			while(mediaPlayer.isPlaying());
			mediaPlayer.stop();
			mediaPlayer.release();
		
			Intent i = new Intent(this, Book.class);
			// Pass all data
			i.putExtra("START", 33);
			i.putExtra("END", 35);
			// Open SingleItemView.java Activity
			startActivity(i);
		}
	
	public void LoadYantra(View view)
		{
			//Play Ghanti
			mediaPlayer=MediaPlayer.create(this,R.raw.ghanta);
			mediaPlayer.start();
			while(mediaPlayer.isPlaying());
			mediaPlayer.stop();
			mediaPlayer.release();
		
			Intent i = new Intent(this, Book.class);
			// Pass all data
			i.putExtra("START", 40);
			i.putExtra("END", 40);
			// Open SingleItemView.java Activity
			startActivity(i);
		}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.index, menu);
		return true;
	}

}
