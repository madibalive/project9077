package com.venu.venutheta.eventmanager;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.parse.ParseObject;
import com.venu.venutheta.R;
import com.venu.venutheta.adapter.SingletonDataSource;
import com.venu.venutheta.navtransitions.NavigateTo;
import com.venu.venutheta.utils.NetUtils;
import com.venu.venutheta.utils.TimeUitls;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.rxloader.RxLoader;
import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;

public class InvitesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<ParseObject> mDatas = new ArrayList<>();
    private TextView totalCount;
    private UserEventsAdapter mAdapter;
    RxLoaderManager loaderManager;



    public InvitesFragment() {
    }

    public static InvitesFragment newInstance() {
        return new InvitesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.container_core, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.container_recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.core_swipelayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loaderManager = RxLoaderManagerCompat.get(this);

        mAdapter = new UserEventsAdapter(R.layout.my_event_layout,mDatas);
        mAdapter.setOnRecyclerViewItemClickListener((view1, i) -> gotoEvent(i));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL ,false));
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onRefresh() {
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext()))
            reload();
        else
            mSwipeRefreshLayout.setRefreshing(false);
    }

    private void initload(){
        loaderManager.create(
                LoaderEventManager.loadInvites(),
                new RxLoaderObserver<List<ParseObject>>() {
                    @Override
                    public void onNext(List<ParseObject> value) {
                        Timber.d("onnext");
                        new Handler().postDelayed(() -> {
                            mSwipeRefreshLayout.setRefreshing(false);
                            if (value.size()>0)
                                mAdapter.setNewData(value);
                        },500);
                    }

                    @Override
                    public void onStarted() {
                        Timber.d("stated");
                        super.onStarted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("stated error %s",e.getMessage());
                        super.onError(e);
                        mSwipeRefreshLayout.setRefreshing(false);

                    }

                    @Override
                    public void onCompleted() {
                        Timber.d("completed");
                        super.onCompleted();
                    }
                }

        ).start();
    }
    private void reload(){
        RxLoader<List<ParseObject>> reload =loaderManager.create(
                LoaderEventManager.loadInvites(),
                new RxLoaderObserver<List<ParseObject>>() {
                    @Override
                    public void onNext(List<ParseObject> value) {
                        Timber.d("onnext");
                        new Handler().postDelayed(() -> {
                            mSwipeRefreshLayout.setRefreshing(false);
                            if (value.size()>0)
                                mAdapter.setNewData(value);
                            totalCount.setText(value.size());
                        },500);
                    }

                    @Override
                    public void onStarted() {
                        Timber.d("stated");
                        super.onStarted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("stated error %s",e.getMessage());
                        super.onError(e);
                        mSwipeRefreshLayout.setRefreshing(false);

                    }

                    @Override
                    public void onCompleted() {
                        Timber.d("completed");
                        super.onCompleted();
                    }
                }

        );
    }


    private class UserEventsAdapter
            extends BaseQuickAdapter<ParseObject> {

        public UserEventsAdapter(int layoutResId, List<ParseObject> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, final ParseObject data) {

            holder.setText(R.id.title, data.getString("title") +"#" + data.getString("hashtag"))
                    .setText(R.id.date_day, TimeUitls.Format_dayOnly(data.getDate("date")))
                    .setText(R.id.date_month, DateFormat.format("MMM", data.getDate("date")))
                    .setText(R.id.date_time, DateFormat.format("HH:mm", data.getDate("time")))
                    .setText(R.id.favorites,String.valueOf(data.getInt("reactions")))
                    .setText(R.id.comments,String.valueOf(data.getInt("commenst")));

            Glide.with(mContext).load(data.getParseFile("image").getUrl())
                    .thumbnail(0.1f)
                    .crossFade()
                    .centerCrop()
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.placeholder_error_media)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fallback(R.drawable.ic_default_avatar)
                    .into((ImageView) holder.getView(R.id.image));

        }

    }

    private void gotoEvent(int pos) {
        SingletonDataSource.getInstance().setCurrentEvent(mAdapter.getItem(pos));
        NavigateTo.goToEventPage(getActivity(),mAdapter.getItem(pos).getObjectId(),mAdapter.getItem(pos).getClassName());
    }


    @Override
    public void onResume() {
        super.onResume();
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext()))
            initload();
    }
}
