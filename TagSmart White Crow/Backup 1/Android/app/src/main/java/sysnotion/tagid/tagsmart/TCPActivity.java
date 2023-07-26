package sysnotion.tagid.tagsmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import sysnotion.tagid.tagsmart.utils.Constants;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TCPActivity extends AppCompatActivity {
    EditText editTextTCP, editTextPort;
    Button TCPSetBtn;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pref = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        editTextTCP =(EditText) findViewById(R.id.editTextTCP);
        editTextPort =(EditText) findViewById(R.id.editTextPort);
        TCPSetBtn= (Button)findViewById(R.id.TCPSetBtn);
        TCPSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextTCP.getText().length() >0 && editTextPort.getText().length()>0) {
                    Log.d("TCP", "Port "+editTextPort.getText().toString());
                    Log.d("TCP", "hostname "+editTextTCP.getText().toString());
                    SharedPreferences.Editor edit = pref.edit();

                    edit.putString(Constants.PORT_NUMER, editTextPort.getText().toString());
                    edit.putString(Constants.HOSTNAME, editTextTCP.getText().toString());

                    edit.commit();
                    Toast.makeText(TCPActivity.this,"TCP Hostname and Port number is saved", Toast.LENGTH_LONG).show();

                }
                else
                {

                    Toast.makeText(TCPActivity.this,"Please enter TCP Hostname and Port number", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}