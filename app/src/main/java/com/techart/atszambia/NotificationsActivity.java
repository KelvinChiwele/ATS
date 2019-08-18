package com.techart.atszambia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.techart.atszambia.constants.Constants;
import com.techart.atszambia.constants.FireBaseUtils;
import com.techart.atszambia.models.Notice;
import com.techart.atszambia.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Arrays;

import static com.techart.atszambia.constants.Constants.STAMP_KEY;

/**
 * Holds notification and news fragments
 */
public class NotificationsActivity extends AppCompatActivity {
    private RecyclerView rvNotice;
    private ArrayList<String> contents;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private ProgressBar progressBarNotifications;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_activity);
        rvNotice = findViewById(R.id.rv_news);
        rvNotice.setHasFixedSize(true);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int lastAccessedPage = getIntent().getIntExtra(Constants.STAMP_KEY, 0);
        setTimeAccessed(lastAccessedPage);
        contents = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.chemical)));
        progressBarNotifications = findViewById(R.id.pb_notifications);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvNotice.setLayoutManager(linearLayoutManager);
        bindView();
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    private void setTimeAccessed(int lastAccessedPage) {
        SharedPreferences mPref = getSharedPreferences(String.format("%s", getString(R.string.app_name)), MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt(STAMP_KEY,lastAccessedPage);
        editor.apply();
    }

    private void bindView() {
        FirebaseRecyclerOptions<Notice> response = new FirebaseRecyclerOptions.Builder<Notice>()
                                                         .setQuery(FireBaseUtils.mDatabaseNotifications, Notice.class)
                                                         .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Notice, NoticeViewHolder>(response) {
            @NonNull
            @Override
            public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_notifications, parent, false);
                return new NoticeViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull NoticeViewHolder viewHolder, int position, @NonNull final Notice model) {
                progressBarNotifications.setVisibility(View.GONE);
                viewHolder.makePortionBold(getAuthor(model) + " " + model.getAction(), getAuthor(model));
                if (model.getTimeCreated() != null) {
                    String time = TimeUtils.timeElapsed( model.getTimeCreated());
                    viewHolder.tvTime.setText(time);
                }
                if (model.getImageUrl() != null && !model.getImageUrl().equals("default")) {
                    viewHolder.setIvImage(NotificationsActivity.this, model.getImageUrl());
                } else if (model.getImageUrl() == null){
                    viewHolder.setIvImage(NotificationsActivity.this, R.drawable.placeholder);
                } else if (FireBaseUtils.staff.contains(model.getEmail())) {
                    viewHolder.setIvImage(NotificationsActivity.this, R.drawable.logo);
                }
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectIntent(model);
                    }
                });
            }
        };
        rvNotice.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    private String getAuthor(Notice notice) {
        return FireBaseUtils.isAllowed(notice.getEmail()) ? getString(R.string.staff) : notice.getUserName();
    }

    /**
     * Resolves activity to start
     *
     * @param notice name of category clicked
     */
    private void selectIntent(Notice notice) {
        Intent readPoemIntent;
        if (notice.getCategory().equals("FAQ")) {
            readPoemIntent = new Intent(NotificationsActivity.this, AnswerNoticeActivity.class);
            readPoemIntent.putExtra(Constants.POST_KEY, notice.getPostKey());
            readPoemIntent.putExtra(Constants.USER_NAME, notice.getUserName());
            readPoemIntent.putExtra(Constants.CATEGORY, notice.getCategory());
            startActivity(readPoemIntent);
        } else if (notice.getCategory().equals("News")) {
            readPoemIntent = new Intent(NotificationsActivity.this, NewsNoticeActivity.class);
            readPoemIntent.putExtra(Constants.POST_KEY, notice.getPostKey());
            readPoemIntent.putExtra(Constants.NEWS_TITLE, notice.getProduct());
            startActivity(readPoemIntent);
        } else if (contents.contains(notice.getCategory())) {
            readPoemIntent = new Intent(NotificationsActivity.this, ReviewActivity.class);
            readPoemIntent.putExtra(Constants.POST_KEY, notice.getPostKey());
            readPoemIntent.putExtra(Constants.USER_NAME, notice.getUserName());
            readPoemIntent.putExtra(Constants.NAME, notice.getProduct());
            readPoemIntent.putExtra(Constants.CATEGORY, notice.getCategory());
            startActivity(readPoemIntent);
        } else {
            Toast.makeText(NotificationsActivity.this, "Could not open new screen", Toast.LENGTH_LONG).show();
        }
    }


    public static class NoticeViewHolder extends RecyclerView.ViewHolder {
        TextView tvNotice;
        public ImageView ivImage;
        TextView tvTime;
        View mView;

        public NoticeViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvNotice = itemView.findViewById(R.id.tv_notifications);
            ivImage = itemView.findViewById(R.id.iv_disease);
            this.mView = itemView;
        }

        public void setIvImage(Context context, String image)  {
            Glide.with(context)
                    .load(image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivImage);
        }

        public void setIvImage(Context context, int image) {
            Glide.with(context)
                    .load(image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivImage);
        }

        void makePortionBold(String text, String spanText) {
            StyleSpan boldStyle = new StyleSpan(Typeface.BOLD);
            SpannableStringBuilder sb = new SpannableStringBuilder(text);
            int start = text.indexOf(spanText);
            int end = start + spanText.length();
            sb.setSpan(boldStyle, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            tvNotice.setText(sb);
        }
    }
}
