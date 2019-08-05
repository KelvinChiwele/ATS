package com.techart.ats;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.techart.ats.constants.Constants;

public class FullImageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image_activity);
        ImageButton ivBack = findViewById(R.id.iv_back);
        ivBack.bringToFront();
        String imageUrl = getIntent().getStringExtra(Constants.IMAGE_URL);
        PhotoView iv_sample = findViewById(R.id.iv_sample);
        Glide.with(FullImageActivity.this)
                .load(imageUrl)
                .into(iv_sample);
    }

    public void goBack(View view) {
        finish();
    }
}
