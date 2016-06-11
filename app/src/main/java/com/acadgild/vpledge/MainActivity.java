package com.acadgild.vpledge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.acadgild.vpledge.constants.MyProfileConstant;
import com.acadgild.vpledge.service.PrefUtils;

/**
 * Created by pushp_000 on 6/5/2016.
 */
public class MainActivity extends Activity{
// Splash screen timerpublic class SplashScreen extends Activity {

private static int SPLASH_TIME_OUT = 3000;
        String id;

@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);

        id = PrefUtils.getFromPrefs(getApplicationContext(), MyProfileConstant.KEY_ID, "");

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

@Override
public void run() {
        // This method will be executed once the timer is over
        if (!id.equals("")) {

                // This method will be executed once the timer is over
                // Start your app pledge activity
                Intent i = new Intent(getApplicationContext(), AllPledgesActivity.class);
                startActivity(i);

                // close this activity
                finish();
        }
        else {
                // Start your app login activity
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                // close this activity
                finish();
        }
        }
        }, SPLASH_TIME_OUT);
        }


}
