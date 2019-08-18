package com.techart.atszambia;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.techart.atszambia.admin.news.NewsListActivity;
import com.techart.atszambia.admin.news.NewsPostActivity;
import com.techart.atszambia.constants.Constants;
import com.techart.atszambia.constants.FireBaseUtils;
import com.techart.atszambia.models.News;
import com.techart.atszambia.utils.NumberUtils;
import com.techart.atszambia.utils.TimeUtils;
import com.techart.atszambia.viewholder.NewsViewHolder;

/**
 * Will be available to admins for postings articles
 */
public class NewsActivity extends AppCompatActivity {
    private RecyclerView rvNews;
    private boolean mProcessView = false;
    private TextView tvEmpty;
    private ProgressBar progressBar;
    private int count;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rvNews = findViewById(R.id.rv_news);
        rvNews.setHasFixedSize(true);
        tvEmpty = findViewById(R.id.tv_empty);
        progressBar = findViewById(R.id.pb_loading);

        count = getIntent().getIntExtra(Constants.COUNT, 0);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvNews.setLayoutManager(linearLayoutManager);
        progressBar.setVisibility(View.GONE);


        bindView();
        // ATTENTION: This was auto-generated to handle app links.
       /* Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        if (appLinkData != null){
            String newId = appLinkData.getLastPathSegment();
        }*/
    }



    private void bindView() {
        FirebaseRecyclerOptions<News> response = new FirebaseRecyclerOptions.Builder<News>()
                                                              .setQuery(FireBaseUtils.mDatabaseNews, News.class)
                                                              .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<News, NewsViewHolder>(response) {
            @NonNull
            @Override
            public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_news, parent, false);
                return new NewsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull NewsViewHolder viewHolder, int position, @NonNull final News model) {
                final String post_key = getRef(position).getKey();
                progressBar.setVisibility(View.GONE);

                viewHolder.post_title.setText(model.getNewsTitle());
                viewHolder.setIvImage(NewsActivity.this, model.getImageUrl());

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
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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

                        Intent readPoemIntent = new Intent(NewsActivity.this, ScrollingActivity.class);
                        readPoemIntent.putExtra(Constants.POST_CONTENT, model.getNews());
                        readPoemIntent.putExtra(Constants.POST_TITLE, model.getNewsTitle());
                        readPoemIntent.putExtra(Constants.IMAGE_URL, model.getImageUrl());
                        startActivity(readPoemIntent);
                    }
                });

                viewHolder.btnComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentIntent = new Intent(NewsActivity.this, CommentActivity.class);
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

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu contains components to be displayed
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null && FireBaseUtils.isAllowed(FireBaseUtils.getEmail())){
            getMenuInflater().inflate(R.menu.menu_add, menu);
        }
        return true;
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    /**
     *Handle action bar item clicks here. The action bar will automatically
     *  handle clicks on the Home/Up button,
     * @param item item on the menu
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        int id = item.getItemId();
        if (id == R.id.action_add) {
            intent = new Intent(NewsActivity.this, NewsPostActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_edit) {
            //ToDO check if there are any existing posts
            intent = new Intent(NewsActivity.this, NewsListActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
