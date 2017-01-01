package com.venu.venutheta.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Madiba on 11/23/2016.
 */

public class TimeUitls {
    private static Calendar dateTime = Calendar.getInstance();

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    public static final long HOUR = 3600*1000; // in milli-seconds.


    public TimeUitls() {
    }

    public static long calculateDays(Date mDate1, Date mDate2) {
        return Math.abs((mDate1.getTime() - mDate2.getTime()) / (24 * 60 * 60 * 1000) + 1);
    }

    public static Date getCurrentDate(){
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

// start of today
       return cal.getTime();

    }
    static long getCurrentTime() {
        return new Date().getTime();
    }

    public static String getTimeAgo(long time, Context context) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = getCurrentTime();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return diff / SECOND_MILLIS + "s";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return 1 + "m";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + "m";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return 1 + "h";
        } else if (diff < 24 * HOUR_MILLIS) {
            if (diff / HOUR_MILLIS == 1)
                return 1 + "h";
            else
                return diff / HOUR_MILLIS + "h";
        } else if (diff < 48 * HOUR_MILLIS) {
            return 1 + "d";
        } else {
            return diff / DAY_MILLIS + "d";
        }
    }

    public static String getLiveBadgeText(Date start) {
        long now = getCurrentTime();
        Date end = new Date(start.getTime()+2*HOUR);


        if (now < start.getTime()) {
            // Will be live later
            return "UPCOMING";
        } else if (start.getTime() <= now && now <= end.getTime()) {
            // Live right now!
            // Indicated by a visual live now badge
            return "LIVE NOW";
        } else {
            // Too late.
            return "PAST";
        }
    }



    public static void showDatePickerDialog(final Context mContext,
                                            final String format, final TextView mTextView) {
        new DatePickerDialog(mContext, (view, year, monthOfYear, dayOfMonth) -> {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(format, Locale.getDefault());
            dateTime.set(year, monthOfYear, dayOfMonth);

            mTextView.setText(dateFormatter.format(dateTime.getTime()));
        }, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH),
                dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    public static void showTimePickerDialog(final Context mContext,
                                            final TextView mTextView) {
        new TimePickerDialog(mContext, (view, hourOfDay, minute) -> {
            SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a",Locale.getDefault());
            dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateTime.set(Calendar.MINUTE, minute);

            mTextView.setText(timeFormatter.format(dateTime.getTime()));
        }, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE),
                false).show();
    }

    public static String Format_dayOnly(Date date){
        String day = (String) DateFormat.format("dd", date); //20
        return day;
    }

    public static String Format12Dec(Date date){

        String day = (String) DateFormat.format("dd", date); //20
        String stringMonth = (String) DateFormat.format("MMM", date); //Jun

        return day + " " + stringMonth;
    }

    public static String Format12_34(Date date){
        return (String) DateFormat.format("HH:mm", date); //Jun

    }

    public static String elapseTime(Date date){
        long now =getCurrentTime();
        long diffInMillisec = date.getTime() -now;
        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMillisec);
        float hours = diffInSec % 24;
        return String.valueOf(Math.round(hours));
    }

    public static String getRelativeTime( Date start) {

        if(start!=null) {
            return DateUtils.getRelativeTimeSpanString(start.getTime(),
                    System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS)
                    .toString().toLowerCase();
        }
        return null;
    }

    public static Date gossipQuery(Date date){
        long now = getCurrentTime();

        Date end = new Date(date.getTime()+12*HOUR);


            // gossipn +12 hours

        // difference <less 12
        return null;

    }
    public static Date addExpiryDate(){
        Date now = getCurrentDate();
        Date end = new Date(now.getTime()+24*HOUR);
        return end;
    }


}
