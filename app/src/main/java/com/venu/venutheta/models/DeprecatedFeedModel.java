package com.venu.venutheta.models;

import com.parse.ParseObject;

/**
 * Created by Madiba on 11/3/2016.
 */

public class DeprecatedFeedModel {

    public static final int MEDIA_IMAGE=1;
    public static final int  MEDIA_VIDEO=2;
    public static final int GOSSIP=4;
    public static final int EVENT=3;


    // general

    private int mcomment = 0;
    private int mshare = 0;
    private int mlikes = 0;
    private String mdate = null;
    private String mavatar = null;
    private int mislike = 0;
    private Boolean mislikeBoolean = false;
    private String mhashtag = null;
    private String mname = null;

    private String murl = null;

    // Peep
    private int isvideo = 0;


    //Event
    private String meventtitle = null;
    private String meventlocation = null;
    private String meventdate = null;
    private String meventtime = null;
    private Boolean isinterested =false;
    private int mprice = 0;


    //Gossip
    private String mgossipenddate = null;
    private String mgossiptitle = null;



    private int mtype = 0;
    private Boolean isShared = false;



    private ParseObject object;

    public DeprecatedFeedModel() {
    }

    public DeprecatedFeedModel(int mislike, String mdate, int mtype, String mgossipenddate, String mhashtag, String mname, String mavatar, String murl, int mcomment, int mshare, int mlikes, String mgossiptitle) {
        this.mislike = mislike;
        this.mdate = mdate;
        this.mtype = mtype;
        this.mgossipenddate = mgossipenddate;
        this.mhashtag = mhashtag;
        this.mname = mname;
        this.mavatar = mavatar;
        this.murl = murl;
        this.mcomment = mcomment;
        this.mshare = mshare;
        this.mlikes = mlikes;
        this.mgossiptitle = mgossiptitle;
    }

    public int getMislike() {
        return mislike;
    }

    public void setMislike(int mislike) {
        this.mislike = mislike;
    }

    public String getMdate() {
        return mdate;
    }

    public void setMdate(String mdate) {
        this.mdate = mdate;
    }

    public int getMtype() {
        return mtype;
    }

    public void setMtype(int mtype) {
        this.mtype = mtype;
    }

    public String getMgossipenddate() {
        return mgossipenddate;
    }

    public void setMgossipenddate(String mgossipenddate) {
        this.mgossipenddate = mgossipenddate;
    }

    public int getIsvideo() {
        return isvideo;
    }

    public void setIsvideo(int isvideo) {
        this.isvideo = isvideo;
    }

    public String getMhashtag() {
        return mhashtag;
    }

    public void setMhashtag(String mhashtag) {
        this.mhashtag = mhashtag;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getMavatar() {
        return mavatar;
    }

    public void setMavatar(String mavatar) {
        this.mavatar = mavatar;
    }

    public String getMurl() {
        return murl;
    }

    public void setMurl(String murl) {
        this.murl = murl;
    }

    public int getMcomment() {
        return mcomment;
    }

    public void setMcomment(int mcomment) {
        this.mcomment = mcomment;
    }

    public int getMshare() {
        return mshare;
    }

    public void setMshare(int mshare) {
        this.mshare = mshare;
    }

    public int getMlikes() {
        return mlikes;
    }

    public void setMlikes(int mlikes) {
        this.mlikes = mlikes;
    }
    public String getMgossiptitle() {
        return mgossiptitle;
    }

    public void setMgossiptitle(String mgossiptitle) {
        this.mgossiptitle = mgossiptitle;
    }

    public Boolean getShared() {
        return isShared;
    }

    public void setShared(Boolean shared) {
        isShared = shared;
    }

    public ParseObject getObject() {
        return object;
    }

    public void setObject(ParseObject object) {
        this.object = object;
    }

    public Boolean getMislikeBoolean() {
        return mislikeBoolean;
    }

    public void setMislikeBoolean(Boolean mislikeBoolean) {
        this.mislikeBoolean = mislikeBoolean;
    }

    public Boolean getIsinterested() {
        return isinterested;
    }

    public void setIsinterested(Boolean isinterested) {
        this.isinterested = isinterested;
    }

    public String getMeventtitle() {
        return meventtitle;
    }

    public void setMeventtitle(String meventtitle) {
        this.meventtitle = meventtitle;
    }

    public String getMeventlocation() {
        return meventlocation;
    }

    public void setMeventlocation(String meventlocation) {
        this.meventlocation = meventlocation;
    }

    public int getMprice() {
        return mprice;
    }

    public void setMprice(int mprice) {
        this.mprice = mprice;
    }

    public String getMeventdate() {
        return meventdate;
    }

    public void setMeventdate(String meventdate) {
        this.meventdate = meventdate;
    }

    public String getMeventtime() {
        return meventtime;
    }

    public void setMeventtime(String meventtime) {
        this.meventtime = meventtime;
    }
}

