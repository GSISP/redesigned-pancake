package com.acadgild.mypledge.ipledge.model;

import java.io.Serializable;


public class FriendItem implements Serializable
{
    private String userId;
    private String userName;
    private String pictureURL;

    public boolean getTagStatus() {
        return tagStatus;
    }

    public void setTagStatus(boolean tagStatus) {
        this.tagStatus = tagStatus;
    }

    private boolean tagStatus;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }
}
