package com.venu.venutheta.settings;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.liuzhuang.rcimageview.RoundCornerImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.venu.venutheta.R;
import com.venu.venutheta.adapter.SingletonDataSource;
import com.venu.venutheta.mainfragment.LoaderMainFragment;
import com.venu.venutheta.navtransitions.NavigateTo;
import com.venu.venutheta.ui.StateButton;
import com.venu.venutheta.utils.ImageUitls;
import com.venu.venutheta.utils.NetUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import bolts.Continuation;
import bolts.Task;
import me.tatarka.rxloader.RxLoader;
import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;


public class MyGalleryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,BaseQuickAdapter.RequestLoadMoreListener,EasyPermissions.PermissionCallbacks {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 300;
    private static final int RC_CAMERA_PERM = 123;
    private List<ParseObject>   mDatas = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ChallangesAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private StateButton globalBtn,localBtn;
    private View headView;
    private TextView mHashtag,mTitle,mDesc;
    private ImageView mImage;
    private ProgressDialog progressDialog;
    private RxLoader<List<ParseObject>> listRxLoader;
    private RoundCornerImageView avatar;
    private TextView name, peeps, followers, following;
    private RxLoaderManager loaderManager;

    public MyGalleryFragment() {
    }

    public static MyGalleryFragment newInstance() {
        return new MyGalleryFragment();
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

        avatar = (RoundCornerImageView) view.findViewById(R.id.account_avatar);
        name = (TextView) view.findViewById(R.id.account_followers_cnt);
        peeps = (TextView) view.findViewById(R.id.account_peeps_cnt);
        following = (TextView) view.findViewById(R.id.account_following_cnt);
        followers = (TextView) view.findViewById(R.id.account_name);

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

    @Override
    public void onLoadMoreRequested() {

    }

    private void setUpHeader(){
        avatar.setOnClickListener(view -> {
            if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
                changeProfilePicture();
            }
        });


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


    @AfterPermissionGranted(RC_CAMERA_PERM)
    private void changeProfilePicture(){
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            chooser();
        } else {
            EasyPermissions.requestPermissions(this,  getString(R.string.rationale_camera),
                    RC_CAMERA_PERM, perms);
        }
    }

    private void chooser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Gallery", "Camera"},
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Intent galleryIntent = new Intent();
                            galleryIntent.setType("image/*");
                            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(galleryIntent, "Select File"), GALLERY_REQUEST_CODE);
                            break;
                        case 1:
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                            break;
                        default:
                            break;
                    }
                }).show();
    }

    private void updateDp(final String mUrl){

        Task.callInBackground(() -> {

            Bitmap image = BitmapFactory.decodeFile(mUrl);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 80, bos);

            byte[] scaledData = bos.toByteArray();

            final ParseFile photoFile = new ParseFile("peepy.jpg", scaledData);

            photoFile.save();

            ParseUser.getCurrentUser().put("avatar",photoFile);
            ParseUser.getCurrentUser().put("avatarUrl",photoFile.getUrl());
            ParseUser.getCurrentUser().save();

            return ParseUser.getCurrentUser().getString("avatarUrl");

        }).continueWith(new Continuation<String, Void>() {
            @Override
            public Void then(Task<String> task) throws Exception {
                if (task.getResult() != null){
                    Glide.with(getActivity())
                            .load(task.getResult())
                            .placeholder(R.drawable.ic_default_avatar)
                            .error(R.drawable.placeholder_error_media)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .centerCrop()
                            .thumbnail(0.4f)
                            .fallback(R.drawable.ic_default_avatar)
                            .into(avatar);
                }else {
                    Toast.makeText(getActivity(),"Cannot Update image",Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data.getData() != null) {
                    try {
                        updateDp(ImageUitls.getPath(data.getData(),getActivity().getApplicationContext()));
                    }catch (Exception e){
                        Timber.e("erorr getting path for image %s",e.getMessage());
                    }
                } else {

                }
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data.getData() != null) {
                    try {
                        updateDp(ImageUitls.getPath(data.getData(),getActivity().getApplicationContext()));
                    }catch (Exception e){
                        Timber.e("erorr getting path for image %s",e.getMessage());
                    }
                } else {

                }
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            listRxLoader.start();
        }
    }
}
