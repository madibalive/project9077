package com.venu.venutheta.invite;


import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.greenfrvr.hashtagview.HashtagView;
import com.venu.venutheta.Actions.ActionInvitesList;
import com.venu.venutheta.R;
import com.venu.venutheta.services.LoaderGeneral;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import me.tatarka.rxloader.RxLoaderObserver;
import timber.log.Timber;


public class InvitesFragment extends DialogFragment {

    private HashtagView hashtagView;
    private Map<String,String> data = new HashMap<>();
    private Set<String> selectData = new HashSet<>();
    RxLoaderManager loaderManager;
    Button proceed,cancel;
    CheckBox selectAll;
    private ArrayList<String> pendingInvitesList = new ArrayList<>();


    public InvitesFragment() {
    }

    public static InvitesFragment newInstance(String param1, String param2) {
        InvitesFragment fragment = new InvitesFragment();
             return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_gossip, container, false);
        proceed = (Button) view.findViewById(R.id.buttonStart);
        cancel = (Button) view.findViewById(R.id.buttonEnd);
        selectAll = (CheckBox) view.findViewById(R.id.select_all);
        selectAll.setChecked(false);
        hashtagView = (HashtagView) view.findViewById(R.id.hashtags);
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loaderManager = RxLoaderManagerCompat.get(this);

        selectAll.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                hashtagView.setVisibility(View.INVISIBLE);
            }else {
                hashtagView.setVisibility(View.VISIBLE);
            }
        });
        hashtagView.addOnTagSelectListener((item, selected) -> {
            if (selected ){
               selectData.add(data.get(item.toString()));
            }else {
                selectData.remove(data.get(item.toString()));
            }
        });


        proceed.setOnClickListener(view12 -> {
            ArrayList<String> returnData1 = new ArrayList<String>(selectData);
            EventBus.getDefault().post(new ActionInvitesList(returnData1));
            dismiss();
        });


        cancel.setOnClickListener(view1 -> dismiss());
        initload();
    }

    private void createPendingInvites(){

    }

    private void initload(){
        loaderManager.create(
                LoaderGeneral.loadInvites(),
                new RxLoaderObserver<Map<String,String>>() {
                    @Override
                    public void onNext(Map<String,String> value) {
                        Timber.d("onnext");
                        new Handler().postDelayed(() -> {
                            data = value;
                            List<String > datas = new ArrayList<>(value.keySet());
                           hashtagView.setData(datas,Hash_Selected);
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
                    }

                    @Override
                    public void onCompleted() {
                        Timber.d("completed");
                        super.onCompleted();
                    }
                }
        ).start();
    }


    private static final HashtagView.DataTransform<String> Hash_Selected =  new HashtagView.DataStateTransform<String>() {
        @Override
        public CharSequence prepareSelected(String item) {
            SpannableString spannableString = new SpannableString(item);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#85F5F5F5")), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;        }

        @Override
        public CharSequence prepare(String item) {
            SpannableString spannableString = new SpannableString(item);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#85F5F5F5")), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
    };


}
