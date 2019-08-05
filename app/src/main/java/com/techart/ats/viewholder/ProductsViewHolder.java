package com.techart.ats.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.techart.ats.R;
import com.techart.ats.constants.FireBaseUtils;

/**
 * ViewHolder for Comments & reviews
 * Created by kelvin on 2/12/18.
 */

public final class ProductsViewHolder extends RecyclerView.ViewHolder {
    public TextView tvTitle;
    public TextView tvCount;
    public TextView tvViews;
    public TextView tvDescription;
    private ImageView ivImage;
    private ImageView ivView;
    public View mView;

    public ProductsViewHolder(View itemView) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.tv_title);
        tvCount = itemView.findViewById(R.id.tv_count);
        tvViews = itemView.findViewById(R.id.tv_views);
        ivView = itemView.findViewById(R.id.bt_views);
        tvDescription = itemView.findViewById(R.id.tv_description);
        ivImage = itemView.findViewById(R.id.iv_icon);
        this.mView = itemView;
    }

    public void setIvImage(Context context, int resourceValue) {
        Glide.with(context)
                .load(resourceValue)
                .into(ivImage);
    }
    public void setProductViewed(String category) {
        FireBaseUtils.setProductsViewed(category,ivView);
    }

    public void setResourceViewed(String category) {
        FireBaseUtils.setResourceViewed(category,ivView);
    }
}