package nitin.sangale.shreeswamipath;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.widget.ImageView;

public class Book extends Activity implements OnGestureListener {

	private GestureDetector gDetector;
	//AdView adView2;
	int page = 0, page_ID = 0;
	ImageView abhanga;//, backarrow, nextarrow;

	int start, end;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book);
		
		gDetector = new GestureDetector((OnGestureListener) this);
		
		//for(int i = 1; i <= 243; i++)
			//{
				//pages[i-1] = getResources().getIdentifier("nitin.sangale.manacheshlok:drawable/p" + i, null, null);
			//}
		
		Intent intent = getIntent();
		//int select = intent.getIntExtra("PAGE", 0);
		start = intent.getIntExtra("START", 1);
		end = intent.getIntExtra("END", 1);
		
		page = start;
		page_ID = getResources().getIdentifier("nitin.sangale.shreeswamipath:drawable/p" + page, null, null);
		
		this.abhanga = (ImageView)findViewById(R.id.imageView1);
		abhanga.setImageResource(page_ID);
		
		//this.nextarrow = (ImageView)findViewById(R.id.imageView3);
		//this.backarrow = (ImageView)findViewById(R.id.imageView1);
		
		//this.adView1 = (AdView)findViewById(R.id.adView1);
	    //AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        //adView1.loadAd(adRequestBuilder.build());
        
        //adView1.loadAd(new AdRequest());
        
        //this.adView2 = (AdView)findViewById(R.id.adView2);
        //adView2.loadAd(adRequestBuilder.build());
        
        //adView2.loadAd(new AdRequest());
	}

	public boolean onDown(MotionEvent arg0) {
            // TODO Auto-generated method stub
            return false;
    }
	
    public boolean onFling(MotionEvent start, MotionEvent finish, float xVelocity, float yVelocity) {
            if (start.getRawX() < finish.getRawX()) {
            	ShowPreviousPage(null);
            } else {
            	ShowNextPage(null);
            }
            return true;
    }
    
    public void onLongPress(MotionEvent arg0) {
            // TODO Auto-generated method stub
    }
    
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
              // TODO Auto-generated method stub
              return false;
    }
    
    public void onShowPress(MotionEvent arg0) {
            // TODO Auto-generated method stub
    }
    
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
			page = page - 1;
			if(page <= start)
				{
					page = start;
					//backarrow.setImageResource(R.drawable.blank);
				}
			if(page == 37)
				{
					//nextarrow.setImageResource(R.drawable.next);
				}
			page_ID = getResources().getIdentifier("nitin.sangale.shreeswamipath:drawable/p" + page, null, null);
			abhanga.setImageResource(page_ID);
		}
	
	@SuppressLint("CutPasteId")
	public void ShowNextPage(View view)
		{
			page = page + 1;
			if(page >= end)
				{
					page = end;
					//nextarrow.setImageResource(R.drawable.blank);
				}
			if(page == 1)
				{
					//backarrow.setImageResource(R.drawable.previous);
				}
			page_ID = getResources().getIdentifier("nitin.sangale.shreeswamipath:drawable/p" + page, null, null);
			abhanga.setImageResource(page_ID);
		}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.book, menu);
		return true;
	}

}
