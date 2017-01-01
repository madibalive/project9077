package com.venu.venutheta.gallery;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.parse.ParseObject;
import com.venu.venutheta.R;
import com.venu.venutheta.adapter.SingletonDataSource;
import com.venu.venutheta.models.GlobalConstants;
import com.venu.venutheta.services.LoaderGeneral;

import java.util.Date;
import java.util.List;

import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;

public class GalleryPagerActivity extends AppCompatActivity {
    private GalleryPagerAdapter mAdapter;
    private ViewPager mPager;
    private List<ParseObject> mDatas;
    RxLoaderManager loaderManager;
    private Boolean isLoading=false;
    private Boolean finishLoading=false;
    private Date date;
    private int pagerType;
    private String mId,mClassName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > 10) {
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;

            getWindow().getDecorView().setSystemUiVisibility(flags);
        } else {
            getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_gallery_pager);

        loaderManager = RxLoaderManagerCompat.get(this);
        mPager = (ViewPager)findViewById(R.id.gallerypager_viewpager);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                if (position == mDatas.size() - 3){
                 if (!isLoading && !finishLoading){
                        loadMore();
                 }}
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initLoad();


    }

    private void initLoad(){
        Intent intentData= getIntent();
        int pos=0;
        if (intentData !=null){
            mId = intentData.getStringExtra(GlobalConstants.PASS_ID);
            mClassName =intentData.getStringExtra(GlobalConstants.PASS_CLASSNAME);
            pagerType = intentData.getIntExtra("type",1);
            pos = intentData.getIntExtra("position",0);
        }
        mDatas = SingletonDataSource.getInstance().getgalleryPagerData();
        mAdapter= new GalleryPagerAdapter(getSupportFragmentManager(),mDatas,getApplicationContext());
        ParseObject a =mDatas.get(0);
        date=a.getDate("createdAt");
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(pos);
    }

    private void loadMore() {
        loaderManager.create(
                LoaderGeneral.PagerLoader(mId,mClassName, mDatas.size(),pagerType,date),
                new RxLoaderObserver<List<ParseObject>>() {
                    @Override
                    public void onNext(List<ParseObject> value) {
                        Timber.d("onnext");
                        new Handler().postDelayed(() -> {
                            if (value.size()>0){
                                mDatas.addAll(value);
                                mAdapter.update(value);
                            }else
                                finishLoading =false;
                        },500);
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
                        super.onError(e);
                        isLoading =false;
                    }

                    @Override
                    public void onCompleted() {
                        Timber.d("completed");
                        isLoading =false;
                        super.onCompleted();
                    }
                }
        ).restart();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}


