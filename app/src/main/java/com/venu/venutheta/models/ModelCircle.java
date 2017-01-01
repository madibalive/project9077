package com.venu.venutheta.models;


/**
 * Created by Madiba on 11/3/2016.
 */

public class ModelCircle  {

    public static final int LOCAL=0;
    public static final int FOLLOWER=1;
    public static final int FOLLOW=2;

    public String parseId = null;
    public String parseClassName   = null;
    public String parseUserId   = null;

    public String localId = null;

    public String name = null;
    public String phone = null;
    public String avatar = null;
    public int type = 0;

    public ModelCircle() {
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getParseUserId() {
        return parseUserId;
    }

    public void setParseUserId(String parseUserId) {
        this.parseUserId = parseUserId;
    }

    public String getParseId() {
        return parseId;
    }

    public void setParseId(String parseId) {
        this.parseId = parseId;
    }

    public String getParseClassName() {
        return parseClassName;
    }

    public void setParseClassName(String parseClassName) {
        this.parseClassName = parseClassName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
