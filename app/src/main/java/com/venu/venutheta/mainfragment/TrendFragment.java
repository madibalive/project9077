package com.venu.venutheta.mainfragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.venu.venutheta.R;
import com.venu.venutheta.adapter.trends.TrendingAdapterVersion;
import com.venu.venutheta.models.TrendingModel;
import com.venu.venutheta.utils.NetUtils;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.rxloader.RxLoader;
import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;


public class TrendFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private TrendingAdapterVersion mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<TrendingModel>  mDatas = new ArrayList<>();
    private RxLoader<List<TrendingModel>> listRxLoader;
    private RxLoaderManager loaderManager;
    private View headView;
    private TextView mHeaderText;
    private SpannableString header;


    public TrendFragment() {
    }

    public static TrendFragment newInstance() {
        return new TrendFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new TrendingAdapterVersion(R.layout.container_recycler, mDatas);

        header = new SpannableString("What's\n" // 0-7
                + "Trending  \n" //14
                + "on --Venu\n"   // index 14 - 19
        );
        header.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.venu_blue)), 21,27, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.container_core, container, false);
        headView = inflater.inflate(R.layout.header_trending, container, false);
        mHeaderText = (TextView) headView.findViewById(R.id.header_text);
        mHeaderText.setText(mHeaderText.toString());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.core_recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.core_swipelayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mHeaderText.setText(header);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loaderManager = RxLoaderManagerCompat.get(this);
        initAdapter();
        initload();


    }

    @Override
    public void onRefresh() {
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            listRxLoader.restart();
        }else
            mSwipeRefreshLayout.setRefreshing(false);}


    private void initAdapter(){
        mAdapter.addHeaderView(headView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

    }

    private void initload(){
        listRxLoader =loaderManager.create(
                LoaderMainFragment.loadrend(),
                new RxLoaderObserver<List<TrendingModel>>() {
                    @Override
                    public void onNext(List<TrendingModel> value) {
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
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            listRxLoader.start();
        }
    }

}
