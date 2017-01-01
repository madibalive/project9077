package com.venu.venutheta.utils;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Madiba on 12/10/2016.
 */

public class ViewUtils {

    public static void fadeInView(@Nullable final View view) {
        fadeInView(view, 1, new ValueAnimator().getDuration(), 0);
    }

    public static void fadeInView(@Nullable final View view, long duration) {
        fadeInView(view, 1, duration, 0);
    }

    public static void fadeInView(@Nullable final View view, final float targetAlpha, final long duration, final long startDelay) {
        if (view == null) {
            return;
        }
        view.animate()
                .alpha(targetAlpha)
                .setDuration(duration)
                .setStartDelay(startDelay)
                .withStartAction(() -> view.setVisibility(View.VISIBLE))
                .start();
    }

    public static void fadeOutView(@Nullable final View view) {
        fadeOutView(view, true);
    }

    public static void fadeOutView(@Nullable final View view, boolean setToGoneWithEndAction) {
        fadeOutView(view, new ValueAnimator().getDuration(), 0, setToGoneWithEndAction);
    }

    public static void fadeOutView(@Nullable final View view, final long duration) {
        fadeOutView(view, duration, 0);
    }

    public static void fadeOutView(@Nullable final View view, final long duration, final long startDelay) {
        fadeOutView(view, duration, startDelay, true);
    }

    public static void fadeOutView(@Nullable final View view, final long duration, final long startDelay, final boolean setToGoneWithEndAction) {
        if (view == null) {
            return;
        }
        if (view.getVisibility() == View.GONE || view.getVisibility() == View.INVISIBLE) {
            return;
        }

        view.animate()
                .alpha(0)
                .setDuration(duration)
                .setStartDelay(startDelay)
                .withEndAction(() -> view.setVisibility(setToGoneWithEndAction ? View.GONE : View.INVISIBLE))
                .start();
    }

    public static void setMarginTop(View v, int topMargin) {
        ((ViewGroup.MarginLayoutParams) v.getLayoutParams()).topMargin = topMargin;
        v.invalidate();
    }
    public static void setMarginBottom(View v, int bottomMargin) {
        ((ViewGroup.MarginLayoutParams) v.getLayoutParams()).bottomMargin = bottomMargin;
        v.invalidate();
    }

    public static void setMarginLeft(View v, int leftMargin) {
        ((ViewGroup.MarginLayoutParams) v.getLayoutParams()).leftMargin = leftMargin;
        v.invalidate();
    }

    public static void setMarginRight(View v, int rightMargin) {
        ((ViewGroup.MarginLayoutParams) v.getLayoutParams()).rightMargin = rightMargin;
        v.invalidate();
    }

    public static AlertDialog showAlertDialog(Context context,
                                              CharSequence title,
                                              CharSequence message,
                                              CharSequence button,
                                              DialogInterface.OnClickListener onClickListener,
                                              boolean cancelable) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setCancelable(cancelable)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(button, onClickListener)
                .create();
        dialog.show();
        return dialog;
    }

    public static AlertDialog showAlertDialog(Context context,
                                              CharSequence title,
                                              CharSequence message,
                                              CharSequence positiveButton,
                                              CharSequence negativeButton,
                                              DialogInterface.OnClickListener positiveAction,
                                              DialogInterface.OnClickListener negativeAction) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, positiveAction)
                .setNegativeButton(negativeButton, negativeAction)
                .create();
        dialog.show();
        return dialog;
    }

    @SuppressLint("com.waz.ViewUtils")
    public static <T extends View> T getView(@NonNull View v, @IdRes int resId) {
        return (T) v.findViewById(resId);
    }

    @SuppressLint("com.waz.ViewUtils")
    public static <T extends View> T getView(@NonNull Dialog d, @IdRes int resId) {
        return (T) d.findViewById(resId);
    }


    @SuppressLint("com.waz.ViewUtils")
    public static <T extends View> T getView(@NonNull Activity activity, @IdRes int resId) {
        return  (T) activity.findViewById(resId);
    }


}
