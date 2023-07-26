package nitin.sangale.haripath;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	boolean read;
	MediaPlayer mediaPlayer;
	
	private InterstitialAd interstitial;
	/* Your ad unit id. Replace with your actual ad unit id. */
    private static final String AD_UNIT_ID = "ca-app-pub-6525390886417173/9299261264";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mediaPlayer=MediaPlayer.create(this,R.raw.m);
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

    public void LoadHaripath(View view)
    	{
    		if(mediaPlayer.isPlaying())
    			{
    				Toast.makeText(this,"Please Wait...!",
    						Toast.LENGTH_SHORT).show();
    				return;
    			}
    		read = true;
    	   	Intent intent = new Intent(this, StartHaripathActivity.class);
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
    				String thanks = "पुंडलिक वरदे हरी विठ्ठल" + "\n" + "श्री ज्ञानदेव तुकाराम" + "\n" + "पंढरीनाथ महाराज की जय!" + "\n" + "जय जय रामकृष्ण हरी";
    				Toast toast = Toast.makeText(this, thanks, Toast.LENGTH_LONG);
    				TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
    				if( v != null) v.setGravity(Gravity.CENTER);
    				toast.show();
    			}
    	}
    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        //return true;
    //}
    
}
