package sysnotion.tagid.tagsmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import okhttp3.OkHttpClient;
import sysnotion.tagid.tagsmart.db.DBManager;
import sysnotion.tagid.tagsmart.utils.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomeActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    SharedPreferences pref;
    private CardView card_view_encoding,card_view_inventory,card_view_movement,card_view_item_info,card_view_epc_checking;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        pref = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

       card_view_encoding = (CardView) findViewById(R.id.card_view_encoding);
        card_view_inventory = (CardView) findViewById(R.id.card_view_inventory);
        card_view_movement = (CardView) findViewById(R.id.card_view_movement);
        card_view_item_info = (CardView) findViewById(R.id.card_view_item_info);
        card_view_epc_checking = (CardView) findViewById(R.id.card_view_epc_checking);

        card_view_encoding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this , EncodingEPCActivity.class);

                startActivity(intent);
            }
        });

        card_view_epc_checking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this , CheckingEPCActivity.class);

                startActivity(intent);
            }
        });


        card_view_inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
                dialog.setTitle("Stock Take" )
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("Select Stock Take Option")
                        .setNegativeButton("Unplanned Stock Take", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                dialoginterface.cancel();
                                Intent intent = new Intent(HomeActivity.this , SessionActivity.class);
                                startActivity(intent);
                            }})
                        .setPositiveButton("Planned Stock Take", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                Intent intent = new Intent(HomeActivity.this , StockTakeOptionBCategoryActivity.class);

                                startActivity(intent);
                            }
                        }).show();
            }
        });

        card_view_item_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this , SearchDashboardActivity.class);

                startActivity(intent);

            }
        });

       card_view_movement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this , MovementDashboardActivity.class);

                startActivity(intent);

            }
        });

        //showMessageDialogWithBluetoothActivity("Bluetooth Status", "Bluetooth not connected");


    }

    private void showMessageDialogWithBluetoothActivity(String title, String message)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
        dialog.setTitle(title )
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(message)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        dialoginterface.cancel();
                    }})
                .setPositiveButton("Bluetooth Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {

                        Intent intent = new Intent(HomeActivity.this, BTConnectivityActivity.class);
                        startActivity(intent);
                    }
                }).show();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.app_menu);
        popup.show();
    }

    public void onResume(){
        super.onResume();

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                SharedPreferences.Editor edit = pref.edit();
                edit.clear();
                edit.commit();

                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);

                finish();
                return true;

            case R.id.sound:
                Intent intent2 = new Intent(HomeActivity.this, SoundManagerActivity.class);
                startActivity(intent2);

                return true;
            case R.id.bluetooth_settings:
                Intent intent3 = new Intent(HomeActivity.this, BTConnectivityActivity.class);
                startActivity(intent3);

                return true;

           /* case R.id.tcp:

                Intent intent2 = new Intent(HomeActivity.this, TCPActivity.class);
                startActivity(intent2);
                return true;*/

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}