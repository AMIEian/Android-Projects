package sysnotion.tagid.tagsmart.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import co.kr.bluebird.sled.SDConsts;
import sysnotion.tagid.tagsmart.utils.Constants;

public class RFIDReceiver extends BroadcastReceiver {
    private static final String TAG = RFIDReceiver.class.getSimpleName();

    private static final boolean D = Constants.RECV_D;

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // TODO Auto-generated method stub
        String action = arg1.getAction();
        if (SDConsts.ACTION_SLED_ATTACHED.equals(action)) {
            Toast.makeText(arg0, "SLED_ATTACHED", Toast.LENGTH_SHORT).show();
            if (D) Log.d(TAG, "SLED_ATTACHED");
//            Intent intent = new Intent(arg0, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            arg0.startActivity(intent);
        }
        else if (SDConsts.ACTION_SLED_DETACHED.equals(action)) {
            Toast.makeText(arg0, "SLED_DETACHED", Toast.LENGTH_SHORT).show();
            if (D) Log.d(TAG, "SLED_DETACHED");
        }
    }
}
