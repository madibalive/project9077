package com.venu.venutheta.galleryPicker;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.venu.venutheta.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GalleryPickerActivity extends AppCompatActivity {
    static final int REQ_CODE_CAMERA = 1;
    private File imageFile;


    Uri cameraImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_picker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(view -> finish());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery_picker,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_camera:
                startCameraIntent();
                break;
            case R.id.action_next:
                returnBack();
                break;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    private void returnBack() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("url",Session.getInstance().getFileToUpload());
        setResult(Activity.RESULT_OK,returnIntent);
        finish();

    }

    private void startCameraIntent() {
        Intent cameraInent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraInent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this,"This Application do not have Camera Application",Toast.LENGTH_LONG).show();
            return;
        }

        imageFile = getImageFile();
        cameraInent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
        startActivityForResult(cameraInent, REQ_CODE_CAMERA);

    }

    private File getImageFile() {
        // Create an image file name
        File imageFile = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);


            imageFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );


            // Save a file: path for use with ACTION_VIEW intents
            cameraImageUri = Uri.fromFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return imageFile;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = null;
            if (requestCode == REQ_CODE_CAMERA) {
                // Do something with imagePath
                selectedImageUri = cameraImageUri;
                MediaScannerConnection.scanFile(this, new String[]{selectedImageUri.getPath()}, new String[]{"image/jpeg"},null);
                Session.getInstance().setFileToUpload(selectedImageUri.getPath());
                returnBack();

            }

            if (selectedImageUri != null) {
                Toast.makeText(this,selectedImageUri.toString(),Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,"This Application do not have Camera Application",Toast.LENGTH_LONG).show();

            }
        }

    }
}
