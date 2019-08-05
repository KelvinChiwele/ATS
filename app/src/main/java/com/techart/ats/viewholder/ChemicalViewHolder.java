package com.techart.ats.viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.techart.ats.R;

/**
 * ViewHolder for Comments & reviews
 * Created by kelvin on 2/12/18.
 */

public final class ChemicalViewHolder extends RecyclerView.ViewHolder {
    public TextView tvName;
    public TextView tvPackage;
    public TextView tvDescription;
    public Button btPests;
    public Button btReviews;
    public View mView;

    public ChemicalViewHolder(View itemView) {
        super(itemView);
        btPests = itemView.findViewById(R.id.bt_pests);
        tvPackage = itemView.findViewById(R.id.tv_packaging);
        btReviews = itemView.findViewById(R.id.bt_reviews);
        tvName = itemView.findViewById(R.id.tv_item);
        tvDescription = itemView.findViewById(R.id.tv_description);
        this.mView = itemView;
    }
}