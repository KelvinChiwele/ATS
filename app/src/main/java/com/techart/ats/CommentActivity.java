package com.techart.ats;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.techart.ats.constants.Constants;
import com.techart.ats.constants.FireBaseUtils;
import com.techart.ats.models.ImageUrl;
import com.techart.ats.models.Message;
import com.techart.ats.models.News;
import com.techart.ats.utils.TimeUtils;
import com.techart.ats.viewholder.CommentHolder;

import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {
    private RecyclerView mCommentList;

    private EditText mEtComment;
    private String post_key;
    private Boolean isSent;
    private TextView tvEmpty;
    private int count;
    private ProgressBar progressBar;
    String postName;
    String imageUrl;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        post_key = getIntent().getStringExtra(Constants.POST_KEY);
        postName = getIntent().getStringExtra(Constants.POST_TITLE);
        setTitle("Comments on "+ postName);
        tvEmpty = findViewById(R.id.tv_empty);
        progressBar = findViewById(R.id.pb_loading);
        mCommentList = findViewById(R.id.comment_recyclerview);
        count = getIntent().getIntExtra(Constants.COUNT, 0);
        mCommentList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mCommentList.setLayoutManager(linearLayoutManager);
        if (count == 0){
            progressBar.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
        init();
        initCommentSection();
    }

    private void initCommentSection() {
        FirebaseRecyclerOptions<Message> response = new FirebaseRecyclerOptions.Builder<Message>()
                                                                .setQuery(FireBaseUtils.mDatabaseComments.child(post_key), Message.class)
                                                                .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, CommentHolder>(response) {
            @NonNull
            @Override
            public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_comment, parent, false);
                return new CommentHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CommentHolder viewHolder, int position, @NonNull final Message model) {
                if (count != 0){
                    progressBar.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                }

                viewHolder.commentTextView.setText(model.getMessage());
                viewHolder.authorTextView.setText(model.getUserName());
                viewHolder.setStaffVisibility(model.getEmail(), CommentActivity.this);
                if (model.getTimeCreated() != null){
                    viewHolder.timeTextView.setText(TimeUtils.timeElapsed(model.getTimeCreated()));
                }
                if (model.getUserUrl() != null) {
                    setVisibility(model.getUserUrl(), viewHolder);
                }
            }
        };
        mCommentList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    private void setVisibility(String url, CommentHolder viewHolder) {
        if (FireBaseUtils.getUiD() != null && FireBaseUtils.getUiD().equals(url)) {
            viewHolder.commentTextView.setBackground(getResources().getDrawable(R.drawable.tv_circular_active_background));
        }
    }

    private void init() {
        mEtComment = findViewById(R.id.et_comment);
    }

    private void sendComment() {
        final String comment = mEtComment.getText().toString().trim();
        isSent = false;
        if (canSend(comment)) {
            FireBaseUtils.mDatabaseComments.child(post_key)
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!isSent) {
                        imageUrl = ImageUrl.getInstance().getImageUrl();
                        DatabaseReference newComment = FireBaseUtils.mDatabaseComments.child(post_key).push();
                        Map<String,Object> values = new HashMap<>();
                        values.put(Constants.USER_URL, FireBaseUtils.getUiD());
                        values.put(Constants.USER_NAME, FireBaseUtils.getAuthor());
                        values.put(Constants.CATEGORY, "News");
                        values.put(Constants.EMAIL, FireBaseUtils.getEmail());
                        values.put(Constants.MESSAGE,comment);
                        values.put(Constants.POST_KEY,post_key);
                        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
                        newComment.setValue(values);
                        isSent = true;
                        setCommentCount();
                        FireBaseUtils.updateNotifications("News", postName, "commented on " + postName, post_key, comment, imageUrl);
                        mEtComment.setText("");
                        //ToDo crushing on send. To be resolved
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private boolean haveNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netWorkInfo = cm.getActiveNetworkInfo();
            return netWorkInfo != null && netWorkInfo.getState() == NetworkInfo.State.CONNECTED;
        }
        return false;
    }

    private boolean canSend(String comment) {
        boolean valid = true;
        if (comment.isEmpty()) {
            Toast.makeText(this, "Nothing to send", Toast.LENGTH_LONG).show();
            valid = false;
        } else if (!haveNetworkConnection() && count == 0) {
            Toast.makeText(this, "Switch on data or wifi", Toast.LENGTH_LONG).show();
            valid = false;
        }
        return valid;
    }


    private void setCommentCount() {
        FireBaseUtils.mDatabaseNews.child(post_key).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                News news = mutableData.getValue(News.class);
                if (news == null) {
                    return Transaction.success(mutableData);
                }
                news.setNumComments(news.getNumComments() + 1 );
                // Set value and report transaction success
                mutableData.setValue(news);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }

    public void sendComment(View view) {
        sendComment();
    }
}
