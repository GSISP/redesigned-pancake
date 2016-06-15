package com.acadgild.vpledge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.acadgild.vpledge.constants.MyProfileConstant;
import com.acadgild.vpledge.constants.ServiceConstants;
import com.acadgild.vpledge.model.AllPledgeModel;
import com.acadgild.vpledge.service.PrefUtils;
import com.acadgild.vpledge.service.ServiceHandler;
import com.acadgild.vpledge.util.Connectivity;
import com.facebook.AccessToken;
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

public class LoginActivity extends Activity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    ServiceHandler sh;
    String id;
    static AccessToken accessToken;

    Connectivity isNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.content_main);

        facebookSDKInitialize();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_friends"));
        loginButton.setBackgroundResource(R.drawable.facebook);

        getLoginDetails(loginButton);

        isNetwork=new Connectivity(getApplicationContext());

        printKeyHash(LoginActivity.this);

        sh = new ServiceHandler();

    }
    /*

      Register a callback function with LoginButton to respond to the login result.

    */
    protected void getLoginDetails(final LoginButton login_button) {

        // Callback registration
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult login_result) {

                GraphRequest request = GraphRequest.newMeRequest(
                        login_result.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {

                                accessToken = login_result.getAccessToken();

                                String ids=accessToken.getUserId();

                                PrefUtils.saveToPrefs(getApplicationContext(), "fb_access_token", String.valueOf(login_result.getAccessToken()));

                                if (response.getError() != null) {
                                    // handle error
                                } else {

                                    if(isNetwork.isConnectingToInternet()) {
                                        // Building Parameters
                                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                                        params.add(new BasicNameValuePair(MyProfileConstant.KEY_NAME, me.optString("name")));
                                        params.add(new BasicNameValuePair(MyProfileConstant.KEY_EMAIL, me.optString("email")));
                                        params.add(new BasicNameValuePair("fb_id", me.optString("id")));

                                        try {
                                            Toast.makeText(getApplicationContext(),ids+"..."+me.getString("id"), Toast.LENGTH_SHORT).show();
                                            Log.e("imp : ",ids+"..."+me.getString("id"));

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        try {

                                        // posting JSON string to server URL
                                        ArrayList<AllPledgeModel> allPledgeModels = null;
                                        String data = sh.makeServiceCall(ServiceConstants.ADD_USER_URL, 2, params);

                                            JSONObject user_data = new JSONObject(data);
                                            id = String.valueOf(user_data.get(MyProfileConstant.KEY_ID));
                                            PrefUtils.saveToPrefs(getApplicationContext(), MyProfileConstant.KEY_ID, id);
                                            PrefUtils.saveToPrefs(getApplicationContext(), MyProfileConstant.KEY_NAME, me.optString("name"));
                                            PrefUtils.saveToPrefs(getApplicationContext(), "fb_id", me.optString("id"));


                                            finish();

                                            Intent intent = new Intent(getApplicationContext(), AllPledgesActivity.class);
                                            startActivity(intent);

                                        } catch (JSONException e) {

                                            e.printStackTrace();

                                        }
                                    }
                                    else{
                                        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                                        alertDialog.setTitle("Internet Connection");
                                        alertDialog.setMessage("Please check your internet connection");
                                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        alertDialog.show() ;
                                    }
                                }
                            }
                        });//.executeAsync();

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name, email,gender, birthday, location");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // code for cancellation

            }

            @Override
            public void onError(FacebookException exception) {
                //  code to handle error

                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                alertDialog.setTitle("Login Failed");
                alertDialog.setMessage("Please retry...");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                alertDialog.show() ;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

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
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }
}