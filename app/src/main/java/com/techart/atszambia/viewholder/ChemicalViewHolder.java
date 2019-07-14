package com.techart.atszambia.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.techart.atszambia.R;

/**
 * ViewHolder for Comments & reviews
 * Created by kelvin on 2/12/18.
 */

public final class ChemicalViewHolder extends RecyclerView.ViewHolder {
    public TextView tvName;
    public TextView tvDescription;
    public Button btPests;
    public Button btReviews;
    public View mView;

    public ChemicalViewHolder(View itemView) {
        super(itemView);
        btPests = itemView.findViewById(R.id.bt_pests);
        btReviews = itemView.findViewById(R.id.bt_reviews);
        tvName = itemView.findViewById(R.id.tv_item);
        tvDescription = itemView.findViewById(R.id.tv_description);
        this.mView = itemView;
    }
}