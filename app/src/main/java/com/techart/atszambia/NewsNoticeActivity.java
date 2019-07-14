package com.techart.atszambia;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.techart.atszambia.constants.Constants;
import com.techart.atszambia.constants.FireBaseUtils;
import com.techart.atszambia.models.News;
import com.techart.atszambia.utils.NumberUtils;
import com.techart.atszambia.utils.TimeUtils;
import com.techart.atszambia.viewholder.NewsViewHolder;

/**
 * Will be available to admins for postings articles
 */
public class NewsNoticeActivity extends AppCompatActivity {
    private RecyclerView rvNews;
    private boolean mProcessView = false;
    private ProgressBar progressBar;
    private String newTile;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rvNews = findViewById(R.id.rv_news);
        rvNews.setHasFixedSize(true);
        progressBar = findViewById(R.id.pb_loading);
        newTile = getIntent().getStringExtra(Constants.NEWS_TITLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvNews.setLayoutManager(linearLayoutManager);
        bindView();
    }

    private void bindView() {
        FirebaseRecyclerOptions<News> response = new FirebaseRecyclerOptions.Builder<News>()
                                                         .setQuery(FireBaseUtils.mDatabaseNews.orderByChild(Constants.NEWS_TITLE).equalTo(newTile), News.class)
                                                         .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<News, NewsViewHolder>(response) {
            @NonNull
            @Override
            public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_notifications, parent, false);
                return new NewsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull NewsViewHolder viewHolder, int position, @NonNull final News model) {
                final String post_key = getRef(position).getKey();
                progressBar.setVisibility(View.GONE);
                viewHolder.post_title.setText(model.getNewsTitle());
                viewHolder.setIvImage(NewsNoticeActivity.this, model.getImageUrl());
                if (model.getNumComments() != null) {
                    String count = NumberUtils.shortenDigit(model.getNumComments());
                    viewHolder.numComments.setText(count);
                }
                if (model.getNumViews() != null) {
                    String count = NumberUtils.shortenDigit(model.getNumViews());
                    viewHolder.tvNumViews.setText(count);
                }
                if (model.getTimeCreated() != null) {
                    String time = TimeUtils.timeElapsed(model.getTimeCreated());
                    viewHolder.timeTextView.setText(time);
                }
                viewHolder.setPostViewed(post_key);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProcessView = true;
                        FireBaseUtils.mDatabaseNewsViews.child(post_key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mProcessView) {
                                    if (!dataSnapshot.hasChild(FireBaseUtils.getUiD())) {
                                        FireBaseUtils.addNewsView(model, post_key);
                                        FireBaseUtils.onQuestionAnswered(post_key);
                                        FireBaseUtils.onNewsViewed(post_key);
                                        mProcessView = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        Intent readPoemIntent = new Intent(NewsNoticeActivity.this, ScrollingActivity.class);
                        readPoemIntent.putExtra(Constants.POST_CONTENT, model.getNews());
                        readPoemIntent.putExtra(Constants.POST_TITLE, model.getNewsTitle());
                        startActivity(readPoemIntent);
                    }
                });

                viewHolder.btnComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentIntent = new Intent(NewsNoticeActivity.this, CommentActivity.class);
                        commentIntent.putExtra(Constants.POST_KEY, post_key);
                        commentIntent.putExtra(Constants.POST_TITLE, model.getNewsTitle());
                        commentIntent.putExtra(Constants.COUNT, model.getNumComments().intValue());
                        startActivity(commentIntent);
                    }
                });
            }
        };
        rvNews.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }
}
