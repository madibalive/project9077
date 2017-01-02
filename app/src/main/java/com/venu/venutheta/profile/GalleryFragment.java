package com.venu.venutheta.profile;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.parse.ParseObject;
import com.venu.venutheta.R;
import com.venu.venutheta.adapter.SingletonDataSource;
import com.venu.venutheta.modal.RequestFragment;
import com.venu.venutheta.navtransitions.NavigateTo;
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


public class GalleryFragment extends Fragment implements BaseQuickAdapter.RequestLoadMoreListener {

    private static final int RC_CAMERA_PERM = 123;
    private RecyclerView mRecyclerview;
    private EventsAdapter mAdapter;
    private List<ParseObject> mDatas=new ArrayList<>();
    private Date lastSince;
    private int skip=0;
    private String mId,mClassName;
    private Boolean isLoading=false;
    private  RxLoaderManager loaderManager;
    private RxLoader<List<ParseObject>> listRxLoader;


    public GalleryFragment() {

    }


    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastSince = TimeUitls.getCurrentDate();

        listRxLoader = loaderManager.create(
                ProfileLoader.loadGallery(mId,mClassName,skip,lastSince),
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
                        isLoading=true;
                        super.onError(e);

                    }

                    @Override
                    public void onCompleted() {
                        Timber.d("completed");
                        isLoading=false;
                        super.onCompleted();
                    }
                }

        );


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.container_recycler, container, false);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        mRecyclerview = (RecyclerView) view.findViewById(R.id.core_recyclerview);

        fab.setOnClickListener(view1 -> openRequest());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loaderManager = RxLoaderManagerCompat.get(this);
        initAdapter();


    }

    private void initAdapter(){
        mAdapter=new EventsAdapter(R.layout.item_ontap,mDatas);
        mAdapter.setOnLoadMoreListener(this);
        mAdapter.openLoadMore(30,true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setAdapter(mAdapter);


        mAdapter.setOnRecyclerViewItemChildClickListener((baseQuickAdapter, view, i) -> {
            switch (view.getId()){
                case 1:
                    SingletonDataSource.getInstance().setCurrentVideo(mAdapter.getItem(i));
                    NavigateTo.goToVideoViewer(getActivity(),mAdapter.getItem(i).getObjectId(),mAdapter.getItem(i).getClassName());
                    break;
                case 2:
                    NavigateTo.goToGalleryPager(getActivity(),i,1,mId,mClassName);
                    break;
                default:
                    break;
            }
        });

    }
    private void openRequest(){
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext()) && !isLoading){
            DialogFragment requestDialog = new RequestFragment();
            requestDialog.show(getChildFragmentManager(),"request");
        }
    }

    @Override
    public void onLoadMoreRequested() {
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext()) && !isLoading){
            skip=mAdapter.getData().size();
            listRxLoader.restart();
        }
    }


    private class EventsAdapter
            extends BaseQuickAdapter<ParseObject> {

        public EventsAdapter(int layoutResId, List<ParseObject> data) {
            super(layoutResId, data);
        }
        @Override
        protected void convert(BaseViewHolder holder, final ParseObject request) {
            holder.setText(R.id.ot_i_order_item, request.getString("categoryName"));
//                    .setText(R.id.ot_i_number,request.getString("number"));
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
            skip=mAdapter.getData().size();
            listRxLoader.restart();
        }
    }

}
