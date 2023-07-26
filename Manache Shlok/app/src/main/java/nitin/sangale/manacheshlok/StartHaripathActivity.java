package nitin.sangale.manacheshlok;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class StartHaripathActivity extends Activity implements OnGestureListener {
	private GestureDetector gDetector;
	int page = 0, page_ID = 0;
	boolean sarth = false;
	boolean pVisible = true;
	//int [] pages = new int [243];
	//for(int i = 1; i <= 243; i++)
		{
		
		}
	ImageView abhanga;//, backarrow, nextarrow;
	AdView adView2;
	int start, end;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_haripath);
		gDetector = new GestureDetector(this);
		
		//for(int i = 1; i <= 243; i++)
			//{
				//pages[i-1] = getResources().getIdentifier("nitin.sangale.manacheshlok:drawable/p" + i, null, null);
			//}
		
		Intent intent = getIntent();
		if(intent.getIntExtra("LOAD", start) == 1)
			{
				start = 1;
				end = 207;
			}
		else if(intent.getIntExtra("LOAD", start) == 2)
			{
				start = 208;
				end = 243;
			}
		else
			{
				start = 1;
				end = 207;
				sarth = true;
			}
		
		page = start;
		page_ID = getResources().getIdentifier("nitin.sangale.manacheshlok:drawable/p" + page, null, null);
		
		this.abhanga = (ImageView)findViewById(R.id.myCanvas);
		abhanga.setImageResource(page_ID);
		pVisible = false;

		//this.nextarrow = (ImageView)findViewById(R.id.imageView3);
		//this.backarrow = (ImageView)findViewById(R.id.imageView1);
		
		//this.adView1 = (AdView)findViewById(R.id.adView1);
	    AdRequest.Builder adRequestBuilder = new AdRequest.Builder();        
        //adView1.loadAd(adRequestBuilder.build());
        
        //adView1.loadAd(new AdRequest());
        
        this.adView2 = (AdView)findViewById(R.id.adView2);
        adView2.loadAd(adRequestBuilder.build());
        
        //adView2.loadAd(new AdRequest());
	}
	@Override
    public boolean onDown(MotionEvent arg0) {
            // TODO Auto-generated method stub
            return false;
    }
    @Override
    public boolean onFling(MotionEvent start, MotionEvent finish, float xVelocity, float yVelocity) {
            if (start.getRawX() < finish.getRawX()) {
            	ShowPreviousPage(null);
            } else {
            	ShowNextPage(null);
            }
            return true;
    }
    @Override
    public void onLongPress(MotionEvent arg0) {
            // TODO Auto-generated method stub
    }
    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
              // TODO Auto-generated method stub
              return false;
    }
    @Override
    public void onShowPress(MotionEvent arg0) {
            // TODO Auto-generated method stub
    }
    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
            // TODO Auto-generated method stub
            return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent me) {
    return gDetector.onTouchEvent(me);
    }
	@SuppressLint("CutPasteId")
	public void ShowPreviousPage(View view)
		{
			if(sarth == true)
				{
					if(pVisible == true)
						page = page - 1;
				}
			else
				page = page - 1;
			if(page <= start)
				{
					page = start;
					//backarrow.setImageResource(R.drawable.blank);
				}
			//if(page == 37)
				//{
					//nextarrow.setImageResource(R.drawable.next);
				//}
			if(sarth == true)
				{
					if(pVisible == false) {
						page_ID = getResources().getIdentifier("nitin.sangale.manacheshlok:drawable/p" + page, null, null);
						abhanga.setImageResource(page_ID);
						pVisible = true;
					}
					else {
						page_ID = getResources().getIdentifier("nitin.sangale.manacheshlok:drawable/q" + page, null, null);
						abhanga.setImageResource(page_ID);
						pVisible = false;
					}
				}
			else {
				page_ID = getResources().getIdentifier("nitin.sangale.manacheshlok:drawable/p" + page, null, null);
				abhanga.setImageResource(page_ID);
			}
		}
	@SuppressLint("CutPasteId")
	public void ShowNextPage(View view)
		{
			if(sarth == true)
				{
					if(pVisible == false)
						page = page + 1;
				}
			else
				page = page + 1;
			if(page >= end)
				{
					page = end;
					//nextarrow.setImageResource(R.drawable.blank);
				}
			//if(page == 1)
				//{
					//backarrow.setImageResource(R.drawable.previous);
				//}
			if(sarth == true)
				{
					if(pVisible == false) {
						page_ID = getResources().getIdentifier("nitin.sangale.manacheshlok:drawable/p" + page, null, null);
						abhanga.setImageResource(page_ID);
						pVisible = true;
					}
					else {
						page_ID = getResources().getIdentifier("nitin.sangale.manacheshlok:drawable/q" + page, null, null);
						abhanga.setImageResource(page_ID);
						pVisible = false;
					}
				}
			else {
				page_ID = getResources().getIdentifier("nitin.sangale.manacheshlok:drawable/p" + page, null, null);
				abhanga.setImageResource(page_ID);
			}
		}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_haripath, menu);
		return true;
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    	{
			Intent intent = new Intent(this,AboutActivity.class);
			startActivity(intent);
			return true;
    	}
}
