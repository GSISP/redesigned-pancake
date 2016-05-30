package com.acadgild.mypledge.ipledge;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.acadgild.mypledge.ipledge.constants.MyProfileConstant;
import com.acadgild.mypledge.ipledge.service.PrefUtils;
import com.facebook.login.widget.ProfilePictureView;

/**
 * Created by pushp_000 on 5/23/2016.
 */
public class MyProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.myprofile);

        TextView nameO = (TextView) findViewById(R.id.tvName);
        // ImageView img_profile = (ImageView) findViewById(R.id.img_pro);
        String name = PrefUtils.getFromPrefs(getApplicationContext(), MyProfileConstant.KEY_NAME, "");
        String user_id = PrefUtils.getFromPrefs(getApplicationContext(), "fb_id", "");

        nameO.setText(name);

        ProfilePictureView profilePictureView;

        profilePictureView = (ProfilePictureView) findViewById(R.id.imgProfile);

        if (profilePictureView != null) {
            profilePictureView.setProfileId(user_id);
        }
    }
}
