package com.techart.atszambia;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.techart.atszambia.constants.Constants;
import com.techart.atszambia.constants.FireBaseUtils;
import com.techart.atszambia.models.Question;
import com.techart.atszambia.utils.NumberUtils;
import com.techart.atszambia.utils.TimeUtils;
import com.techart.atszambia.viewholder.QuestionHolder;

public class QuestionActivity extends AppCompatActivity  {
    private RecyclerView mQuestionList;
    private String filter;
    private LinearLayout layoutEmpty;
    private ProgressBar progressBar;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView tvLabel = findViewById(R.id.tv_label);
        tvLabel.setText(R.string.questions);
        layoutEmpty = findViewById(R.id.rv_empty);
        progressBar = findViewById(R.id.pb_loading);


        layoutEmpty.setVisibility(View.GONE);
        mQuestionList = findViewById(R.id.review_recyclerview);
        mQuestionList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mQuestionList.setLayoutManager(linearLayoutManager);
        bindView();
        // Setup spinner
        //setupSpinner();
    }

    /*
    private void setupSpinner() {
        final String[] crops = getResources().getStringArray(R.array.crops);
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> pagesAdapter = new ArrayAdapter<>(QuestionActivity.this, R.layout.tv_filter, crops);
        pagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pagesAdapter.notifyDataSetChanged();
        spinner.setAdapter(pagesAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // When the given dropdown item is selected, show its contents in the
                if (position != 0) {
                    filter = crops[position];
                   // questionsQuery = FireBaseUtils.mDatabaseQuestions.orderByChild(Constants.CROP).equalTo(filter);
                } else {
                    //questionsQuery = FireBaseUtils.mDatabaseQuestions.orderByChild(Constants.TIME_CREATED);

                }
                bindView();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }*/

    public void setVisibility(Boolean isVisible) {
        if (isVisible) {
            layoutEmpty.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void bindView() {
        FirebaseRecyclerOptions<Question> response = new FirebaseRecyclerOptions.Builder<Question>()
                                                             .setQuery(FireBaseUtils.mDatabaseQuestions.orderByChild(Constants.TIME_CREATED), Question.class)
                                                             .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Question, QuestionHolder>(response) {
            @NonNull
            @Override
            public QuestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_question, parent, false);
                return new QuestionHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull QuestionHolder viewHolder, int position, @NonNull final Question model) {
                final String postKey = getRef(position).getKey();
                progressBar.setVisibility(View.GONE);
                viewHolder.tvCrop.setText(model.getCrop());

                viewHolder.tvAuthor.setText(model.getUserName());
                if (model.getTimeCreated() != null) {
                    String time = TimeUtils.timeElapsed(model.getTimeCreated());
                    viewHolder.tvTime.setText(time);
                }
                //ToDo find way of displaying question count
                //viewHolder.tvQuestion.setText(getResources().getString(R.string.question, model.getQuestionNumber(), model.getQuestion()));
                viewHolder.tvQuestion.setText(model.getQuestion());
                viewHolder.btAnswers.setText(getResources().getString(R.string.answers, NumberUtils.setPlurality(model.getAnswerCount(), "Response")));
                if (model.getImageUrl() != null){
                    viewHolder.setImage(getApplicationContext(), model.getImageUrl());
                } else {
                    viewHolder.iv_sample.setVisibility(View.GONE);
                }

                viewHolder.btAnswers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent answerIntent = new Intent(QuestionActivity.this, AnswersActivity.class);
                        answerIntent.putExtra(Constants.POST_KEY, postKey);
                        answerIntent.putExtra(Constants.USER_URL, model.getUserUrl());
                        answerIntent.putExtra(Constants.IMAGE_URL, model.getImageUrl());
                        answerIntent.putExtra(Constants.PRODUCT, model.getCrop());
                        answerIntent.putExtra(Constants.ANSWER_COUNT, model.getAnswerCount().intValue());
                        answerIntent.putExtra(Constants.QUESTION_NUMBER, model.getQuestionNumber().intValue());
                        startActivity(answerIntent);
                    }
                });

                viewHolder.iv_sample.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (model.getImageUrl() != null) {
                            Intent intent = new Intent(QuestionActivity.this, FullImageActivity.class);
                            intent.putExtra(Constants.IMAGE_URL, model.getImageUrl());
                            startActivity(intent);
                        }
                    }
                });
            }
        };
        mQuestionList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ask, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case  R.id.action_ask:
                Intent intent = new Intent(QuestionActivity.this, AskActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
