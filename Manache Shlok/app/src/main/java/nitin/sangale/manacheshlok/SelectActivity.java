package nitin.sangale.manacheshlok;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class SelectActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);
	}
	
	public void loadShlok(View v)
		{
			Intent intent = new Intent(this, StartHaripathActivity.class);
			intent.putExtra("LOAD", 1);
			startActivity(intent);
		}
	
	public void loadAshtake(View v)
		{
			Intent intent = new Intent(this, StartHaripathActivity.class);
			intent.putExtra("LOAD", 2);
			startActivity(intent);
		}

	public void loadSarth(View v)
		{
			Intent intent = new Intent(this, StartHaripathActivity.class);
			intent.putExtra("LOAD", 3);
			startActivity(intent);
		}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select, menu);
		return true;
	}

}
