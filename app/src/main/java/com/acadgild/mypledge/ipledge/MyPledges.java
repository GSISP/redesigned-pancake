package com.acadgild.mypledge.ipledge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.acadgild.mypledge.ipledge.model.AllPledge;
import com.facebook.login.LoginManager;

import java.util.ArrayList;

/**
 * Created by pushp_000 on 5/12/2016.
 */
public class MyPledges extends AppCompatActivity {

    ArrayList<AllPledge> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getActionBar();


        super.onCreate(savedInstanceState);


        setContentView(R.layout.myprofile);

        (findViewById(R.id.logout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginManager.getInstance().logOut();
                Intent intent = new Intent(MyPledges.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.profile :
                Toast.makeText(getApplicationContext(),"Profile Page",Toast.LENGTH_SHORT).show();
                break;

            case R.id.myPledges :
                Toast.makeText(getApplicationContext(),"My Pledges Page",Toast.LENGTH_SHORT).show();
                break;

            case R.id.allPledges:
                Toast.makeText(getApplicationContext(),"All Pledges Page",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
