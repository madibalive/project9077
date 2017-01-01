package com.venu.venutheta.utils;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import timber.log.Timber;

/**
 * Created by Madiba on 12/10/2016.
 */

public class TextUitls {

    public static void highlightAndBoldText(TextView textView, int highlightColor) {
        final String string = textView.getText().toString();
        textView.setText(getHighlightText(textView.getContext(), string, highlightColor, true));
    }

    public static CharSequence getHighlightText(Context context, String string, int highlightColor, boolean bold) {
        final int highlightStart = string.indexOf('_');

        if (highlightStart < 0) {
            Timber.e("Failed to highlight text - could not find _ marker in string.");
            return string;
        }

        final int highlightEnd = string.lastIndexOf('_') - 1;
        if (highlightStart >= highlightEnd) {
            Timber.e("Failed to highlight text - make sure you have 2 _ markers to denote start and end of highlight region");
            return string;
        }

        SpannableString colorSpannable = new SpannableString(string.replaceAll("_", ""));
        colorSpannable.setSpan(new ForegroundColorSpan(highlightColor),
                highlightStart,
                highlightEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (bold) {

        }
        return colorSpannable;
    }
}
