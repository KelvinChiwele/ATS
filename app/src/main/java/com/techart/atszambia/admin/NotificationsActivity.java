package com.techart.atszambia.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.techart.atszambia.R;
import com.techart.atszambia.constants.FireBaseUtils;
import com.techart.atszambia.models.Message;
import com.techart.atszambia.utils.TimeUtils;

/**
 * Holds notification and news fragments
 */
public class NotificationsActivity extends AppCompatActivity {
    private RecyclerView rvNotice;
    private TextView tvEmpty;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_activity);
        rvNotice = findViewById(R.id.rv_news);
        rvNotice.setHasFixedSize(true);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvEmpty = findViewById(R.id.tv_empty);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvNotice.setLayoutManager(linearLayoutManager);
        bindView();
    }

    private void bindView() {
        FirebaseRecyclerOptions<Message> response = new FirebaseRecyclerOptions.Builder<Message>()
                                                         .setQuery(FireBaseUtils.mDatabaseNotifications, Message.class)
                                                         .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, NoticeViewHolder>(response) {
            @NonNull
            @Override
            public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_notifications, parent, false);
                return new NoticeViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull NoticeViewHolder viewHolder, int position, @NonNull final Message model) {
                viewHolder.tvNotice.setText(model.getMessage());
                tvEmpty.setVisibility(View.GONE);
                if (model.getTimeCreated() != null) {
                    String time = TimeUtils.timeElapsed( model.getTimeCreated());
                    viewHolder.tvTime.setText(time);
                }
            }
        };
        rvNotice.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvNotice;
        TextView tvTime;
        View mView;

        public NoticeViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvNotice = itemView.findViewById(R.id.tv_notifications);
            this.mView = itemView;
        }
    }
}
