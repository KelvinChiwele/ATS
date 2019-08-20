package com.techart.atszambia.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.techart.atszambia.R;

public class ChemicalPostViewHolder extends RecyclerView.ViewHolder {
    public TextView btPests;
    public View mView;

    public ChemicalPostViewHolder(View itemView) {
        super(itemView);
        btPests = itemView.findViewById(R.id.bt_crop);
        this.mView = itemView;
    }
}