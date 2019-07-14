package com.techart.atszambia;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;
import com.techart.atszambia.constants.Constants;
import com.techart.atszambia.constants.FireBaseUtils;
import com.techart.atszambia.models.Question;
import com.techart.atszambia.utils.NumberUtils;
import com.techart.atszambia.utils.TimeUtils;
import com.techart.atszambia.viewholder.QuestionHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MyQuestionsActivity extends AppCompatActivity  {
    private RecyclerView mQuestionList;
    private List<String> pageNumbers;
    private Query questionsQuery;
    private LinearLayout layoutEmpty;
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
        layoutEmpty = findViewById(R.id.rv_empty);
        Button btAsk = findViewById(R.id.bt_ask);
        tvLabel.setText("My Questions");


        mQuestionList = findViewById(R.id.review_recyclerview);
        mQuestionList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mQuestionList.setLayoutManager(linearLayoutManager);
        ProgressBar progressBar = findViewById(R.id.pb_loading);
        progressBar.setVisibility(View.GONE);
        //Setup spinner
        //setupSpinner();
        questionsQuery = FireBaseUtils.mDatabaseQuestions.orderByChild(Constants.USER_URL).equalTo(FireBaseUtils.getUiD());
        filterContent(questionsQuery);

        btAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent answerIntent = new Intent(MyQuestionsActivity.this,AskActivity.class);
                startActivity(answerIntent);
            }
        });
    }

    private void setupSpinner() {
        pageNumbers = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.crops)));
        Spinner spinner = findViewById(R.id.spinner);

        ArrayAdapter<String> pagesAdapter = new ArrayAdapter<>(MyQuestionsActivity.this, R.layout.tv_filter, pageNumbers);
        pagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pagesAdapter.notifyDataSetChanged();
        spinner.setAdapter(pagesAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // When the given dropdown item is selected, show its contents in the
                if (position != 0) {
                    questionsQuery = FireBaseUtils.mDatabaseQuestions.orderByChild(Constants.CLIENT_CROP).equalTo(FireBaseUtils.getUiD() + " " + pageNumbers.get(position));
                    filterContent(questionsQuery);
                } else {
                    questionsQuery = FireBaseUtils.mDatabaseQuestions.orderByChild(Constants.USER_URL).equalTo(FireBaseUtils.getUiD());
                    filterContent(questionsQuery);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    public void setVisibility(Boolean isVisible) {
        if (isVisible){
            layoutEmpty.setVisibility(View.GONE);
        }else{
            layoutEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void filterContent(Query questionsQuery) {
        FirebaseRecyclerOptions<Question> response = new FirebaseRecyclerOptions.Builder<Question>()
                                                            .setQuery(FireBaseUtils.mDatabaseQuestions.orderByChild(Constants.USER_URL).equalTo(FireBaseUtils.getUiD()), Question.class)
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
                layoutEmpty.setVisibility(View.GONE);
                viewHolder.tvCrop.setText(model.getCrop());
                viewHolder.tvAuthor.setText(model.getUserName());
                if (model.getTimeCreated() != null) {
                    String time = TimeUtils.timeElapsed(model.getTimeCreated());
                    viewHolder.tvTime.setText(time);
                }
                viewHolder.tvQuestion.setText(getResources().getString(R.string.question,model.getQuestionNumber(),model.getQuestion()));
                viewHolder.btAnswers.setText(getResources().getString(R.string.answers, NumberUtils.setPlurality(model.getAnswerCount(),"Answer")));
                viewHolder.setImage(getApplicationContext(),model.getImageUrl());

                viewHolder.btAnswers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent answerIntent = new Intent(MyQuestionsActivity.this,AnswersActivity.class);
                        answerIntent.putExtra(Constants.POST_KEY,postKey);
                        answerIntent.putExtra(Constants.USER_URL,model.getUserUrl());
                        answerIntent.getIntExtra(Constants.ANSWER_COUNT,model.getAnswerCount().intValue());
                        answerIntent.getLongExtra(Constants.QUESTION_NUMBER,model.getQuestionNumber());
                        startActivity(answerIntent);
                    }
                });

                viewHolder.iv_sample.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (model.getImageUrl() != null){
                            Intent intent = new Intent(MyQuestionsActivity.this,FullImageActivity.class);
                            intent.putExtra(Constants.IMAGE_URL,model.getImageUrl());
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
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
