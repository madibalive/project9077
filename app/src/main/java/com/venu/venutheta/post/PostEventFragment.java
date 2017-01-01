package com.venu.venutheta.post;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.rongi.async.Callback;
import com.github.rongi.async.Tasks;
import com.greenfrvr.hashtagview.HashtagView;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.venu.venutheta.Actions.ActionDate;
import com.venu.venutheta.Actions.ActionInvitesList;
import com.venu.venutheta.Actions.ActionTime;
import com.venu.venutheta.R;
import com.venu.venutheta.adapter.SingletonDataSource;
import com.venu.venutheta.invite.InvitesFragment;
import com.venu.venutheta.map.MapEnterLocationActivity;
import com.venu.venutheta.modal.RequestFragment;
import com.venu.venutheta.services.UploadService;
import com.venu.venutheta.ui.StateButton;
import com.venu.venutheta.utils.ImageUitls;
import com.venu.venutheta.utils.NetUtils;
import com.venu.venutheta.utils.SelectDateFragment;
import com.venu.venutheta.utils.TimePickerFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;


public class PostEventFragment extends Fragment {

    public static final List<String> DATA = Arrays.asList("Entertainment", "Party", "Fitness",
            "Gospel", "Social", "Promotion", "VenuChallange");
    public static final HashtagView.DataTransform<String> HASH = item -> {
        SpannableString spannableString = new SpannableString(item.toUpperCase());
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#85F5F5F5")), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    };
    private static final int CODE_MAP_LOCATION=100;
    private static final int TYPE_GENERAL=1;
    private static final int TYPE_PAID=2;
    private static final int TYPE_INVITE=3;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 300;
    private static final int RC_CAMERA_PERM = 123;
    public ParseGeoPoint mCordinate;
    private int flag=0;
    private ParseQuery<ParseObject> hashtagQuery;
    private ParseObject event;
    private HashtagView hashtagView;
    private TextView mDate, mTime,mCategory;
    private TextInputEditText mTitle, mTag;
    private EditText mDesc,mPrcie;
    private ImageView mImageBackground;
    private ImageButton mAddImage,addVideo;
    private CheckBox mIs18;
    private FloatingActionButton fab;
    private ProgressBar mProgressBar;
    private StateButton onTapBtn,onLocationBtn,onInviteBtn,paidBtn,generalBtn,inviteOnlyBtn;
    private Uri imagePathUri;
    private String imagePath,Category,mLocation;
    private ArrayList<String> privateListIds= new ArrayList<>();
    private Date time,date;
    private int mEventType=1;
    private ProgressDialog progress;
    private Timer timer;
    private Callback<Boolean> loadCallBack = (result, callable, e) -> {
        if (e == null) {
            Timber.d("got data");
            if (result)
                mTag.setError("Similar Hashtag found");
            else {
                mProgressBar.setIndeterminate(false);
                mProgressBar.setVisibility(View.GONE);
            }

            // On success
        } else {
            // On error
            mProgressBar.setIndeterminate(false);
            mProgressBar.setVisibility(View.GONE);
            Timber.d("stated error %s", e.getMessage());

        }
    };

    public PostEventFragment() {
    }

    public static PostEventFragment newInstance() {
        return new PostEventFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent data =getActivity().getIntent();
        if (data.hasExtra("flag")){
            flag = data.getIntExtra("flag",0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_post_event, container, false);
        initVariables(view);
        return view;
    }

    //REQUEST SETUP

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (flag==1){
            presset(); // load preset data
        }else {
            initHashtagListener();
        }
        initClistenere();
    }

    private void presset() {
        event = SingletonDataSource.getInstance().getCurrentEvent();
        if (event != null){ // check not null
            mTitle.setText(event.getString("title"));
            mTag.setText(event.getString("hashTag"));
            mDesc.setText(event.getString("desc"));
            mCategory.setText(event.getString("category"));
            mDate.setText(event.getDate("date").toString());
            mTime.setText(event.getDate("time").toString());
            updateImage(event.getString("url"));


            // disable private ,categories ,12 ,hashtagview,,hashtagview

            mTag.setEnabled(false);
            onInviteBtn.setEnabled(false);
            hashtagView.setEnabled(false);
            mIs18.setEnabled(false);

            if (event.getInt("type")==TYPE_INVITE){


            }else if (event.getInt("type")==TYPE_PAID){

            }else if (event.getInt("type")==TYPE_GENERAL){

            }

            paidBtn.setEnabled(false);
            inviteOnlyBtn.setEnabled(false);
            generalBtn.setEnabled(false);

            // mlocation, cordinate, type, category

            mLocation = event.getString("locations");
            mCordinate = event.getParseGeoPoint("cordinate");
            mCategory.setText(event.getString("category"));
            mCategory.setEnabled(false);

        }
    }

    private void openRequest(){
        DialogFragment frg = new RequestFragment();
        frg.show(getChildFragmentManager(),"request");
    }

    private void attemptEventPost(){
        Boolean send =true;

        if (!NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            Toast.makeText(getContext(),"NO NETWORK",Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mTitle.getText())) {
            send = false;
            mTitle.setError("Cannot be empty");
            return;
        }

        if (TextUtils.isEmpty(mTag.getText())) {
            send = false;
            mTag.setError("Cannot be empty");
            return;
        }

        if (date == null) {
            send = false;
            mDate.setError("Cannot be empty");
            return;

        }
        if (mCordinate== null ){
            send = false;
            Toast.makeText(getContext(),"Location is not set",Toast.LENGTH_SHORT).show();
            return;
        }

        if (mEventType == TYPE_GENERAL ){

        }

        if (mEventType == TYPE_PAID && TextUtils.isEmpty(mPrcie.getText())){
            send = false;
            Toast.makeText(getContext(),"Price cannot be emty",Toast.LENGTH_SHORT).show();
            return;
        }

        if (mEventType == TYPE_INVITE && privateListIds==null){
            send = false;
            Toast.makeText(getContext(),"Event is set to private , but no users invited ",Toast.LENGTH_SHORT).show();
            return;
        }


        if (time == null) {
            send = false;
            mTime.setError("");
            return;
        }

        if (TextUtils.isEmpty(mDesc.getText())) {
            send = false;
            mDesc.setError("Cannot be empty");
            return;

        }

        if (imagePathUri == null) {
            send = false;
            Toast.makeText(getContext(),"Image cannot be empty",Toast.LENGTH_SHORT).show();
            return;
        }

        if (send){
            postEvent();
        }


    }

    private void postEvent(){

        progress = ProgressDialog.show(getActivity(), null,
                "Creating Event", true);

        if (flag == 0){
             event = new ParseObject("EventsVersion3");
        }
        event.put("title",mTitle.getText().toString());
        event.put("time",time);
        event.put("date",date);
        event.put("address",mLocation);
        event.put("cordinate",mCordinate);
        event.put("desc",mDesc.getText().toString());

        if (flag ==0){
            event.put("tag",mTag.getText().toString());
            event.put("category",mCategory.getText().toString());
            event.put("from", ParseUser.getCurrentUser());
            event.put("fromId", ParseUser.getCurrentUser().getObjectId());
            event.put("type",mEventType);

            if (mEventType == TYPE_INVITE){
                event.put("group",privateListIds);
            }

            if (mEventType == TYPE_PAID && !TextUtils.isEmpty(mPrcie.getText())){
                event.put("price",mPrcie.getText().toString());
            }else {
                event.put("price","not set");
            }
        }



        event.saveInBackground(e -> {
            if (e == null){
                if (flag ==0){
                    event.pinInBackground(e1 -> {
                        if (e1 == null){
                            UploadService.startActionEvent(getContext().getApplicationContext(),event.getObjectId(),event.getClassName(),imagePath,false);
                        }
                        progress.dismiss();
                        getActivity().finish();

                    });
                }else {
                    progress.dismiss();
                    getActivity().finish();
                }

            }else {
                progress.dismiss();
                Toast.makeText(getActivity(),"Something when wrong",Toast.LENGTH_LONG); // TODO: 12/17/2016  replace with alert dailog
            }


        });
    }

    private void initVariables(View view){
        fab= (FloatingActionButton) view.findViewById(R.id.pe_submit);

//        hashtagView= (HashtagView) view.findViewById(R.id.pe_hashtagview);
        mProgressBar = (ProgressBar) view.findViewById(R.id.pe_progress);
        onTapBtn = (StateButton) view.findViewById(R.id.pe_request_btn);
        onInviteBtn = (StateButton) view.findViewById(R.id.pe_btn_invite);
        onLocationBtn = (StateButton) view.findViewById(R.id.pe_btn_location);

        //new
        paidBtn= (StateButton) view.findViewById(R.id.pe_btn_paid);
        generalBtn= (StateButton) view.findViewById(R.id.pe_btn_general);
        inviteOnlyBtn= (StateButton) view.findViewById(R.id.pe_btn_invite_only);

        mDate = (TextView) view.findViewById(R.id.pe_date);
        mTime = (TextView) view.findViewById(R.id.pe_time);
        mCategory = (TextView) view.findViewById(R.id.pe_category);
        mAddImage = (ImageButton) view.findViewById(R.id.pe_add_image);

        mTitle = (TextInputEditText) view.findViewById(R.id.pe_title);
        mTag = (TextInputEditText) view.findViewById(R.id.pe_tag);
        mDesc = (EditText) view.findViewById(R.id.pe_desc);
        mImageBackground = (ImageView) view.findViewById(R.id.pe_background_image);
    }

    private void initClistenere(){

        fab.setOnClickListener(view -> attemptEventPost());
        mAddImage.setOnClickListener(view -> openCamera());
        onTapBtn.setOnClickListener(view -> openRequest());
        onLocationBtn.setOnClickListener(view -> openLocation());
        onInviteBtn.setOnClickListener(view -> startInviteFrg());

        mDate.setOnClickListener(view -> {
            DialogFragment newFragment = new SelectDateFragment();
            newFragment.show(getFragmentManager(), "DatePicker");
        });

        mTime.setOnClickListener(view -> {
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getFragmentManager(), "timePicker");
        });

        hashtagView.setData(DATA,HASH);
        hashtagView.addOnTagClickListener(item -> mCategory.setText(item.toString()));

    }

    private void initSpinner(){
        // TODO: 12/17/2016  replace hashtag with spinner
    }

    private void initHashtagListener() {

        mTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (timer != null) {
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                timer = new Timer();
                if (editable.length() > 5) {
                    if (NetUtils.hasInternetConnection(getActivity().getApplicationContext()))
                        scheduleTimer();
                }
            }
        });
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

    @AfterPermissionGranted(RC_CAMERA_PERM)
    private void openCamera(){
        if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.CAMERA)) {
            chooser();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera),
                    RC_CAMERA_PERM, Manifest.permission.CAMERA);
        }
    }

    void attemptGetHashTag() {
        mTag.setError(null);

        if (!TextUtils.isEmpty(mTag.getText().toString())) {
            if (hashtagQuery != null)
                hashtagQuery.cancel();

            getHashTag(mTag.getText().toString());
        } else {
            mTitle.setError("Cannot be empty");
        }
    }

    private void getHashTag(String term) {
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            Timber.d("stated");
            TaskCheckTag taskLoad = new TaskCheckTag(term);
            Tasks.execute(taskLoad,loadCallBack);
        }

    }

    private void openLocation(){
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            startActivityForResult(new Intent(getActivity(), MapEnterLocationActivity.class),CODE_MAP_LOCATION);
        }

    }

    public void startInviteFrg() {
        DialogFragment newFragment = new InvitesFragment();
        newFragment.show(getChildFragmentManager(), "post");
    }

    private void scheduleTimer() {

        new Timer().schedule(new NimbleTimerTask(), Long.MAX_VALUE >> 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data.getData() != null) {
                    try {
                        updateImage(ImageUitls.getPath(data.getData(),getActivity().getApplicationContext()));
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
                        updateImage(ImageUitls.getPath(data.getData(),getActivity().getApplicationContext()));
                    }catch (Exception e){
                        Timber.e("erorr getting path for image %s",e.getMessage());
                    }
                } else {
                }
            }
        }

        if (requestCode == CODE_MAP_LOCATION) {
            if (resultCode == RESULT_OK) {
                if (data.getData() != null) {
                    try {
                        // TODO: 12/17/2016 add cordinate and location

                    }catch (Exception e){
                        Timber.e("erorr getting path for image %s",e.getMessage());
                    }
                } else {

                }
            }
        }
    }

    private void updateImage(String url){
        Glide.with(getActivity())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .thumbnail(0.5f)
                .fallback(R.drawable.placeholder_error_media)
                .error(R.drawable.placeholder_error_media)
                .into(mImageBackground);
        imagePath =url;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActionInvitesList action) {
        privateListIds = action.userList;
        SingletonDataSource.getInstance().setPrivateUserList(action.userList);

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActionDate action) {
        date = action.date;
        mDate.setText(DateFormat.getMediumDateFormat(getActivity().getApplicationContext()).format(date));

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActionTime action) {
        time = action.date;
        mTime.setText( DateFormat.getTimeFormat(getActivity().getApplicationContext()).format(time));

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {
        Glide.clear(mImageBackground);
        mImageBackground = null;
        EventBus.getDefault().unregister(this);
        super.onStop();

    }

    private class NimbleTimerTask extends TimerTask {
        @Override
        public void run() {
            getActivity().runOnUiThread(() -> {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setIndeterminate(true);
                attemptGetHashTag();

            });
        }
    }
}
