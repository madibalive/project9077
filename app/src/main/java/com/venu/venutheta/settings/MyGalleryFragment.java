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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.venu.venutheta.modal.RequestFragment;
import com.venu.venutheta.navtransitions.NavigateTo;
import com.venu.venutheta.profile.ProfileLoader;
import com.venu.venutheta.ui.StateButton;
import com.venu.venutheta.utils.ImageUitls;
import com.venu.venutheta.utils.NetUtils;
import com.venu.venutheta.utils.TimeUitls;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
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


public class MyGalleryFragment extends Fragment implements BaseQuickAdapter.RequestLoadMoreListener {


    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 300;
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

    ProgressDialog progressDialog;

    private RoundCornerImageView mAavtar;
    private TextView mName,mFollowersCount,mFollowingCount,mEventsCount,mPeepCount;
    private StateButton actionBtn;
    public MyGalleryFragment() {
    }

    public static MyGalleryFragment newInstance() {
        return new MyGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastSince = TimeUitls.getCurrentDate();
        mId = ParseUser.getCurrentUser().getObjectId();

        listRxLoader = loaderManager.create(
                ProfileLoader.loadGallery(mId,"_User",skip,lastSince),
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
        mName = (TextView) view.findViewById(R.id.user_prof_name);
        mFollowersCount = (TextView) view.findViewById(R.id.user_prof_follower);
        mFollowingCount = (TextView) view.findViewById(R.id.user_prof_following);
        mEventsCount = (TextView) view.findViewById(R.id.user_prof_events);
        mPeepCount = (TextView) view.findViewById(R.id.user_prof_peeps);
        mAavtar = (RoundCornerImageView) view.findViewById(R.id.user_prof_image);

        fab.setOnClickListener(view1 -> openRequest());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loaderManager = RxLoaderManagerCompat.get(this);
        initAdapter();


    }

    private void initView(){
        mName.setText(ParseUser.getCurrentUser().getUsername());

        // TODO: 1/1/2017 include progress view  
        mName.setOnClickListener(view -> changeUsername());
        mAavtar.setOnClickListener(view -> changeProfilePicture());
        mFollowersCount.setText(String.valueOf(ParseUser.getCurrentUser().getInt("followers")));
        mFollowingCount.setText(String.valueOf(ParseUser.getCurrentUser().getInt("following")));
        mEventsCount.setText(String.valueOf(ParseUser.getCurrentUser().getInt("peeps")));
        mPeepCount.setText(String.valueOf(ParseUser.getCurrentUser().getInt(GlobalConstants.CLASS_EVENT)));

        Glide.with(getActivity())
                .load(ParseUser.getCurrentUser().getString("avatar"))
                .crossFade()
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.placeholder_error_media)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .fallback(R.drawable.ic_default_avatar)
                .thumbnail(0.4f)
                .into(mAavtar);
    }
    
    
    private void changeUsername(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dailog_settings, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        edt.setHint(ParseUser.getCurrentUser().getUsername());
        dialogBuilder.setTitle("Change Username");
        dialogBuilder.setMessage("Enter username below");
        dialogBuilder.setPositiveButton("Done", (dialog, whichButton) -> {
            String newUsername = edt.getText().toString();
            if (newUsername.length() > 0) {
                username.setText(newUsername);
                ParseUser.getCurrentUser().setUsername(newUsername);
                ParseUser.getCurrentUser().saveInBackground(e -> {
                    if (e == null){
                        Toast.makeText(getActivity(),"Username Updated",Toast.LENGTH_SHORT).show();
                    }else {
                        Timber.e("error updateing user %s",e.getMessage());
                    }
                });
            }
        });
        dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> {
            //pass
        });
        AlertDialog b = dialogBuilder.create();
        b.show();

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
