package com.acadgild.vpledge.application;

import android.app.Application;

import com.acadgild.vpledge.util.Connectivity;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by pushp_000 on 5/12/2016.
 */
public class App extends Application {

    Connectivity connectivity;
    @Override
    public void onCreate() {
        super.onCreate();
        connectivity=new Connectivity(getApplicationContext());
       // if(connectivity.isConnectingToInternet()) {
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(this);
     //   }
       // else{
     //       Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
     //   }
    }

}
