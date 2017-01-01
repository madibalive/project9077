package com.venu.venutheta.mainfragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.venu.venutheta.Actions.ActionLike;
import com.venu.venutheta.Actions.ActionPostComplete;
import com.venu.venutheta.Actions.ActionStar;
import com.venu.venutheta.Actions.ActionThumbs;
import com.venu.venutheta.Actions.ActionUnStar;
import com.venu.venutheta.Actions.ActionUnThumbs;
import com.venu.venutheta.Actions.ActionUnlike;
import com.venu.venutheta.R;
import com.venu.venutheta.adapter.SingletonDataSource;
import com.venu.venutheta.adapter.feed.FeedAdapter;
import com.venu.venutheta.models.ModelFeedItem;
import com.venu.venutheta.navtransitions.NavigateTo;
import com.venu.venutheta.services.GeneralService;
import com.venu.venutheta.utils.NetUtils;
import com.venu.venutheta.utils.TimeUitls;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.List;

import me.tatarka.rxloader.RxLoader;
import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;

public class FeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,BaseQuickAdapter.RequestLoadMoreListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerview;
    private FeedAdapter mAdapter;
    private RxLoaderManager loaderManager;
    private RxLoader<List<ModelFeedItem>> listRxLoader;
    private View notLoadingView;
    private int PAGE_SIZE=10;
    private int mCurrentCounter = 0;
    private Boolean isLoading;
    private Date lastSince;
    private PopupMenu popupMenu ;

    public FeedFragment() {
    }

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastSince = TimeUitls.getCurrentDate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.container_core, container, false);
        mRecyclerview = (RecyclerView) view.findViewById(R.id.core_recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.core_swipelayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loaderManager = RxLoaderManagerCompat.get(this);
        initLoader();
    }


    private void initAdapter(int post){
    }

    private void gotoEvent(int post){
        SingletonDataSource.getInstance().setCurrentEvent(mAdapter.getItem(post).getParseObject());
        NavigateTo.goToEventPage(getActivity(),mAdapter.getItem(post).getParseObject().getObjectId(),mAdapter.getItem(post).getParseObject().getClassName());
    }

    private void gotoImage(int post){

        SingletonDataSource.getInstance().setCurrentEvent(mAdapter.getItem(post).getParseObject());
        NavigateTo.goToEventPage(getActivity(),mAdapter.getItem(post).getParseObject().getObjectId(),mAdapter.getItem(post).getParseObject().getClassName());
    }
    private void gotoVideo(int post){
        SingletonDataSource.getInstance().setCurrentEvent(mAdapter.getItem(post).getParseObject());
        NavigateTo.goToEventPage(getActivity(),mAdapter.getItem(post).getParseObject().getObjectId(),mAdapter.getItem(post).getParseObject().getClassName());
    }

    private void gotoUserpage(int post){

        SingletonDataSource.getInstance().setCurrentEvent(mAdapter.getItem(post).getParseObject());
        NavigateTo.goToEventPage(getActivity(),mAdapter.getItem(post).getParseObject().getObjectId(),mAdapter.getItem(post).getParseObject().getClassName());
    }

    private void gotoGossip(int pos){
        NavigateTo.goToGossip(getActivity(),mAdapter.getItem(pos).getParseObject().getInt("chat_id"));
    }

    private void showDeletePopUp(View view,int pos){
        popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.getMenu().add(Menu.NONE, pos, pos,"delete");
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId()==pos){
                deleteEvent(pos);
            }
            return false;
        });
    }

    private void deleteEvent(int pos ){
        mAdapter.getItem(pos).getParseObject().deleteInBackground();
        mAdapter.remove(pos);
        mAdapter.notifyItemRemoved(pos);

    }

    @Override
    public void onRefresh() {
        lastSince = TimeUitls.getCurrentDate();
        mCurrentCounter = 0;
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){

        }
    }

    @Override
    public void onLoadMoreRequested() {
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){

        }
    }

    private void initLoader(){
        listRxLoader= loaderManager.create(
                LoaderFeed.load(mCurrentCounter,lastSince),
                new RxLoaderObserver<List<ModelFeedItem>>() {
                    @Override
                    public void onNext(List<ModelFeedItem> value) {
                        Timber.d("onnext");
                        if (value.size() <= 0) {
                            mAdapter.notifyDataChangedAfterLoadMore(false);
                            if (notLoadingView == null) {
                                notLoadingView = getActivity().getLayoutInflater().inflate(R.layout.view_no_more_data, (ViewGroup) mRecyclerview.getParent(), false);
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

                            }, 500);

                        }
                    }

                    @Override
                    public void onStarted() {
                        Timber.d("stated");
                        isLoading =true;
                        super.onStarted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("stated error %s",e.getMessage());
                        isLoading=false;
                        super.onError(e);
                        mSwipeRefreshLayout.setRefreshing(false);

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

    private void onShare(int  pos){

        mAdapter.notifyItemChanged(pos);
        GeneralService.startActionShare(getActivity().getApplicationContext(),"","");
    }

    private void unLike(int pos){
        ModelFeedItem a =mAdapter.getItem(pos);
        if (a.getPpIsLike()){
            a.setPpIsLike(false);
            a.setReactions(a.getReactions()-1);
        }
        //background service
    }

    private void like(int pos){
        ModelFeedItem a =mAdapter.getItem(pos);

        a.setPpIsLike(true);
        a.setReactions(a.getReactions()+1);
        GeneralService.startActionGenericAction(getActivity().getApplication(),true,a.getParseId(),a.getClassName(),"likes");

        mAdapter.notifyItemChanged(pos);

    }

    private void unStar(int pos){
        ModelFeedItem a =mAdapter.getItem(pos);
        if (a.getEvIsInterest()){
            a.setEvIsInterest(false);
            a.setReactions(a.getReactions()-1);
        }
        //background service
    }

    private void star(int pos){
        ModelFeedItem a =mAdapter.getItem(pos);

        a.setEvIsInterest(true);
        a.setReactions(a.getReactions()+1);
        GeneralService.startActionGenericAction(getActivity().getApplication(),true,a.getParseId(),a.getClassName(),"likes");

        mAdapter.notifyItemChanged(pos);

    }
    private void unThumbs(int pos){
        ModelFeedItem a =mAdapter.getItem(pos);
        if (a.getGpIsThumbsUp()){
            a.setGpIsThumbsUp(false);
            a.setReactions(a.getReactions()-1);
        }
        //background service
    }

    private void thumbs(int pos){
        ModelFeedItem a =mAdapter.getItem(pos);
        a.setGpIsThumbsUp(true);
        a.setReactions(a.getReactions()+1);
        GeneralService.startActionGenericAction(getActivity().getApplication(),true,a.getParseId(),a.getClassName(),"likes");
        mAdapter.notifyItemChanged(pos);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActionLike action) {
        like(action.m);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActionUnlike action) {
        unLike(action.m);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActionStar action) {
        star(action.m);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActionUnStar action) {
        unStar(action.m);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActionThumbs action) {
        thumbs(action.m);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActionUnThumbs action) {
        unThumbs(action.m);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActionPostComplete action) {
        if (!action.success){
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            listRxLoader.start();
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

}
