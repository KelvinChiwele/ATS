package com.techart.atszambia;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.techart.atszambia.constants.Constants;
import com.techart.atszambia.constants.FireBaseUtils;
import com.techart.atszambia.models.Message;
import com.techart.atszambia.utils.TimeUtils;
import com.techart.atszambia.utils.UploadUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AnswersActivity extends AppCompatActivity implements View.OnClickListener  {
    //IU components
    private RecyclerView rvComment;
    private EditText etAnswer;
    private CardView cvTypeAnswer;
    private static final int GALLERY_REQUEST = 1;
    private String postKey;
    private String imageUrl;
    private String userUrl;
    private String product;
    private ProgressBar progressBar;
    private LinearLayout tvEmpty;
    private Uri uri;
    private int count;
    private StorageReference filePath;
    private int questionNumber;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        postKey = getIntent().getStringExtra(Constants.POST_KEY);
        imageUrl = getIntent().getStringExtra(Constants.IMAGE_URL);
        product = getIntent().getStringExtra(Constants.PRODUCT);
        userUrl = getIntent().getStringExtra(Constants.USER_URL);
        questionNumber = getIntent().getIntExtra(Constants.QUESTION_NUMBER, 0);
        count = getIntent().getIntExtra(Constants.ANSWER_COUNT, 0);

        rvComment = findViewById(R.id.answers_recycler_view);
        progressBar = findViewById(R.id.pb_loading);
        tvEmpty = findViewById(R.id.rv_empty);
         Button btAsk = findViewById(R.id.bt_ask);

        setTitle("Answers to question " + questionNumber);
        rvComment.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvComment.setLayoutManager(linearLayoutManager);
        if (count == 0){
            progressBar.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }

        btAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent directoryIntent = new Intent(AnswersActivity.this,DirectoryActivity.class);
                startActivity(directoryIntent);
            }
        });
        init();
        initAnswers();
    }

    private void initAnswers() {
        FirebaseRecyclerOptions<Message> response = new FirebaseRecyclerOptions.Builder<Message>()
                                                            .setQuery(FireBaseUtils.mDatabaseAnswers.child(postKey), Message.class)
                                                            .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, AnswerHolder>(response) {
            @NonNull
            @Override
            public AnswerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_answer, parent, false);
                return new AnswerHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AnswerHolder viewHolder, int position, @NonNull final Message model) {
                if (count != 0){
                    progressBar.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                }
                if (model.getMessage() != null){
                    viewHolder.tvAnswer.setText(model.getMessage());
                    viewHolder.ivSample.setVisibility(View.GONE);
                } else {
                    viewHolder.setImage(getApplicationContext(),model.getImageUrl());
                    viewHolder.tvAnswer.setVisibility(View.GONE);
                }

                viewHolder.tvAuthor.setText(model.getUserName());
                viewHolder.setStaffVisibility(model.getEmail(), AnswersActivity.this);
                if (model.getUserUrl() != null) {
                    setVisibility(model.getUserUrl(), viewHolder);
                }

                if (model.getTimeCreated() != null){
                    viewHolder.tvTimeCreated.setText(TimeUtils.timeElapsed(model.getTimeCreated()));
                }

                viewHolder.ivSample.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (model.getImageUrl() != null){
                            Intent intent = new Intent(AnswersActivity.this,FullImageActivity.class);
                            intent.putExtra(Constants.IMAGE_URL,model.getImageUrl());
                            startActivity(intent);
                        }
                    }
                });
            }
        };
        rvComment.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    private void setVisibility(String url, AnswersActivity.AnswerHolder viewHolder) {
        if (FireBaseUtils.getUiD() != null && FireBaseUtils.getUiD().equals(url)) {
            viewHolder.tvAnswer.setBackground(getResources().getDrawable(R.drawable.tv_circular_active_background));
        }
    }

    private void init() {
        etAnswer = findViewById(R.id.et_comment);
        cvTypeAnswer = findViewById(R.id.cvTypeAnswer);
        setVisibility(userUrl);
        findViewById(R.id.iv_send).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_send:
                sendResponse();
                break;
            case R.id.iv_image:
                Intent intent = new Intent(AnswersActivity.this,ImageActivity.class);
                postKey = getIntent().getStringExtra(Constants.POST_KEY);
                startActivityForResult(intent,GALLERY_REQUEST);
                break;
        }
    }

    private void sendResponse() {
        final String message = etAnswer.getText().toString().trim();
        Boolean isSent = false;
        if (!message.isEmpty()) {
            final ProgressDialog progressDialog = new ProgressDialog(AnswersActivity.this);
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
                values.put(Constants.POST_KEY, postKey);
                values.put(Constants.EMAIL, FireBaseUtils.getEmail());
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

    private void sendImage(String newComment,String imageUrl) {
        final ProgressDialog progressDialog = new ProgressDialog(AnswersActivity.this);
        progressDialog.setMessage("Sending answer...");
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        Map<String,Object> values = new HashMap<>();
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

    public void setVisibility(String url) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null && FireBaseUtils.isAllowed(FireBaseUtils.getEmail()) || FireBaseUtils.getUiD().equals(url)){
            cvTypeAnswer.setVisibility(View.VISIBLE);
        }else{
            cvTypeAnswer.setVisibility(View.GONE);
            TextView tvNote = findViewById(R.id.tv_note);
            tvNote.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Uploads image to cloud storage
     */
    private void upload() {
        final String url = FireBaseUtils.mDatabaseAnswers.child(postKey).push().getKey();
        final ProgressDialog mProgress = new ProgressDialog(AnswersActivity.this);
        mProgress.setMessage("Uploading photo, please wait...");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
        filePath = FireBaseUtils.mStorageQuestions.child("A2Q"+ questionNumber + "/" + url);
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
                    sendImage(url,task.getResult().toString());
                    mProgress.dismiss();
                } else {
                    // Handle failures
                    UploadUtils.makeNotification("Image upload failed",AnswersActivity.this);
                }
            }
        });

    }


    public static class AnswerHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor;
        TextView tvStaff;
        LinearLayout linearLayout;
        TextView tvAnswer;
        ImageView ivSample;
        TextView tvTimeCreated;
        public TextView tvMark;

        public AnswerHolder(View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            linearLayout = itemView.findViewById(R.id.ll_answer);
            tvStaff = itemView.findViewById(R.id.tv_staff);
            tvTimeCreated = itemView.findViewById(R.id.tv_time);
            tvAnswer = itemView.findViewById(R.id.tv_answer);
            ivSample = itemView.findViewById(R.id.iv_sample);
            tvMark = itemView.findViewById(R.id.tv_mark);
        }

        public void setStaffVisibility(String email, Context context) {
            //if user is staff
            if (FirebaseAuth.getInstance().getCurrentUser() != null &&
                    FireBaseUtils.isAllowed(FireBaseUtils.getEmail()) && //if viewer is staff
                    FireBaseUtils.isAllowed(email)) {  //if written by staff
                //Show staff name
                tvAuthor.setTextColor(context.getResources().getColor(R.color.colorAccent));
                tvAuthor.setTextSize(12);
            } else if (FirebaseAuth.getInstance().getCurrentUser() != null &&
                    !FireBaseUtils.isAllowed(FireBaseUtils.getEmail()) && //viewer is not staff
                    FireBaseUtils.isAllowed(email)) {// written by staff
                //Do not show staff name
                tvAuthor.setVisibility(View.GONE);
                tvMark.setVisibility(View.GONE);
            } else {
                tvStaff.setVisibility(View.GONE);
                tvMark.setVisibility(View.GONE);
            }
        }
        public void setImage(Context context, String image) {
            Glide.with(context)
                    .load(image)
                    .into(ivSample);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            uri = Uri.parse(data.getStringExtra(Constants.URI));
            postKey = getIntent().getStringExtra(Constants.POST_KEY);
            upload();
        }
    }
}
