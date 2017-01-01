package com.venu.venutheta.adapter;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.venu.venutheta.models.ModelFeedItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Madiba on 12/8/2016.
 */
public class SingletonDataSource {

    private List<ParseObject> galleryPagerData;
    private ParseObject currentComment;
    private ParseObject currentEvent;
    private ParseObject currentPhoto;
    private ParseObject currentVideo;
    private ParseUser currentUser;
    private ArrayList<String> privateUserList;
    private ModelFeedItem currentFeedItem;

    public int getRotateX() {
        return RotateX;
    }

    public void setRotateX(int rotateX) {
        RotateX = rotateX;
    }

    private int RotateX;


    private static SingletonDataSource ourInstance = new SingletonDataSource();

    public static SingletonDataSource getInstance() {
        return ourInstance;
    }

    private SingletonDataSource() {
    }

    public List<ParseObject> getgalleryPagerData() {
        return galleryPagerData;
    }

    public void setgalleryPagerData(List<ParseObject> pagerdata) {
        galleryPagerData = pagerdata;
    }

    public ParseObject getCurrentComment() {
        return currentComment;
    }

    public void setCurrentComment(ParseObject currentComment) {
        this.currentComment = currentComment;
    }

    public ParseObject getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(ParseObject currentEvent) {
        this.currentEvent = currentEvent;
    }

    public ParseObject getCurrentPhoto() {
        return currentPhoto;
    }

    public void setCurrentPhoto(ParseObject currentPhoto) {
        this.currentPhoto = currentPhoto;
    }

    public ParseObject getCurrentVideo() {
        return currentVideo;
    }

    public void setCurrentVideo(ParseObject currentVideo) {
        this.currentVideo = currentVideo;
    }

    public ParseUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(ParseUser currentUser) {
        this.currentUser = currentUser;
    }

    public ModelFeedItem getCurrentFeedItem() {
        return currentFeedItem;
    }

    public void setCurrentFeedItem(ModelFeedItem currentFeedItem) {
        this.currentFeedItem = currentFeedItem;
    }

    public ArrayList<String> getPrivateUserList() {
        return privateUserList;
    }

    public void setPrivateUserList(ArrayList<String> privateUserList) {
        this.privateUserList = privateUserList;
    }
}
