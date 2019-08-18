package com.techart.atszambia;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.techart.atszambia.constants.Constants;
import com.techart.atszambia.constants.FireBaseUtils;
import com.techart.atszambia.models.Chemical;
import com.techart.atszambia.models.ImageUrl;
import com.techart.atszambia.models.Message;
import com.techart.atszambia.utils.TimeUtils;
import com.techart.atszambia.viewholder.CommentHolder;

import java.util.HashMap;
import java.util.Map;

public class ReviewActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mCommentList;
    private EditText mEtComment;
    private String post_key;
    private String category;
    private Boolean isSent;
    private TextView tvEmpty;
    private ProgressBar progressBar;
    private int count;
    private String product;
    private String imageUrl;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        post_key = getIntent().getStringExtra(Constants.POST_KEY);
        product = getIntent().getStringExtra(Constants.NAME);
        category = getIntent().getStringExtra(Constants.CATEGORY);
        setTitle("Reviews on " + product);

        mCommentList = findViewById(R.id.review_recyclerview);
        tvEmpty = findViewById(R.id.tv_empty);
        progressBar = findViewById(R.id.pb_loading);
        count = getIntent().getIntExtra(Constants.COUNT,0);
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
                                                         .setQuery(FireBaseUtils.mDatabaseReviews.child(category).child(post_key), Message.class)
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
                viewHolder.setStaffVisibility(model.getEmail(), ReviewActivity.this);
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
        findViewById(R.id.iv_send).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_send:
                sendComment();
        }
    }

    private void sendComment() {
        final String review = mEtComment.getText().toString().trim();
        isSent = false;
        if (canSend(review)) {
            FireBaseUtils.mDatabaseReviews.child(category).child(post_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!isSent){
                        imageUrl = ImageUrl.getInstance().getImageUrl();
                        DatabaseReference newComment = FireBaseUtils.mDatabaseReviews.child(category).child(post_key).push();
                        Map<String,Object> values = new HashMap<>();
                        values.put(Constants.USER_URL, FireBaseUtils.getUiD());
                        values.put(Constants.USER_NAME, FireBaseUtils.getAuthor());
                        values.put(Constants.CATEGORY,category);
                        values.put(Constants.MESSAGE,review);
                        values.put(Constants.EMAIL, FireBaseUtils.getEmail());
                        values.put(Constants.PRODUCT, product);
                        values.put(Constants.POST_KEY,post_key);
                        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
                        newComment.setValue(values);
                        isSent = true;
                        FireBaseUtils.onChemicalReviewed(post_key);
                        FireBaseUtils.updateNotifications(category, product, getAction() + product, post_key, review, imageUrl);
                        onReviewSent();
                        mEtComment.setText("");
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(this,"Nothing to send",Toast.LENGTH_LONG ).show();
        }
    }

    private String getAction() {
        if (FireBaseUtils.isAllowed(FireBaseUtils.getEmail())) {
            return " responded to a review on ";
        } else {
            return " reviewed ";
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

    private void onReviewSent() {
        FireBaseUtils.mDatabaseChemicals.child(category).child(post_key).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Chemical chemical = mutableData.getValue(Chemical.class);
                if (chemical == null) {
                    return Transaction.success(mutableData);
                }
                chemical.setNumReviews(chemical.getNumReviews() + 1 );
                // Set value and report transaction success
                mutableData.setValue(chemical);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                Intent data = new Intent();
                data.putExtra(Constants.CATEGORY,category);
                setResult(RESULT_OK);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
