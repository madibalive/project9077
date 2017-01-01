package com.venu.venutheta.galleryPicker;

public class Session {

    private static Session sInstance = null;
    private String mFileToUpload;

    private Session() {
    }

    public static Session getInstance() {
        if (sInstance == null) {
            sInstance = new Session();
        }
        return sInstance;
    }

    public String getFileToUpload() {
        return mFileToUpload;
    }

    public void setFileToUpload(String fileToUpload) {
        mFileToUpload = fileToUpload;
    }

}