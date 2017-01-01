package com.venu.venutheta.mainfragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.venu.venutheta.ui.StateButton;
import com.venu.venutheta.utils.NetUtils;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.rxloader.RxLoader;
import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;


public class ChallangeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private List<ParseObject>   mDatas = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ChallangesAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private StateButton globalBtn,localBtn;
    private ParseObject mMyChallange;
    private View headView;
    private TextView mHashtag,mTitle,mDesc;
    private ImageView mImage;
    private RxLoader<List<ParseObject>> listRxLoader;
    private RxLoaderManager loaderManager;

    public ChallangeFragment() {
    }

    public static ChallangeFragment newInstance() {
        return new ChallangeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderManager = RxLoaderManagerCompat.get(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view= inflater.inflate(R.layout.container_core, container, false);
        headView = inflater.inflate(R.layout.header_challange, container, false);
        mTitle = (TextView) headView.findViewById(R.id.title);
        mDesc = (TextView) headView.findViewById(R.id.desc);
        mImage = (ImageView) headView.findViewById(R.id.image);
        globalBtn = (StateButton) headView.findViewById(R.id.ch_view_Global);
        localBtn = (StateButton) headView.findViewById(R.id.ch_view_Friends);
        localBtn.setRadius(new float[]{60, 60, 0, 0, 0, 0, 60, 60});
        globalBtn.setRadius(new float[]{0, 0, 60, 60, 60, 60, 0, 0});
        mRecyclerView = (RecyclerView) view.findViewById(R.id.core_recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.core_swipelayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();
        initload();
    }


    @Override
    public void onRefresh() {

        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            listRxLoader.restart();
        }else
            mSwipeRefreshLayout.setRefreshing(false);

    }

    private void initload(){
        listRxLoader =loaderManager.create(
                LoaderMainFragment.LoadChallange(true),
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

        );
    }


    private void initAdapter(){

        mAdapter = new ChallangesAdapter(R.layout.item_challange, mDatas);

//        challange_viewer.setOnClickListener(view1 -> {
//            DialogFragment challangeViewDailog = new ChallangeInformationDialog();
//            challangeViewDailog.show(getChildFragmentManager(), "private");
//        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mAdapter.setOnRecyclerViewItemClickListener((view, i) -> {
            onClick(i);
        });

        mAdapter.setOnRecyclerViewItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.challange_avatar:
                    onAvatarClick(position);
                    break;
            }
        });

        mAdapter.addHeaderView(headView);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void onAvatarClick(int pos){
        SingletonDataSource.getInstance().setCurrentUser(mAdapter.getItem(pos).getParseUser("from"));
        NavigateTo.goToUserPage(getActivity(),mAdapter.getItem(pos).getParseUser("from").getObjectId());
    }
    private void onClick(int pos){
        SingletonDataSource.getInstance().setgalleryPagerData(mDatas);
        NavigateTo.goToGalleryPager(getActivity(),pos,1,"","");
    }


    private class ChallangesAdapter extends BaseQuickAdapter<ParseObject> {

        ChallangesAdapter(int layoutResId, List<ParseObject> data) {
            super(layoutResId, data);
            setHasStableIds(true);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
            super.onBindViewHolder(holder, position, payloads);
        }

        @Override
        protected void convert(BaseViewHolder holder, ParseObject challange) {

            holder.setText(R.id.challange_name, challange.getParseUser("from").getUsername())
                    .setText(R.id.challange_content, "sample text")
                    .setText(R.id.challange_pos, "#" + getData().indexOf(challange)+1)
                    .setOnClickListener(R.id.challange_avatar, new OnItemChildClickListener());

            Glide.with(mContext)
                    .load(challange.getParseUser("from").getParseFile("avatar"))
                    .thumbnail(0.4f)
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.placeholder_error_media)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .fallback(R.drawable.ic_default_avatar)
                    .crossFade()
                    .into((ImageView) holder.getView(R.id.challange_avatar));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            listRxLoader.start();
        }
    }
}
