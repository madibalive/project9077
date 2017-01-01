package com.venu.venutheta.gallery;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.venu.venutheta.R;
import com.venu.venutheta.models.GlobalConstants;

import java.util.ArrayList;
import java.util.List;


public class GalleryActivityFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    private static final String TAG = "GalleryActivity";
    private RecyclerView mRecyclerView;
    private GalleryAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View notLoadingView;
    private int delayMillis = 1000;
    private int mCurrentCounter = 0;
    private List<ParseObject> mDatas = new ArrayList<>();
    private ParseObject object;
    private ParseQuery<ParseObject> query;
    private Boolean running =true;

    public GalleryActivityFragment() {
    }

    public static GalleryActivityFragment newInstance() {
        return new GalleryActivityFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       if (getActivity().getIntent() != null){
           object = ParseObject.createWithoutData(GlobalConstants.CLASS_EVENT,getActivity().getIntent().getStringExtra(GlobalConstants.PASS_ID));
           object.fetchInBackground();
       }
        mAdapter = new GalleryAdapter(R.layout.gallery_picker_view, mDatas);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.container_core, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.core_swipelayout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.core_recyclerview);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mAdapter.setOnLoadMoreListener(this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter.setOnRecyclerViewItemClickListener((view1, i) -> {

        });

        mRecyclerView.setAdapter(mAdapter);
        load();
    }

    @Override
    public void onRefresh() {
        load();
    }

    @Override
    public void onLoadMoreRequested() {
        if (query != null)
            query = null;
        query= ParseQuery.getQuery(GlobalConstants.CLASS_EVENT);
        query.setLimit(20);
        query.setSkip(mCurrentCounter);
        query.orderByAscending("updatedAt");
        query.findInBackground((data, e) -> {
            if (e == null) {
                if (running) {
                    if (data.size() <= 0) {
                        mAdapter.notifyDataChangedAfterLoadMore(false);
                        if (notLoadingView == null) {
                            notLoadingView = getActivity().getLayoutInflater().inflate(R.layout.view_no_more_data, (ViewGroup) mRecyclerView.getParent(), false);
                        }
                        mAdapter.addFooterView(notLoadingView);
                    } else {
                        new Handler().postDelayed(() -> {
                            mAdapter.notifyDataChangedAfterLoadMore(data, true);
                            mCurrentCounter = mAdapter.getData().size();

                        }, delayMillis);
                    }

                } else {
                    Log.i(TAG, "done:error " + e.getMessage());
                }
            }
        });
    }

    private void load() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(GlobalConstants.CLASS_EVENT);
        query.orderByAscending("updatedAt");
        query.setLimit(20);
        query.findInBackground((objects, e) -> {
            if (e == null) {
                if (running) {
                    new Handler().postDelayed(() -> {
                        mAdapter.setNewData(objects);
                        mAdapter.openLoadMore(20, true);
                        mAdapter.removeAllFooterView();
                        mSwipeRefreshLayout.setRefreshing(false);
                        mCurrentCounter = mAdapter.getData().size();

                    }, 500);
                } else {
                    Log.i(TAG, "done:error " + e.getMessage());
                }
            }
        });
    }

    private class GalleryAdapter
            extends BaseQuickAdapter<ParseObject> {

        public GalleryAdapter(int layoutResId, List<ParseObject> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, final ParseObject notif) {

//            holder.setText(R.id.notif_i_name, "sample name")
//                    .setText(R.id.notif_i_content, "sample text")
//                    .setOnClickListener(R.id.notif_i_content, new OnItemChildClickListener())
//                    .setOnClickListener(R.id.notif_i_avatar, new OnItemChildClickListener())
//                    .setOnClickListener(R.id.notif_i_rightimage, new OnItemChildClickListener());

//            Glide.with(mContext).load("https://s-media-cache-ak0.pinimg.com/originals/db/e3/2d/dbe32dbbb95a3b79a15426dac1d364ea.jpg")
//                    .thumbnail(0.5f)
//                    .crossFade()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into((ImageView) holder.getView(R.id.gallery_image));


        }
    }

    @Override
    public void onStop() {
        if (running)
            running=false;
        if (query != null)
            query = null;
        super.onStop();
    }

    @Override
    public void onResume() {
        running =true;
        super.onResume();
    }
}
