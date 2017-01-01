package com.venu.venutheta.post;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.rongi.async.Callback;
import com.github.rongi.async.Tasks;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.venu.venutheta.Actions.ActionInvitesList;
import com.venu.venutheta.R;
import com.venu.venutheta.adapter.SingletonDataSource;
import com.venu.venutheta.detector.MentionDetector;
import com.venu.venutheta.invite.InvitesFragment;
import com.venu.venutheta.services.UploadService;
import com.venu.venutheta.utils.NetUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import timber.log.Timber;


public class PostMediaActivityFragment extends Fragment {

    private static final String TAG = "mediapostevent";
    private HashTagAutoCompleteAdapter mAdapter;
    private List<ParseObject> mDatas;
    private TextInputEditText mTashEditText;
    private Button mPost;
    private ImageButton mClose;
    private ImageView mbackground;
    private Uri imagePathUri;
    private ProgressBar mProgressBar;
    private Switch mPrivate;
    private RecyclerView mRecyclerView;
    private Timer timer;
    private String imagePath,oldMention;
    private ParseObject toObject;
    private int type = 0;
    private ProgressDialog progress;

    private ArrayList<String> privateListIds= new ArrayList<>();

    public PostMediaActivityFragment() {
    }

    public static PostMediaActivityFragment newInstance() {
        return new PostMediaActivityFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent data = getActivity().getIntent();

        if (data!=null){
            if (data.hasExtra("type")){
                type=data.getIntExtra("type",0);
                imagePath=data.getStringExtra("LocalUrl");
                imagePathUri = Uri.parse(data.getStringExtra("thumbnail"));
                Log.i(TAG, "onCreate: uri " + imagePathUri);
            }else {
                imagePathUri = Uri.parse(data.getStringExtra("background"));
                imagePath=data.getStringExtra("LocalUrl");
                type = data.getIntExtra("type",0);
            }
        }else {
            Log.i(TAG, "onCreate: error fetching intent data ");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_post_media, container, false);
        mPrivate = (Switch) view.findViewById(R.id.mp_switch);
        mPost = (Button) view.findViewById(R.id.mp_new_btn);
        mClose = (ImageButton) view.findViewById(R.id.mp_closeBtn);
        mTashEditText = (TextInputEditText) view.findViewById(R.id.mp_new_edittext);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.mp_new_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mbackground = (ImageView) view.findViewById(R.id.mp_new_background);
        mProgressBar = (ProgressBar) view.findViewById(R.id.mp_loading);
        mPrivate.setChecked(false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (imagePathUri != null) {
            Glide.with(getActivity()).load(imagePathUri)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(mbackground);
        }

        mPrivate.setOnCheckedChangeListener((compoundButton, b) -> {
            Log.i(TAG, "onCheckedChanged: " +b);
            if (b) {
                startInviteFrg();
            }
        });

        mClose.setOnClickListener(view13 -> getActivity().finish());

        mPost.setOnClickListener(view12 -> attemptPost());

        mDatas = new ArrayList<>();
        mAdapter = new HashTagAutoCompleteAdapter(android.R.layout.simple_list_item_1, mDatas);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnRecyclerViewItemClickListener((view1, i) -> {
            toObject = mAdapter.getItem(i);
            mTashEditText.setText(toObject.getString("tag"));
            mRecyclerView.setVisibility(View.GONE);
            mPost.setEnabled(true);
        });

        mTashEditText.addTextChangedListener(new TextWatcher() {
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

                if (!TextUtils.isEmpty(editable)&& editable.length()>4) {
                    if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())) {
                        try {
                            MentionDetector detector = new MentionDetector(editable.toString());
                            if (detector.hasMentions() && !detector.getMentions().get(0).usernameWithoutAtSymbol().equals(oldMention)) {
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        // do your actual work here
                                        getActivity().runOnUiThread(() -> {
                                            mProgressBar.setVisibility(View.VISIBLE);
                                            mProgressBar.setIndeterminate(true);
                                            getHashTag(detector.getMentions().get(0).usernameWithoutAtSymbol());
                                            oldMention = detector.getMentions().get(0).usernameWithoutAtSymbol();
                                        });
                                    }
                                }, 600);
                            }
                        } catch (Exception e) {
                            Log.e("error", "afterTextChanged: " + e);
                        }


                    }
                }

                if (editable.length() > 0) {
                    if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())) {
                     // 600ms delay before the timer executes the "run" method from TimerTask
                    }
                }
            }
        });
    }

    private void attemptPost(){
        mTashEditText.setError(null);
        Boolean send =true;

        if (!NetUtils.hasInternetConnection(getActivity().getApplicationContext()))
            send=false;

        if (TextUtils.isEmpty(mTashEditText.getText().toString())) {
            send =false;
            mTashEditText.setError("Cannot be empty");
        }

        if ( mPrivate.isChecked() && privateListIds==null ){
            send=false;
            Toast.makeText(getContext(), "Peep is set to private , but no users invited ", Toast.LENGTH_SHORT).show();

        }
        if (toObject==null)
            send=false;

        if (send)
            postEvent();


    }

    void postEvent(){
        progress = ProgressDialog.show(getActivity(), null,
                "Uploading Peep", true);
        final ParseObject media = new ParseObject("Peep");
        media.put("from", ParseUser.getCurrentUser());
        media.put("fromId", ParseUser.getCurrentUser().getObjectId());
        media.put("likes",0);
        media.put("comments",0);
        media.put("shares",0);
        media.put("tov2",toObject);
        media.put("toIdv2",toObject.getObjectId());
        media.put("eventv2",toObject);
        media.put("tag",toObject.getString("tag"));
        media.put("tagLowerCase",toObject.getString("tag").toLowerCase());
        media.put("isPrivate",mPrivate.isChecked());
        if (mPrivate.isChecked()){
            media.put("group",privateListIds);
        }
        media.saveInBackground(e -> {
            if (e == null){
                media.pinInBackground(e1 -> {
                    if (e1 == null){
                        if (type ==0){
                            UploadService.startActionImage(getContext().getApplicationContext(),imagePath,media.getObjectId(),media.getClassName());
                            progress.dismiss();
                            getActivity().finish();
                        }else {
                            Log.i(TAG, "done: starting video here");
                            if (imagePath != null) {
                                UploadService.startActionVideo(getContext().getApplicationContext(), imagePath, media.getObjectId(), media.getClassName());
                                progress.dismiss();
                                getActivity().finish();
                            }
                            else
                                Log.i(TAG, "done: Image Path is null");
                        }

                    }else {
                        progress.dismiss();
                        Toast.makeText(getActivity(), e1.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

            }else {
                progress.dismiss();
                Toast.makeText(getActivity(), e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }


    private Callback<List<ParseObject>> loadCallBack = new Callback<List<ParseObject>>() {
        @Override
        public void onFinish(List<ParseObject> result, Callable callable, Throwable e) {
            if (e == null) {
                Timber.d("got data");
                mPost.setEnabled(true);
                mAdapter.setNewData(result);
                mProgressBar.setIndeterminate(false);
                mProgressBar.setVisibility(View.GONE);
                if (result.size() > 0)
                    mRecyclerView.setVisibility(View.VISIBLE);
                // On success
            } else {
                // On error
                Timber.d("stated error %s",e.getMessage());

            }
        }

    };

    void getHashTag(String term) {
        Timber.d("stated");
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            TaskGetHashtags taskLoad = new TaskGetHashtags(term);
            Tasks.execute(taskLoad,loadCallBack);
        }
    }


    public void startInviteFrg() {
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            DialogFragment newFragment = new InvitesFragment();
            newFragment.show(getChildFragmentManager(), "private");
        }
    }


    private class HashTagAutoCompleteAdapter extends BaseQuickAdapter<ParseObject> {

        public HashTagAutoCompleteAdapter(int layoutResId, List<ParseObject> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, final ParseObject parseObject) {
            holder.setText(android.R.id.text1, parseObject.getString("tag"));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActionInvitesList action) {
        privateListIds = action.userList;
        SingletonDataSource.getInstance().setPrivateUserList(action.userList);
    }


    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        Glide.clear(mbackground);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
