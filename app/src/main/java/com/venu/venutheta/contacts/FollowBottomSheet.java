package com.venu.venutheta.contacts;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.parse.ParseObject;
import com.venu.venutheta.R;
import com.venu.venutheta.models.GlobalConstants;
import com.venu.venutheta.services.LoaderGeneral;
import com.venu.venutheta.ui.DividerItemDecoration;
import com.venu.venutheta.utils.NetUtils;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;


public class FollowBottomSheet extends BottomSheetDialogFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerview;
    private List<ParseObject> mDatas = new ArrayList<>();
    private MainAdapter mAdapter;

    RxLoaderManager loaderManager;
    private Boolean loadFollowers;
    private String userId;

    public FollowBottomSheet() {
    }

    public static FollowBottomSheet newInstance(String id, Boolean followers) {
        FollowBottomSheet frg = new FollowBottomSheet();
        Bundle extras= new Bundle();
        extras.putBoolean("loadFollowers",followers);
        extras.putString(GlobalConstants.INTENT_ID,id);
        frg.setArguments(extras);
        return frg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            userId = getArguments().getString(GlobalConstants.INTENT_ID);
            loadFollowers = getArguments().getBoolean("followers");
        }else {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_base, container, false);
        mRecyclerview = (RecyclerView) view.findViewById(R.id.core_recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.core_swipelayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loaderManager = RxLoaderManagerCompat.get(this);
        initAdapter();
    }

    private void initAdapter(){
        mAdapter=new MainAdapter(R.layout.item_ontap,mDatas);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setAdapter(mAdapter);

        mAdapter.setOnRecyclerViewItemClickListener((view, i) -> {

        });


    }

    @Override
    public void onRefresh() {
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            initload();
        }else
            mSwipeRefreshLayout.setRefreshing(false);
    }

    void initload(){
        loaderManager.create(
                LoaderGeneral.loadUsersContacts(userId,1),
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


    private class MainAdapter
            extends BaseQuickAdapter<ParseObject> {

        MainAdapter(int layoutResId, List<ParseObject> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, final ParseObject data) {
//
            if (loadFollowers){
                holder.setText(R.id.cc_i_name, data.getParseUser("from").getUsername());
                Glide.with(mContext).load(data.getParseUser("from").getParseFile("avatar").getUrl())
                        .thumbnail(0.1f).dontAnimate().into((ImageView) holder.getView(R.id.cc_i_avatar));

            }else {
                holder.setText(R.id.cc_i_name, data.getParseUser("to").getUsername());
                Glide.with(mContext).load(data.getParseUser("to").getParseFile("avatar").getUrl())
                        .thumbnail(0.1f).dontAnimate().into((ImageView) holder.getView(R.id.cc_i_avatar));

            }


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            initload();
        }
    }
}
