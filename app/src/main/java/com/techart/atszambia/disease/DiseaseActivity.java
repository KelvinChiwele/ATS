package com.techart.atszambia.disease;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.techart.atszambia.R;
import com.techart.atszambia.constants.FireBaseUtils;
import com.techart.atszambia.models.Users;


public class DiseaseActivity extends AppCompatActivity {

    private ImageView imProfilePicture;
    private String currentPhotoUrl;
    private boolean isAttached;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_list);
            setTitle("ToDO");
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        loadProfilePicture();

        imProfilePicture = findViewById(R.id.ib_profile);

        RecyclerView rvReadingList = findViewById(R.id.rv_libraryBook);
        rvReadingList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DiseaseActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvReadingList.setLayoutManager(linearLayoutManager);

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttached = true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
    }

    private void loadProfilePicture(){
        FireBaseUtils.mDatabaseUsers.child(FireBaseUtils.getUiD()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                if (users.getImageUrl() != null && users.getImageUrl().length() > 7) {
                    currentPhotoUrl = users.getImageUrl();
                    setPicture(currentPhotoUrl);
                } else {
                    Toast.makeText(getBaseContext(),"No image found",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setPicture(String url) {
        RequestOptions options = new RequestOptions()
                .centerCrop();
        if (isAttached){
            Glide.with(this)
            .load(url)
            .apply(options)
            .into(imProfilePicture);
            imProfilePicture.setColorFilter(ContextCompat.getColor(this, R.color.colorTint));
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}