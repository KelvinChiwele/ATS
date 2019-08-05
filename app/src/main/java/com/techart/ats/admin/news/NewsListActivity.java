package com.techart.ats.admin.news;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.techart.ats.R;
import com.techart.ats.constants.Constants;
import com.techart.ats.constants.FireBaseUtils;
import com.techart.ats.models.News;

public class NewsListActivity extends AppCompatActivity {
    private RecyclerView mNewsList;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newslist);
        setTitle("Click to edit");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNewsList = findViewById(R.id.rv_news);
        mNewsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NewsListActivity.this);
        mNewsList.setLayoutManager(linearLayoutManager);
        bindView();
    }

    private void bindView() {
        FirebaseRecyclerOptions<News> response = new FirebaseRecyclerOptions.Builder<News>()
                                                         .setQuery(FireBaseUtils.mDatabaseNews, News.class)
                                                         .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<News, ListViewHolder>(response) {
            @NonNull
            @Override
            public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_notifications, parent, false);
                return new ListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ListViewHolder viewHolder, int position, @NonNull final News model) {
                final String post_key = getRef(position).getKey();
                viewHolder.tvTitle.setText(model.getNewsTitle());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent readIntent = new Intent(NewsListActivity.this, NewsEditActivity.class);
                        readIntent.putExtra(Constants.POST_KEY,post_key);
                        readIntent.putExtra(Constants.NEWS_TITLE,model.getNewsTitle());
                        readIntent.putExtra(Constants.NEWS,model.getNews());
                        startActivity(readIntent);
                    }
                });
            }
        };
        mNewsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_OK,getIntent());
        finish();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvTitle;
        TextView tvAuthor;
        View mView;

        public ListViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            this.mView = itemView;
        }
    }
}

