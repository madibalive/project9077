package com.venu.venutheta.adapter.trends;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.venu.venutheta.R;
import com.venu.venutheta.models.TrendingModel;

import java.util.List;

import timber.log.Timber;

public class TrendingAdapterVersion extends BaseQuickAdapter<TrendingModel> {

    public TrendingAdapterVersion(int layoutResId, List<TrendingModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected int getDefItemViewType(int position) {
        Log.i(TAG, "getDefItemViewType: " + getItem(position).getType());

        if (getItem(position).getType() == TrendingModel.TRENDING)
            return TrendingModel.TRENDING;

        else if (getItem(position).getType() == TrendingModel.LIVE_NOW)
                    return TrendingModel.LIVE_NOW;

        else if (getItem(position).getType() == TrendingModel.GOSSIP)
            return TrendingModel.GOSSIP;

        else if (getItem(position).getType() == TrendingModel.EVENTS)
            return TrendingModel.EVENTS;

        return super.getDefItemViewType(position);
    }

    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TrendingModel.TRENDING)
            return new HashTagsViewHolder(getItemView(R.layout.container_bare, parent));

        else if (viewType == TrendingModel.LIVE_NOW)
            return new LiveNowViewHolder(getItemView(R.layout.container_bare, parent));

        else if (viewType == TrendingModel.GOSSIP)
            return new TopGossipViewHolder(getItemView(R.layout.container_bare, parent));
        else if (viewType == TrendingModel.EVENTS)
            return new TopEventViewHolder(getItemView(R.layout.container_bare, parent));

        return super.onCreateDefViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        try {
            TrendingModel n = getItem(position);

        }catch (Exception e){
            Timber.e(e.getMessage());
        }

        if (holder instanceof LiveNowViewHolder) {
            RecyclerView rview = ((LiveNowViewHolder) holder).getView(R.id.bare_recyclerview);
            TextView title = ((LiveNowViewHolder) holder).getView(R.id.bare_title);
            title.setText("Trending Hashtags");
//            LiveNowAdapter mAdapter = new LiveNowAdapter(R.layout.item_live_now, n.getdata());
//            mAdapter.setOnRecyclerViewItemClickListener((view, i) -> {
//                SingletonDataSource.getInstance().setCurrentEvent(mAdapter.getItem(i));
//                NavigateTo.goToEventPage(mContext,mAdapter.getItem(i).getObjectId(),mAdapter.getItem(i).getClassName());
//            });
//            rview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
//            rview.setAdapter(mAdapter);

        }

        else if (holder instanceof HashTagsViewHolder) {


//              HashtagView hashtagView = ((HashTagsViewHolder)holder).getView(R.id.hashtagview);
//
//              hashtagView.setData(n.getmObjectData(), item -> {
//                  SpannableString spannableString = new SpannableString(item.getString("tag"));
//                  spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#85F5F5F5")), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                  return spannableString;
//              });
//
//              hashtagView.addOnTagClickListener(item -> {
//                  ParseObject a = (ParseObject) item;
//                  Toast.makeText(mContext,a.getString("title"),Toast.LENGTH_SHORT).show();
//              });
          }


        else if (holder instanceof TopGossipViewHolder) {
            RecyclerView rview = ((TopGossipViewHolder) holder).getView(R.id.bare_recyclerview);
            TextView title = ((TopGossipViewHolder) holder).getView(R.id.bare_title);
            title.setText("Trending Gossips");
//            HashTagsAdapter mAdapter = new HashTagsAdapter(R.layout.gossip_small, n.getdata());
//            rview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
//            rview.setAdapter(mAdapter);
//            mAdapter.setOnRecyclerViewItemClickListener((view, i) -> {
//                NavigateTo.goToGossip(mContext,mAdapter.getItem(i).getInt("chat_id"));
//            });
        }

        else if (holder instanceof TopEventViewHolder) {
            RecyclerView rview = ((TopEventViewHolder) holder).getView(R.id.bare_recyclerview);
            TextView title = ((TopEventViewHolder) holder).getView(R.id.bare_title);
            title.setText("Popular Events");
//            TopEventAdapter mAdapter = new TopEventAdapter(R.layout.sub_event_box_row, n.getdata());
//            rview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
//            rview.setAdapter(mAdapter);
//            mAdapter.setOnRecyclerViewItemClickListener((view, i) -> {
//                SingletonDataSource.getInstance().setCurrentEvent(mAdapter.getItem(i));
//                NavigateTo.goToEventPage(mContext,mAdapter.getItem(i).getObjectId(),mAdapter.getItem(i).getClassName());
//            });
        }

        super.onBindViewHolder(holder, position, payloads);

    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, TrendingModel discoverModel) {

    }

    private class LiveNowViewHolder extends BaseViewHolder {
        public LiveNowViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class TopEventViewHolder extends BaseViewHolder {
        public TopEventViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class HashTagsViewHolder extends BaseViewHolder {
        public HashTagsViewHolder(View itemView) {
            super(itemView);
        }
    }
     private class TopGossipViewHolder extends BaseViewHolder {
            public TopGossipViewHolder(View itemView) {
                super(itemView);
            }
        }


}
