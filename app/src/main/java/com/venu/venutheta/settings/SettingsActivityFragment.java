package com.venu.venutheta.settings;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.liuzhuang.rcimageview.RoundCornerImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.venu.venutheta.R;
import com.venu.venutheta.auth.LoginActivity;
import com.venu.venutheta.utils.ImageUitls;
import com.venu.venutheta.utils.NetUtils;

import net.hockeyapp.android.FeedbackManager;

import java.io.ByteArrayOutputStream;
import java.util.List;

import bolts.Continuation;
import bolts.Task;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

public class SettingsActivityFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 300;
    private static final int RC_CAMERA_PERM = 123;
    ProgressDialog progressDialog;
    private RoundCornerImageView avatar;
    private TextView name, peeps, followers, following;
    private Button password, username, logout, feedback;
    private ParseUser mCurrentUser;


    public SettingsActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentUser = ParseUser.getCurrentUser();
        FeedbackManager.register(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_settings, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDataView();
        initClick();

    }

    private void initClick() {

        avatar.setOnClickListener(view -> {
            if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
                changeProfilePicture();
            }
        });
        password.setOnClickListener(view -> {
            if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
                changePassword();
            }
        });
        username.setOnClickListener(view -> {
            if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
                changeUsername();
            }
        });
        logout.setOnClickListener(view -> {
            if (NetUtils.hasInternetConnection(getActivity().getApplicationContext())){
                deleteAccount();
            }
        });

        feedback.setOnClickListener(view -> {
//            FeedbackManager.showFeedbackActivity(getActivity());

        });
    }

    private void initDataView() {

        Glide.with(getActivity())
                .load(mCurrentUser.getParseFile("avatar_small").getUrl())
                .crossFade()
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.placeholder_error_media)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .fallback(R.drawable.ic_default_avatar)
                .thumbnail(0.4f)
                .into(avatar);

    }

    private void initView(View view) {

        avatar = (RoundCornerImageView) view.findViewById(R.id.account_avatar);
        name = (TextView) view.findViewById(R.id.account_followers_cnt);
        peeps = (TextView) view.findViewById(R.id.account_peeps_cnt);
        following = (TextView) view.findViewById(R.id.account_following_cnt);
        followers = (TextView) view.findViewById(R.id.account_name);
        username = (Button) view.findViewById(R.id.account_change_username);
        password = (Button) view.findViewById(R.id.account_change_password);
        logout = (Button) view.findViewById(R.id.account_logout);
    }


    private void deleteAccount(){
        ParseUser.logOut();
        startActivity(new Intent(getActivity(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void changePassword(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dailog_settings, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        dialogBuilder.setTitle("Change Password");
        dialogBuilder.setMessage("New Password Below");
        dialogBuilder.setPositiveButton("Done", (dialog, whichButton) -> {
            String newUsername = edt.getText().toString();
            if (newUsername.length() > 0) {
                ParseUser.getCurrentUser().setPassword(newUsername);
                ParseUser.getCurrentUser().saveInBackground(e -> {
                    if (e == null){
                        Toast.makeText(getActivity(),"Username Updated",Toast.LENGTH_SHORT).show();
                    }else {
                        Timber.e("error updateing user %s",e.getMessage());
                    }
                });
            }
        });
        dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> {
        });
        AlertDialog b = dialogBuilder.create();
        b.show();

    }

    private void changeUsername(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dailog_settings, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        edt.setHint(ParseUser.getCurrentUser().getUsername());
        dialogBuilder.setTitle("Change Username");
        dialogBuilder.setMessage("Enter username below");

        dialogBuilder.setPositiveButton("Done", (dialog, whichButton) -> {
            String newUsername = edt.getText().toString();
            if (newUsername.length() > 0) {
                username.setText(newUsername);
                ParseUser.getCurrentUser().setUsername(newUsername);
                ParseUser.getCurrentUser().saveInBackground(e -> {
                    if (e == null){

                        Toast.makeText(getActivity(),"Username Updated",Toast.LENGTH_SHORT).show();

                    }else {
                        Timber.e("error updateing user %s",e.getMessage());
                    }
                });
            }
        });

        dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> {

        });

        AlertDialog b = dialogBuilder.create();
        b.show();

    }

    @AfterPermissionGranted(RC_CAMERA_PERM)
    private void changeProfilePicture(){
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
           chooser();
        } else {
            EasyPermissions.requestPermissions(this,  getString(R.string.rationale_camera),
                    RC_CAMERA_PERM, perms);
        }
    }

    private void chooser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Gallery", "Camera"},
                (dialog, which) -> {
                       switch (which) {
                        case 0:
                            Intent galleryIntent = new Intent();
                            galleryIntent.setType("image/*");
                            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(galleryIntent, "Select File"), GALLERY_REQUEST_CODE);
                            break;
                        case 1:
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                            break;
                        default:
                            break;
                    }
                }).show();
    }


    private void updateDp(final String mUrl){

        Task.callInBackground(() -> {

            Bitmap image = BitmapFactory.decodeFile(mUrl);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 80, bos);

            byte[] scaledData = bos.toByteArray();

            final ParseFile photoFile = new ParseFile("peepy.jpg", scaledData);

            photoFile.save();

            ParseUser.getCurrentUser().put("avatar",photoFile);
            ParseUser.getCurrentUser().put("avatarUrl",photoFile.getUrl());
            ParseUser.getCurrentUser().save();

            return ParseUser.getCurrentUser().getString("avatarUrl");

        }).continueWith(new Continuation<String, Void>() {
            @Override
            public Void then(Task<String> task) throws Exception {
                if (task.getResult() != null){
                    Glide.with(getActivity())
                            .load(task.getResult())
                            .placeholder(R.drawable.ic_default_avatar)
                            .error(R.drawable.placeholder_error_media)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .centerCrop()
                            .thumbnail(0.4f)
                            .fallback(R.drawable.ic_default_avatar)
                            .into(avatar);
                }else {
                    Toast.makeText(getActivity(),"Cannot Update image",Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data.getData() != null) {
                    try {
                        updateDp(ImageUitls.getPath(data.getData(),getActivity().getApplicationContext()));
                    }catch (Exception e){
                        Timber.e("erorr getting path for image %s",e.getMessage());
                    }
                } else {

                }
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data.getData() != null) {
                    try {
                        updateDp(ImageUitls.getPath(data.getData(),getActivity().getApplicationContext()));
                    }catch (Exception e){
                        Timber.e("erorr getting path for image %s",e.getMessage());
                    }
                } else {

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

    }

}
