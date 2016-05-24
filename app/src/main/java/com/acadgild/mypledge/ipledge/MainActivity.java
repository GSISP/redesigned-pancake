package com.acadgild.mypledge.ipledge;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.acadgild.mypledge.ipledge.constants.MyProfileConstant;
import com.acadgild.mypledge.ipledge.constants.ServiceConstants;
import com.acadgild.mypledge.ipledge.model.AllPledgeModel;
import com.acadgild.mypledge.ipledge.service.PrefUtils;
import com.acadgild.mypledge.ipledge.service.ServiceHandler;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    ServiceHandler sh;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookSDKInitialize();
        setContentView(R.layout.activity_main);
      //  printKeyHash(MainActivity.this);

        sh=new ServiceHandler();
        id=PrefUtils.getFromPrefs(getApplicationContext(), MyProfileConstant.KEY_ID,"");

        Log.e("data id : ",id);

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);

        if(!id.equals("")) {

            loginButton.setVisibility(View.INVISIBLE);
            new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    Intent i = new Intent(getApplicationContext(), AllPledgesActivity.class);
                    startActivity(i);

                    // close this activity
                    finish();
                }
            }, 3000);
        }
        else{
            loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_friends"));
            getLoginDetails(loginButton);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
 Register a callback function with LoginButton to respond to the login result.
*/
    protected void getLoginDetails(LoginButton login_button){

        // Callback registration
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {

                GraphRequest request = GraphRequest.newMeRequest(
                        login_result.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {

                                if (response.getError() != null) {
                                    // handle error
                                } else {

                                    // Building Parameters
                                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                                    params.add(new BasicNameValuePair(MyProfileConstant.KEY_NAME,me.optString("name")));
                                    params.add(new BasicNameValuePair(MyProfileConstant.KEY_EMAIL,me.optString("email")));

                                    // posting JSON string to server URL
                                    ArrayList<AllPledgeModel> allPledgeModels = null;
                                    String data = sh.makeServiceCall(ServiceConstants.ADD_USER_URL, 2,params);

                                    Log.e("Data e : ",data);

                                    try {
                                        JSONObject user_data=new JSONObject(data);
                                        id= ""+user_data.get(MyProfileConstant.KEY_ID);
                                        PrefUtils.saveToPrefs(getApplicationContext(), MyProfileConstant.KEY_ID,id);
                                        PrefUtils.saveToPrefs(getApplicationContext(), MyProfileConstant.KEY_NAME,me.optString("name"));
                                        PrefUtils.saveToPrefs(getApplicationContext(), "fb_id",me.optString("id"));


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });//.executeAsync();

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name, email,gender, birthday, location");
                request.setParameters(parameters);
                request.executeAsync();

                Intent intent = new Intent(MainActivity.this, AllPledgesActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancel() {
                // code for cancellation
            }

            @Override
            public void onError(FacebookException exception) {
                //  code to handle error
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("data", data.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    protected void facebookSDKInitialize() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());
            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }
}
