package com.venu.venutheta.profile;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.venu.venutheta.R;
import com.venu.venutheta.adapter.SingletonDataSource;
import com.venu.venutheta.navtransitions.NavigateTo;
import com.venu.venutheta.ui.DividerItemDecoration;
import com.venu.venutheta.utils.NetUtils;
import com.venu.venutheta.utils.TimeUitls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.tatarka.rxloader.RxLoader;
import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;


public class EventsFragment extends Fragment implements BaseQuickAdapter.RequestLoadMoreListener {

    private RecyclerView mRecyclerview;
    private EventsAdapter mAdapter;
    private List<ParseObject> mDatas=new ArrayList<>();
    RxLoaderManager loaderManager;
    private RxLoader<List<ParseObject>> listRxLoader;
    private String mId,mClassName;

    private Date lastSince;
    private int skip=0;
    private Boolean isLoading=false;

    public EventsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastSince= TimeUitls.getCurrentDate();



    }
    public static EventsFragment newInstance() {
        return new EventsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.container_recycler, container, false);
        mRecyclerview = (RecyclerView) view.findViewById(R.id.core_recyclerview);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loaderManager = RxLoaderManagerCompat.get(this);
        initload();
        initAdapter();



    }
    private void initAdapter(){
        mAdapter=new EventsAdapter(R.layout.item_ontap,mDatas);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setAdapter(mAdapter);

        mAdapter.setOnRecyclerViewItemClickListener((view, i) -> {
            SingletonDataSource.getInstance().setCurrentEvent(mAdapter.getItem(i));
            NavigateTo.goToEventPage(getActivity(),mAdapter.getItem(i).getObjectId(),mAdapter.getItem(i).getClassName());
        });


    }

    @Override
    public void onLoadMoreRequested() {
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext()) && !isLoading){
            skip = mAdapter.getData().size();
            listRxLoader.restart();
        }

    }


    void initload(){
       listRxLoader= loaderManager.create(
                ProfileLoader.loadEvent(mId, ParseUser.getCurrentUser().getClassName(),skip,lastSince),
                new RxLoaderObserver<List<ParseObject>>() {
                    @Override
                    public void onNext(List<ParseObject> value) {
                        Timber.d("onnext");
                        new Handler().postDelayed(() -> {
                            if (value.size()>0)
                                mAdapter.setNewData(value);
                        },500);
                    }

                    @Override
                    public void onStarted() {
                        Timber.d("stated");
                        isLoading=true;
                        super.onStarted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("stated error %s",e.getMessage());
                        isLoading =false;
                        super.onError(e);

                    }

                    @Override
                    public void onCompleted() {
                        isLoading=false;
                        Timber.d("completed");
                        super.onCompleted();
                    }
                }

        );
    }


    private class EventsAdapter
            extends BaseQuickAdapter<ParseObject> {

        public EventsAdapter(int layoutResId, List<ParseObject> data) {
            super(layoutResId, data);
        }
        @Override
        protected void convert(BaseViewHolder holder, final ParseObject event) {
                holder.setText(R.id.ev_tag, event.getString("hashTag"))
                    .setText(R.id.ev_feature_tag, event.getString("category"))
                    .setText(R.id.ev_status, TimeUitls.getLiveBadgeText(event.getDate("date")));


            Glide.with(mContext)
                    .load(event.getString("url"))
                    .crossFade()
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.placeholder_error_media)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .fallback(R.drawable.ic_default_avatar)
                    .thumbnail(0.4f)
                    .into((ImageView) holder.getView(R.id.notif_i_avatar));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            listRxLoader.restart();
        }
    }

}
