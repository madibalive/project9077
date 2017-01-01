package com.venu.venutheta.adapter.discover;

import android.util.Log;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.venu.venutheta.R;
import com.venu.venutheta.models.CategoriesModel;
import com.venu.venutheta.utils.ColorUtils;

import java.util.List;

/**
 * Created by Madiba on 10/9/2016.
 */

public class CategoriesAdapter extends BaseQuickAdapter<CategoriesModel> {

    public CategoriesAdapter(int layoutResId, List<CategoriesModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, CategoriesModel category) {
        Log.i(TAG, "convert: categories" + category.getmName());
        holder.setText(R.id.ct_title, category.getmName());

        RelativeLayout m = holder.getView(R.id.ct_bgrnd);
        m.setBackground(ColorUtils.colorDrawable(mContext.getResources(),R.drawable.category_background_default,R.color.venu_red,mContext));


    }
}