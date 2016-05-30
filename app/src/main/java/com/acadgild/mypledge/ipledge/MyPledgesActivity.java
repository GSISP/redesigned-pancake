package com.acadgild.mypledge.ipledge;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.acadgild.mypledge.ipledge.adapter.MyPledgesAdapter;
import com.acadgild.mypledge.ipledge.constants.AllPledgesConstant;
import com.acadgild.mypledge.ipledge.constants.MyProfileConstant;
import com.acadgild.mypledge.ipledge.constants.ServiceConstants;
import com.acadgild.mypledge.ipledge.model.AllPledgeModel;
import com.acadgild.mypledge.ipledge.service.PrefUtils;
import com.acadgild.mypledge.ipledge.service.ServiceHandler;
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

    MyPledgesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar();

        setContentView(R.layout.listview);

        lv=(ListView)findViewById(R.id.listView);

        sh=new ServiceHandler();

        GetAllPledges allPledges=new GetAllPledges();
        allPledges.execute("");


    }
    class GetAllPledges extends AsyncTask<String, String, ArrayList<AllPledgeModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MyPledgesActivity.this);
            progressDialog.setCancelable(true);
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

                String user_id = PrefUtils.getFromPrefs(getApplicationContext(), MyProfileConstant.KEY_ID, "");
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

                        Log.e("data e value ", "" + user_pledge_id);

                        allPledgeModel.setId(Integer.parseInt(singlePledge.get(AllPledgesConstant.KEY_ID).toString()));
                        allPledgeModel.setName(singlePledge.get(AllPledgesConstant.KEY_NAME).toString());
                        allPledgeModel.setDescription(singlePledge.get(AllPledgesConstant.KEY_DESCRIPTION).toString());
                        allPledgeModel.setPoints(Integer.parseInt(singlePledge.get(AllPledgesConstant.KEY_POINTS).toString()));
                        //System.out.println("Data : "+Integer.parseInt(singlePledge.get(AllPledgesConstant.KEY_POINTS).toString()));
                        allPledgeModel.setPledge_unit_quantity(Integer.parseInt(singlePledge.get(AllPledgesConstant.KEY_UNIT_QUATITY).toString()));
                        allPledgeModel.setProgress_auto_update(Integer.parseInt(singlePledge.get(AllPledgesConstant.KEY_PROGRESS_AUTO_UPDATE).toString()));

                        // Building Parameters
                        List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                        params1.add(new BasicNameValuePair("user_pledge_id", "" + user_pledge_id));

                        String data1 = sh.makeServiceCall(ServiceConstants.GET_PLEDGE_PROGRESS_URL, 2,params1);

                        JSONObject obj=new JSONObject(data1);

                        allPledgeModel.setPledge_units_completed(Integer.parseInt(obj.get(AllPledgesConstant.KEY_QUANTITY_COMPLETED).toString()));

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

            adapter=new MyPledgesAdapter(MyPledgesActivity.this, R.id.myPledges,s);
            lv.setAdapter(adapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.myPledges:
                Intent intent=new Intent(getApplicationContext(),AllPledgesActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.logout:
                LoginManager.getInstance().logOut();
                PrefUtils.removeFromPrefs(getApplicationContext(), MyProfileConstant.KEY_ID);
                PrefUtils.removeFromPrefs(getApplicationContext(), "fb_id");
                finish();
                break;

            case R.id.profile:
                Intent intent1=new Intent(getApplicationContext(),MyProfile.class);
                startActivity(intent1);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
