package com.techart.ats;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.techart.ats.constants.FireBaseUtils;
import com.techart.ats.models.CropProgram;

public class CropProgramActivity extends AppCompatActivity  {
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
        setTitle("Crop Programs");
        progressBar = findViewById(R.id.pb_loading);

        rvDisease = findViewById(R.id.rv_disease);
        rvDisease.setHasFixedSize(true);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new GridLayoutManager(this, 2);
        rvDisease.setLayoutManager(recyclerViewLayoutManager);
        bindDisases();
    }

    private void bindDisases() {
        FirebaseRecyclerOptions<CropProgram> response = new FirebaseRecyclerOptions.Builder<CropProgram>()
                                                             .setQuery(FireBaseUtils.mDatabasePrograms, CropProgram.class)
                                                             .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CropProgram, CropProgramHolder>(response) {
            @NonNull
            @Override
            public CropProgramHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_program, parent, false);
                return new CropProgramHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CropProgramHolder viewHolder, int position, @NonNull final CropProgram model) {
                progressBar.setVisibility(View.GONE);
                viewHolder.tvName.setText(model.getName());
                viewHolder.btPreview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getFbImageUrl()));
                        startActivity(browserIntent);
                    }
                });
                viewHolder.btDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse(model.getFileUrl()));
                        startActivity(browserIntent);
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

    public static class CropProgramHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        Button btPreview;
        Button btDownload;
        View mView;

        public CropProgramHolder(View itemView) {
            super(itemView);
            btPreview = itemView.findViewById(R.id.bt_preview);
            btDownload = itemView.findViewById(R.id.bt_download);
            tvName = itemView.findViewById(R.id.tv_item);
            this.mView = itemView;
        }
    }
}
