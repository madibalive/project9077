package com.venu.venutheta.Discover;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.venu.venutheta.R;
import com.venu.venutheta.adapter.discover.DiscoverAdapter;
import com.venu.venutheta.models.DiscoverModel;
import com.venu.venutheta.services.LoaderGeneral;
import com.venu.venutheta.utils.NetUtils;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.rxloader.RxLoader;
import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;

public class DiscoverActivty extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout mSwipeRefreshLayout;
    DiscoverAdapter mAdapter;
    RecyclerView mRecyclerview;
    List<DiscoverModel> mDatas= new ArrayList<>();
    RxLoaderManager loaderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderManager =  RxLoaderManagerCompat.get(this);
        setContentView(R.layout.activity_discover);
        mRecyclerview = (RecyclerView)findViewById(R.id.discover_recyclverview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.core_swipelayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DiscoverAdapter(R.layout.view_empty,mDatas);
        mRecyclerview.setAdapter(mAdapter);
        if (NetUtils.hasInternetConnection(getApplicationContext())){
            initload();
        }
    }

    @Override
    public void onRefresh() {
        if (NetUtils.hasInternetConnection(getApplicationContext())){
            reload();
        }else
            mSwipeRefreshLayout.setRefreshing(false);
    }

    void initload(){

        loaderManager.create(
                LoaderGeneral.loadDiscover(),
                new RxLoaderObserver<List<DiscoverModel>>() {
                    @Override
                    public void onNext(List<DiscoverModel> value) {
                        Timber.d("onnext");
                        new Handler().postDelayed(() -> {
                            mSwipeRefreshLayout.setRefreshing(false);
                            if (value.size()>0)
                                mAdapter.setNewData(value);
                        },500);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                }

        ).start();
    }

    void reload(){

        final RxLoader<List<DiscoverModel>> myLoader =loaderManager.create(
                LoaderGeneral.loadDiscover(),
                new RxLoaderObserver<List<DiscoverModel>>() {
                    @Override
                    public void onNext(List<DiscoverModel> value) {
                        new Handler().postDelayed(() -> {
                            mSwipeRefreshLayout.setRefreshing(false);
                            if (value.size()>0)
                                mAdapter.setNewData(value);
                        },500);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                }
        );
        myLoader.restart();
    }


    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
