package com.venu.venutheta.modal;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.venu.venutheta.R;
import com.venu.venutheta.utils.NetUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Madiba on 11/5/2016.
 */



public class RequestFragment extends DialogFragment {

    private RecyclerView mRecyclerView;
    private LinearLayout mDetailLayout;
    private LinearLayout mHomeLayout;
    private PostRequestAdapter mAdapter;

    private ProgressDialog progress;
    private ParseQuery<ParseObject> categoryQuery;
    private Button back,submit;
    private TextView itemName;
    private ParseObject selectedCategory;
    private List<ParseObject> data= new ArrayList<>();
    public RequestFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_request, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.home_recyclerview);
        mDetailLayout = (LinearLayout) view.findViewById(R.id.request_order_layout);
        mHomeLayout = (LinearLayout) view.findViewById(R.id.request_home_layout);
        back = (Button) view.findViewById(R.id.buttonEnd);
        submit = (Button) view.findViewById(R.id.buttonStart);
        itemName = (TextView) view.findViewById(R.id.order_item);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListener();
        mAdapter = new PostRequestAdapter(R.layout.item_request_category,data);
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        mAdapter.setOnRecyclerViewItemClickListener((view1, i) -> {
            selectedCategory = mAdapter.getItem(i);
            loadDetail(i);
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL ,false));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

    }

    private void initListener(){
        back.setOnClickListener(view -> loadHome());

        submit.setOnClickListener(view -> {
            progress = ProgressDialog.show(getActivity(), null,
                    "Sending Request", true);
            ParseObject request = new ParseObject("OnTapRequest");
            request.put("from", ParseUser.getCurrentUser());
            request.put("category",selectedCategory);
            request.put("categoryName",selectedCategory.getString("name"));
            request.saveInBackground(e -> {
                if (e == null){
                    Toast.makeText(getActivity(),"Request sent, go to ontap to view",Toast.LENGTH_LONG).show();
                }else {
                    Timber.e("Error posting request $s",e.getMessage());
                    Toast.makeText(getActivity(),"Request failed",Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
                dismiss();
            });
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog d = getDialog();
        if (d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    private void loadHome(){
        if (mDetailLayout.getVisibility() == View.VISIBLE) {
            mDetailLayout.setVisibility(View.GONE);
        }
        if (mHomeLayout.getVisibility() == View.GONE) {
            mHomeLayout.setVisibility(View.VISIBLE);
        }


    }

    private void loadDetail(int pos){

        if (mDetailLayout.getVisibility() == View.GONE) {
            mDetailLayout.setVisibility(View.VISIBLE);
        }
        if (mHomeLayout.getVisibility() == View.VISIBLE) {
            mHomeLayout.setVisibility(View.GONE);
        }
    }

    private void load(){
        categoryQuery= ParseQuery.getQuery("OnTapCategory");
        categoryQuery.findInBackground((objects, e) -> {
            if (e == null){
                mAdapter.setNewData(objects);
            }
        });
    }


    class PostRequestAdapter
            extends BaseQuickAdapter<ParseObject> {

        PostRequestAdapter(int layoutResId, List<ParseObject> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, final ParseObject category) {

//            holder.setText(R.id.ontap_dir_item_name,category.getString("name"));
//            Glide.with(mContext).load("http://uauage.org/upload/2014/10/avatar-round.png").crossFade().fitCenter().into((ImageView) holder.getView(R.id.notif_i_avatar));


        }
    }

    @Override
    public void onPause() {
        if (categoryQuery != null){
            categoryQuery.cancel();
        }
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext()))
            load();
    }

    @Override
    public void onStop() {
        if (categoryQuery != null){
            categoryQuery.cancel();
        }
        super.onStop();
    }
}
