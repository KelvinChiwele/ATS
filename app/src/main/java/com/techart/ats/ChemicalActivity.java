package com.techart.ats;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.techart.ats.constants.Constants;
import com.techart.ats.constants.FireBaseUtils;
import com.techart.ats.models.Chemical;
import com.techart.ats.utils.NumberUtils;
import com.techart.ats.viewholder.ChemicalViewHolder;

public class ChemicalActivity extends AppCompatActivity  {
    private RecyclerView mQuestionList;
    private ProgressBar progressBar;

    private String category;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chemical);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView tvLabel = findViewById(R.id.tv_label);

        category = getIntent().getStringExtra(Constants.CATEGORY);
        tvLabel.setText(category);
        progressBar = findViewById(R.id.pb_loading);


      //  layoutEmpty.setVisibility(View.GONE);
        mQuestionList = findViewById(R.id.review_recyclerview);
        mQuestionList.setHasFixedSize(true);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new GridLayoutManager(this, 2);
        mQuestionList.setLayoutManager(recyclerViewLayoutManager);

        // Setup spinner
        /*
        Query chemicalQuery = FireBaseUtils.mDatabaseChemicals.child(category);
        final String[] crops = getResources().getStringArray(R.array.crops);
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> pagesAdapter = new ArrayAdapter<>(ChemicalActivity.this, R.layout.tv_filter, crops);
        pagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pagesAdapter.notifyDataSetChanged();
        spinner.setAdapter(pagesAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // When the given dropdown item is selected, show its contents in the
                if (position != 0) {
                    filter = crops[position];
                    chemicalQuery = FireBaseUtils.mDatabaseChemicals.child(category).orderByChild(Constants.NAME);
                    bindView();
                } else {
                    bind();
                }
              //  bindView();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
        bindView();
    }

    /**
     * Displays chemicals on load
     */
    private void bindView() {
        FirebaseRecyclerOptions<Chemical> response = new FirebaseRecyclerOptions.Builder<Chemical>()
                                                            .setQuery(FireBaseUtils.mDatabaseChemicals.child(category).orderByChild(Constants.NAME), Chemical.class)
                                                            .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Chemical, ChemicalViewHolder>(response) {
            @NonNull
            @Override
            public ChemicalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_row, parent, false);
                return new ChemicalViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ChemicalViewHolder viewHolder, int position, @NonNull final Chemical model) {
                final String post_key = getRef(position).getKey();
                progressBar.setVisibility(View.GONE);
                viewHolder.tvName.setText(model.getName());
                if (model.getPackaging() != null){
                    viewHolder.tvPackage.setText(model.getPackaging());
                } else {
                    viewHolder.tvPackage.setText("Consult");
                }
                viewHolder.tvDescription.setText(model.getDescription());
                viewHolder.btReviews.setText(getResources().getString(R.string.reviews, NumberUtils.setPlurality(model.getNumReviews(),"review")));
                viewHolder.btPests.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent diseaseIntent = new Intent(ChemicalActivity.this,PestsActivity.class);
                        startActivity(diseaseIntent);
                    }
                });

                viewHolder.btReviews.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent reviewIntent = new Intent(ChemicalActivity.this,ReviewActivity.class);
                        reviewIntent.putExtra(Constants.POST_KEY,post_key);
                        reviewIntent.putExtra(Constants.NAME,model.getName());
                        reviewIntent.putExtra(Constants.CATEGORY,category);
                        reviewIntent.putExtra(Constants.COUNT,model.getNumReviews().intValue());
                        startActivity(reviewIntent);
                    }
                });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        };
        mQuestionList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if (resultCode == RESULT_OK){
            category = data.getStringExtra(Constants.CATEGORY);
        }
    }
}
