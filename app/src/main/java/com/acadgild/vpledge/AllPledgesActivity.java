package com.acadgild.vpledge;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.acadgild.vpledge.adapter.AllPledgesAdapter;
import com.acadgild.vpledge.constants.AllPledgesConstant;
import com.acadgild.vpledge.constants.MyProfileConstant;
import com.acadgild.vpledge.constants.ServiceConstants;
import com.acadgild.vpledge.model.AllPledgeModel;
import com.acadgild.vpledge.service.PrefUtils;
import com.acadgild.vpledge.service.ServiceHandler;
import com.acadgild.vpledge.util.Connectivity;
import com.facebook.login.LoginManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pushp_000 on 5/22/2016.
 */
public class AllPledgesActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    ServiceHandler sh;

    String user_id;

    ListView lv;

    AllPledgesAdapter adapter;

    Connectivity isInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.listview);

        lv=(ListView)findViewById(R.id.listView);

        isInternet=new Connectivity(getApplicationContext());

       /*Intent intent= getIntent();
        if(intent!=null){
            user_id=intent.getStringExtra("user_id");
            Log.e("JSON ID Bundle : ", ""+user_id);
        }
        else {*/

        user_id = PrefUtils.getFromPrefs(getApplicationContext(), MyProfileConstant.KEY_ID, "");

       //}
            if(isInternet.isConnectingToInternet()) {

                sh = new ServiceHandler();

                GetAllPledges allPledges = new GetAllPledges();
                allPledges.execute("");
            }
        else{
                AlertDialog alertDialog = new AlertDialog.Builder(AllPledgesActivity.this).create();
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
    class GetAllPledges extends AsyncTask<String, String, ArrayList<AllPledgeModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(AllPledgesActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        protected ArrayList<AllPledgeModel> doInBackground(String... args) {

            // posting JSON string to server URL
            ArrayList<AllPledgeModel> allPledgeModels = null;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair(MyProfileConstant.KEY_ID, user_id));

                String data = sh.makeServiceCall(ServiceConstants.ALL_PLEDGES_URL,2, params);

                try {

                    JSONArray allPledgesList = new JSONArray(data);

                    allPledgeModels = new ArrayList<AllPledgeModel>();

                    for (int i = 0; i < data.length(); i++) {

                        JSONObject singlePledge = allPledgesList.getJSONObject(i);

                        AllPledgeModel allPledgeModel = new AllPledgeModel();
                        allPledgeModel.setId(Integer.parseInt(singlePledge.get(AllPledgesConstant.KEY_ID).toString()));
                        allPledgeModel.setName(singlePledge.get(AllPledgesConstant.KEY_NAME).toString());
                        allPledgeModel.setDescription(singlePledge.get(AllPledgesConstant.KEY_DESCRIPTION).toString());
                        allPledgeModel.setPoints(Integer.parseInt(singlePledge.get(AllPledgesConstant.KEY_POINTS).toString()));
                        allPledgeModel.setPledge_image_url(singlePledge.get(AllPledgesConstant.KEY_IMAGE_URL).toString());
                        // allPledgeModel.setPledge_image_url("http://androidexample.com/media/webservice/LazyListView_images/image0.png");
                        //System.out.println("Data : "+Integer.parseInt(singlePledge.get(AllPledgesConstant.KEY_POINTS).toString()));
                        allPledgeModel.setPledge_unit_quantity(Integer.parseInt(singlePledge.get(AllPledgesConstant.KEY_UNIT_QUATITY).toString()));
                        allPledgeModel.setAlready_taken(Boolean.parseBoolean(singlePledge.get(AllPledgesConstant.KEY_ALREADY_TAKEN).toString()));
                        allPledgeModels.add(allPledgeModel);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }

            } catch (NullPointerException e) {

            }
            return allPledgeModels;
        }

        @Override
        protected void onPostExecute(ArrayList<AllPledgeModel> s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

            if(s!=null) {
                adapter = new AllPledgesAdapter(AllPledgesActivity.this, R.layout.all_pledges, s);
                lv.setAdapter(adapter);
            }
            else{
                Toast.makeText(getApplicationContext(),"Failed to load data",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.pledges:


                if(isInternet.isConnectingToInternet()) {
                    finish();
                    Intent intent = new Intent(getApplicationContext(), AllPledgesActivity.class);
                    startActivityForResult(intent, 100);
                }
                else{
                    AlertDialog alertDialog = new AlertDialog.Builder(AllPledgesActivity.this).create();
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
                    Intent intent1 = new Intent(getApplicationContext(), MyProfile.class);
                    startActivity(intent1);
                }
                else{
                    AlertDialog alertDialog = new AlertDialog.Builder(AllPledgesActivity.this).create();
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
                    startActivity(intent);
                }
                else{
                    AlertDialog alertDialog = new AlertDialog.Builder(AllPledgesActivity.this).create();
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