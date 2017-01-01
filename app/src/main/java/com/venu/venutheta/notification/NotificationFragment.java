package com.venu.venutheta.notification;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.liuzhuang.rcimageview.CircleImageView;
import com.android.liuzhuang.rcimageview.RoundCornerImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.parse.ParseObject;
import com.venu.venutheta.R;
import com.venu.venutheta.services.GeneralService;
import com.venu.venutheta.services.LoaderGeneral;
import com.venu.venutheta.ui.DividerItemDecoration;
import com.venu.venutheta.utils.NetUtils;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;


public class NotificationFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    private RecyclerView mRecyclerView;
    private NotifAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View notLoadingView;
    private int mCurrentCounter = 0;
    private List<ParseObject> mDatas;
    RxLoaderManager loaderManager;

    public NotificationFragment() {
    }

    public static NotificationFragment newInstance() {
        return new NotificationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatas = new ArrayList<>();
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
        loaderManager = RxLoaderManagerCompat.get(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        initAdapter();
    }

    private void initAdapter(){
        mAdapter = new NotifAdapter(R.layout.item_notif, mDatas);
        mAdapter.setOnLoadMoreListener(this);
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);

        mAdapter.setOnRecyclerViewItemChildClickListener((baseQuickAdapter, view, i) -> {

            if (view.getId() == R.id.notif_i_btn){
                acceptInvite(mAdapter.getItem(i));

            }
        });

        mAdapter.setOnRecyclerViewItemClickListener((view1, i) -> {
            ParseObject notif = mAdapter.getItem(i);
            switch (notif.getInt("type")) {
                case 0:
                    // event
                    break;
                case 1:
                    //Photo
                    break;
                case 2:
                    //video;
                    break;
                default:
                    //handle defualt
                    break;
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

    }

    private void acceptInvite(ParseObject obj){
        GeneralService.startActionSetGoing(getActivity().getApplicationContext(),obj.getObjectId(),obj.getClassName());
    }


    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext()))
            initialLoad();
        else
            mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onLoadMoreRequested() {
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            initialLoad();
        }else
            mSwipeRefreshLayout.setRefreshing(false);
    }

    private void initialLoad(){
        loaderManager.create(
                LoaderGeneral.loadNotifications(mCurrentCounter),
                new RxLoaderObserver<List<ParseObject>>() {
                    @Override
                    public void onNext(List<ParseObject> value) {
                        Timber.d("onnext");
                        if (value.size() <= 0) {
                            mAdapter.notifyDataChangedAfterLoadMore(false);
                            if (notLoadingView == null) {
                                notLoadingView = getActivity().getLayoutInflater().inflate(R.layout.view_no_more_data, (ViewGroup) mRecyclerView.getParent(), false);
                            }
                            mAdapter.addFooterView(notLoadingView);

                        } else {
                            new Handler().postDelayed(() -> {
                                mAdapter.removeAllFooterView();
                                if (mSwipeRefreshLayout.isRefreshing()) {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                    mAdapter.setNewData(value);
                                    mAdapter.openLoadMore(20, true);
                                    mAdapter.removeAllFooterView();
                                }else {
                                    mAdapter.notifyDataChangedAfterLoadMore(value, true);
                                    mCurrentCounter = mAdapter.getData().size();
                                }
                                mCurrentCounter = mAdapter.getData().size();
                                ParseObject.unpinAllInBackground(GlobalConstants.CLASS_NOTIFICATION, e1 -> {
                                    ParseObject.pinAllInBackground(GlobalConstants.CLASS_NOTIFICATION, value);
                                });
                            }, 500);

                        }
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


    private class NotifAdapter
            extends BaseQuickAdapter<ParseObject> {

        NotifAdapter(int layoutResId, List<ParseObject> data) {
            super(layoutResId, data);
        }


        @Override
        protected void convert(BaseViewHolder holder, final ParseObject notif) {


            String msg ;

            switch (notif.getInt("type")){

                case 1:
                    msg = "liked your post";
                    break;
                case 2:
                    msg="Commented on your post";
                    break;
                case 3:
                    msg="Followed you";
                    break;
                case 4:
                    msg="Invited to an event";
                    break;

                default:
                    msg="";
                    break;
            }
            holder.setText(R.id.notif_i_name, notif.getParseUser("from").getUsername())
                    .setText(R.id.notif_i_content,msg);

            Glide.with(mContext)
                    .load(notif.getParseUser("from").getParseFile("avatar"))
                    .crossFade()
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.placeholder_error_media)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .fallback(R.drawable.ic_default_avatar)
                    .thumbnail(0.4f)
                    .into((CircleImageView) holder.getView(R.id.notif_i_avatar));

            if (notif.getInt("type")==4){
                holder.setOnClickListener(R.id.notif_i_btn,new OnItemChildClickListener())
                        .setVisible(R.id.notif_i_btn,true);
            }

            if (notif.getInt("type") != 4){
                Glide.with(mContext)
                        .load(notif.getParseObject("object").getParseFile("url"))
                        .crossFade()
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.placeholder_error_media)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .fallback(R.drawable.ic_default_avatar)
                        .thumbnail(0.4f)
                        .into((RoundCornerImageView) holder.getView(R.id.notif_i_rightimage));
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            initialLoad();
        }
    }

}
