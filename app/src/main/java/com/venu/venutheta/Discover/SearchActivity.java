package com.venu.venutheta.Discover;

import android.app.SearchManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.github.rongi.async.Callback;
import com.github.rongi.async.Tasks;
import com.venu.venutheta.R;
import com.venu.venutheta.adapter.search.SearchAdapter;
import com.venu.venutheta.models.SearchModel;
import com.venu.venutheta.services.TaskSearchLoad;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import timber.log.Timber;

public class SearchActivity extends AppCompatActivity {

    private SearchView mSearchView;
    private String mQuery = "";
    private RecyclerView mRecyclerview;
    public SearchAdapter mSearchAdapter;
    List<SearchModel> mSearchDatas ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mSearchView = (SearchView) findViewById(R.id.search_view);
        mRecyclerview = (RecyclerView) findViewById(R.id.search_results);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mSearchDatas =new ArrayList<>();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> {
//            navigateUpOrBack(SearchActivity.this, null);

        });

   }

    private void setupSearchView(){
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconified(false);

        mSearchView.setQueryHint(getString(R.string.search_hint));

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mSearchView.setOnCloseListener(() -> false);
    }

    private void searchFor(String query){

        if (TextUtils.isEmpty(query)) {

            if (mRecyclerview.getVisibility() == View.GONE) {
                mRecyclerview.setVisibility(View.VISIBLE);
            }
            doSearch(query);
        } else {
        }
    }

    private Callback<List<SearchModel>> loadCallBack = new Callback<List<SearchModel>>() {
        @Override
        public void onFinish(List<SearchModel> value, Callable callable, Throwable e) {
            if (e == null) {
                Timber.d("got data");
                new Handler().postDelayed(() -> {
                    if (value.size()>0)
                        mSearchAdapter.setNewData(value);
                },500);
            } else {
                // On error
                Timber.d("stated error %s",e.getMessage());

            }
        }

    };

    private void doSearch(String searchTerm){
        TaskSearchLoad taskLoad = new TaskSearchLoad(searchTerm);
        Tasks.execute(taskLoad,loadCallBack);
    }


    @Override
    public void onBackPressed() {
        if (mSearchView.isInEditMode()){
        }
        super.onBackPressed();
    }
}
