package com.acadgild.vpledge;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.Arrays;
import java.util.List;

/**
 * Created by pushp_000 on 6/13/2016.
 */
public class ShareActivity extends Activity {

    CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String pledge=getIntent().getExtras().getString("pledge");

        callbackManager = CallbackManager.Factory.create();

        ShareLinkContent shareContent = new ShareLinkContent.Builder()
                    .setContentTitle("")
                    .setQuote("Yipee, I am a proud vPledger now! Just took a pledge to "
                            +pledge
                            +". Would encourage all my friends to download vPledge and make a difference.")
                    .
                            setShareHashtag(new ShareHashtag.Builder().setHashtag("#vPledge").build())
                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.acadgild.vpledge&hl=en"))
                    .build();

            ShareDialog shareDialog = new ShareDialog(ShareActivity.this);
            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    // Toast.makeText(getApplicationContext(), "Posted on Facebook wall successfully", Toast.LENGTH_SHORT).show();

                    Intent pledges = new Intent(getApplicationContext(), MyPledgesActivity.class);
                    pledges.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(pledges);

                }

                @Override
                public void onCancel() {
                    //  Toast.makeText(getApplicationContext(), "Posting on Facebook wall has been cancelled", Toast.LENGTH_SHORT).show();
                    Intent pledges = new Intent(getApplicationContext(), MyPledgesActivity.class);
                    pledges.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(pledges);

                }

                @Override
                public void onError(FacebookException exception) {
                    //   Toast.makeText(getContext().getApplicationContext(), "Please try to post again", Toast.LENGTH_LONG).show();
                    exception.printStackTrace();

                    Toast.makeText(getApplicationContext(), "Failed to Post on Facebook wall", Toast.LENGTH_SHORT).show();

                    Intent pledges = new Intent(getApplicationContext(), MyPledgesActivity.class);
                    pledges.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(pledges);

                }
            }, 101);

            if (ShareDialog.canShow(ShareLinkContent.class)) {
                shareDialog.show(shareContent);
            } else {
                List<String> permissions = Arrays.asList("publish_actions");
                LoginManager.getInstance().logInWithPublishPermissions(ShareActivity.this, permissions);
            }


        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
}


