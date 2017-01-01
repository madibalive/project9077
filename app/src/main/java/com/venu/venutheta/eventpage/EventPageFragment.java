package com.venu.venutheta.eventpage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.liuzhuang.rcimageview.CircleImageView;
import com.android.liuzhuang.rcimageview.RoundCornerImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fangxu.allangleexpandablebutton.AllAngleExpandableButton;
import com.fangxu.allangleexpandablebutton.ButtonEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseObject;
import com.venu.venutheta.Actions.ActionEvantPageBuy;
import com.venu.venutheta.Actions.ActionEventGoing;
import com.venu.venutheta.Actions.ActionMediaCheckIslike;
import com.venu.venutheta.R;
import com.venu.venutheta.adapter.SingletonDataSource;
import com.venu.venutheta.comment.CommentActivityFragment;
import com.venu.venutheta.modal.RequestFragment;
import com.venu.venutheta.models.ModelFeedItem;
import com.venu.venutheta.navtransitions.NavigateTo;
import com.venu.venutheta.services.GeneralService;
import com.venu.venutheta.ui.StateButton;
import com.venu.venutheta.utils.NetUtils;
import com.venu.venutheta.utils.TimeUitls;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;


public class EventPageFragment extends Fragment {

    RxLoaderManager loaderManager;
    private RecyclerView mGoingRcview;
    private RecyclerView mMediaRcview;
    private EventGoingAdapter mGoingAdapter;
    private EventMediaAdapter mMediaAdapter;
    private List<ParseObject> mAttendeeDatas=new ArrayList<>();
    private List<ParseObject> mMediaDatas=new ArrayList<>();
    private ParseObject mCurrentEvent;
    private TextView mTime,mDate,mActionMsg,mActionType,mInviteName;
    private TextView mTitle,mDesc,mName,mLocation,mAttendeeCnt,mMediaMore,mFavBtn,mCmtBtn;
    private FloatingActionButton actionBtn;
    private CircleImageView mAvatar,mInviteAvatar;
    private StateButton mActionBtn;
    private ModelFeedItem dataItem;
    private AllAngleExpandableButton mMoreButton;
    private FrameLayout mapContainer;
    private LinearLayout mInviteBar;
    private Button mFirstBtn,mSecondBtn;
    private ProgressDialog progressDialog;
    private Boolean waiting;
    private Drawable icon ;




    public EventPageFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.container_core_fab, container, false);
        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        mMoreButton= (AllAngleExpandableButton) root.findViewById(R.id.button_expandable_90_180);
        mTitle = (TextView) root.findViewById(R.id.event_page_title);
        mFavBtn = (TextView) root.findViewById(R.id.event_page_favs);
        mCmtBtn = (TextView) root.findViewById(R.id.event_page_cmts);
        mName = (TextView) root.findViewById(R.id.event_page_name);
//        mTime = (TextView) root.findViewById(R.id.event_page_time);
//        mDate = (TextView) root.findViewById(R.id.event_page_date);
        mLocation = (TextView) root.findViewById(R.id.event_page_location);
        mDesc = (TextView) root.findViewById(R.id.event_page_desc);
        mAvatar= (CircleImageView) root.findViewById(R.id.event_page_avatar);
//        mActionMsg= (TextView) root.findViewById(R.id.event_page_action_msg);
//        mFirstBtn = (Button) root.findViewById(R.id.event_page_action_btn);
        mGoingRcview = (RecyclerView) root.findViewById(R.id.recycler_no_frame);
        mMediaRcview = (RecyclerView) root.findViewById(R.id.recycler_no_frame);


        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> getActivity().finish());
        fab.setOnClickListener(view -> openRequest());
        return root;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loaderManager = RxLoaderManagerCompat.get(this);
        initAdapter();

    }

    private void initView(){

        Intent data = getActivity().getIntent();
        if (data.hasExtra("fromFeed")){


            dataItem = SingletonDataSource.getInstance().getCurrentFeedItem();
            if (dataItem !=null) {

                mTitle.setText(String.format("%s%s", dataItem.getEvTitle(), dataItem.getHashtag()));
//            mDesc.setText(dataItem.getpa());
                mLocation.setText(dataItem.getEvLocation());
                mName.setText(dataItem.getName());

                mDate.setText(dataItem.getEvDateToString());
                mTime.setText(dataItem.getEvTimeToString());

                mFavBtn.setText(dataItem.getReactions());
                mCmtBtn.setText(dataItem.getComment());


                //avatar
                Glide.with(getActivity())
                        .load(dataItem.getAvatar())
                        .crossFade()
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.placeholder_error_media)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .fallback(R.drawable.ic_default_avatar)
                        .thumbnail(0.4f)
                        .into(mAvatar);


            }else {
                mCurrentEvent =SingletonDataSource.getInstance().getCurrentEvent();
                mTitle.setText(String.format("%s%s", mCurrentEvent.getString("title"), mCurrentEvent.getString("hashTag")));
                mDesc.setText(mCurrentEvent.getString("desc"));
                mLocation.setText(mCurrentEvent.getString("location"));
                mName.setText(mCurrentEvent.getParseUser("from").getUsername());

                mDate.setText(TimeUitls.Format_dayOnly(mCurrentEvent.getDate("time")));
                mTime.setText(TimeUitls.getRelativeTime(mCurrentEvent.getDate("time")));

                mFavBtn.setText(String.valueOf(mCurrentEvent.getInt("comment")));
                mCmtBtn.setText(String.valueOf(mCurrentEvent.getInt("reactions")));


                //avatar
                Glide.with(getActivity())
                        .load(mCurrentEvent.getParseUser("from").getParseFile("avatar"))
                        .crossFade()
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.placeholder_error_media)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .fallback(R.drawable.ic_default_avatar)
                        .thumbnail(0.4f)
                        .into(mAvatar);


                isMapAvailabe(mCurrentEvent);



            }
            // initialise views
        }

    }

    private void isMapAvailabe(ParseObject parseObject){

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        if(parseObject.getParseGeoPoint("coordinates") !=null) {
            getChildFragmentManager().beginTransaction().show(mapFragment).commit();
            mapFragment.getMapAsync(googleMap -> {
                LatLng latLng =  new LatLng(parseObject.getParseGeoPoint("lat").getLatitude(), parseObject.getParseGeoPoint("lat").getLongitude());
                CameraPosition selfLoc = CameraPosition.builder()
                        .target(latLng)
                        .zoom(13)
                        .build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(selfLoc));

                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Event Location!"));
            });
        }
        else {
            getChildFragmentManager().beginTransaction().hide(mapFragment).commit();
        }
    }

    private void setAvailableAction(ActionEvantPageBuy action){
        new Handler().postDelayed(() -> {
            if (action.getInvited()){
                // show liniear layout

                Glide.with(getActivity())
                        .load(mCurrentEvent.getParseUser("from").getParseFile("avatar"))
                        .crossFade()
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.placeholder_error_media)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .fallback(R.drawable.ic_default_avatar)
                        .thumbnail(0.4f)
                        .into(mAvatar);

                mName.setText(String.format("%s Invited you", mCurrentEvent.getParseUser("from").getUsername()));


                mInviteBar.setVisibility(View.VISIBLE);

            }

            if (action.isGoing()){

                // set button cancel
                mFirstBtn.setText(action.getMsg());
                mFirstBtn.setOnClickListener(view -> {
                    waiting =true;
                    progressDialog.show();
                    GeneralService.startActionCancelGoing(getActivity().getApplicationContext(),mCurrentEvent.getObjectId(),mCurrentEvent.getClassName());
                    // optional show progress loader

                });


            }else {
                // set button cancel
                mFirstBtn.setText(action.getMsg());
                mFirstBtn.setOnClickListener(view -> {

                });
                if (action.getSecondButton()){
                    // set second buton
                    mSecondBtn.setText(action.getMsg());
                    mSecondBtn.setOnClickListener(view -> GeneralService.startActionSetGoing(getActivity().getApplicationContext(),mCurrentEvent.getObjectId(),mCurrentEvent.getClassName()));
                }
            }

            if (waiting){
                progressDialog.dismiss();
            }

        },500);
    }



    private void setListeners(){
        mCmtBtn.setOnClickListener(view -> {
            DialogFragment newFragment = CommentActivityFragment.newInstance(dataItem.getParseId(),dataItem.getClassName(),true);
            newFragment.show(getChildFragmentManager(), "comment");
        });

        mFavBtn.setOnClickListener(view -> {

        });

        mMoreButton.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(int index) {

                switch (index){
                    case 4:

                        break;
                    case 2:
                        break;

                    case 3:
                        break;
                    case 1:
                        break;
                    default:
                        return;                }
            }

            @Override
            public void onExpand() {
//                showToast("onExpand");
            }

            @Override
            public void onCollapse() {
//                showToast("onCollapse");
            }
        });

    }

    private void checkFavs(){

    }

    private void setListener(AllAngleExpandableButton button) {


        mMoreButton.setOnClickListener(view -> openMedia());


    }


    private void initAdapter(){
//
//        final List<ButtonData> buttonDatas = new ArrayList<>();
//        int[] drawable = {R.drawable.ic_add, R.drawable.ic_add, R.drawable.ic_add, R.drawable.ic_add,R.drawable.ic_add};
//        int[] color = {R.color.venu_blue, R.color.venu_red, R.color.venu_green, R.color.venu_yellow,R.color.venu_orange};
//
//        for (int i = 0; i < 5; i++) {
//            ButtonData buttonData;
//            if (i == 0) {
//                buttonData = ButtonData.buildIconButton(getActivity(), drawable[i], 15);
//            } else {
//                buttonData = ButtonData.buildIconButton(getActivity(), drawable[i], 0);
//            }
//            buttonData.setBackgroundColorId(getActivity(), color[i]);
//            buttonDatas.add(buttonData);
//        }


        mGoingAdapter=new EventGoingAdapter(R.layout.item_ontap,mAttendeeDatas);
        mGoingRcview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mGoingRcview.setHasFixedSize(true);
        mGoingRcview.setAdapter(mGoingAdapter);

        mMediaAdapter=new EventMediaAdapter(R.layout.item_ontap,mMediaDatas);
        mMediaRcview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMediaRcview.setHasFixedSize(true);
        mMediaRcview.setAdapter(mMediaAdapter);


        mGoingAdapter.setOnRecyclerViewItemClickListener((view, i) -> {

            NavigateTo.gotoGoingList(getActivity(),mCurrentEvent.getObjectId(),mCurrentEvent.getClassName(),getChildFragmentManager());

        });

        mMediaAdapter.setOnRecyclerViewItemClickListener((view, i) -> {

            SingletonDataSource.getInstance().setCurrentEvent(mCurrentEvent);
            NavigateTo.goToHashtagGallery(getActivity(),mCurrentEvent.getObjectId(),mCurrentEvent.getClassName(),mCurrentEvent.getString("hashTag"));

        });
    }

    private void openGoing(){

    }

    private void openMedia(){

    }

    private void openMap(){

    }

    private void openRequest(){
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            DialogFragment requestDialog = new RequestFragment();
            requestDialog.show(getChildFragmentManager(),"request");
        }
    }

    private void initload(){
        loaderManager.create(
                LoaderEventPage.loadMedia(mCurrentEvent.getObjectId(),mCurrentEvent.getClassName()),
                new RxLoaderObserver<List<ParseObject>>() {
                    @Override
                    public void onNext(List<ParseObject> value) {
                        Timber.d("onnext");
                        new Handler().postDelayed(() -> {
                            if (value.size()>0){
                                mMediaAdapter.setNewData(value);
                            }
//                                mAdapter.setNewData(value);
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
                    }

                    @Override
                    public void onCompleted() {
                        Timber.d("completed");
                        super.onCompleted();
                    }
                }
        ).start();

        loaderManager.create(
                LoaderEventPage.loadGoing(mCurrentEvent.getObjectId(),mCurrentEvent.getClassName()),
                new RxLoaderObserver<List<ParseObject>>() {
                    @Override
                    public void onNext(List<ParseObject> value) {
                        Timber.d("onnext");
                        new Handler().postDelayed(() -> {
                            if (value.size()>0){
                                mGoingAdapter.setNewData(value);
                            }
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
                    }

                    @Override
                    public void onCompleted() {
                        Timber.d("completed");
                        super.onCompleted();
                    }
                }
        ).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActionMediaCheckIslike action) {
        if (action.status) {
            icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_heart_active);
            mFavBtn.setCompoundDrawables(icon, null, null, null);
        } else {
            icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_heart);
            mFavBtn.setCompoundDrawables(icon, null, null, null);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActionEventGoing action) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActionEvantPageBuy action) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())) {
            initload();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private class EventGoingAdapter
            extends BaseQuickAdapter<ParseObject> {

        public EventGoingAdapter(int layoutResId, List<ParseObject> data) {
            super(layoutResId, data);
        }
        @Override
        protected void convert(BaseViewHolder holder, final ParseObject request) {
            if (holder.getAdapterPosition()>3){
                holder.setText(R.id.going_count,"+"+request.getString("number"))
                        .setOnClickListener(R.id.going_bgrnd,new OnItemChildClickListener());
            }else {
                Glide.with(mContext)
                        .load(request.getString("avatar"))
                        .crossFade()
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.placeholder_error_media)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .fallback(R.drawable.ic_default_avatar)
                        .thumbnail(0.4f)
                        .into(((RoundCornerImageView) holder.getView(R.id.going_imageview)));
            }
        }
    }

    private class EventMediaAdapter
            extends BaseQuickAdapter<ParseObject> {

        public EventMediaAdapter(int layoutResId, List<ParseObject> data) {
            super(layoutResId, data);
        }
        @Override
        protected void convert(BaseViewHolder holder, final ParseObject request) {

            if (holder.getAdapterPosition()>3){
                holder.setText(R.id.going_count,"+"+request.getString("number"))
                        .setOnClickListener(R.id.going_bgrnd,new OnItemChildClickListener());
            }else {
                Glide.with(mContext)
                        .load(request.getString("url"))
                        .crossFade()
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.placeholder_error_media)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .fallback(R.drawable.ic_default_avatar)
                        .thumbnail(0.4f)
                        .into(((RoundCornerImageView) holder.getView(R.id.going_imageview)));
            }
        }
    }
}
