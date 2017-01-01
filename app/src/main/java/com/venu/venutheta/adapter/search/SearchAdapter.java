package com.venu.venutheta.adapter.search;

import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.greenfrvr.hashtagview.HashtagView;
import com.parse.ParseObject;
import com.venu.venutheta.R;
import com.venu.venutheta.adapter.SingletonDataSource;
import com.venu.venutheta.models.SearchModel;
import com.venu.venutheta.navtransitions.NavigateTo;

import java.util.List;

/**
 * Created by Madiba on 10/9/2016.
 */

public class SearchAdapter extends BaseQuickAdapter<SearchModel> {
    private final static String Tag ="Adapter";
    public SearchAdapter(int layoutResId, List<SearchModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected int getDefItemViewType(int position) {

        if (getItem(position).getmType() == SearchModel.PEOPLE)
            return SearchModel.PEOPLE;
        else if (getItem(position).getmType() == SearchModel.GOSSIP)
            return SearchModel.GOSSIP;
        else if (getItem(position).getmType() == SearchModel.EVENT)
            return SearchModel.EVENT;
        else {
            Log.i(Tag, "getDefItemViewType: nothing ");
        }
        return super.getDefItemViewType(position);
    }

    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SearchModel.PEOPLE){
            return new PeopleViewHolder(getItemView(R.layout.container_bare, parent));
        }
        else if (viewType == SearchModel.GOSSIP){
            return new GossipViewHolder(getItemView(R.layout.container_bare, parent));
        }
        else if (viewType == SearchModel.EVENT){
            return new EventsViewHolder(getItemView(R.layout.container_bare, parent));
        }else {
            Log.i(Tag, "onCreateDefViewHolder: nothing ");

        }

        return super.onCreateDefViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        SearchModel n = getItem(position);

        if (holder instanceof PeopleViewHolder) {

            GridLayoutManager layMng = new GridLayoutManager(mContext, 4);
            TextView t = ((PeopleViewHolder) holder).getView(R.id.bare_title);
            t.setText("Users found");
            RecyclerView rview = ((PeopleViewHolder) holder).getView(R.id.bare_recyclerview);
            final SearchPeopleAdapter mAdapter = new SearchPeopleAdapter(R.layout.item_search_user, n.getUsers());

            mAdapter.setOnRecyclerViewItemClickListener((view, i) -> {
                SingletonDataSource.getInstance().setCurrentUser(mAdapter.getItem(i));
                NavigateTo.goToUserPage(mContext,mAdapter.getItem(i).getObjectId());
            });
            rview.setLayoutManager(layMng);
            rview.setAdapter(mAdapter);
        }

        else if (holder instanceof GossipViewHolder) {

            RecyclerView rview = ((GossipViewHolder) holder).getView(R.id.bare_recyclerview);
            RelativeLayout rl = ((GossipViewHolder) holder).getView(R.id.bare_header);
            TextView t = ((GossipViewHolder) holder).getView(R.id.bare_title);
            t.setText("Gossips found");

            SearchGossipAdapter mAdapter = new SearchGossipAdapter(R.layout.item_search_gossip, n.getData());
            rview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            rview.setAdapter(mAdapter);

            mAdapter.setOnRecyclerViewItemClickListener((view, i) -> NavigateTo.goToGossip(mContext,mAdapter.getItem(i).getInt("chatId")));


        } else if (holder instanceof EventsViewHolder) {

            RecyclerView rview = ((EventsViewHolder) holder).getView(R.id.bare_recyclerview);
            TextView t = ((EventsViewHolder) holder).getView(R.id.bare_title);
            t.setText("Events found");

            SearchEventAdapter mAdapter = new SearchEventAdapter(R.layout.item_search_event, n.getData());
            rview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            rview.setAdapter(mAdapter);
            mAdapter.setOnRecyclerViewItemClickListener((view, i) -> {
                SingletonDataSource.getInstance().setCurrentEvent(mAdapter.getItem(i));
                NavigateTo.goToEventPage(mContext,mAdapter.getItem(i).getObjectId(),mAdapter.getItem(i).getClassName());
            });

        }

        super.onBindViewHolder(holder, position, payloads);

    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, SearchModel discoverModel) {

    }

    public static final HashtagView.DataTransform<ParseObject> HASH = item -> {

        SpannableString spannableString = new SpannableString("#" + item.getString("tag"));
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#85F5F5F5")), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;

    };

    private class EventsViewHolder extends BaseViewHolder {
        public EventsViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class PeopleViewHolder extends BaseViewHolder {
        public PeopleViewHolder(View itemView) {
            super(itemView);
        }
    }


    private class GossipViewHolder extends BaseViewHolder {
        public GossipViewHolder(View itemView) {
            super(itemView);
        }
    }

  }
