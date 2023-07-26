package nitin.sangale.shreeswamipath;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class BookIndex extends Activity {
	
	MediaPlayer mediaPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_index);
	}

	public void LoadDhyan(View view)
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
		i.putExtra("END", 1);
		// Open SingleItemView.java Activity
		startActivity(i);
	}
	
	public void LoadBhupali(View view)
	{
		//Play Ghanti
		mediaPlayer=MediaPlayer.create(this,R.raw.ghanta);
		mediaPlayer.start();
		while(mediaPlayer.isPlaying());
		mediaPlayer.stop();
		mediaPlayer.release();
		
		Intent i = new Intent(this, Book.class);
		// Pass all data
		i.putExtra("START", 2);
		i.putExtra("END", 4);
		// Open SingleItemView.java Activity
		startActivity(i);
	}
	
	public void LoadAbhanga(View view)
	{
		//Play Ghanti
		mediaPlayer=MediaPlayer.create(this,R.raw.ghanta);
		mediaPlayer.start();
		while(mediaPlayer.isPlaying());
		mediaPlayer.stop();
		mediaPlayer.release();
		
		Intent i = new Intent(this, Book.class);
		// Pass all data
		i.putExtra("START", 5);
		i.putExtra("END", 37);
		// Open SingleItemView.java Activity
		startActivity(i);
	}
	
	public void LoadShlok(View view)
	{
		//Play Ghanti
		mediaPlayer=MediaPlayer.create(this,R.raw.ghanta);
		mediaPlayer.start();
		while(mediaPlayer.isPlaying());
		mediaPlayer.stop();
		mediaPlayer.release();
		
		Intent i = new Intent(this, Book.class);
		// Pass all data
		i.putExtra("START", 38);
		i.putExtra("END", 39);
		// Open SingleItemView.java Activity
		startActivity(i);
	}
	
	public void LoadPad(View view)
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
		i.putExtra("START", 41);
		i.putExtra("END", 42);
		// Open SingleItemView.java Activity
		startActivity(i);
	}
	
	public void LoadVidnyapana(View view)
	{
		//Play Ghanti
		mediaPlayer=MediaPlayer.create(this,R.raw.ghanta);
		mediaPlayer.start();
		while(mediaPlayer.isPlaying());
		mediaPlayer.stop();
		mediaPlayer.release();
		
		Intent i = new Intent(this, Book.class);
		// Pass all data
		i.putExtra("START", 43);
		i.putExtra("END", 45);
		// Open SingleItemView.java Activity
		startActivity(i);
	}
	
	public void LoadVida(View view)
	{
		//Play Ghanti
		mediaPlayer=MediaPlayer.create(this,R.raw.ghanta);
		mediaPlayer.start();
		while(mediaPlayer.isPlaying());
		mediaPlayer.stop();
		mediaPlayer.release();
		
		Intent i = new Intent(this, Book.class);
		// Pass all data
		i.putExtra("START", 46);
		i.putExtra("END", 48);
		// Open SingleItemView.java Activity
		startActivity(i);
	}
	
	public void LoadShejarti(View view)
	{
		//Play Ghanti
		mediaPlayer=MediaPlayer.create(this,R.raw.ghanta);
		mediaPlayer.start();
		while(mediaPlayer.isPlaying());
		mediaPlayer.stop();
		mediaPlayer.release();
		
		Intent i = new Intent(this, Book.class);
		// Pass all data
		i.putExtra("START", 49);
		i.putExtra("END", 52);
		// Open SingleItemView.java Activity
		startActivity(i);
	}
	
	public void LoadParichay(View view)
	{
		//Play Ghanti
		mediaPlayer=MediaPlayer.create(this,R.raw.ghanta);
		mediaPlayer.start();
		while(mediaPlayer.isPlaying());
		mediaPlayer.stop();
		mediaPlayer.release();
		
		Intent i = new Intent(this, Book.class);
		// Pass all data
		i.putExtra("START", 53);
		i.putExtra("END", 58);
		// Open SingleItemView.java Activity
		startActivity(i);
	}
	
	public void LoadAbout(View view)
	{
		//Play Ghanti
		mediaPlayer=MediaPlayer.create(this,R.raw.ghanta);
		mediaPlayer.start();
		while(mediaPlayer.isPlaying());
		mediaPlayer.stop();
		mediaPlayer.release();
		
		Intent i = new Intent(this, Book.class);
		// Pass all data
		i.putExtra("START", 59);
		i.putExtra("END", 59);
		// Open SingleItemView.java Activity
		startActivity(i);
	}
	
	//@Override
	//public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.book_index, menu);
		//return true;
	//}

}
