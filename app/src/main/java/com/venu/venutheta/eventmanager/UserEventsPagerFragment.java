package com.venu.venutheta.eventmanager;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseObject;
import com.venu.venutheta.R;
import com.venu.venutheta.utils.NetUtils;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.rxloader.RxLoader;
import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;

public class UserEventsPagerFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private List<ParseObject> mDatas = new ArrayList<>();
    private TextView totalCount;
    private MyEventPagerAdapter mAdapter;

    private ViewPager mPager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    PopupMenu popupMenu;
    RxLoaderManager loaderManager;

    public UserEventsPagerFragment() {
    }

    public static UserEventsPagerFragment newInstance() {
        return new UserEventsPagerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.container_core, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.core_swipelayout);
        mPager = (ViewPager) view.findViewById(R.id.gallerypager_viewpager);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loaderManager = RxLoaderManagerCompat.get(this);
    }



//    private void showPopup(View view,int pos) {
//        popupMenu = new PopupMenu(getActivity(), view);
//        popupMenu.inflate(R.menu.menu_discover);
//        popupMenu.setOnMenuItemClickListener(item -> {
//            if (item.getItemId() ==R.id.action_editt){
//
//                // what to do
//
//
//            }else if (item.getItemId() == R.id.action_delete){
//                mAdapter.getItem(pos).deleteInBackground();
//                mAdapter.remove(pos);
//                mAdapter.notifyItemRemoved(pos);
//            }
//            return false;
//        });
//        popupMenu.show();
//    }

    private void setupPagerAdapter(List<ParseObject> mDatas){
        mAdapter = new MyEventPagerAdapter(getChildFragmentManager(),mDatas);
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
                LoaderEventManager.loadOnMyEvents(),
                new RxLoaderObserver<List<ParseObject>>() {
                    @Override
                    public void onNext(List<ParseObject> value) {
                        Timber.d("onnext");
                        new Handler().postDelayed(() -> {
                            mSwipeRefreshLayout.setRefreshing(false);
                            if (value.size()>0)
                                setupPagerAdapter(value);
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
                LoaderEventManager.loadOnMyEvents(),
                new RxLoaderObserver<List<ParseObject>>() {
                    @Override
                    public void onNext(List<ParseObject> value) {
                        Timber.d("onnext");
                        new Handler().postDelayed(() -> {
                            mSwipeRefreshLayout.setRefreshing(false);
                            if (value.size()>0)
                                setupPagerAdapter(value);
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

        reload.restart();
    }


    public class MyEventPagerAdapter extends FragmentStatePagerAdapter {
        private List<ParseObject> mDatas;

        public MyEventPagerAdapter(FragmentManager fm, List<ParseObject> data) {
            super(fm);
            mDatas = data;
        }

        @Override
        public Fragment getItem(int position) {

        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext()))
            initload();
    }
}
