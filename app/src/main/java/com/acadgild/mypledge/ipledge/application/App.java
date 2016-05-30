package com.acadgild.mypledge.ipledge.application;

import android.app.Application;
import android.content.DialogInterface;

import com.acadgild.mypledge.ipledge.util.Connectivity;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by pushp_000 on 5/12/2016.
 */
public class App extends Application {

    private static Connectivity connectivity;
    @Override
    public void onCreate() {
        super.onCreate();
        if(Connectivity.isConnected(getApplicationContext())) {
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(this);
        }
        else{
            android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(getApplicationContext()).create();

            alertDialog.setTitle("Info");
            alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                }
            });

            alertDialog.show();
        }
    }

}
