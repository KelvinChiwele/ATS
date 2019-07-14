package com.techart.atszambia;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.techart.atszambia.constants.FireBaseUtils;
import com.techart.atszambia.models.Pest;

/**
 * Created by Kelvin on 30/07/2017.
 */

public class PestsActivity extends AppCompatActivity {
    private RecyclerView mPestList;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pest);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Pests");
        mPestList = findViewById(R.id.rv_directory);
        mPestList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mPestList.setLayoutManager(linearLayoutManager);
        bindView();
    }

    private void bindView() {
        FirebaseRecyclerOptions<Pest> response = new FirebaseRecyclerOptions.Builder<Pest>()
                                                         .setQuery(FireBaseUtils.mDatabasePests, Pest.class)
                                                         .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pest, ViewHolder>(response) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_notifications, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, @NonNull final Pest model) {
                viewHolder.setIvImage(getApplicationContext(),model.getImageUrl());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        };
        mPestList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvName;
        ImageView ivImage;
        View mView;
        public ViewHolder(View itemView) {
            super(itemView);

            this.mView = itemView;
        }


        public void setIvImage(Context context, String image)
        {
            ivImage = itemView.findViewById(R.id.iv_pest);
            Glide.with(context)
                    .load(image)
                    .into(ivImage);
        }
    }
}