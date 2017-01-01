package com.venu.venutheta.eventpage;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.venu.venutheta.R;
import com.venu.venutheta.models.GlobalConstants;
import com.venu.venutheta.utils.ProxyFactory;

import java.io.File;

import timber.log.Timber;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.WINDOW_SERVICE;


public class BareVideoPlayerFragment extends Fragment implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnBufferingUpdateListener, View.OnTouchListener, CacheListener {

    private static final int MESSAGE_SHOW_UI = 0;
    private static final int MESSAGE_HIDE_UI = 1;
    private static final int MESSAGE_UPDATE_PROGRESS = 2;
    private static final int MESSAGE_STOP_UPDATE_PROGRESS = 3;
    private final long timeToShowLoading = 500;
    private VideoView videoView;
    private String url;
    private Uri uri;
    private GestureDetectorCompat detector;
    private AlphaAnimation showAnimation, hideAnimation;
    private RelativeLayout bottom_bar;
    private LinearLayout top_bar;
    private ProgressBar pbLoading;
    private ImageButton ibPlay, ibBack, ibForward, close_btn;
    private boolean isControllerShown;
    private UIHandler mHandler;
    private int totalTimeMilli, maxVolume;
    private int lastPosition;
    private AudioManager audioManager;
    private ProgressBar seekArcComplete;
    private int screenY;
    private int screenX;
    private float volumeInFloat;
    private boolean isOperating;
    private boolean isScrollingX;
    private boolean isScrollingY;
    private boolean isOffline = false;
    private long lastPlayingTime;
    Handler progressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_UPDATE_PROGRESS:
                    if (videoView != null) {
                        setProgress();
                        progressHandler.sendEmptyMessage(MESSAGE_UPDATE_PROGRESS);
                    }
                    break;
                case MESSAGE_STOP_UPDATE_PROGRESS:
                    break;
            }
        }
    };
    private CountDownTimer hideCounter = new CountDownTimer(5000, 5000) {

        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            if (isControllerShown && !isOperating) {
                hideUI();
            }
        }
    };

    public BareVideoPlayerFragment() {
    }

    public static BareVideoPlayerFragment newInstance(String url) {

        BareVideoPlayerFragment fragment = new BareVideoPlayerFragment();
        Bundle arguments = new Bundle();
        arguments.putString(GlobalConstants.PHP_URL, url);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_player, container, false);
        videoView = (VideoView) view.findViewById(R.id.video_view);
        pbLoading = (ProgressBar) view.findViewById(R.id.videoProgress);
        close_btn = (ImageButton) view.findViewById(R.id.md_closeBtn);
        view.setOnTouchListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initData();
        initUI(view);
        initListener();
        initialIistener();
        createAnimation();
        initGestureListinner();
        parseIntent();
        checkCachedState();

        HttpProxyCacheServer proxy = ProxyFactory.getProxy(getActivity().getApplicationContext());
        proxy.registerCacheListener(this, url);
        String proxyUrl = proxy.getProxyUrl(url);
        videoView.setVideoPath(proxyUrl);
        videoView.start();

    }

    private void checkCachedState() {
        HttpProxyCacheServer proxy = ProxyFactory.getProxy(getActivity().getApplicationContext());
        boolean fullyCached = proxy.isCached(url);
        if (fullyCached) {
            seekArcComplete.setProgress(100);
        }
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        seekArcComplete.setProgress(percentsAvailable);

    }

    @Override
    public void onPause() {
        if (videoView != null) {
            videoView.stopPlayback();
        }
        super.onPause();

    }

    @Override
    public void onStop() {
        changeProgress(false);

        super.onStop();

    }

    @Override
    public boolean onTouch(View view, MotionEvent ev) {

        Timber.e("onTouch: " + ev);
        this.detector.onTouchEvent(ev);

        return true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        Timber.d("buffered:" + i);
        seekArcComplete.setProgress(i);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        changeProgress(false);
        if (seekArcComplete.getVisibility() == View.VISIBLE) {
            seekArcComplete.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        Toast.makeText(getActivity(), mediaPlayer.toString(), Toast.LENGTH_SHORT).show();

//        pbLoading.setVisibility(View.GONE);
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        totalTimeMilli = mediaPlayer.getDuration();
//        pbLoading.setVisibility(View.GONE);
//        seekArcComplete.set(videoView.getDuration());

        changeProgress(true);
    }

    private void parseIntent() {
        Bundle extras;
        if (getArguments() != null) {
            extras = getArguments();
        } else {
            extras = getActivity().getIntent().getExtras();
        }

        if (extras != null) {
            uri = Uri.parse(url);
        }
    }

    private void initData() {
        mHandler = new UIHandler();
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        audioManager = (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volumeInFloat = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Display display = ((WindowManager) getActivity().getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        screenY = display.getHeight();
        screenX = display.getWidth();
    }

    private void initUI(View view) {
        isControllerShown = true;
        videoView.postDelayed(() -> mHandler.sendEmptyMessage(MESSAGE_HIDE_UI), 3000);

    }

    private void initialIistener() {

        close_btn.setOnClickListener(view -> {
            Toast.makeText(getActivity(), "close", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        });

    }

    private void initListener() {
        videoView.setOnPreparedListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnCompletionListener(this);

    }

    private void initGestureListinner() {
        detector = new GestureDetectorCompat(getActivity(), new MyGestureListener());
    }

    private void setProgress() {
        if (lastPosition == videoView.getCurrentPosition()) {
            if ((System.currentTimeMillis() - lastPlayingTime) > timeToShowLoading
                    && videoView.isPlaying()) {
//                pbLoading.setVisibility(View.VISIBLE);
            }
        } else {
//            Timber.d("video started to play");
//            pbLoading.setVisibility(View.GONE);
            lastPlayingTime = System.currentTimeMillis();
        }
        lastPosition = videoView.getCurrentPosition();
//        sbProgress.setProgress(videoView.getCurrentPosition());
        seekArcComplete.setProgress(videoView.getDuration() * videoView.getBufferPercentage() / 100);

    }

    private void backward() {
        if (videoView.canSeekBackward()) {
            videoView.pause();
            videoView.seekTo(videoView.getCurrentPosition() - 30000);
            videoView.start();
        }
    }

    private void forward() {
        if (videoView.canSeekForward()) {
            videoView.pause();
            videoView.seekTo(videoView.getCurrentPosition() + 10000);
            videoView.start();
        }
    }

    private void setProgress(float percent) {
        int position = (int) (videoView.getCurrentPosition() - totalTimeMilli * percent);
        if (position < 0) {
            position = 0;
        } else if (position > totalTimeMilli) {
            position = totalTimeMilli;
        }
        videoView.seekTo(position);
    }

    private void setVolume(float percent) {
        volumeInFloat = volumeInFloat + maxVolume * percent;
        if (volumeInFloat < 0) {
            volumeInFloat = 0;
        } else if (volumeInFloat > maxVolume) {
            volumeInFloat = maxVolume;
        }
        Timber.d("maxVolume:" + maxVolume);
        Timber.d("deltColume:" + (int) (maxVolume * percent));
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) volumeInFloat, AudioManager.FLAG_SHOW_UI);
    }

    private void changeProgress(boolean isChangeProgress) {
        if (isChangeProgress) {
            progressHandler.sendEmptyMessage(MESSAGE_UPDATE_PROGRESS);
        } else {
            progressHandler.removeMessages(MESSAGE_UPDATE_PROGRESS);
        }
    }

    private void showUI() {
        if (!isControllerShown) {

            isControllerShown = true;
        }
    }

    private void hideUI() {
        if (isControllerShown) {
            isControllerShown = false;

            if (hideCounter != null) {
                hideCounter.cancel();
            }
        }
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

                close_btn.setVisibility(View.VISIBLE);


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

                close_btn.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onDestroy() {
        videoView.stopPlayback();
        ProxyFactory.getProxy(getActivity().getApplicationContext()).unregisterCacheListener(this);
        super.onDestroy();
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float distanceX, float distanceY) {
            Timber.d("onScroll_v1:" + distanceX);
            Timber.d("onScroll_v2:" + distanceY);
            Timber.d("onScroll_v2_divide:" + distanceY / screenY);
            isOperating = true;
            if (Math.abs(distanceY) > Math.abs(distanceX)) {
                if (!isScrollingX) {
                    isScrollingY = true;
                    setVolume(distanceY / screenY);
                    return true;
                } else {
                    return false;
                }
            } else {
                if (!isScrollingY && isOffline) {
                    isScrollingX = true;
                    setProgress(distanceX / screenX);
                    if (!isControllerShown) {
                        showUI();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (isControllerShown) {
                hideUI();
            } else {
                showUI();
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (videoView.isPlaying()) {
                videoView.pause();
            } else {
                videoView.start();
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v_x, float v_y) {
            Timber.d("onFling_v1:" + v_x);
            Timber.d("onFling_v2:" + v_y);
            if (Math.abs(v_x) > Math.abs(v_y)) {//上下滑动
                if (v_x > 0) {
                    forward();
                } else {
                    backward();
                }
                return true;
            } else {
                return false;
            }
        }
    }

    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_HIDE_UI:
                    hideUI();
                    break;
                case MESSAGE_SHOW_UI:
                    showUI();
                    break;
            }
        }
    }

}
