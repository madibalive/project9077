package com.venu.venutheta;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.fangxu.allangleexpandablebutton.AllAngleExpandableButton;
import com.fangxu.allangleexpandablebutton.ButtonData;
import com.fangxu.allangleexpandablebutton.ButtonEventListener;
import com.venu.venutheta.Actions.ActionCompleteGossip;
import com.venu.venutheta.Actions.ActionNewGossip;
import com.venu.venutheta.Discover.DiscoverMainActivity;
import com.venu.venutheta.contacts.ContactActivity;
import com.venu.venutheta.eventmanager.EventManagerActivity;
import com.venu.venutheta.galleryPicker.GalleryPickerActivity;
import com.venu.venutheta.gossip.CreateGossipSheet;
import com.venu.venutheta.mainfragment.ChallangeFragment;
import com.venu.venutheta.mainfragment.ChannelFragment;
import com.venu.venutheta.mainfragment.FeedFragment;
import com.venu.venutheta.mainfragment.TrendFragment;
import com.venu.venutheta.navtransitions.NavigateTo;
import com.venu.venutheta.ontap.OnTapActivity;
import com.venu.venutheta.post.PostEventActivity;
import com.venu.venutheta.services.UploadService;
import com.venu.venutheta.settings.SettingsActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

//
//import net.hockeyapp.android.CrashManager;
//import net.hockeyapp.android.UpdateManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,EasyPermissions.PermissionCallbacks {
    private static final String TAG = "MainActivity";
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private static final int CAPTURE_LOCAL_PHOTO_ACTIVITY_REQUEST_CODE = 300;
    private static final int RC_CAMERA_PERM = 123;
    private static final int RC_LOCATION_CONTACTS_PERM = 124;
    static final int REQ_CODE_CSDK_IMAGE_EDITOR = 3001;

    private static final int RC_SETTINGS_SCREEN = 125;
    private ProgressDialog progressDialog;
    private AllAngleExpandableButton button;
    private AllAngleExpandableButton mExploreBtn;
    private Toolbar toolbar;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private File saveDir = null;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.container);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(4);

        navigationView.setNavigationItemSelectedListener(this);
        setupTabs();
        setUpFab();
        setUpExploreButton();

        saveDir = new File(Environment.getExternalStorageDirectory(), "Venu_Videos");
        saveDir.mkdirs();
    }


    private void setupNavView(){
        try {
            saveDir = new File(Environment.getExternalStorageDirectory(), "MaterialCamera");
            saveDir.mkdirs();
        }catch (Exception e){
            Timber.e("error %s",e.getMessage());
        }

    }

    //////////////////////////////////////////////////////
    //////// SETUP PHASE /////////////////////////////////
    /////////////////////////////////////////////////////

    private void setUpFab(){

        button= (AllAngleExpandableButton) findViewById(R.id.button_expandable_90_180);
        final List<ButtonData> buttonDatas = new ArrayList<>();
        int[] drawable = {R.drawable.ic_add, R.drawable.ic_cloud, R.drawable.ic_live_now, R.drawable.ic_camera,R.drawable.ic_event};
        int[] color = {R.color.venu_blue, R.color.white, R.color.white, R.color.white,R.color.white};
        for (int i = 0; i < 5; i++) {
            ButtonData buttonData;
            if (i == 0) {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 15);
            } else {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 0);
            }
            buttonData.setBackgroundColorId(this, color[i]);
            buttonDatas.add(buttonData);
        }
        button.setButtonDatas(buttonDatas);
        setListener(button);

    }

    private void setUpExploreButton(){
        mExploreBtn= (AllAngleExpandableButton) findViewById(R.id.mButtonExplore);
        final List<ButtonData> buttonDatas = new ArrayList<>();
        int[] drawable = {R.drawable.ic_add, R.drawable.ic_camera, R.drawable.ic_photo_library_black_};
        int[] color = {R.color.venu_blue, R.color.white, R.color.white};
        for (int i = 0; i < 3; i++) {
            ButtonData buttonData;
            if (i == 0) {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 15);
            } else {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 0);
            }
            buttonData.setBackgroundColorId(this, color[i]);
            buttonDatas.add(buttonData);
        }
        mExploreBtn.setButtonDatas(buttonDatas);
        setExploreListener(mExploreBtn);
    }

    private void setExploreListener(AllAngleExpandableButton button) {


        button.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(int index) {
                switch (index){
                    case 1:
                        // goto camera
                        openCamera();

                        break;
                    case 2:
                        // goto media gallery
                        NavigateTo.goToHashtagGallery(MainActivity.this,"","","#VenuPeepy");
                        break;
                    default:
                }
            }

            @Override
            public void onExpand() {
//                showToast("onExpand");
            }

            @Override
            public void onCollapse() {
//                showToast("onCollapse");
            }
        });
    }



    private void setupTabs() {

//        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
//        tabLayout.getTabAt(1).setIcon(R.drawable.ic_trend_active);
//        tabLayout.getTabAt(2).setIcon(R.drawable.ic_channel_active);
//        tabLayout.getTabAt(3).setIcon(R.drawable.ic_challange_active);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (toolbar != null) {
                    switch (tab.getPosition()) {
//                        case 0:
//                            tabLayout.getTabAt(0).setIcon(R.drawable.homeactive);
//                            tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.disc_red));
//                            break;
//                        case 1:
//                            tabLayout.getTabAt(1).setIcon(R.drawable.trendsactive);
//                            tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.trend));
//                            break;
//                        case 2:
//                            tabLayout.getTabAt(2).setIcon(R.drawable.channelactive);
//                            tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.channel));
//                            break;
//                        case 3:
//                            tabLayout.getTabAt(3).setIcon(R.drawable.challengeactive);
//                            tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.challange));
//                            break;
                        case 4:

                            break;

                        default:

                            break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (toolbar != null) {
                    switch (tab.getPosition()) {
//                        case 0:
//                            tabLayout.getTabAt(0).setIcon(R.drawable.homeinactive);
//
//                            break;
//                        case 1:
//                            tabLayout.getTabAt(1).setIcon(R.drawable.trendinactive);
//
//                            break;
//                        case 2:
//                            tabLayout.getTabAt(2).setIcon(R.drawable.channel);
//                            break;
//                        case 3:
//                            tabLayout.getTabAt(3).setIcon(R.drawable.challenge);
//
//                            break;
                        default:
                            break;
                    }
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                updateFab(position);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

    }




    //////////////////////////////////////////////////////
    //////// NAVIGATING PHASE ////////////////////////////
    /////////////////////////////////////////////////////

    @AfterPermissionGranted(RC_CAMERA_PERM)
    private void openEvent(){

        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            startActivity(new Intent(MainActivity.this,PostEventActivity.class));

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this,  getString(R.string.rationale_camera),
                    RC_CAMERA_PERM, perms);
        }
    }

    @AfterPermissionGranted(RC_CAMERA_PERM)
    private void openCamera(){
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(this, perms)) {
            startActivity(new Intent(MainActivity.this, GalleryPickerActivity.class));
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera),
                    RC_CAMERA_PERM, perms);
        }
    }


    @AfterPermissionGranted(RC_CAMERA_PERM)
    private void openVideo(){

        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            MaterialCamera materialCamera = new MaterialCamera(this)
                    .saveDir(saveDir)
                    .primaryColorAttr(R.attr.colorPrimaryDark)             // The theme color used for the camera, defaults to colorPrimary of Activity in the constructor
                    .showPortraitWarning(false)
                    .allowRetry(true)
                    .qualityProfile(MaterialCamera.QUALITY_480P);

            materialCamera
                    .countdownSeconds(30)
                    .start(CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);

        } else {
            EasyPermissions.requestPermissions(this,  getString(R.string.rationale_camera),
                    RC_CAMERA_PERM, perms);
        }


    }

    private void sendtoAdobe(String url){
            /* 1) Make a new Uri object (Replace this with a real image on your device) */
        Uri imageUri = Uri.parse(url);

        /* 2) Create a new Intent */
//        Intent imageEditorIntent = new AdobeImageIntent.Builder(this)
//                .setData(imageUri)
//                .build();
//
//        /* 3) Start the Image Editor with request code 1 */
//        startActivityForResult(imageEditorIntent, REQ_CODE_CSDK_IMAGE_EDITOR);
    }

    public void openGossip(){
        DialogFragment gossipD = new CreateGossipSheet();
        gossipD.show(getSupportFragmentManager(),"gossip");
    }

    private void updateFab(int selectedPage) {
        switch (selectedPage) {
            case 3:
                button.setVisibility(View.INVISIBLE);
                mExploreBtn.setVisibility(View.VISIBLE);
                break;
            default:
                if (mExploreBtn.getVisibility() == View.VISIBLE)
                    mExploreBtn.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                break;
        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_events) {
            startActivity(new Intent(MainActivity.this, EventManagerActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        } else if (id == R.id.nav_circle) {

            startActivity(new Intent(MainActivity.this, ContactActivity.class));

        } else if (id == R.id.nav_discover) {
            startActivity(new Intent(MainActivity.this, DiscoverMainActivity.class));

        } else if (id == R.id.nav_ontap) {
            startActivity(new Intent(MainActivity.this, OnTapActivity.class));

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void setListener(AllAngleExpandableButton button) {


        button.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(int index) {



                switch (index){
                    case 4:
                        Toast.makeText(getApplicationContext(),"gossip",Toast.LENGTH_SHORT).show();
                        DialogFragment gossipD = new  CreateGossipSheet();
                        gossipD.show(getSupportFragmentManager(),"gossip");
                        break;
                    case 2:
                        openCamera();
                        break;

                    case 3:
                        openVideo();
                        break;
                    case 1:
                        openEvent();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onExpand() {
//                showToast("onExpand");
            }

            @Override
            public void onCollapse() {
//                showToast("onCollapse");
            }
        });
    }

    //////////////////////////////////////////////////////
    //////// RETURN ACTIVITY PHASE ///////////////////////
    //////////////////////////////////////////////////////



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_LOCAL_PHOTO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data.getData() != null) {
//                    NavigateTo.gotoPostMedia(MainActivity.this,data.getStringExtra("url"),String.valueOf(data.getData()),1);
                    sendtoAdobe(data.getStringExtra("url"));
                } else {
                }
            }
        }

        if (requestCode == REQ_CODE_CSDK_IMAGE_EDITOR) {
            if (resultCode == RESULT_OK) {
                if (data.getData() != null) {
//                    Uri editedImageUri = data.getParcelableExtra(AdobeImageIntent.EXTRA_OUTPUT_URI);
//                    NavigateTo.gotoPostMedia(MainActivity.this,editedImageUri.toString(),String.valueOf(data.getData()),1);
                } else {
                }
            }
        }

        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                final File file = new File(data.getData().getPath());
                String fileUrl = file.getAbsolutePath();

                NavigateTo.gotoPostMedia(MainActivity.this,fileUrl,String.valueOf(data.getData()),1);

            } else if (data != null) {
                Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
                if (e != null) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, getString(R.string.rationale_ask_again))
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel), null /* click listener */)
                    .setRequestCode(RC_SETTINGS_SCREEN)
                    .build()
                    .show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActionNewGossip action) {
        UploadService.startActionGossip(getApplicationContext(), action.title);
        progressDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActionCompleteGossip action) {
        if (!action.error) {
            progressDialog.dismiss();
            NavigateTo.goToGossip(MainActivity.this, action.chat_id);
        }
    }

//    private void checkForCrash() {
//        CrashManager.register(MainActivity.this, "APP_ID", new CustomCrashManagerListener());
//
//    }

//    private void checkForUpdates() {
//        UpdateManager.register(this);
//    }
//
//    private void unregisterManagers() {
//        UpdateManager.unregister();
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
//        unregisterManagers();
    }

    @Override
    protected void onDestroy() {
//        unregisterManagers();
        super.onDestroy();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return FeedFragment.newInstance();
                case 1:
                    return TrendFragment.newInstance();
                case 2:
                    return ChannelFragment.newInstance();
                case 3:
                    return ChallangeFragment.newInstance();
                default:
                    return ChannelFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
