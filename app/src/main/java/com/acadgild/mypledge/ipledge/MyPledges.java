package com.acadgild.mypledge.ipledge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.acadgild.mypledge.ipledge.model.AllPledgeModel;
import com.facebook.login.LoginManager;

import java.util.ArrayList;

/**
 * Created by pushp_000 on 5/12/2016.
 */
public class MyPledges extends AppCompatActivity {

    ArrayList<AllPledgeModel> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getActionBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myprofile);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.allPledges:
                Intent intent=new Intent(getApplicationContext(),AllPledgesActivity.class);
                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
