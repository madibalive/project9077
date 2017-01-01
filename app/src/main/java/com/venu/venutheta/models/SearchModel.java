package com.venu.venutheta.models;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class SearchModel {

    public static final int EVENT = 0;
    public static final int PEOPLE = 1;
    public static final int GOSSIP = 2;
    public static final int HASHTAG = 3;

    private List<ParseObject> mData = new ArrayList<>();
    private List<ParseUser> users = new ArrayList<>();
    private String mTitle;
    private int mType;


    public SearchModel(String title, int type) {
        mTitle = title;
        mType = type;
    }

    public String getmTitle() {
        return mTitle;
    }

    public int getmType() {
        return mType;
    }

    public void setmData(List<ParseObject> mData) {
        this.mData = mData;
    }

    public List<ParseObject> getData() {
        return mData;
    }

    public List<ParseUser> getUsers() {
        return users;
    }

    public void setUsers(List<ParseUser> users) {
        this.users = users;
    }
}
