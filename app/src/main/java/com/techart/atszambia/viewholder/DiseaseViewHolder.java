package com.techart.atszambia.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.techart.atszambia.R;

/**
 * ViewHolder for Comments & reviews
 * Created by kelvin on 2/12/18.
 */

public final class DiseaseViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivImage;
    public TextView tvCrop;
    public TextView tvSymptoms;
    public View mView;
    public ProgressBar progressBar;

    public DiseaseViewHolder(View itemView) {
        super(itemView);
        ivImage = itemView.findViewById(R.id.iv_disease);
        tvCrop = itemView.findViewById(R.id.tvTitle);
        tvSymptoms = itemView.findViewById(R.id.tv_symptoms);
        progressBar = itemView.findViewById(R.id.pb_news);
        this.mView = itemView;
    }

    public void setIvImage(Context context, String image) {
        ivImage.setColorFilter(ContextCompat.getColor(context, R.color.imageOverlay));
        Glide.with(context)
                .load(image)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(ivImage);
    }
}