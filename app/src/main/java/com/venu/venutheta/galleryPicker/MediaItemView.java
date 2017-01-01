package com.venu.venutheta.galleryPicker;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.venu.venutheta.R;

import java.io.File;

public class MediaItemView extends RelativeLayout {
    ImageView mMediaThumb;
    private File mCurrentFile;

    public MediaItemView(Context context) {
        super(context);
        View v = View.inflate(context, R.layout.media_item_view, this);
        mMediaThumb = (ImageView) v.findViewById(R.id.mMediaThumb);
    }

    public void bind(File file) {
        mCurrentFile = file;
        Glide.with(getContext())
                .load(Uri.fromFile(file))
                .centerCrop()
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.placeholder_media)
                .error(R.drawable.placeholder_error_media)
                .dontAnimate()
                .into(mMediaThumb);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }


}
