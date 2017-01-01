package com.venu.venutheta.galleryPicker;

import android.content.Context;
import android.view.ViewGroup;

import com.venu.venutheta.Actions.ActionGalleryClick;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

class GridAdapter extends RecyclerViewAdapterBase<File, MediaItemView> {

    private final Context context;

    GridAdapter(Context context) {
        this.context = context;
    }


    @Override
    protected MediaItemView onCreateItemView(ViewGroup parent, int viewType) {
        return new MediaItemView(context);
    }

    @Override
    public void onBindViewHolder(ViewWrapper<MediaItemView> viewHolder, final int position) {
        MediaItemView itemView = viewHolder.getView();
        itemView.bind(mItems.get(position));
        itemView.setOnClickListener(view -> EventBus.getDefault().post(new ActionGalleryClick(mItems.get(position))));
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


}