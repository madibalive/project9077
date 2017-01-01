package com.venu.venutheta.contacts;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.venu.venutheta.R;


public class BaseFragment extends Fragment {

    private static final String TAG = "NOTIF_FRAGMENT";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerview;

    public BaseFragment() {
    }

    public static BaseFragment newInstance() {
        return new BaseFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_base, container, false);
//        mRecyclerview = (RecyclerView) view.findViewById(R.id.core_recyclerview);
//        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.core_swipelayout);
//        mSwipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

}
