/*
 * Copyright 2015 Google Inc.
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

package com.venu.venutheta.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;

/**
 * Utility methods for working with colors.
 */
public class ColorUtils {

    private ColorUtils() { }

    /**
     * Set the alpha component of {@code color} to be {@code alpha}.
     */
    public static int modifyAlpha(@ColorInt int color, @IntRange(from = 0, to = 255) int alpha) {
        return (color & 0x00ffffff) | (alpha << 24);
    }

    /**
     * Set the alpha component of {@code color} to be {@code alpha}.
     */
    public static int modifyAlpha(@ColorInt int color,
                                  @FloatRange(from = 0f, to = 1f) float alpha) {
        return modifyAlpha(color, (int) (255f * alpha));
    }

    public static Drawable colorDrawable(Resources res, @DrawableRes int drawableResId, @ColorRes int colorResId,Context context) {

        Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
        int color = res.getColor(colorResId);
        drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    public static @Nullable
    Palette.Swatch getMostPopulousSwatch(Palette palette) {
        Palette.Swatch mostPopulous = null;
        if (palette != null) {
            for (Palette.Swatch swatch : palette.getSwatches()) {
                if (mostPopulous == null || swatch.getPopulation() > mostPopulous.getPopulation()) {
                    mostPopulous = swatch;
                }
            }
        }
        return mostPopulous;
    }

}
