package com.venu.venutheta.profile;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.liuzhuang.rcimageview.RoundCornerImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.parse.ParseUser;
import com.venu.venutheta.Actions.ActionIsFollowCheck;
import com.venu.venutheta.R;
import com.venu.venutheta.adapter.SingletonDataSource;
import com.venu.venutheta.models.GlobalConstants;
import com.venu.venutheta.services.GeneralService;
import com.venu.venutheta.ui.StateButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class UserPageFragment extends Fragment {
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private RoundCornerImageView mAavtar;
    private TextView mName,mFollowersCount,mFollowingCount,mEventsCount,mPeepCount;
    private StateButton actionBtn;
    private String mUserId;
    private ParseUser user;
    private Boolean isFollowing =false;


    public UserPageFragment() {
        // Required empty public constructor
    }


    public static UserPageFragment newInstance() {
        UserPageFragment fragment = new UserPageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity().getIntent().hasExtra(GlobalConstants.PASS_ID)){
            mUserId = getActivity().getIntent().getStringExtra(GlobalConstants.PASS_ID);

        }
        if (SingletonDataSource.getInstance().getCurrentUser() !=null){
            user = SingletonDataSource.getInstance().getCurrentUser();
        }else{
            user = ParseUser.createWithoutData(ParseUser.class,mUserId);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_user_page, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.container);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);

        mName = (TextView) view.findViewById(R.id.user_prof_name);
        mFollowersCount = (TextView) view.findViewById(R.id.user_prof_follower);
        mFollowingCount = (TextView) view.findViewById(R.id.user_prof_following);
        mEventsCount = (TextView) view.findViewById(R.id.user_prof_events);
        mPeepCount = (TextView) view.findViewById(R.id.user_prof_peeps);
        mAavtar = (RoundCornerImageView) view.findViewById(R.id.user_prof_image);
        actionBtn = (StateButton) view.findViewById(R.id.user_prof_follow_btn);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(4);

        actionBtn.setOnClickListener(view1 -> {
            if (isFollowing){
                // init background
                // change text
                GeneralService.startActionUnFollow(getActivity(),mUserId,user.getClassName(),1);
                actionBtn.setNormalBackgroundColor(getResources().getColor(R.color.white));
                actionBtn.setPressedBackgroundColor(getResources().getColor(R.color.venu_blue));

                actionBtn.setText("Follow");

            }else {
                // init background
                GeneralService.startActionFollow(getActivity(),mUserId,user.getClassName(),1);
                actionBtn.setNormalBackgroundColor(getResources().getColor(R.color.venu_blue));
                actionBtn.setPressedBackgroundColor(getResources().getColor(R.color.white));

                actionBtn.setText("Followed");
                //change test
            }
        });

        initView();
        checkStatus();
    }


    private void initView(){
        mName.setText(user.getUsername());
        mFollowersCount.setText(String.valueOf(user.getInt("followers")));
        mFollowingCount.setText(String.valueOf(user.getInt("following")));
        mEventsCount.setText(String.valueOf(user.getInt("peeps")));
        mPeepCount.setText(String.valueOf(user.getInt("events")));

        Glide.with(getActivity())
                .load(user.getString("avatar"))
                .crossFade()
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.placeholder_error_media)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .fallback(R.drawable.ic_default_avatar)
                .thumbnail(0.4f)
                .into(mAavtar);
    }

    private void checkStatus(){
        GeneralService.startActionCheckFollowing(getActivity(),mUserId,user.getClassName(),1);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (position==1)
                return GalleryFragment.newInstance();
            else
                return EventsFragment.newInstance();
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Peeps";
                case 1:
                    return "Events";
                case 2:
                    return "Followers";
                case 3:
                    return "Following";
            }
            return null;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActionIsFollowCheck action){
        if (!action.error){
            if (action.isFollow){
                isFollowing =true;
                actionBtn.setNormalBackgroundColor(getResources().getColor(R.color.venu_blue));
                actionBtn.setPressedBackgroundColor(getResources().getColor(R.color.white));

                actionBtn.setText("Followed");
            }else {
                actionBtn.setNormalBackgroundColor(getResources().getColor(R.color.white));
                actionBtn.setPressedBackgroundColor(getResources().getColor(R.color.venu_blue));

                actionBtn.setText("Follow");
            }
        }
    }


    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

}
