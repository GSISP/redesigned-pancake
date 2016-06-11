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

import com.acadgild.vpledge.adapter.MyPledgesAdapter;
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
 * Created by pushp_000 on 5/12/2016.
 */
public class MyPledgesActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    ServiceHandler sh;

    ListView lv;

    String user_id;

    MyPledgesAdapter adapter;

    Connectivity isInternet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.listview);

        lv=(ListView)findViewById(R.id.listView);

        isInternet=new Connectivity(getApplicationContext());

        if(isInternet.isConnectingToInternet()) {

            sh = new ServiceHandler();

            user_id = PrefUtils.getFromPrefs(getApplicationContext(), MyProfileConstant.KEY_ID, "");

            GetAllPledges allPledges = new GetAllPledges();
            allPledges.execute("");
        }
        else{
            AlertDialog alertDialog = new AlertDialog.Builder(MyPledgesActivity.this).create();
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

            progressDialog = new ProgressDialog(MyPledgesActivity.this);
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

                params.add(new BasicNameValuePair(MyProfileConstant.KEY_ID,user_id));

                String data = sh.makeServiceCall(ServiceConstants.GET_USER_PLEDGE_URL,2, params);

                try {

                    JSONArray allPledgesList = new JSONArray(data);

                    allPledgeModels = new ArrayList<AllPledgeModel>();

                    for (int i = 0; i < data.length(); i++) {

                        JSONObject singlePledge1 = allPledgesList.getJSONObject(i);

                        JSONObject singlePledge=singlePledge1.getJSONObject("pledge");

                        AllPledgeModel allPledgeModel = new AllPledgeModel();
                        int user_pledge_id=Integer.parseInt(singlePledge1.get(AllPledgesConstant.KEY_USER_PLEDGE_ID).toString());
                        allPledgeModel.setPledge_user_id(user_pledge_id);

                        allPledgeModel.setId(Integer.parseInt(singlePledge.get(AllPledgesConstant.KEY_ID).toString()));
                        allPledgeModel.setName(singlePledge.get(AllPledgesConstant.KEY_NAME).toString());
                        allPledgeModel.setDescription(singlePledge.get(AllPledgesConstant.KEY_DESCRIPTION).toString());
                        allPledgeModel.setPoints(Integer.parseInt(singlePledge.get(AllPledgesConstant.KEY_POINTS).toString()));
                        allPledgeModel.setPledge_image_url(singlePledge.get(AllPledgesConstant.KEY_IMAGE_URL).toString());
                        //System.out.println("Data : "+Integer.parseInt(singlePledge.get(AllPledgesConstant.KEY_POINTS).toString()));
                        allPledgeModel.setPledge_unit_quantity(Integer.parseInt(singlePledge.get(AllPledgesConstant.KEY_UNIT_QUATITY).toString()));
                        allPledgeModel.setProgress_auto_update(Integer.parseInt(singlePledge.get(AllPledgesConstant.KEY_PROGRESS_AUTO_UPDATE).toString()));

                        // Building Parameters
                        List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                        params1.add(new BasicNameValuePair("user_pledge_id", "" + user_pledge_id));

                        String data1 = sh.makeServiceCall(ServiceConstants.GET_PLEDGE_PROGRESS_URL, 2,params1);

                        JSONObject obj=new JSONObject(data1);

                        allPledgeModel.setPledge_units_completed(Integer.parseInt(obj.get(AllPledgesConstant.KEY_QUANTITY_COMPLETED).toString()));

                        if(allPledgeModel.getId()==1 && allPledgeModel.getPledge_units_completed()==30){
                            PrefUtils.saveToPrefs(getApplicationContext(), "unlock","1");
                        }

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

                adapter = new MyPledgesAdapter(MyPledgesActivity.this, R.layout.my_pledges, s);
                lv.setAdapter(adapter);
            }
            else{
                Toast.makeText(getApplicationContext(), "Failed to load data", Toast.LENGTH_SHORT).show();

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
                    Intent intent = new Intent(getApplicationContext(), AllPledgesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivityForResult(intent, 100);
                }
                else{
                    AlertDialog alertDialog = new AlertDialog.Builder(MyPledgesActivity.this).create();
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
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
                }
                else{
                    AlertDialog alertDialog = new AlertDialog.Builder(MyPledgesActivity.this).create();
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
                    finish();
                    Intent intent = new Intent(getApplicationContext(), MyPledgesActivity.class);
                    startActivityForResult(intent, 100);
                }
                else{
                    AlertDialog alertDialog = new AlertDialog.Builder(MyPledgesActivity.this).create();
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
