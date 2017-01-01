package com.venu.venutheta.eventpage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.venu.venutheta.R;

import uk.co.senab.photoview.PhotoViewAttacher;

public class BareImageViewerFragment extends Fragment implements View.OnTouchListener {
    private ImageView mShot;
    private String mImageUrl ;
    private PhotoViewAttacher mPhotoViewAttacher;
    private ImageView mClose;
    private GestureDetectorCompat detector;


    public BareImageViewerFragment() {
    }

    public static BareImageViewerFragment newInstance() {

        return new BareImageViewerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_photo_viewer, container, false);

        mShot = (ImageView) view.findViewById(R.id.pv_image_view);
        mClose = (ImageButton) view.findViewById(R.id.md_closeBtn);
        view.setOnTouchListener(this);
        initGestureListinner();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSetup();
        intiListener();

    }

    @Override
    public boolean onTouch(View view, MotionEvent ev) {
        this.detector.onTouchEvent(ev);
        return true;
    }

    private void initGestureListinner() {
        detector = new GestureDetectorCompat(getActivity(), new MyGestureListener());
    }

    private void intiListener (){

        mClose.setOnClickListener(view -> {
            Toast.makeText(getActivity(),"close",Toast.LENGTH_SHORT).show();
            getActivity().finish();
        });

    }

    private void initSetup(){

        Bundle extras;
        extras = getActivity().getIntent().getExtras();
        if (extras != null){
            mImageUrl = extras.getString("url");
            setupImage();
        }else {
        }
    }

    private void setupImage() {

        Glide.with(getActivity())
                .load(mImageUrl)
                .thumbnail(0.5f)
                .priority(Priority.HIGH)
                .crossFade()
                .fitCenter()
                .into(mShot);

        mPhotoViewAttacher = new PhotoViewAttacher(mShot);

    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

    }

}
