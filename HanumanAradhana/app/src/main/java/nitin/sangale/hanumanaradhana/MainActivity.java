package nitin.sangale.hanumanaradhana;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	MediaPlayer mediaPlayer;
	boolean read;
	
	private InterstitialAd interstitial;
	/* Your ad unit id. Replace with your actual ad unit id. */
    private static final String AD_UNIT_ID = "ca-app-pub-6525390886417173/7822528068";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mediaPlayer=MediaPlayer.create(this,R.raw.shankh);
        mediaPlayer.start();
		read = false;
		
		AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
		// Prepare the Interstitial Ad
     	interstitial = new InterstitialAd(this);
     	// Insert the Ad Unit ID
     	interstitial.setAdUnitId(AD_UNIT_ID);
     	// Load ads into Interstitial Ads       
     	interstitial.loadAd(adRequestBuilder.build());
	}
	
	public void LoadIndex(View view)
		{
			if(mediaPlayer.isPlaying())
				{
					Toast.makeText(this,"Please Wait...!",
	    				Toast.LENGTH_SHORT).show();
					return;
				}
			//mediaPlayer.release();
			read = true;
			Intent intent = new Intent(this, Index.class);
			startActivity(intent);
		}
	
	@Override
    public void onResume()
    	{
			super.onResume();
			if(read)
				{
					if(interstitial.isLoaded())
						{
							interstitial.show();
						}
				}
    	}
	
	@Override
    public void onDestroy()
    	{
    		super.onDestroy(); // Always call superclass method first.
    		if(mediaPlayer.isPlaying())
    			{
    				mediaPlayer.stop();
    			}
    		mediaPlayer.release();
    	
    		if(read)
    			{
    				String thanks = "|| जय श्रीराम - जय हनुमान || ";
    				Toast.makeText(this,thanks,
    				Toast.LENGTH_LONG).show();
    			}
    	}
}
