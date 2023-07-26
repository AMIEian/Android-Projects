package nitin.sangale.shreeswamipath;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.InterstitialAd;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	boolean read;
	
	//private InterstitialAd interstitial;
	/* Your ad unit id. Replace with your actual ad unit id. */
    private static final String AD_UNIT_ID = "ca-app-pub-6525390886417173/2936705264";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		read = false;
		
		//AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
		// Prepare the Interstitial Ad
     	//interstitial = new InterstitialAd(this);
     	// Insert the Ad Unit ID
     	//interstitial.setAdUnitId(AD_UNIT_ID);
     	// Load ads into Interstitial Ads       
     	//interstitial.loadAd(adRequestBuilder.build());
	}

	public void loadindex(View view)
	{
		read = true;
		Intent intent = new Intent(this, BookIndex.class);
		startActivity(intent);
	}
	
	@Override
    public void onResume()
    	{
			super.onResume();
			/*
			if(read)
				{
					if(interstitial.isLoaded())
						{
							interstitial.show();
						}
				}
			 */
    	}
	
	
	@Override
    public void onDestroy()
    	{
    		super.onDestroy(); // Always call superclass method first.
    		
    		if(read)
    			{
    				String thanks = "|| श्री स्वामीसमर्थ जय जय स्वामीसमर्थ ||";
    				Toast.makeText(this,thanks,
    				Toast.LENGTH_LONG).show();
    			}
    	}
//@Override
//public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	//getMenuInflater().inflate(R.menu.main, menu);
	//return true;
//}

}
