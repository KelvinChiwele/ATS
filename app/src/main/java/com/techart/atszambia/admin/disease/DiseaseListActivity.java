package com.techart.atszambia.admin.disease;

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
import com.techart.atszambia.R;
import com.techart.atszambia.constants.Constants;
import com.techart.atszambia.constants.FireBaseUtils;
import com.techart.atszambia.models.Disease;

public class DiseaseListActivity extends AppCompatActivity {
    private RecyclerView mNewsList;
    private String intentType;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newslist);
        setTitle("Click to edit");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        intentType = getIntent().getStringExtra(Constants.NAME);
        mNewsList = findViewById(R.id.rv_news);
        mNewsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DiseaseListActivity.this);
        mNewsList.setLayoutManager(linearLayoutManager);
        bindView();
    }

    private void bindView() {
        FirebaseRecyclerOptions<Disease> response = new FirebaseRecyclerOptions.Builder<Disease>()
                                                            .setQuery(FireBaseUtils.mDatabaseCaseStudy, Disease.class)
                                                            .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Disease, ViewDiseasesActivity.ListViewHolder>(response) {
            @NonNull
            @Override
            public ViewDiseasesActivity.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_notifications, parent, false);
                return new ViewDiseasesActivity.ListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewDiseasesActivity.ListViewHolder viewHolder, int position, @NonNull final Disease model) {
                final String post_key = getRef(position).getKey();
                viewHolder.tvTitle.setText(model.getName());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent readIntent;
                        if (intentType.equals("Add")){
                            readIntent = new Intent(DiseaseListActivity.this, DiseasePostActivity.class);
                        } else {
                            readIntent = new Intent(DiseaseListActivity.this, ViewDiseasesActivity.class);
                        }
                        readIntent.putExtra(Constants.CROP_NAME,model.getName());
                        readIntent.putExtra(Constants.CASE_STUDY_URL,post_key);
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

