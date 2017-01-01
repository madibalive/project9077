package com.venu.venutheta.ontap;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.parse.ParseObject;
import com.venu.venutheta.R;
import com.venu.venutheta.modal.RequestFragment;
import com.venu.venutheta.services.LoaderGeneral;
import com.venu.venutheta.ui.DividerItemDecoration;
import com.venu.venutheta.utils.NetUtils;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;


public class OnTapActivityFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerview;
    private View root;
    private OnTapDirAdapter mAdapter;
    private List<ParseObject> mDatas=new ArrayList<>();

    RxLoaderManager loaderManager;

    public OnTapActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root=inflater.inflate(R.layout.container_core_fab, container, false);
        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        mRecyclerview = (RecyclerView) root.findViewById(R.id.core_recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.core_swipelayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        fab.setOnClickListener(view -> openRequest());
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loaderManager = RxLoaderManagerCompat.get(this);
        initAdapter();


    }
    private void initAdapter(){
        mAdapter=new OnTapDirAdapter(R.layout.item_ontap,mDatas);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setAdapter(mAdapter);

        mAdapter.setOnRecyclerViewItemClickListener((view, i) -> {

        });

        mAdapter.setOnRecyclerViewItemLongClickListener((view, i) -> {
//            delete(i);
            return false;

        });
    }
    private void openRequest(){
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            DialogFragment requestDialog = new RequestFragment();
            requestDialog.show(getChildFragmentManager(),"request");
        }
    }

    @Override
    public void onRefresh() {
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            initload();
        }
    }


    private void delete(int pos){
        try {
            mAdapter.getItem(pos).deleteInBackground(e -> {
                if (e == null ){
                    if (root != null)
                        Snackbar.make(root, "Delete Successful", Snackbar.LENGTH_SHORT).show();

                }else{
                    if (root != null)
                        Snackbar.make(root, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            });
            mAdapter.remove(pos);
            mAdapter.notifyItemRemoved(pos);

        }catch (IndexOutOfBoundsException e){
            Timber.e("error deleting object %s",e.getMessage());
        }

    }

    void initload(){
        loaderManager.create(
                LoaderGeneral.loadOnTap(),
                new RxLoaderObserver<List<ParseObject>>() {
                    @Override
                    public void onNext(List<ParseObject> value) {
                        Timber.d("onnext");
                        new Handler().postDelayed(() -> {
                            mSwipeRefreshLayout.setRefreshing(false);
                            if (value.size()>0)
                                mAdapter.setNewData(value);
                        },500);
                    }

                    @Override
                    public void onStarted() {
                        Timber.d("stated");
                        super.onStarted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("stated error %s",e.getMessage());
                        super.onError(e);
                        mSwipeRefreshLayout.setRefreshing(false);

                    }

                    @Override
                    public void onCompleted() {
                        Timber.d("completed");
                        super.onCompleted();
                    }
                }

        ).start();
    }


    private class OnTapDirAdapter
            extends BaseQuickAdapter<ParseObject> {

        public OnTapDirAdapter(int layoutResId, List<ParseObject> data) {
            super(layoutResId, data);
        }
        @Override
        protected void convert(BaseViewHolder holder, final ParseObject request) {
            holder.setText(R.id.ot_i_order_status, request.getString("categoryName"))
                    .setText(R.id.ot_i_order_item,request.getString("number"))
                    .setText(R.id.ot_i_order_no,request.getObjectId());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            initload();
        }
    }

}
