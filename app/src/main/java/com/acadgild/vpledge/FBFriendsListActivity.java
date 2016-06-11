package com.acadgild.vpledge;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.acadgild.vpledge.adapter.FriendsAdapter;
import com.acadgild.vpledge.model.FriendItem;
import com.acadgild.vpledge.service.PrefUtils;
import com.acadgild.vpledge.util.Connectivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequest.Callback;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FBFriendsListActivity extends AppCompatActivity {
    CallbackManager callbackManager;

    int size=1;

    boolean statusLoad = true, load = false;
    private ProgressDialog progressDialog;

    private AccessToken fbToken = null;
    String fb_access_token = "";
    private ArrayList<FriendItem> friendsList;
    private ListView lvFriendsList;
    private FriendsAdapter friendsAdapter;

    FbFriendsDetails fbfriendsAsync;

    String title, description;
    Button loadMore;

    String next = null;

    Connectivity isNetwork;

    String user_id;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbfriends_list);

        isNetwork=new Connectivity(getApplicationContext());

        title = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");

        fbfriendsAsync = new FbFriendsDetails();

        user_id = PrefUtils.getFromPrefs(getApplicationContext(), "fb_id", "");

        friendsList = new ArrayList<FriendItem>();

       // List<String> permissions = Arrays.asList("taggable_friends");
       // LoginManager.getInstance().logInWithPublishPermissions(FBFriendsListActivity.this, permissions);


        lvFriendsList = (ListView) findViewById(R.id.lv_FriendsList);

        loadMore = (Button) findViewById(R.id.bt_load);


        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isNetwork.isConnectingToInternet()) {
                    if (next != null) {
                        getFBFriendsList(next);
                        size = friendsList.size();
                        Toast.makeText(getApplicationContext(), "" + size, Toast.LENGTH_SHORT).show();
                    } else {
                        //  Log.d("daa ", "fail");
                        loadMore.setVisibility(View.INVISIBLE);
                        // Log.d("daa ", "fail 1");

                    }
                }
                else{
                    android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(FBFriendsListActivity.this).create();
                    alertDialog.setTitle("Internet Connection");
                    alertDialog.setMessage("Please check your internet connection");
                    alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show() ;
                }
            }
        });

        fbToken = AccessToken.getCurrentAccessToken();

        fb_access_token = PrefUtils.getFromPrefs(getApplicationContext(), "fb_access_token", "");
        Object o = (Object) fb_access_token;
        //fbToken = (AccessToken)o;

        lvFriendsList.setAdapter(friendsAdapter);
       // friendsAdapter.notifyDataSetChanged();

        if(isNetwork.isConnectingToInternet()) {
            getFBFriendsList();
        }
        else{
            android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(FBFriendsListActivity.this).create();
            alertDialog.setTitle("Internet Connection");
            alertDialog.setMessage("Please check your internet connection");
            alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show() ;
        }


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void getFBFriendsList() {
        //fbToken return from login with facebook
        GraphRequestAsyncTask r = GraphRequest.newGraphPathRequest(fbToken, "/"+user_id+"/taggable_friends", new Callback() {

                    @Override
                    public void onCompleted(GraphResponse response) {

                        fbfriendsAsync.execute(response.getJSONObject());
                    }
                }

        ).executeAsync();
    }



    private void getFBFriendsList(String next) {
        //here i used volley to get next page
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.GET, next,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject friends = null;
                        try {

                            Log.e("imp",response);
                            friends = new JSONObject(response);
                            //parseResponse(friends);
                            FbFriendsDetails fbfriendsAsync = new FbFriendsDetails();
                            fbfriendsAsync.execute(friends);
                            

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Toast.makeText(getApplicationContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){

                }
            }
        })

        {
            @Override
            protected Map<String, String> getParams() {
                return null;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        queue.add(sr);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "FBFriendsList Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.acadgild.vpledge/com.acadgid.vpledge/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "FBFriendsList Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.acadgild.vpledge/com.acadgild.vpledge/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    class FbFriendsDetails extends AsyncTask<JSONObject, String, ArrayList<FriendItem>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(FBFriendsListActivity.this);

            if (statusLoad) {
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Loading...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setProgress(0);
                progressDialog.show();
            }
        }

        @Override
        protected ArrayList<FriendItem> doInBackground(JSONObject... friends) {

            try {
//            JSONObject friends = response.getJSONObject();
//            JSONObject friends = new JSONObject(response);
                JSONArray friendsArray = (JSONArray) friends[0].get("data");
                if (friendsArray != null) {
                    for (int i = 0; i < friendsArray.length(); i++) {
                        FriendItem item = new FriendItem();
                        try {
                            item.setUserId(friendsArray.getJSONObject(i).get("id") + "");

                            item.setUserName(friendsArray.getJSONObject(i).get("name") + "");
                            JSONObject picObject = new JSONObject(friendsArray.getJSONObject(i).get("picture") + "");
                            String picURL = (String) (new JSONObject(picObject.get("data").toString())).get("url");
                            item.setPictureURL(picURL);
                            item.setTagStatus(false);
                            friendsList.add(item);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // facebook use paging if have "next" this mean you still have friends if not start load fbFriends list
                    next = friends[0].getJSONObject("paging").getString("next");

                }
            } catch (JSONException e1) {
                // loadFriendsList();
              //  Log.d("daa ", "fail");
                next=null;

                e1.printStackTrace();


            }

            return friendsList;
        }

        @Override
        protected void onPostExecute(ArrayList<FriendItem> s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

            if(s!=null) {
                friendsAdapter = new FriendsAdapter(FBFriendsListActivity.this, R.layout.item_friend, s);
                friendsAdapter.notifyDataSetChanged();
                lvFriendsList.setAdapter(friendsAdapter);

                lvFriendsList.post(new Runnable() {
                    @Override
                    public void run() {
                        lvFriendsList.smoothScrollToPosition(size);
                    }
                });

            }
            else
            {
                Toast.makeText(getApplicationContext(),"Failed to load data",Toast.LENGTH_SHORT).show();
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.share:
                List<String> id = friendsAdapter.getAllData();

                if (id.size() > 0) {

                    if (isNetwork.isConnectingToInternet()) {

                        try {
                            callbackManager = CallbackManager.Factory.create();


                            ShareLinkContent shareContent = new ShareLinkContent.Builder()
                                    .setContentTitle("Pledge Challenge - " + title)
                                    .setContentDescription(description)
                                    .setQuote("Hurray! I have completed \"" + title + "\" pledge. Can you?")
                                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.acadgild.vpledge&hl=en"))
                                    .setPeopleIds(id)
                                    .build();

                            ShareDialog shareDialog = new ShareDialog(FBFriendsListActivity.this);
                            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                                @Override
                                public void onSuccess(Sharer.Result result) {
                                    Toast.makeText(getApplicationContext(), "Posted on facebook wall successfully", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancel() {
                                    Toast.makeText(getApplicationContext(), "Posting in Facebook has been cancelled", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(FacebookException exception) {
                                    Toast.makeText(getApplicationContext(), "Please try to post again", Toast.LENGTH_LONG).show();
                                    exception.printStackTrace();
                                }
                            }, 101);

                            if (ShareDialog.canShow(ShareLinkContent.class)) {
                                shareDialog.show(shareContent);
                            } else {
                                List<String> permissions = Arrays.asList("publish_actions");
                                LoginManager.getInstance().logInWithPublishPermissions(FBFriendsListActivity.this, permissions);
                            }
                        }
                        catch(Exception e){
                            Toast.makeText(getApplicationContext(),"Failed to initiate post request",Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(FBFriendsListActivity.this).create();
                        alertDialog.setTitle("Internet Connection");
                        alertDialog.setMessage("Please check your internet connection");
                        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
                else{
                        Toast.makeText(getApplicationContext(), "Select atleast one friend", Toast.LENGTH_SHORT).show();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
