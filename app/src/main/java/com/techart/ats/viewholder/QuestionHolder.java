package com.techart.ats.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.techart.ats.R;

public class QuestionHolder extends RecyclerView.ViewHolder {
    public TextView tvCrop;
    public TextView tvAuthor;
    public TextView tvQuestion;
    public TextView tvTime;
    public ImageView iv_sample;
    public Button btAnswers;

    public QuestionHolder(View itemView) {
        super(itemView);
        tvCrop = itemView.findViewById(R.id.tv_crop);
        tvAuthor = itemView.findViewById(R.id.tv_author);
        tvQuestion = itemView.findViewById(R.id.tv_question);
        tvTime = itemView.findViewById(R.id.tv_time);
        iv_sample = itemView.findViewById(R.id.iv_sample);
        btAnswers = itemView.findViewById(R.id.bt_answers);
    }

    public void setImage(Context context, String image) {
        Glide.with(context)
                .load(image)
                .into(iv_sample);
    }
}