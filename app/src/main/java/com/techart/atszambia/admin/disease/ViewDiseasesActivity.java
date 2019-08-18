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

public class ViewDiseasesActivity extends AppCompatActivity {
    private RecyclerView mNewsList;
    private String postUrl;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newslist);
        setTitle("Click to edit");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        postUrl = getIntent().getStringExtra(Constants.CASE_STUDY_URL);
        mNewsList = findViewById(R.id.rv_news);
        mNewsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewDiseasesActivity.this);
        mNewsList.setLayoutManager(linearLayoutManager);
        bindView();
    }

    private void bindView() {
        FirebaseRecyclerOptions<Disease> response = new FirebaseRecyclerOptions.Builder<Disease>()
                                                         .setQuery(FireBaseUtils.mDatabaseDiseases.child(postUrl), Disease.class)
                                                         .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Disease, ListViewHolder>(response) {
            @NonNull
            @Override
            public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_notifications, parent, false);
                return new ListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ListViewHolder viewHolder, int position, @NonNull final Disease model) {
                final String post_key = getRef(position).getKey();
                viewHolder.tvTitle.setText(model.getName());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent readIntent = new Intent(ViewDiseasesActivity.this, DiseaseEditActivity.class);
                        readIntent.putExtra(Constants.POST_KEY,post_key);
                        readIntent.putExtra(Constants.MANAGEMENT,model.getManagement());
                        readIntent.putExtra(Constants.SYMPTOMS,model.getSymptoms());
                        readIntent.putExtra(Constants.GENERAL_INFORMATION,model.getManagement());
                        readIntent.putExtra(Constants.CASE_STUDY_URL,postUrl);
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

