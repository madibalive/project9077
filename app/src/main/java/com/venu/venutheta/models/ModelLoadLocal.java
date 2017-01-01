package com.venu.venutheta.models;

import com.parse.ParseUser;

/**
 * Created by Madiba on 12/7/2016.
 */

public class ModelLoadLocal {

    public static final int TYPE_LOCAL=0;
    public static final int TYPE_PARSE=1;

    private ParseUser parseUser=null;
    private PhoneContact phoneContact=null;
    private int type;

    public ModelLoadLocal() {
    }

    public ParseUser getParseUser() {
        return parseUser;
    }

    public void setParseUser(ParseUser parseUser) {
        this.parseUser = parseUser;
    }

    public PhoneContact getPhoneContact() {
        return phoneContact;
    }

    public void setPhoneContact(PhoneContact phoneContact) {
        this.phoneContact = phoneContact;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
