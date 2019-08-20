package com.techart.atszambia.disease;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.techart.atszambia.CropProgramActivity;
import com.techart.atszambia.R;
import com.techart.atszambia.constants.Constants;

/**
 * Displays users private content. Such as
 * 1. Posted items
 * 2. Locally stored Articles
 * 3. Action such as changing and setting of dps
 */
public class DiseaseDetailActivity extends AppCompatActivity {
    private ImageView imProfilePicture;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String name = getIntent().getStringExtra(Constants.NAME);
        String generalInformation = getIntent().getStringExtra("generalInformation");
        String symptoms = getIntent().getStringExtra("symptoms");
        String management = getIntent().getStringExtra("management");
        String Crop = getIntent().getStringExtra(Constants.CROP);
        String currentPhotoUrl = getIntent().getStringExtra(Constants.IMAGE_URL);
        setTitle(name);
        imProfilePicture = findViewById(R.id.ib_profile);
        TextView tvGeneralInformation = findViewById(R.id.tv_general_information);
        progressBar = findViewById(R.id.pb_news);
        TextView tvSymptoms = findViewById(R.id.tv_symptoms);
        TextView tvManagement = findViewById(R.id.tv_management);
        Button btTreatment = findViewById(R.id.bt_treatment);
        if (generalInformation != null){
            tvGeneralInformation.setText(getResources().getString(R.string.generalInformation,generalInformation));
        } else {
            tvGeneralInformation.setVisibility(View.GONE);
        }
        if (management != null){
            tvManagement.setText(getResources().getString(R.string.management,management));
        } else {
            tvManagement.setVisibility(View.GONE);
        }
        if (symptoms != null){
            tvSymptoms.setText(getResources().getString(R.string.symptoms,symptoms));
        } else {
            tvSymptoms.setVisibility(View.GONE);
        }

        setPicture(currentPhotoUrl);

        //Handles on clicks which brings up a larger image than that displayed
        btTreatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cropPrograms = new Intent(DiseaseDetailActivity.this, CropProgramActivity.class);
                startActivity(cropPrograms);
            }
        });
    }

    private void setPicture(String currentPhotoUrl) {
        Glide.with(this)
                .load(currentPhotoUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imProfilePicture);
    }
}
