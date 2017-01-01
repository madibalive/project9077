package com.venu.venutheta.Actions;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Madiba on 10/31/2016.
 */

public class ActionTime {

    public Date date;


    public ActionTime(int hour, int min) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.getTime();

        this.date =cal.getTime();

    }
}
