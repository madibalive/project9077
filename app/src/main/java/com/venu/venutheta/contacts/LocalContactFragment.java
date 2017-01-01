package com.venu.venutheta.contacts;


import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.parse.ParseUser;
import com.venu.venutheta.R;
import com.venu.venutheta.models.ModelOtherContact;
import com.venu.venutheta.models.PhoneContact;
import com.venu.venutheta.services.LoaderGeneral;
import com.venu.venutheta.ui.DividerItemDecoration;
import com.venu.venutheta.utils.NetUtils;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;


public class LocalContactFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener ,EasyPermissions.PermissionCallbacks{
    private static final int RC_LOCATION_CONTACTS_PERM = 124;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerview;
    private List<ModelOtherContact> mDatas = new ArrayList<>();
    private MainAdapter mAdapter;
    private View facebookHeader;
    private View LocalContactHeader;
    private RxLoaderManager loaderManager;
    private Boolean loadFollowers;
    private String userId;

    public LocalContactFragment() {
    }

    public static LocalContactFragment newInstance() {
        return new LocalContactFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.container_core, container, false);
        mRecyclerview = (RecyclerView) view.findViewById(R.id.core_recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.core_swipelayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loaderManager = RxLoaderManagerCompat.get(this);
        initAdapter();
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            Timber.e("passed the network test ");
            load();
        }
    }

    private void initAdapter(){
        mAdapter=new MainAdapter(R.layout.item_ontap,mDatas);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setAdapter(mAdapter);
        mAdapter.setOnRecyclerViewItemClickListener((view, i) -> {
        });


    }

    @Override
    public void onRefresh() {
        if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
            initload();
        }else
            mSwipeRefreshLayout.setRefreshing(false);
    }

    void initload(){
        Timber.e("starting network call ");

        loaderManager.create(LoaderGeneral.ContactLoadOnce(getActivity()),
                new RxLoaderObserver<List<ModelOtherContact>>() {
                    @Override
                    public void onNext(List<ModelOtherContact> value) {
                        new Handler().postDelayed(() -> {
                            mSwipeRefreshLayout.setRefreshing(false);
                            if (value.size()>0)
                                mAdapter.setNewData(value);
                        },500);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Timber.e(e.getMessage());

                    }

                    @Override
                    public void onCompleted() {

                        super.onCompleted();
                        Timber.e("return alled ");
                    }
                }).start();

    }


    private class LocalAdapter
            extends BaseQuickAdapter<PhoneContact> {

        LocalAdapter(int layoutResId, List<PhoneContact> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, final PhoneContact data) {
//
            holder.setText(R.id.user_act_username, data.getUsername())
                    .setVisible(R.id.user_act_avatar,false)
                    .setOnClickListener(R.id.user_act_check,new OnItemChildClickListener());

        }
    }

    private class PraseAdapter
            extends BaseQuickAdapter<ParseUser> {

        PraseAdapter(int layoutResId, List<ParseUser> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, final ParseUser data) {
//

            holder.setText(R.id.user_act_username, data.getUsername())
                    .setOnClickListener(R.id.user_act_check,new OnItemChildClickListener());
            Glide.with(mContext).load(data.getParseFile("avatar").getUrl())
                    .thumbnail(0.1f).dontAnimate().into((ImageView) holder.getView(R.id.cc_i_avatar));

        }
    }

    private class FacebookAdapter
            extends BaseQuickAdapter<ParseUser> {

        FacebookAdapter(int layoutResId, List<ParseUser> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, final ParseUser data) {
//

            holder.setText(R.id.user_act_username, data.getUsername())
                    .setOnClickListener(R.id.user_act_check,new OnItemChildClickListener());

            Glide.with(mContext).load(data.getParseFile("avatar").getUrl())
                    .thumbnail(0.1f).dontAnimate().into((ImageView) holder.getView(R.id.cc_i_avatar));

        }
    }



    public class MainAdapter extends BaseQuickAdapter<ModelOtherContact> {

        public MainAdapter(int layoutResId, List<ModelOtherContact> data) {
            super(layoutResId, data);
        }

        @Override
        protected int getDefItemViewType(int position) {

            if (getItem(position).getType() == ModelOtherContact.TYPE_FACEBOOK)
                return ModelOtherContact.TYPE_FACEBOOK;

            else if (getItem(position).getType() == ModelOtherContact.TYPE_PARSE)
                return ModelOtherContact.TYPE_PARSE;

            else if (getItem(position).getType() == ModelOtherContact.TYPE_LOCAL)
                return ModelOtherContact.TYPE_LOCAL;

            return super.getDefItemViewType(position);
        }

        @Override
        protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ModelOtherContact.TYPE_FACEBOOK)
                return new FacebookContactViewHolder(getItemView(R.layout.container_bare, parent));

            else if (viewType == ModelOtherContact.TYPE_PARSE)
                return new ParseContactViewHolder(getItemView(R.layout.container_bare, parent));

            else if (viewType == ModelOtherContact.TYPE_LOCAL)
                return new LocalContactViewHolder(getItemView(R.layout.container_bare, parent));


            return super.onCreateDefViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
            ModelOtherContact n;
            try {
                n = getItem(position);

                if (holder instanceof LocalContactViewHolder) {
                    RecyclerView rview = ((LocalContactViewHolder) holder).getView(R.id.bare_recyclerview);
                    TextView title = ((LocalContactViewHolder) holder).getView(R.id.bare_title);
                    title.setText("Trending Gossips");
                    LocalAdapter mAdapter = new LocalAdapter(R.layout.user_acnt_layout_small, n.getLocalContact());
                    rview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                    rview.setAdapter(mAdapter);
                    mAdapter.setOnRecyclerViewItemChildClickListener((baseQuickAdapter, view, i) -> {
                        if (view.getId() == R.id.user_act_check){
                            // TODO: 12/29/2016  call follow here
                        }
                    });
                }

                else if (holder instanceof FacebookContactViewHolder) {
                    RecyclerView rview = ((FacebookContactViewHolder) holder).getView(R.id.bare_recyclerview);
                    TextView title = ((FacebookContactViewHolder) holder).getView(R.id.bare_title);
                    title.setText("Trending Gossips");
                    FacebookAdapter mAdapter = new FacebookAdapter (R.layout.user_acnt_layout_small, n.getFacebookContacts());
                    rview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                    rview.setAdapter(mAdapter);
                    mAdapter.setOnRecyclerViewItemChildClickListener((baseQuickAdapter, view, i) -> {
                        if (view.getId() == R.id.user_act_check){
                            // TODO: 12/29/2016  call follow here
                        }
                    });
                }

                else if (holder instanceof ParseContactViewHolder) {
                    RecyclerView rview = ((ParseContactViewHolder) holder).getView(R.id.bare_recyclerview);
                    TextView title = ((ParseContactViewHolder) holder).getView(R.id.bare_title);
                    title.setText("Trending Gossips");
                    PraseAdapter mAdapter = new PraseAdapter (R.layout.user_acnt_layout_small, n.getParseContacts());
                    rview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                    rview.setAdapter(mAdapter);
                    mAdapter.setOnRecyclerViewItemChildClickListener((baseQuickAdapter, view, i) -> {
                        if (view.getId() == R.id.user_act_check){
                            // TODO: 12/29/2016  call follow here
                        }
                    });
                }



                super.onBindViewHolder(holder, position, payloads);
            }catch (Exception e){
                Timber.e(e.getMessage());
            }
            super.onBindViewHolder(holder, position, payloads);


        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, ModelOtherContact discoverModel) {

        }

        private class ParseContactViewHolder extends BaseViewHolder {
            public ParseContactViewHolder(View itemView) {
                super(itemView);
            }
        }

        private class LocalContactViewHolder extends BaseViewHolder {
            public LocalContactViewHolder(View itemView) {
                super(itemView);
            }
        }

        private class FacebookContactViewHolder extends BaseViewHolder {
            public FacebookContactViewHolder(View itemView) {
                super(itemView);
            }
        }



    }

    @AfterPermissionGranted(RC_LOCATION_CONTACTS_PERM)
    private void load() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.READ_CONTACTS)) {

                initload();

            } else {
                EasyPermissions.requestPermissions(getActivity(), getString(R.string.permission_contact),
                        RC_LOCATION_CONTACTS_PERM, Manifest.permission.READ_CONTACTS);
            }
        }else {
            initload();
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
