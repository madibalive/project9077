package com.venu.venutheta.eventpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.venu.venutheta.R;
import com.venu.venutheta.adapter.SingletonDataSource;
import com.venu.venutheta.ui.TintTarget;

public class EventPageActivity extends AppCompatActivity {

    private ImageButton mPlayBtn;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_tap);

    }

    private void load(){
        String url;

        if (getIntent().hasExtra("fromFeed") && getIntent().getBooleanExtra("fromFeed",false)) {
            url = SingletonDataSource.getInstance().getCurrentEvent().getString("url");
            if (url !=null){

                Glide.with(EventPageActivity.this)
                        .load(url)
                        .priority(Priority.HIGH)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(new TintTarget(imageView));

                imageView.setOnClickListener(view -> {
                    startActivity(new Intent(EventPageActivity.this, BareImgViewerActivity.class).putExtra("url", url));
                });

            }
        }


    }











}
