package com.techart.atszambia;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.techart.atszambia.constants.Constants;

public class ScrollingActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private  String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        String postTitle = getIntent().getStringExtra(Constants.POST_TITLE);
        String postContent = getIntent().getStringExtra(Constants.POST_CONTENT);
        imageUrl = getIntent().getStringExtra(Constants.IMAGE_URL);
        setTitle(postTitle);
        TextView tvPoem = findViewById(R.id.tvNews);
        TextView tvTitle = findViewById(R.id.tvTitle);
        ImageView ivSample = findViewById(R.id.iv_sample);
        tvPoem.setText(postContent);
        tvTitle.setText(postTitle);
        setImage(ivSample);
      /*  fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });*/
    }

    public void setImage(ImageView ivSample) {
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        Glide.with(this)
                .load(imageUrl)
                .apply(options)
                .into(ivSample);
    }

    private void share() {
        /*int endAt = contents.get(lastAccessedPage).length()/4;
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, Constants.SENT_FROM +  "\n" + contents.get(lastAccessedPage).substring(0,endAt));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);*/
    }

    public void onClick(View view) {
        Intent intent = new Intent(ScrollingActivity.this,FullImageActivity.class);
        intent.putExtra(Constants.IMAGE_URL,imageUrl);
        startActivity(intent);
    }
}
