package com.venu.venutheta.models;

import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Madiba on 12/30/2016.
 */

public class ModelOtherContact {
    public static final int TYPE_FACEBOOK=1;
    public static final int TYPE_LOCAL=2;
    public static final int TYPE_PARSE=3;
    public int type;

    private List<ParseUser>  facebookContacts;
    private List<PhoneContact> localContact;
    private List<ParseUser> parseContacts;


    public ModelOtherContact() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<ParseUser> getFacebookContacts() {
        return facebookContacts;
    }

    public void setFacebookContacts(List<ParseUser> facebookContacts) {
        this.facebookContacts = facebookContacts;
    }

    public List<PhoneContact> getLocalContact() {
        return localContact;
    }

    public void setLocalContact(List<PhoneContact> localContact) {
        this.localContact = localContact;
    }

    public List<ParseUser> getParseContacts() {
        return parseContacts;
    }

    public void setParseContacts(List<ParseUser> parseContacts) {
        this.parseContacts = parseContacts;
    }
}
