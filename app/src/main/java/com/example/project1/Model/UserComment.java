package com.example.project1.Model;

import org.json.JSONArray;

import java.io.Serializable;

public class UserComment implements Serializable {
    String ID, Avatar, userName, Content, cmtImg;
    private transient JSONArray childrenComments;
    String idParent;

    public UserComment(String ID, String avatar, String userName, String content, JSONArray childrenComments, String cmtImg) {
        this.ID = ID;
        Avatar = avatar;
        this.userName = userName;
        Content = content;
        this.childrenComments = childrenComments;
        this.cmtImg=cmtImg;
    }
    public UserComment(String ID, String avatar, String userName, String content, JSONArray childrenComments, String cmtImg,String idParent) {
        this.ID = ID;
        Avatar = avatar;
        this.userName = userName;
        Content = content;
        this.childrenComments = childrenComments;
        this.cmtImg=cmtImg;
        this.idParent=idParent;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCmtImg() {
        return cmtImg;
    }

    public void setCmtImg(String cmtImg) {
        this.cmtImg = cmtImg;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public JSONArray getChildrenComments() {
        return childrenComments;
    }

    public void setChildrenComments(JSONArray childrenComments) {
        this.childrenComments = childrenComments;
    }
}
