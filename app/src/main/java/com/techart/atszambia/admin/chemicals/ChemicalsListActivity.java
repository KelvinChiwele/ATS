package com.techart.atszambia.admin.chemicals;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.techart.atszambia.R;
import com.techart.atszambia.ReviewActivity;
import com.techart.atszambia.constants.Constants;
import com.techart.atszambia.constants.FireBaseUtils;
import com.techart.atszambia.models.Chemical;

import java.util.List;

/**
 * Displays list of chemicals
 */
public class ChemicalsListActivity extends AppCompatActivity {

    private String category;
    private RecyclerView rvChemicals;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Setup spinner
        Button btList = findViewById(R.id.more);
        btList.setText("Chemicals");
        Spinner spinner = findViewById(R.id.spinner);

        rvChemicals = findViewById(R.id.rv_directory);
        rvChemicals.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvChemicals.setLayoutManager(linearLayoutManager);
        final String[] crops = getResources().getStringArray(R.array.chemicals);

        ArrayAdapter<String> pagesAdapter = new ArrayAdapter<>(ChemicalsListActivity.this, R.layout.tv_filter, crops);
        pagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pagesAdapter.notifyDataSetChanged();
        spinner.setAdapter(pagesAdapter);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // When the given dropdown item is selected, show its contents in the
                if (position != 0) {
                    category = crops[position];
                    bindView();
                } else {
                    category = "Adjuvants";
                }
                bindView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    /**
     * Displays chemicals on load
     */
    private void bindView() {
        FirebaseRecyclerOptions<Chemical> response = new FirebaseRecyclerOptions.Builder<Chemical>()
                                                            .setQuery(FireBaseUtils.mDatabaseChemicals
                                                                              .child(category)
                                                                              .orderByChild(Constants.NAME), Chemical.class)
                                                            .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Chemical, ViewHolder>(response) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_chemical_edit, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, @NonNull final Chemical model) {
                final String post_key = getRef(position).getKey();
                viewHolder.tvName.setText(model.getName());
                if (model.getCrops() != null){
                    bind(model.getCrops(), viewHolder.lvCrops);
                }
                viewHolder.tvName.setText(model.getName());
                viewHolder.tvDescription.setText(model.getDescription());

                viewHolder.btReviews.setText("Edit");
                viewHolder.btReviews.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent reviewIntent = new Intent(ChemicalsListActivity.this, ReviewActivity.class);
                        reviewIntent.putExtra(Constants.POST_KEY, post_key);
                        reviewIntent.putExtra(Constants.NAME, model.getName());
                        reviewIntent.putExtra(Constants.CATEGORY, category);
                        //startActivity(reviewIntent);
                    }
                });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        };
        rvChemicals.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvDescription;
        Button btReviews;
        ListView lvCrops;
        View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            btReviews = itemView.findViewById(R.id.bt_reviews);
            tvName = itemView.findViewById(R.id.tv_item);
            lvCrops = itemView.findViewById(R.id.lv_crops);
            tvDescription = itemView.findViewById(R.id.tv_description);


            this.mView = itemView;
        }
    }
    ////

    private void bind(List<String> crops, ListView listView) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, crops);
        listView.setAdapter(adapter);
    }
}
