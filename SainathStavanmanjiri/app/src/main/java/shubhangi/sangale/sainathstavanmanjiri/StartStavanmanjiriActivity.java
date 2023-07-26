
package shubhangi.sangale.sainathstavanmanjiri;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class StartStavanmanjiriActivity extends Activity implements GestureDetector.OnGestureListener {

    private GestureDetector gDetector;
    int page = 0;

    int [] pages = new int [] {
            R.drawable.p1,
            R.drawable.p2,
            R.drawable.p3,
            R.drawable.p4,
            R.drawable.p5,
            R.drawable.p6,
            R.drawable.p7,
            R.drawable.p8,
            R.drawable.p9,
            R.drawable.p10,
            R.drawable.p11,
            R.drawable.p12,
            R.drawable.p13,
            R.drawable.p14,
            R.drawable.p15,
            R.drawable.p16,
            R.drawable.p17,
            R.drawable.p18,
            R.drawable.p19,
            R.drawable.p20,
            R.drawable.p21,
            R.drawable.p22,
            R.drawable.p23,
            R.drawable.p24,
            R.drawable.p25,
            R.drawable.p26,
            R.drawable.p27,
            R.drawable.p28,
            R.drawable.p29,
            R.drawable.p30,
            R.drawable.p31,
            R.drawable.p32,
            R.drawable.p33,
            R.drawable.p34,
            R.drawable.p35,
            R.drawable.p36,
            R.drawable.p37,
            R.drawable.p38,
            R.drawable.p39,
            R.drawable.p40,
            R.drawable.p41,
            R.drawable.p42,
            R.drawable.p43,
            R.drawable.p44,
            R.drawable.p45,
            R.drawable.p46,
            R.drawable.p47,
            R.drawable.p48,
            R.drawable.p49,
            R.drawable.p50,
            R.drawable.p51,
            R.drawable.p52,
            R.drawable.p53,
            R.drawable.p54,
            R.drawable.p55,
            R.drawable.p56
    };
    ImageView abhanga, backarrow, nextarrow;


    AdView adView2;

    @SuppressWarnings("deprecation")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_stavanmanjiri);
        gDetector = new GestureDetector(this);
        this.abhanga = (ImageView)findViewById(R.id.myCanvas);
        this.nextarrow = (ImageView)findViewById(R.id.imageView3);
        this.backarrow = (ImageView)findViewById(R.id.imageView1);
// Look up the AdView as a resource and load a request.

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
        page = page - 1;
        if(page <= 0)
        {
            page = 0;
            backarrow.setImageResource(R.drawable.blank);
        }
        if(page == 54)
        {
            nextarrow.setImageResource(R.drawable.next11);
        }
        abhanga.setImageResource(pages[page]);
    }
    @SuppressLint("CutPasteId")
    public void ShowNextPage(View view)
    {
        page = page + 1;
        if(page >= 55)
        {
            page = 55;
            nextarrow.setImageResource(R.drawable.blank);
        }
        if(page == 1)
        {
            backarrow.setImageResource(R.drawable.back11);
        }
        abhanga.setImageResource(pages[page]);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent = new Intent(this,AboutActivity.class);
        startActivity(intent);
        return true;
    }


}
