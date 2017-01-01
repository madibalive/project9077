package com.venu.venutheta.gallery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.venu.venutheta.R;
import com.venu.venutheta.models.GlobalConstants;


public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("#"+getIntent().getStringExtra(GlobalConstants.GAL_HASHTAH));
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
