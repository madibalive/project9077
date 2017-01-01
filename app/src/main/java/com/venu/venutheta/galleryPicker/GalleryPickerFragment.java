package com.venu.venutheta.galleryPicker;

import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.venu.venutheta.Actions.ActionGalleryClick;
import com.venu.venutheta.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;


public class GalleryPickerFragment extends Fragment {

    RecyclerView mGalleryRecyclerView;
    ImageView mPreview;
    AppBarLayout mAppBarContainer;

    private static final String EXTENSION_JPG = ".jpg";
    private static final String EXTENSION_JPEG = ".jpeg";
    private static final String EXTENSION_PNG = ".png";
    private static final int PREVIEW_SIZE = 800;
    private static final int MARGING_GRID = 2;

    private Session mSession = Session.getInstance();
    private GridAdapter mGridAdapter;
    private ArrayList<File> mFiles;
    private boolean isFirstLoad = true;

    public static GalleryPickerFragment newInstance() {
        return new GalleryPickerFragment();
    }

    private void initViews() {
        if (isFirstLoad) {
            mGridAdapter = new GridAdapter(getContext());
        }
        mGalleryRecyclerView.setAdapter(mGridAdapter);
        mGalleryRecyclerView.setHasFixedSize(true);
        mGalleryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mGalleryRecyclerView.addItemDecoration(addItemDecoration());
        fetchMedia();
    }

    private RecyclerView.ItemDecoration addItemDecoration() {
        return new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view,
                                       RecyclerView parent, RecyclerView.State state) {
                outRect.left = MARGING_GRID;
                outRect.right = MARGING_GRID;
                outRect.bottom = MARGING_GRID;
                if (parent.getChildLayoutPosition(view) >= 0 && parent.getChildLayoutPosition(view) <= 3) {
                    outRect.top = MARGING_GRID;
                }
            }
        };
    }

    private void fetchMedia() {
        mFiles = new ArrayList<>();
//        File dirDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        parseDir(dirDownloads);
        fetchDownloads();
//        File dirDcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//        parseDir(dirDcim);
//        File dirPictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        parseDir(dirPictures);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//            File dirDocuments = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
//            parseDir(dirDocuments);
//        }

        if (mFiles.size() > 0) {
            displayPreview(mFiles.get(0));
            mGridAdapter.setItems(mFiles); // getRangePets()
        }
        isFirstLoad = false;
    }

    private void fetchDownloads() {
        Cursor imageCursor = null;
        try {
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.ORIENTATION};
            final String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";

            imageCursor = getActivity().getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
            //imageCursor = sContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);


            if (imageCursor != null) {

                while (imageCursor.moveToNext()) {
                    String imageLocation = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    File file = new File(imageLocation);
                    mFiles.add(file);
                }

            }
            imageCursor = getActivity().getApplicationContext().getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, columns, null, null, orderBy);
            if (imageCursor != null) {

                while (imageCursor.moveToNext()) {
                    String imageLocation = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    File file = new File(imageLocation);
                    mFiles.add(file);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (imageCursor != null && !imageCursor.isClosed()) {
                imageCursor.close();
            }
        }

    }

    private void parseDir(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            parseFileList(files);
        }
    }

    private void parseFileList(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                if (!file.getName().toLowerCase().startsWith(".")) {
                    parseDir(file);
                }
            } else {
                if (file.getName().toLowerCase().endsWith(EXTENSION_JPG)
                        || file.getName().toLowerCase().endsWith(EXTENSION_JPEG)
                        || file.getName().toLowerCase().endsWith(EXTENSION_PNG)) {
                    mFiles.add(file);
                }
            }
        }
    }

    private void displayPreview(File file) {
        mSession.setFileToUpload(file.getAbsolutePath());
        Glide.with(getContext())
                .load(Uri.fromFile(file))
                .dontAnimate()
                .centerCrop()
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mPreview);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.gallery_picker_view, container, false);
        mAppBarContainer = (AppBarLayout) v.findViewById(R.id.mAppBarContainer);
        mPreview = (ImageView) v.findViewById(R.id.mPreview);
        mGalleryRecyclerView = (RecyclerView) v.findViewById(R.id.mGalleryRecyclerView);
        initViews();
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ActionGalleryClick action) {
        if (action.file != null) {
            displayPreview(action.file);
            mSession.setFileToUpload(action.file.getAbsolutePath());
            mAppBarContainer.setExpanded(true, true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {
        Glide.clear(mPreview);
        mPreview = null;
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}