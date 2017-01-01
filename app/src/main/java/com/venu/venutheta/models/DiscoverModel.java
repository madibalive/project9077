package com.venu.venutheta.models;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Madiba on 10/9/2016.
 */

public class DiscoverModel {

    public static final int CATEGORIES = 1;
    public static final int TOP_DISCOUNTED = 2;
    public static final int TOP_PEOPLE = 3;
    public static final int New_PEOPLE = 4;
    public static final int NEW_EVENT = 5;
    public static final int TOP_GOSSIP = 6;
    public static final int DIS_EXPLORE = 7;

    private String mtitle;
    private int mGravity;
    private int Type;
    private List<ParseObject> listObject = new ArrayList<>();
    private List<CategoriesModel> listCategories = new ArrayList<>();
    private List<ParseUser> listUsers = new ArrayList<>();

    public DiscoverModel() {
    }

    public String getMtitle() {
        return mtitle;
    }

    public void setMtitle(String mtitle) {
        this.mtitle = mtitle;
    }

    public int getmGravity() {
        return mGravity;
    }

    public void setmGravity(int mGravity) {
        this.mGravity = mGravity;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public List<ParseObject> getListObject() {
        return listObject;
    }

    public void setListObject(List<ParseObject> listObject) {
        this.listObject = listObject;
    }

    public List<CategoriesModel> getListCategories() {
        return listCategories;
    }

    public void setListCategories(List<CategoriesModel> listCategories) {
        this.listCategories = listCategories;
    }

    public List<ParseUser> getListUsers() {
        return listUsers;
    }

    public void setListUsers(List<ParseUser> listUsers) {
        this.listUsers = listUsers;
    }
}
