package com.acadgild.mypledge.ipledge.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.acadgild.mypledge.ipledge.R;
import com.acadgild.mypledge.ipledge.model.FriendItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class FriendsAdapter extends ArrayAdapter<FriendItem>
{

    private Context context;
    private ArrayList<FriendItem> friendsList;

    static class ViewHolder
    {
        public ImageView ivUser;
        public TextView tvUserName;
        public CheckBox tagStatus;
    }


    public FriendsAdapter(Context context,ArrayList<FriendItem> friendsList)
    {
        super(context, R.layout.item_friend, friendsList);
        this.context = context;
        this.friendsList = friendsList;
    }

    @Override
    public FriendItem getItem(int position) {
        // TODO Auto-generated method stub
        return super.getItem(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final ViewHolder viewHolder;
        if (convertView == null)
        {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_friend, null);
            viewHolder = new ViewHolder();
            viewHolder.ivUser = (ImageView) convertView.findViewById(R.id.iv_User);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_UserName);
            viewHolder.tagStatus = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String text = "";
        text += friendsList.get(position).getUserName();
        viewHolder.tvUserName.setText(text);
        String imageURL = "";

        imageURL = friendsList.get(position).getPictureURL();
        viewHolder.tagStatus.setChecked(friendsList.get(position).getTagStatus());

        if (imageURL != null)
        {
            if (imageURL.trim().length() > 0)
            {
                Picasso.with(context).load(imageURL).into(viewHolder.ivUser);
            }
        }

        viewHolder.tagStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(friendsList.get(position).getTagStatus()){
                    friendsList.get(position).setTagStatus(false);
                }
                else{
                    friendsList.get(position).setTagStatus(true);
                }
            }
        });

        Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        viewHolder.ivUser.setAnimation(anim);



        return convertView;

    }
}
