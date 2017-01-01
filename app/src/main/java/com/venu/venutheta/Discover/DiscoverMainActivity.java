package com.venu.venutheta.Discover;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.venu.venutheta.R;
import com.venu.venutheta.adapter.SingletonDataSource;
import com.venu.venutheta.adapter.discover.CategoriesAdapter;
import com.venu.venutheta.adapter.discover.TopPeopleAdapter;
import com.venu.venutheta.adapter.trends.GossipAdapter;
import com.venu.venutheta.map.MapEventsActivity;
import com.venu.venutheta.models.CategoriesModel;
import com.venu.venutheta.navtransitions.NavigateTo;
import com.venu.venutheta.services.LoaderGeneral;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.rxloader.RxLoader;
import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;

public class DiscoverMainActivity extends Activity {
    private static final int TYPE_PEOPLE =1;
    private static final int TYPE_GOSSIP =1;
    private RecyclerView mMainRecyclerview;
    private RecyclerView mUserRecyclerview;
    private RecyclerView mGossipRecyclerview;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private RxLoaderManager loaderManager;
    private RxLoader<List<ParseObject>> glistLoader;
    private RxLoader<List<ParseUser>> uListLoader;
    private List<ParseObject> mMediaDatas = new ArrayList<>();
    private TopPeopleAdapter mUserAdapter;
    private GossipAdapter mGossipAdapter;
    private CategoriesAdapter mCategoriesAdapter;
    private TextView mHearder;
    private TextView mBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_main);
        loaderManager = RxLoaderManager.get(this);


        mHearder = (TextView) findViewById(R.id.header_text);
        mBody = (TextView) findViewById(R.id.body_text);
        mMainRecyclerview = (RecyclerView) findViewById(R.id.category_recyclver);
        mUserRecyclerview = (RecyclerView) findViewById(R.id.user_recyclver);
        mGossipRecyclerview = (RecyclerView) findViewById(R.id.gossip_recyclver);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        SpannableString header
                = new SpannableString("What's\n" // 0-7
                + "happening on \n" //14
                + "-Venu\n"   // index 14 - 19
        );   // index 103 - 112

        SpannableStringBuilder body
                = new SpannableStringBuilder(
                "discover new -users, most\n"     // index 0 - *13 - 19* - 26
                        + "recent -gossips, most viewed\n"          // 27 - 33 - *34 - 42 *--
                        + "post, and find nearby events on -map\n"    // index *56 - 60* - 88 -*87 - 91 *
        );

        // set color for the blue on venu
        header.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 14, 19, 0);

        // set color for the blue on venu

        // set color for the bold

        ClickableSpan user = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                body.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 13, 19, 0);
                body.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 34, 42, 0);
                toggleAdapter(TYPE_PEOPLE);
            }
        };

        ClickableSpan gossip = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                body.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 13, 19, 0);
                body.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 34, 42, 0);
                toggleAdapter(TYPE_GOSSIP);

            }
        };

        ClickableSpan media = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                SingletonDataSource.getInstance().setgalleryPagerData(mMediaDatas);
                NavigateTo.goToGalleryPager(DiscoverMainActivity.this,1,2,"","");
            }
        };

        ClickableSpan map = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DiscoverMainActivity.this, MapEventsActivity.class));
            }
        };

        body.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 13, 19, 0);
        body.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 34, 42, 0);
        body.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 56, 60, 0);

        body.setSpan( user, 13, 19, 0);
        body.setSpan( gossip, 34, 42, 0);
        body.setSpan( media, 56, 60, 0);
        body.setSpan( map, 87, 91, 0);

        body.setSpan(new StyleSpan(Typeface.BOLD), 13, 19, 0);
        body.setSpan(new StyleSpan(Typeface.BOLD), 34, 42, 0);
        body.setSpan(new StyleSpan(Typeface.BOLD), 56, 60, 0);

        //set underline
        body.setSpan(new UnderlineSpan(), 13, 19, 0);
        body.setSpan(new UnderlineSpan(), 34, 42, 0);
        body.setSpan(new UnderlineSpan(), 56, 60, 0);

        // set color for the bold
        body.setSpan(new UnderlineSpan(), 13, 19, 0);
        body.setSpan(new UnderlineSpan(), 34, 42, 0);
        body.setSpan(new UnderlineSpan(),56, 60, 0);
        mHearder.setText(header);
        mBody.setText(body);
        setupCategories();
        setupLoader();
        mediaLoad();

    }


    private void toggleAdapter(int type){

        if (type == TYPE_GOSSIP && mGossipRecyclerview.getVisibility() != View.VISIBLE){
            if (mUserRecyclerview.getVisibility() == View.VISIBLE)
                mUserRecyclerview.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            glistLoader.start();

        }else if (type == TYPE_PEOPLE && mUserRecyclerview.getVisibility() != View.VISIBLE){
            if (mGossipRecyclerview.getVisibility() == View.VISIBLE)
                mGossipRecyclerview.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            uListLoader.start();
        }
    }

    private void setupCategories(){
        List<CategoriesModel> categoriesList = new ArrayList<>();

        CategoriesModel today = new CategoriesModel("Live \n Today");
        CategoriesModel weekend = new CategoriesModel("Weekend \n Picks");
        CategoriesModel top = new CategoriesModel("Top \nEvent");
        CategoriesModel entertain = new CategoriesModel("Entertain \nEvents");
        CategoriesModel gospel = new CategoriesModel("Gospol \nEvents");
        CategoriesModel social = new CategoriesModel("Social \nEvents");
        CategoriesModel fitness = new CategoriesModel("Fitness \nEvents");

        categoriesList.add(today);
        categoriesList.add(weekend);
        categoriesList.add(top);
        categoriesList.add(entertain);
        categoriesList.add(gospel);
        categoriesList.add(social);
        categoriesList.add(fitness);

         mCategoriesAdapter = new CategoriesAdapter(R.layout.item_category, categoriesList);
        mMainRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mMainRecyclerview.setAdapter(mCategoriesAdapter);
        mCategoriesAdapter.setOnRecyclerViewItemClickListener((view, i) -> NavigateTo.goToCategory(DiscoverMainActivity.this,mCategoriesAdapter.getItem(i).getmName()));
    }


    private void setupGossip(List<ParseObject> data){
        mGossipAdapter = new GossipAdapter(R.layout.gossip_small,data);
        mGossipRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mGossipRecyclerview.setAdapter(mGossipAdapter);
        mGossipAdapter.setOnRecyclerViewItemClickListener((view, i) -> NavigateTo.goToGossip(DiscoverMainActivity.this,mGossipAdapter.getItem(i).getInt("chat_id")));
        mGossipRecyclerview.setVisibility(View.VISIBLE);
    }

    private void setupUsers(List<ParseUser> data){
        mUserAdapter = new TopPeopleAdapter(R.layout.user_acnt_layout_small, data);
        mUserRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mUserRecyclerview.setAdapter(mUserAdapter);
        mUserAdapter.setOnRecyclerViewItemClickListener((view, i) -> {
            SingletonDataSource.getInstance().setCurrentUser(mUserAdapter.getItem(i));
            NavigateTo.goToUserPage(DiscoverMainActivity.this,mUserAdapter.getItem(i).getObjectId());
        });
        mUserRecyclerview.setVisibility(View.VISIBLE);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search){
            startActivity(new Intent(DiscoverMainActivity.this,SearchActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupLoader() {

        glistLoader= loaderManager.create(
                LoaderGeneral.loadRecentGossip(),
                new RxLoaderObserver<List<ParseObject>>() {
                    @Override
                    public void onNext(List<ParseObject> value) {
                        Timber.d("onnext");
                        new Handler().postDelayed(() -> {
                            progressBar.setVisibility(View.GONE);
                            if (value.size() > 0)

                                setupGossip(value);
                        }, 500);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                }

        );

        uListLoader= loaderManager.create(
                LoaderGeneral.loadRecentUsers(),
                new RxLoaderObserver<List<ParseUser>>() {
                    @Override
                    public void onNext(List<ParseUser> value) {
                        Timber.d("onnext");
                        new Handler().postDelayed(() -> {
                            progressBar.setVisibility(View.GONE);
                            if (value.size() > 0)
                                setupUsers(value);
                        }, 500);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                }
        );
    }


    private void mediaLoad(){
        loaderManager.create(
                LoaderGeneral.loadRecentMedia(),
                new RxLoaderObserver<List<ParseObject>>() {
                    @Override
                    public void onNext(List<ParseObject> value) {
                        Timber.d("onnext");
                        new Handler().postDelayed(() -> mMediaDatas=value, 500);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                }

        ).start();
    }




}
