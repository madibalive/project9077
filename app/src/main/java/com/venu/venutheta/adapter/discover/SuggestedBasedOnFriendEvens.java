package com.venu.venutheta.adapter.discover;

import com.android.liuzhuang.rcimageview.RoundCornerImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.parse.ParseObject;
import com.venu.venutheta.R;
import com.venu.venutheta.utils.TimeUitls;

import java.util.List;

/**
 * Created by Madiba on 10/9/2016.
 */
public class SuggestedBasedOnFriendEvens extends BaseQuickAdapter<ParseObject> {

    public SuggestedBasedOnFriendEvens(int layoutResId, List<ParseObject> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, ParseObject event) {
        holder.setText(R.id.ev_title, event.getString("title"))
                .setText(R.id.ev_feature_tag, TimeUitls.getLiveBadgeText(event.getDate("date")))
                .setText(R.id.ev_location, event.getString("location"))
                .setText(R.id.ev_type, String.valueOf(event.getInt("favorties")));

        Glide.with(mContext)
                .load(event.getString("url"))
                .crossFade()
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.placeholder_error_media)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .fallback(R.drawable.ic_default_avatar)
                .thumbnail(0.4f)
                .into(((RoundCornerImageView) holder.getView(R.id.ev_image)));
    }
}