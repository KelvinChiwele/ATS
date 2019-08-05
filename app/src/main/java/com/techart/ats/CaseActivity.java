package com.techart.ats;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.techart.ats.constants.Constants;
import com.techart.ats.constants.FireBaseUtils;
import com.techart.ats.disease.DiseaseListActivity;
import com.techart.ats.models.Disease;
import com.techart.ats.viewholder.DiseaseViewHolder;

public class CaseActivity extends AppCompatActivity  {
    private RecyclerView rvDisease;
    private ProgressBar progressBar;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_grid);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Disease");
        progressBar = findViewById(R.id.pb_loading);

        rvDisease = findViewById(R.id.rv_disease);
        rvDisease.setHasFixedSize(true);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new GridLayoutManager(this, 2);
        rvDisease.setLayoutManager(recyclerViewLayoutManager);
        bindDiseases();
    }

    /**
     * Binds view to the recycler view
     */
    private void bindDiseases() {
        FirebaseRecyclerOptions<Disease> response = new FirebaseRecyclerOptions.Builder<Disease>()
                                                             .setQuery(FireBaseUtils.mDatabaseCaseStudy, Disease.class)
                                                             .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Disease, DiseaseViewHolder>(response) {
            @NonNull
            @Override
            public DiseaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_disease_grid, parent, false);
                return new DiseaseViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DiseaseViewHolder viewHolder, int position, @NonNull final Disease model) {
                final String post_key = getRef(position).getKey();
                progressBar.setVisibility(View.GONE);
                if (model.getImageUrl() != null){
                    viewHolder.setIvImage(CaseActivity.this, model.getImageUrl());
                }
                viewHolder.tvCrop.setText(model.getName());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CaseActivity.this,  DiseaseListActivity.class);
                        intent.putExtra(Constants.CASE_STUDY_URL,post_key);
                        intent.putExtra(Constants.CROP_NAME,model.getName());
                        startActivity(intent);
                    }
                });
            }
        };
        rvDisease.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }
}
