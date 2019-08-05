package com.techart.ats.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.techart.ats.R;
import com.techart.ats.constants.FireBaseUtils;


/**
 * ViewHolder for Comments & reviews
 * Created by kelvin on 2/12/18.
 */

public final class CommentHolder extends RecyclerView.ViewHolder {
    public TextView authorTextView;
    public TextView commentTextView;
    public TextView timeTextView;
    public TextView tvStaff;
    public TextView tvMark;

    public CommentHolder(View itemView) {
        super(itemView);
        authorTextView = itemView.findViewById(R.id.tv_author);
        tvStaff = itemView.findViewById(R.id.tv_staff);
        timeTextView = itemView.findViewById(R.id.tv_time);
        commentTextView = itemView.findViewById(R.id.tv_message);
        tvMark = itemView.findViewById(R.id.tv_mark);
    }

    public void setStaffVisibility(String email, Context context) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null &&
                FireBaseUtils.isAllowed(FireBaseUtils.getEmail()) &&
                FireBaseUtils.isAllowed(email)) {
            authorTextView.setAllCaps(false);
            authorTextView.setTextColor(context.getResources().getColor(R.color.colorAccent));
            authorTextView.setTextSize(12);
        } else if (FirebaseAuth.getInstance().getCurrentUser() != null &&
                !FireBaseUtils.isAllowed(FireBaseUtils.getEmail()) &&
                FireBaseUtils.isAllowed(email)) {
            authorTextView.setVisibility(View.GONE);
            tvMark.setVisibility(View.GONE);
        } else {
            tvStaff.setVisibility(View.GONE);
            tvMark.setVisibility(View.GONE);
        }
    }
}