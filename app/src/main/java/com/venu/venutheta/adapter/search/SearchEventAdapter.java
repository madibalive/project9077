package com.venu.venutheta.adapter.search;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.parse.ParseObject;
import com.venu.venutheta.R;

import java.util.List;

/**
 * Created by Madiba on 10/12/2016.
 */
public class SearchEventAdapter
        extends BaseQuickAdapter<ParseObject> {

    public SearchEventAdapter(int layoutResId, List<ParseObject> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, final ParseObject event) {

        holder.setText(R.id.ev_title, event.getString("title"))
        .setText(R.id.ev_tag, event.getString("hashTag"));

        Glide.with(mContext)
                .load(event.getString("url"))
                .crossFade()
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.placeholder_error_media)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .fallback(R.drawable.ic_default_avatar)
                .thumbnail(0.4f)
                .into(((ImageView) holder.getView(R.id.ev_image)));
    }
}