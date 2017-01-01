package com.venu.venutheta.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Madiba on 9/25/2016.
 */

public class HashTagsAdapter extends BaseQuickAdapter<ParseObject> {
    public HashTagsAdapter(int layoutResId, List<ParseObject> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, ParseObject event) {
//        holder.setText(R.id.lv_name, event.getString("title"));
//        Glide.with(mContext).load(event.getParseFile("photo").getUrl()).crossFade().into((ImageView) holder.getView(R.id.lv_image));

    }
}
