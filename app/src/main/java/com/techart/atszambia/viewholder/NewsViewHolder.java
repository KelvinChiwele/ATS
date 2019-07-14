package com.techart.atszambia.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.techart.atszambia.R;
import com.techart.atszambia.constants.FireBaseUtils;

public final class NewsViewHolder extends RecyclerView.ViewHolder {
    public TextView post_title;
    public ImageView message;
    public TextView numComments;
    public TextView tvNumViews;
    public ProgressBar progressBar;
    public TextView timeTextView;
    public View mView;
    public ImageButton btnComment;
    public ImageButton btnViews;

    public NewsViewHolder(View itemView) {
        super(itemView);


        post_title = itemView.findViewById(R.id.tv_heading);
        timeTextView = itemView.findViewById(R.id.tv_time);
        message = itemView.findViewById(R.id.iv_news);
        progressBar = itemView.findViewById(R.id.pb_news);
        btnComment = itemView.findViewById(R.id.commentBtn);
        tvNumViews = itemView.findViewById(R.id.tv_numviews);
        btnViews = itemView.findViewById(R.id.bt_views);
        numComments = itemView.findViewById(R.id.tv_comments);
        this.mView = itemView;
    }

    public void setPostViewed(String post_key) {
        FireBaseUtils.setPostViewed(post_key,btnViews);
    }

    public void setIvImage(Context context, String image) {
        message.setColorFilter(ContextCompat.getColor(context, R.color.imageOverlay));
        Glide.with(context)
                .load(image)
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
                .into(message);
    }
}
