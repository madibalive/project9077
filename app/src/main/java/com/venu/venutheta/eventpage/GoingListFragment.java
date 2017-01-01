package com.venu.venutheta.eventpage;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.liuzhuang.rcimageview.RoundCornerImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.parse.ParseObject;
import com.venu.venutheta.R;
import com.venu.venutheta.adapter.SingletonDataSource;
import com.venu.venutheta.navtransitions.NavigateTo;
import com.venu.venutheta.services.LoaderGeneral;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;


public class GoingListFragment extends BottomSheetDialogFragment {

    private RecyclerView mRecyclerView;
    private GoingAdapter mAdapter;
    private View notLoadingView;
    private List<ParseObject> mDatas = new ArrayList<>();
    private ImageButton close;
    private ParseObject mSoureEvent;
    private String toId, toClass;
    private RxLoaderManager loaderManager;

    public GoingListFragment() {
    }


    public static GoingListFragment newInstance(String toId, String toClass) {
        GoingListFragment fragment = new GoingListFragment();
        Bundle args = new Bundle();
        args.putString("toId", toId);
        args.putString("toClass", toClass);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            toId = getArguments().getString("toId");
            toClass = getArguments().getString("toClass");
            mSoureEvent = ParseObject.createWithoutData(toClass, toId);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.container_core, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.comment_dailog_recyclerview);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loaderManager = RxLoaderManagerCompat.get(this);

        mAdapter = new GoingAdapter(R.layout.contact_list_item,mDatas);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnRecyclerViewItemClickListener((view1, i) -> openUserPage(i));

        if (mSoureEvent !=null)
            initload();
    }



    private void openUserPage(int pos) {
        SingletonDataSource.getInstance().setCurrentEvent(mAdapter.getItem(pos));
        NavigateTo.goToUserPage(getActivity(),mSoureEvent.getObjectId());
    }


    void initload(){
        loaderManager.create(
                LoaderGeneral.loadGoing(mSoureEvent.getObjectId(),mSoureEvent.getClassName()),
                new RxLoaderObserver<List<ParseObject>>() {
                    @Override
                    public void onNext(List<ParseObject> value) {
                        Timber.d("onnext");
                        new Handler().postDelayed(() -> mAdapter.setNewData(value), 500);
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
                    }

                    @Override
                    public void onCompleted() {
                        Timber.d("completed");
                        super.onCompleted();
                    }
                }
        ).start();
    }


    private class GoingAdapter extends BaseQuickAdapter<ParseObject> {

        GoingAdapter(int layoutResId, List<ParseObject> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder Holder, ParseObject user) {

            Holder.setText(R.id.user_act_username, user.getParseUser("from").getUsername());
            if (user.getString("url") != null){
                Glide.with(mContext)
                        .load(user.getParseUser("from").getParseFile("avatar"))
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.placeholder_error_media)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .thumbnail(0.4f)
                        .fallback(R.drawable.ic_default_avatar)
                        .into((RoundCornerImageView) Holder.getView(R.id.user_act_avatar));
            }
        }
    }

}
