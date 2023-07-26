package com.vsi.smart.dairy1;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


/**
 * Created by sac on 03/02/2018.
 */

public class RegisterMeToFcm  extends FirebaseInstanceIdService {

    private static final String TAG = "MyAndroidFCMIIDService";
    public static String FCMKey = "";
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    @Override
    public void onTokenRefresh() {
        //Get hold of the registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Log the token
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        Toast.makeText(RegisterMeToFcm.this, " टोकन - "+refreshedToken, Toast.LENGTH_LONG).show();
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        loginPrefsEditor.putString("FCMKEY", refreshedToken);
        loginPrefsEditor.putString("FCM_REG_STATUS", "0");
        loginPrefsEditor.commit();

    }
    private void sendRegistrationToServer(String token) {
//Implement this method if you want to store the token on your server
    }

}
