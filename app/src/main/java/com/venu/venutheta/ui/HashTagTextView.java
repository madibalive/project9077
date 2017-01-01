/*
 * Copyright 2015 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.venu.venutheta.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.venu.venutheta.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HashTagTextView extends TextView {

    private static final Pattern PATTERN_SEARCH_TERM = Pattern.compile("#([A-Za-z0-9_-]+)");
    public HashTagTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!TextUtils.isEmpty(text)) {
            Matcher matcher = PATTERN_SEARCH_TERM.matcher(text);
            SpannableStringBuilder ssb = new SpannableStringBuilder(text);
            while (matcher.find()) {
                ssb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.venu_blue)), matcher.start(), matcher.end(), 0);
                ssb.setSpan(new StyleSpan(Typeface.BOLD),matcher.start(), matcher.end(), 0);
            }
            text = ssb;
        }
        super.setText(text, type);
    }
}
