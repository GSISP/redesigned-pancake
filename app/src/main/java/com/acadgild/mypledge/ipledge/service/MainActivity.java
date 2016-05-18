/*
package com.acadgild.mypledge.ipledge.service;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    ServiceHandler serviceHandler;

    EditText number;
    Button search;
    TextView tv_data;

    ArrayList<Contact> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


         if (isNetworkAvailable(MainActivity.this)) {
            // available network

            fetchContacts();

            serviceHandler=new ServiceHandler();

            number= (EditText) findViewById(R.id.et_phone_num);

            search=(Button)findViewById(R.id.bt_search);

            tv_data=(TextView)findViewById(R.id.tv_data);

            search.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    new GetContactFromServer().execute(number.getText().toString());
                }
            });

        } else {
            // no network
             getActionBar().setTitle("No Internet");

        }

    }


    public void fetchContacts() {

        contacts=new ArrayList<Contact>();

        String phoneNumber = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;//Path to contacts app database
        String _ID = ContactsContract.Contacts._ID; //primary key
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        ContentResolver contentResolver = getContentResolver();//refer you to the database fields

        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null,null);

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {//-1 --> 0

                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));

                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0 && !name.equals("")) {

                    // Query and loop for every phone number of the contact
                    Cursor phoneCursor = contentResolver.query(
                            PhoneCONTENT_URI,
                            null, Phone_CONTACT_ID + " = ?",
                            new String[] { contact_id }, null);

                    //table1 ---> contact_id
                    //table2_phone ---> Phone_CONTACT_ID = contact_id

                    while (phoneCursor.moveToNext()) {
                    phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));


                        //replaces ) ( - , with ""
                        phoneNumber=phoneNumber.replace("(","").replace(")","").replace("-","").trim().replace(" ","");

                        Contact contact=new Contact();
                        contact.setPhoneNumber(phoneNumber);
                        contact.setName(name);

                        contacts.add(contact);

                    }

                    phoneCursor.close();
                }

                try {
                    new SendContactsToServer().execute(contacts);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
    }

       */
/**
     * Background Async Task to send contact to server by making HTTP Request
     * *//*

    class SendContactsToServer extends AsyncTask<ArrayList<Contact>, String, String> {
        */
/**
         * post data to server
         * *//*

        protected String doInBackground(ArrayList<Contact>... args) {

            for(int i=0;i<args[0].size();i++) {

                Contact contact=args[0].get(i);
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();


                //C:\xampp\htdocs\xampp\acadgild_webinar\android_contact_server
                params.add(new BasicNameValuePair(Constant.KEY_NAME, contact.getName()));
                params.add(new BasicNameValuePair(Constant.KEY_PHONE_NUMBER, contact.getPhoneNumber()));

                // posting JSON string to server URL
            try {
            serviceHandler.makeServiceCall(Constant.POST_CONTACTS, 2, params);
            }
            catch(NullPointerException e){

            }

            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    */
/**
     * Background Async Task to get contact from server by making HTTP Request
     * *//*

    class GetContactFromServer extends AsyncTask<String, String, String> {
        */
/**
         * get data from server
         * *//*

        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

           // C:\xampp\htdocs\xampp\acadgild_webinar\android_contact_server
            params.add(new BasicNameValuePair(Constant.KEY_PHONE_NUMBER,args[0]));

            // get JSON string from server URL

            String response=serviceHandler.makeServiceCall(Constant.GET_CONTACTS, 1, params);

            return response;
        }

        @TargetApi(VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            JSONObject jsonObjectContact;
            try {
             jsonObjectContact =new JSONObject(response);

                int status=Integer.parseInt(jsonObjectContact.getString(Constant.KEY_SUCCESS));

                if(status==1) {

                    JSONArray data = jsonObjectContact.getJSONArray(Constant.KEY_CONTACT);

                    JSONObject singleContact = data.getJSONObject(0);

                    String name = singleContact.getString(Constant.KEY_NAME);

                    tv_data.setText("Hey ! it's " + name);
                }

                else{
                    tv_data.setText("Unknown Number");
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

}
*/
