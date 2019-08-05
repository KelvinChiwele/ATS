package com.techart.ats.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.techart.ats.R;

public class ChemicalPostViewHolder extends RecyclerView.ViewHolder {
    public TextView btPests;
    public View mView;

    public ChemicalPostViewHolder(View itemView) {
        super(itemView);
        btPests = itemView.findViewById(R.id.bt_crop);
        this.mView = itemView;
    }
}