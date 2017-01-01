package com.venu.venutheta.ui;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ImageViewTarget;

import static com.venu.venutheta.utils.ImageUitls.tint;


/**
 * Created by Madiba on 12/13/2016.
 */

public class TintTarget extends ImageViewTarget<GlideDrawable> {
//    private final ColorStateList placeholderColor;
//    private final ColorStateList resultColor;
//    private final ColorStateList errorColor;
//    public TintTarget(ImageView view,
//                      ColorStateList placeholderColor, ColorStateList resultColor, ColorStateList errorColor) {
//        super(view);
//        this.placeholderColor = placeholderColor;
//        this.resultColor = resultColor;
//        this.errorColor = errorColor;
//    }



    public TintTarget(ImageView view) {
        super(view);
    }

    @Override public void setDrawable(Drawable drawable) {
        // don't tint, this is called with a cross-fade drawable,
        // and we need the inner drawables tinted, but not this
        super.setDrawable(drawable);
    }

    @Override public void onLoadStarted(Drawable placeholder) {
        super.onLoadStarted(tint(placeholder));
    }
    @Override public void onLoadFailed(Exception e, Drawable errorDrawable) {
        super.onLoadFailed(e, tint(errorDrawable));
    }
    @Override public void onLoadCleared(Drawable placeholder) {
        super.onLoadCleared(tint(placeholder));
    }
    @SuppressWarnings("unchecked")
    @Override public void onResourceReady(GlideDrawable resource, GlideAnimation glideAnimation) {
        Drawable tinted = tint(resource);
        // animate works with drawable likely because it's accepting Drawables, but declaring GlideDrawable as generics
        if (glideAnimation == null || !glideAnimation.animate(tinted, this)) {
            view.setImageDrawable(tinted);
        }
    }

    @Override protected void setResource(GlideDrawable resource) {
        throw new UnsupportedOperationException("onResourceReady is overridden, this shouldn't be called");
    }
}