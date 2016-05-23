package com.acadgild.mypledge.ipledge;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.acadgild.mypledge.ipledge.constants.MyProfileConstant;
import com.acadgild.mypledge.ipledge.service.PrefUtils;
import com.facebook.login.widget.ProfilePictureView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by pushp_000 on 5/23/2016.
 */
public class MyProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.myprofile);

        TextView nameO = (TextView) findViewById(R.id.tvName);
       // ImageView img_profile = (ImageView) findViewById(R.id.imgProfile);
        String name = PrefUtils.getFromPrefs(getApplicationContext(), MyProfileConstant.KEY_NAME, "");
        String user_id = PrefUtils.getFromPrefs(getApplicationContext(), MyProfileConstant.KEY_ID, "");

        nameO.setText(name);

        ProfilePictureView profilePictureView;

        profilePictureView = (ProfilePictureView) findViewById(R.id.imgProfile);

        profilePictureView.setProfileId(user_id);

       /* try {
            Bitmap bitmap = getFacebookProfilePicture(user_id);

            img_profile.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static Bitmap getFacebookProfilePicture(String userID)
            throws SocketException, SocketTimeoutException, MalformedURLException, IOException, Exception
    {
        String imageURL;

        Bitmap bitmap = null;
        imageURL = "https://graph.facebook.com/"+userID+"/picture?type=large";
        InputStream in = (InputStream) new URL(imageURL).getContent();
        bitmap = BitmapFactory.decodeStream(in);

        return bitmap;
    }*/

    }
}