package com.acadgild.vpledge;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acadgild.vpledge.constants.MyProfileConstant;
import com.acadgild.vpledge.model.Profile;
import com.acadgild.vpledge.service.PrefUtils;
import com.acadgild.vpledge.service.ServiceHandler;
import com.acadgild.vpledge.util.Connectivity;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pushp_000 on 5/23/2016.
 */
public class MyProfile extends AppCompatActivity {

    CallbackManager callbackManager;

    private ProgressDialog progressDialog;

    ImageView img;

    Connectivity isInternet;

    ServiceHandler sh;

    String user_id;

    TextView completed,taken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.myprofile);

        isInternet=new Connectivity(getApplicationContext());

        TextView nameO = (TextView) findViewById(R.id.tvName);
        taken=(TextView)findViewById(R.id.tv_taken);
        completed=(TextView)findViewById(R.id.tv_completed);



        String name = PrefUtils.getFromPrefs(getApplicationContext(), MyProfileConstant.KEY_NAME, "");
        user_id = PrefUtils.getFromPrefs(getApplicationContext(), "fb_id", "");

        nameO.setText(name);

        img=(ImageView)findViewById(R.id.profile_pic);

        completed = (TextView)findViewById(R.id.tv_completed);
        taken = (TextView)findViewById(R.id.tv_taken);


        if(isInternet.isConnectingToInternet()) {

            sh = new ServiceHandler();

            GetUserDetails userDetails = new GetUserDetails();
            userDetails.execute("");
        }
        else{
            AlertDialog alertDialog = new AlertDialog.Builder(MyProfile.this).create();
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


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        String imageUrl = "http://graph.facebook.com/"+user_id+"/picture?type=large&width=1000&height=1000";

        try {
            img.setImageBitmap(getCircleBitmap(getFacebookProfilePicture(imageUrl)));
        } catch (Exception e) {

        }
    }


    private static Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0,bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }


    private Bitmap getFacebookProfilePicture(String url){
        Bitmap bitmap = null;
        HttpGet httpRequest = new HttpGet(URI.create(url));
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse mResponse;
        try {
            mResponse = (HttpResponse) httpclient.execute(httpRequest);
            HttpEntity entity = mResponse.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            bitmap = BitmapFactory.decodeStream(bufHttpEntity.getContent());
            httpRequest.abort();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onActivityResult(final int requestCode,final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    class GetUserDetails extends AsyncTask<String, String,Profile> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MyProfile.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        protected Profile doInBackground(String... args) {
            Profile profile = new Profile();

            // posting JSON string to server URL
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair(MyProfileConstant.KEY_ID,
                        PrefUtils.getFromPrefs(getApplicationContext(),MyProfileConstant.KEY_ID, "")));

                String data = sh.makeServiceCall("http://vplez.com/get_user",2, params);

              //  Log.e("imp",data);

                try {

                    JSONArray userDetailsList = new JSONArray(data);

                 for (int i = 0; i < data.length(); i++) {

                        JSONObject singlePledge = userDetailsList.getJSONObject(0);

                        profile.setPledge_complete(Integer.parseInt(singlePledge.get(MyProfileConstant.KEY_PLEDGE_COMPLETE).toString()));

                   //  Log.e("imp : ", singlePledge.get(MyProfileConstant.KEY_USER_ID).toString());

                        profile.setPledge_taken(Integer.parseInt(singlePledge.get(MyProfileConstant.KEY_PlEDGE_TAKEN).toString()));
                 }

                } catch (JSONException e) {
                    e.printStackTrace();

                }

            } catch (NullPointerException e) {

            }
            return profile;
        }

        @Override
        protected void onPostExecute(Profile s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

            if(s!=null) {

                taken.setText(""+s.getPledge_taken());
                completed.setText(""+s.getPledge_complete());

            }
            else{
                Toast.makeText(getApplicationContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.pledges:
                if(isInternet.isConnectingToInternet()) {
                    Intent intent = new Intent(getApplicationContext(), MyProfile.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivityForResult(intent, 100);
                }
                else{
                    AlertDialog alertDialog = new AlertDialog.Builder(MyProfile.this).create();
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

                break;

            case R.id.logout:
                LoginManager.getInstance().logOut();
                PrefUtils.removeFromPrefs(getApplicationContext(), MyProfileConstant.KEY_ID);
                PrefUtils.removeFromPrefs(getApplicationContext(), "fb_id");
                PrefUtils.removeFromPrefs(getApplicationContext(), "fb_access_token");

                finish();
                break;

            case R.id.profile:
                if(isInternet.isConnectingToInternet()) {
                    finish();
                    Intent intent1 = new Intent(getApplicationContext(), MyProfile.class);
                    startActivity(intent1);
                }
                else{
                    AlertDialog alertDialog = new AlertDialog.Builder(MyProfile.this).create();
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

                break;

            case R.id.mypledges:
                if(isInternet.isConnectingToInternet()) {
                    Intent intent = new Intent(getApplicationContext(), MyPledgesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivityForResult(intent, 100);
                }
                else{
                    AlertDialog alertDialog = new AlertDialog.Builder(MyProfile.this).create();
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

                break;

        }
        return super.onOptionsItemSelected(item);
    }

}