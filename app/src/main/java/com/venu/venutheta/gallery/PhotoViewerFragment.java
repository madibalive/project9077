package com.venu.venutheta.gallery;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.rongi.async.Callback;
import com.github.rongi.async.Tasks;
import com.parse.ParseObject;
import com.venu.venutheta.Actions.ActionMediaCheckIslike;
import com.venu.venutheta.R;
import com.venu.venutheta.comment.CommentActivityFragment;
import com.venu.venutheta.models.GlobalConstants;
import com.venu.venutheta.services.GeneralService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoViewerFragment extends Fragment implements View.OnTouchListener{
    private ImageView mShot;
    private String mImageUrl ;
    private String mAvatarUrl,mId,mClassname;
    private PhotoViewAttacher mPhotoViewAttacher;
    private LinearLayout top_bar;
    private RelativeLayout bottom_bar;
    private CircleImageView avatar;
    private AlphaAnimation showAnimation, hideAnimation;
    private TextView mHashtag,mLikes,mShares,mComments,mUsername;
    private ImageView mClose;
    private ParseObject mCurrentObject;
    int savedUiFlags = -1;
    private GestureDetectorCompat detector;
    private Boolean mLikeStaus=false;
    private Drawable icon ;


    public PhotoViewerFragment() {
    }

    public static PhotoViewerFragment newInstance(String name, String avatarUrl, String tag,
                                                  String url, String
                                                          id, String className) {

        PhotoViewerFragment fragment = new PhotoViewerFragment();

        Bundle arguments = new Bundle();
        arguments.putString(GlobalConstants.PHP_NAME, name );
        arguments.putString(GlobalConstants.PHP_AVATAR,avatarUrl);
        arguments.putString(GlobalConstants.PHP_HASHTAG, tag);
        arguments.putString(GlobalConstants.PHP_URL,url );
        arguments.putString(GlobalConstants.PASS_ID,id);
        arguments.putString(GlobalConstants.PASS_CLASSNAME, className);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_photo_viewer, container, false);
        mUsername = (TextView) view.findViewById(R.id.md_name);
        mHashtag = (TextView) view.findViewById(R.id.md_hashtag);
        mComments = (TextView) view.findViewById(R.id.md_comment);
        mLikes = (TextView) view.findViewById(R.id.md_cnt);
        mShares = (TextView) view.findViewById(R.id.md_share);
        mShot = (ImageView) view.findViewById(R.id.pv_image_view);
        top_bar = (LinearLayout) view.findViewById(R.id.md_top_bar);
        bottom_bar = (RelativeLayout) view.findViewById(R.id.md_bottom_bar);
        mClose = (ImageButton) view.findViewById(R.id.md_closeBtn);
        view.setOnTouchListener(this);
        initGestureListinner();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initSetup();
        setupImage();
        intiListener();
        createAnimation();

    }

    @Override
    public boolean onTouch(View view, MotionEvent ev) {
        this.detector.onTouchEvent(ev);

        return true;
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            toggleTap();
            return true;
        }

    }

    private void initGestureListinner() {
        detector = new GestureDetectorCompat(getActivity(), new MyGestureListener());
    }

    private void intiListener (){

        mClose.setOnClickListener(view -> {
            Toast.makeText(getActivity(),"close",Toast.LENGTH_SHORT).show();
            getActivity().finish();
        });


        mComments.setOnClickListener(view -> {
            mCurrentObject.pinInBackground("currentComment");
            DialogFragment newFragment = CommentActivityFragment.newInstance(mCurrentObject.getObjectId(),mCurrentObject.getClassName(),true);
            newFragment.show(getChildFragmentManager(), "comment");
        });

        mShares.setOnClickListener(view -> {

        });

        mLikes.setOnClickListener(view -> {
            if (mLikeStaus){
                icon = ContextCompat.getDrawable(getActivity(),R.drawable.ic_heart_active);
                mLikes.setText(String.valueOf(Integer.parseInt(mLikes.getText().toString())-1));
                mLikes.setCompoundDrawables(icon,null,null,null);
                GeneralService.startActionGenericAction(getActivity().getApplicationContext(),false,mId,mClassname,"likes");
            }else {
                icon = ContextCompat.getDrawable(getActivity(),R.drawable.ic_heart);
                mLikes.setText(String.valueOf(Integer.parseInt(mLikes.getText().toString())+1));
                mLikes.setCompoundDrawables(icon,null,null,null);
                GeneralService.startActionGenericAction(getActivity().getApplicationContext(),true,mId,mClassname,"likes");
            }
        });




    }

    private Callback<ParseObject> loadCallBack = (value, callable, e) -> {
        if (e == null) {
            Timber.d("got data");
            new Handler().postDelayed(() -> {

                mComments.setText(String.valueOf(value.getInt("comments")));
                mLikes.setText(String.valueOf(value.getInt("reactions")));

            },500);
        } else {
            // On error
            Timber.d("stated error %s",e.getMessage());

        }
    };

    private void refreshData(String id,String className){
        MediaRefreshLoader taskLoad = new MediaRefreshLoader(id,className);
        Tasks.execute(taskLoad,loadCallBack);
    }


    private void initSetup(){

        Bundle extras;
        // handle fragment arguments
        if(getArguments() != null){
            extras= getArguments();

        }else {
            extras = getActivity().getIntent().getExtras();
        }

        if (extras != null){
            mUsername.setText(extras.getString(GlobalConstants.PHP_NAME));
            mAvatarUrl = extras.getString(GlobalConstants.PHP_AVATAR);
            mHashtag.setText(extras.getString(GlobalConstants.PHP_HASHTAG));
            mImageUrl = extras.getString(GlobalConstants.PHP_URL);
            mId=extras.getString(GlobalConstants.PASS_ID);
            mClassname =extras.getString(GlobalConstants.PASS_CLASSNAME);
            refreshData(mId,mClassname);

        }else {
        }
    }

    private void setupImage() {

        Glide.with(getActivity())
                .load(mImageUrl)
                .thumbnail(0.5f)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .fitCenter()
                .into(mShot);

        mPhotoViewAttacher = new PhotoViewAttacher(mShot);


    }

    private void createAnimation() {
        showAnimation = new AlphaAnimation(0, 1);
        hideAnimation = new AlphaAnimation(1, 0);
        showAnimation.setDuration(500);
        hideAnimation.setDuration(500);
        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bottom_bar.setVisibility(View.VISIBLE);
                top_bar.setVisibility(View.VISIBLE);

            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                bottom_bar.setVisibility(View.INVISIBLE);
                top_bar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public void toggleTap(){

        if (top_bar.getVisibility() == View.VISIBLE && bottom_bar.getVisibility()==View.VISIBLE){
            top_bar.startAnimation(hideAnimation);
            bottom_bar.startAnimation(hideAnimation);
        }else{
            top_bar.startAnimation(showAnimation);
            bottom_bar.startAnimation(showAnimation);
        }

    }

    public void toggleHideyBar() {

        // System UI visibility runs in normal mode
        if (savedUiFlags == -1) {

            int newUiOptions = getActivity().getWindow().getDecorView().getSystemUiVisibility();
            if (Build.VERSION.SDK_INT >= 14) {
                newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }

            if (Build.VERSION.SDK_INT >= 16) {
                newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
            }

            if (Build.VERSION.SDK_INT >= 19) {
                newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }

            getActivity().getWindow().getDecorView().setSystemUiVisibility(newUiOptions);

            if (top_bar.getVisibility() == View.VISIBLE && bottom_bar.getVisibility() == View.VISIBLE) {
                top_bar.startAnimation(hideAnimation);
                bottom_bar.startAnimation(hideAnimation);
            }
            savedUiFlags = newUiOptions;
        } else {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(savedUiFlags);
            if (top_bar.getVisibility() == View.INVISIBLE && bottom_bar.getVisibility() == View.INVISIBLE) {
                top_bar.startAnimation(showAnimation);
                bottom_bar.startAnimation(showAnimation);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActionMediaCheckIslike action) {
        if (action.status){
            icon = ContextCompat.getDrawable(getActivity(),R.drawable.ic_heart_active);
            mLikes.setCompoundDrawables(icon,null,null,null);
        }else {
            icon = ContextCompat.getDrawable(getActivity(),R.drawable.ic_heart);
            mLikes.setCompoundDrawables(icon,null,null,null);
        }
    }


    @Override
    public void onStop() {
        mShot = null;
        EventBus.getDefault().unregister(this);
        mCurrentObject.unpinInBackground("currentComment");
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }


}
