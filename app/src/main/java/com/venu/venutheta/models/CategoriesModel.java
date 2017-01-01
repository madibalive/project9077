package com.venu.venutheta.models;

import android.graphics.drawable.Drawable;

/**
 * Created by Madiba on 10/12/2016.
 */

public class CategoriesModel {

    private String mName;
    private Drawable mDrawable;

    public CategoriesModel(String mName) {
        this.mName = mName;
    }

    public CategoriesModel(String mName, Drawable mDrawable) {
        this.mName = mName;
        this.mDrawable = mDrawable;
    }

    public String getmName() {
        return mName;
    }

    public Drawable getmDrawable() {
        return mDrawable;
    }
}
