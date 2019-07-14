package com.techart.atszambia.disease;

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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.techart.atszambia.R;
import com.techart.atszambia.constants.Constants;
import com.techart.atszambia.constants.FireBaseUtils;
import com.techart.atszambia.models.Disease;
import com.techart.atszambia.utils.NumberUtils;
import com.techart.atszambia.viewholder.DiseaseViewHolder;

/**
 * Will be available to admins for postings articles
 */
public class DiseaseListActivity extends AppCompatActivity {
    private RecyclerView rvBacterial;
    private RecyclerView rvFungal;
    private RecyclerView rvPhysiological;

    private String caseStudyUrl;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String crop = getIntent().getStringExtra(Constants.CROP_NAME);
        setTitle(crop);

        rvBacterial = findViewById(R.id.rv_bacterial);
        rvPhysiological = findViewById(R.id.rv_physiological);
        rvFungal = findViewById(R.id.rv_fungal);

        caseStudyUrl = getIntent().getStringExtra(Constants.CASE_STUDY_URL);
        int count = getIntent().getIntExtra(Constants.COUNT, 0);
        //Bacterial
        rvBacterial.setLayoutManager( new LinearLayoutManager( this,
                LinearLayoutManager.VERTICAL,
                false ) );
        //Fungal
        rvFungal.setLayoutManager( new LinearLayoutManager( this,
                LinearLayoutManager.VERTICAL,
                false ) );

        //Physiological
        rvPhysiological.setLayoutManager( new LinearLayoutManager( this,
                LinearLayoutManager.VERTICAL,
                false ) );
        bindBacterialDiseases();
        bindFungalDiseases();
        bindPhysiologicalDiseases();
    }

    private void bindBacterialDiseases() {
        FirebaseRecyclerOptions<Disease> response = new FirebaseRecyclerOptions.Builder<Disease>()
                                                             .setQuery(FireBaseUtils.mDatabaseDiseases.child(caseStudyUrl)
                                                                               .orderByChild(Constants.DISEASE_TYPE).equalTo("Bacterial"), Disease.class)
                                                             .build();
         firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Disease, DiseaseViewHolder>(response) {
            @NonNull
            @Override
            public DiseaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_diseases, parent, false);
                return new DiseaseViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DiseaseViewHolder viewHolder, int position, @NonNull final Disease model) {
                final String post_key = getRef(position).getKey();
//                progressBar.setVisibility(View.GONE);
                viewHolder.tvSymptoms.setText(model.getSymptoms());
                //viewHolder.tvCrop.setText(getResources().getString(R.string.disease_views,model.getName(),model.getNumViews()));
                viewHolder.tvCrop.setText(model.getName());
                if (model.getImageUrl() != null){
                    viewHolder.setIvImage(DiseaseListActivity.this,model.getImageUrl());
                }

                if (model.getNumViews() != null) {
                    String count = NumberUtils.shortenDigit(model.getNumViews());
                }

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent readPoemIntent = new Intent(DiseaseListActivity.this,DiseaseDetailActivity.class);
                        readPoemIntent.putExtra(Constants.NAME,model.getName());
                        readPoemIntent.putExtra(Constants.IMAGE_URL,model.getImageUrl());
                        readPoemIntent.putExtra("generalInformation",model.getGeneralInformation());
                        readPoemIntent.putExtra("symptoms",model.getSymptoms());
                        readPoemIntent.putExtra("management",model.getManagement());
                        startActivity(readPoemIntent);
                    }
                });
            }
        };
        rvFungal.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    private void bindFungalDiseases() {
        FirebaseRecyclerOptions<Disease> response = new FirebaseRecyclerOptions.Builder<Disease>()
                                                             .setQuery(FireBaseUtils.mDatabaseDiseases.child(caseStudyUrl)
                                                                               .orderByChild(Constants.DISEASE_TYPE).equalTo("Fungal"), Disease.class)
                                                             .build();
        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Disease, DiseaseViewHolder>(response) {
            @NonNull
            @Override
            public DiseaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_diseases, parent, false);
                return new DiseaseViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DiseaseViewHolder viewHolder, int position, @NonNull final Disease model) {
                final String post_key = getRef(position).getKey();
                // cv_fungal.setVisibility(View.VISIBLE);
                viewHolder.tvCrop.setText(getResources().getString(R.string.disease_views,model.getName(),model.getNumViews()));
                viewHolder.tvSymptoms.setText(model.getSymptoms());
                if (model.getImageUrl() != null){
                    viewHolder.setIvImage(DiseaseListActivity.this,model.getImageUrl());
                }

                if (model.getNumViews() != null) {
                    String count = NumberUtils.shortenDigit(model.getNumViews());
                }

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent readPoemIntent = new Intent(DiseaseListActivity.this,DiseaseDetailActivity.class);
                        readPoemIntent.putExtra(Constants.IMAGE_URL,model.getImageUrl());
                        readPoemIntent.putExtra("generalInformation",model.getGeneralInformation());
                        readPoemIntent.putExtra("symptoms",model.getSymptoms());
                        readPoemIntent.putExtra("management",model.getManagement());
                        startActivity(readPoemIntent);
                    }
                });

            }
        };
        rvFungal.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    private void bindPhysiologicalDiseases() {
        FirebaseRecyclerOptions<Disease> response = new FirebaseRecyclerOptions.Builder<Disease>()
                                                             .setQuery(FireBaseUtils.mDatabaseDiseases.child(caseStudyUrl)
                                                                               .orderByChild(Constants.DISEASE_TYPE).equalTo("Physiological"), Disease.class)
                                                             .build();
        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Disease, DiseaseViewHolder>(response) {
            @NonNull
            @Override
            public DiseaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_diseases, parent, false);
                return new DiseaseViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DiseaseViewHolder viewHolder, int position, @NonNull final Disease model) {
                viewHolder.tvSymptoms.setText(model.getSymptoms());
                viewHolder.tvCrop.setText(getResources().getString(R.string.disease_views,model.getName(),model.getNumViews()));
                if (model.getImageUrl() != null){
                    viewHolder.setIvImage(DiseaseListActivity.this,model.getImageUrl());
                }

                if (model.getNumViews() != null) {
                    String count = NumberUtils.shortenDigit(model.getNumViews());
                }

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent readPoemIntent = new Intent(DiseaseListActivity.this,DiseaseDetailActivity.class);
                        readPoemIntent.putExtra(Constants.IMAGE_URL,model.getImageUrl());
                        readPoemIntent.putExtra("generalInformation",model.getGeneralInformation());
                        readPoemIntent.putExtra("symptoms",model.getSymptoms());
                        readPoemIntent.putExtra("management",model.getManagement());
                        startActivity(readPoemIntent);
                    }
                });
            }
        };
        rvPhysiological.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }
}
