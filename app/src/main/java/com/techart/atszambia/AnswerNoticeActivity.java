package com.techart.atszambia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.techart.atszambia.constants.Constants;
import com.techart.atszambia.constants.FireBaseUtils;
import com.techart.atszambia.models.Message;
import com.techart.atszambia.models.Question;
import com.techart.atszambia.utils.TimeUtils;
import com.techart.atszambia.utils.UploadUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class AnswerNoticeActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView rvAnswers;
    private EditText etAnswer;
    private Boolean isSent;
    private String product;
    private String imageUrl;
    private TextView tvEmpty;
    private CardView cvTypeAnswer;
    private ProgressBar progressBar;
    private static final int GALLERY_REQUEST = 1;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    private Uri uri;
    private StorageReference filePath;
    int questionNumber;


    private TextView tvCrop;
    private TextView tvAuthor;
    private TextView tvQuestion;
    private TextView tvTime;
    private ImageView iv_sample;

    private String postKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        String postAuthor = getIntent().getStringExtra(Constants.USER_NAME);
        postKey = getIntent().getStringExtra(Constants.POST_KEY);
        setTitle(postAuthor);
        tvEmpty = findViewById(R.id.tv_empty);
        progressBar = findViewById(R.id.pb_loading);
        tvCrop = findViewById(R.id.tv_crop);
        tvAuthor = findViewById(R.id.tv_author);
        tvQuestion = findViewById(R.id.tv_question);
        tvTime = findViewById(R.id.tv_time);
        iv_sample = findViewById(R.id.iv_sample);
        Button btAnswers = findViewById(R.id.bt_answers);
        initQuesstion();
        rvAnswers = findViewById(R.id.rv_answers);
        rvAnswers.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvAnswers.setLayoutManager(linearLayoutManager);
        init();
    }

    private void initQuesstion() {
        FireBaseUtils.mDatabaseQuestions.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Question question = dataSnapshot.getValue(Question.class);
                if (question != null) {
                    product = question.getCrop();
                    imageUrl = question.getImageUrl();
                    tvCrop.setText(question.getCrop());
                    tvAuthor.setText(question.getUserName());
                    if (question.getTimeCreated() != null) {
                        String time = TimeUtils.timeElapsed(question.getTimeCreated());
                        tvTime.setText(time);
                    }

                    initAnswers();
                    if (question.getAnswerCount() > 0) {
                        initAnswers();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                    }

                    tvQuestion.setText(getResources().getString(R.string.question, question.getQuestionNumber(), question.getQuestion()));
                    if (question.getImageUrl() != null) {
                        Glide.with(AnswerNoticeActivity.this)
                                .load(question.getImageUrl())
                                .into(iv_sample);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initAnswers() {
        FirebaseRecyclerOptions<Message> response = new FirebaseRecyclerOptions.Builder<Message>()
                                                            .setQuery(FireBaseUtils.mDatabaseAnswers.child(postKey), Message.class)
                                                            .build();
        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, AnswersActivity.AnswerHolder>(response) {
            @NonNull
            @Override
            public AnswersActivity.AnswerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_answer, parent, false);
                return new AnswersActivity.AnswerHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AnswersActivity.AnswerHolder viewHolder, int position, @NonNull final Message model) {
                progressBar.setVisibility(View.GONE);
                if (model.getMessage() != null) {
                    viewHolder.tvAnswer.setText(model.getMessage());
                    viewHolder.ivSample.setVisibility(View.GONE);
                } else {
                    viewHolder.setImage(getApplicationContext(), model.getImageUrl());
                    viewHolder.tvAnswer.setVisibility(View.GONE);
                }
                viewHolder.tvAnswer.setText(model.getMessage());
                viewHolder.tvAuthor.setText(model.getUserName());
                viewHolder.setStaffVisibility(model.getEmail(), AnswerNoticeActivity.this);
                if (model.getTimeCreated() != null){
                    viewHolder.tvTimeCreated.setText(TimeUtils.timeElapsed(model.getTimeCreated()));
                }
                if (model.getUserUrl() != null){
                    setVisibility(model.getUserUrl(), viewHolder);
                }
            }
        };
        rvAnswers.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    private void init() {
        etAnswer = findViewById(R.id.et_answer);
        cvTypeAnswer = findViewById(R.id.cvTypeAnswer);
        findViewById(R.id.iv_send).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_send:
                sendResponse();
            case R.id.iv_image:
                Intent intent = new Intent(AnswerNoticeActivity.this, ImageActivity.class);
                postKey = getIntent().getStringExtra(Constants.POST_KEY);
                startActivityForResult(intent, GALLERY_REQUEST);
        }
    }

    private void sendResponse() {
        final String message = etAnswer.getText().toString().trim();
        Boolean isSent = false;
        if (!message.isEmpty()) {
            final ProgressDialog progressDialog = new ProgressDialog(AnswerNoticeActivity.this);
            progressDialog.setMessage("Sending answer...");
            progressDialog.setCancelable(true);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
            if (!isSent) {
                DatabaseReference newComment = FireBaseUtils.mDatabaseAnswers.child(postKey).push();
                Map<String,Object> values = new HashMap<>();
                values.put(Constants.USER_URL, FireBaseUtils.getUiD());
                values.put(Constants.USER_NAME, FireBaseUtils.getAuthor());
                values.put(Constants.MESSAGE,message);
                values.put(Constants.PRODUCT, product);
                values.put(Constants.EMAIL, FireBaseUtils.getEmail());
                values.put(Constants.POST_KEY, postKey);
                values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
                newComment.setValue(values);
                isSent = true;
                progressDialog.dismiss();
                FireBaseUtils.onQuestionAnswered(postKey);
                FireBaseUtils.updateNotifications("FAQ", product, "responded to a question about " + product, postKey, message, imageUrl);
                etAnswer.setText("");
            }
        } else {
            Toast.makeText(this,"Nothing to send",Toast.LENGTH_LONG ).show();
        }
    }

    public void setVisibility(String url) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null && FireBaseUtils.isAllowed(FireBaseUtils.getEmail()) || FireBaseUtils.getUiD().equals(url)) {
            cvTypeAnswer.setVisibility(View.VISIBLE);
        }else{
            cvTypeAnswer.setVisibility(View.GONE);
        }
    }

    private void setVisibility(String url, AnswersActivity.AnswerHolder viewHolder) {
        if (FireBaseUtils.getUiD() != null && FireBaseUtils.getUiD().equals(url)){
            viewHolder.tvAnswer.setBackground(getResources().getDrawable(R.drawable.tv_circular_active_background));
        }
    }

    public void fullImage(View view) {
        if (imageUrl != null) {
            Intent intent = new Intent(AnswerNoticeActivity.this, FullImageActivity.class);
            intent.putExtra(Constants.IMAGE_URL, imageUrl);
            startActivity(intent);
        }
    }

    private void sendImage(String newComment, String imageUrl) {
        final ProgressDialog progressDialog = new ProgressDialog(AnswerNoticeActivity.this);
        progressDialog.setMessage("Sending answer...");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        Map<String, Object> values = new HashMap<>();
        values.put(Constants.USER_URL, FireBaseUtils.getUiD());
        values.put(Constants.USER_NAME, FireBaseUtils.getAuthor());
        values.put(Constants.IMAGE_URL, imageUrl);
        values.put(Constants.POST_KEY, postKey);
        values.put(Constants.PRODUCT, product);
        values.put(Constants.EMAIL, FireBaseUtils.getEmail());
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        FireBaseUtils.mDatabaseAnswers.child(postKey).child(newComment).setValue(values);
        progressDialog.dismiss();
        FireBaseUtils.onQuestionAnswered(postKey);
        FireBaseUtils.updateNotifications("FAQ", product, "uploaded an image on a question about " + product, postKey, "image uploaded", imageUrl);
    }

    /**
     * Uploads image to cloud storage
     */
    private void upload() {
        final String url = FireBaseUtils.mDatabaseAnswers.child(postKey).push().getKey();
        final ProgressDialog mProgress = new ProgressDialog(AnswerNoticeActivity.this);
        mProgress.setMessage("Uploading photo, please wait...");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
        filePath = FireBaseUtils.mStorageQuestions.child("A2Q" + questionNumber + "/" + url);
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        //uploading the image
        UploadTask uploadTask = filePath.putBytes(data);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    sendImage(url, task.getResult().toString());
                    mProgress.dismiss();
                } else {
                    // Handle failures
                    UploadUtils.makeNotification("Image upload failed", AnswerNoticeActivity.this);
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            uri = Uri.parse(data.getStringExtra(Constants.URI));
            postKey = getIntent().getStringExtra(Constants.POST_KEY);
            upload();
        }
    }
}
