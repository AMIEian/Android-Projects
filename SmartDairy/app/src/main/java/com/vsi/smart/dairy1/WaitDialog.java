package com.vsi.smart.dairy1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by sac on 18/11/2017.
 */

public class WaitDialog {
    private static ProgressDialog dialog ;

    public static void start(Activity act) {
        dialog = new ProgressDialog(act);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");
        dialog.show();
    }

    public static void stop(){
        //Update your UI here.... Get value from doInBackground ....
        if (null != dialog &&  dialog.isShowing()) {
            dialog.dismiss();
        }

    }

}
