package com.venu.venutheta.Actions;

/**
 * Created by Madiba on 12/15/2016.
 */

public class ActionEvantPageBuy {
    private Boolean error;
    private Boolean SecondButton;
    private Boolean isInvited;
    private boolean isGoing;
    private String msg;

    public ActionEvantPageBuy() {
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public boolean isGoing() {
        return isGoing;
    }

    public void setGoing(boolean going) {
        isGoing = going;
    }

    public Boolean getSecondButton() {
        return SecondButton;
    }

    public void setSecondButton(Boolean secondButton) {
        SecondButton = secondButton;
    }

    public Boolean getInvited() {
        return isInvited;
    }

    public void setInvited(Boolean invited) {
        isInvited = invited;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
