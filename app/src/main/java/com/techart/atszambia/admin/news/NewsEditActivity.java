package com.techart.atszambia.admin.news;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.techart.atszambia.R;
import com.techart.atszambia.constants.Constants;
import com.techart.atszambia.constants.FireBaseUtils;
import com.techart.atszambia.utils.EditorUtils;

import java.util.HashMap;
import java.util.Map;

public class NewsEditActivity extends AppCompatActivity {
    private EditText etNews;
    private EditText etTitle;
    private String postKey;
    private String newText;
    private String newTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newspost);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        etNews = findViewById(R.id.et_news);
        etTitle = findViewById(R.id.et_newTitle);
        Intent intent = getIntent();
        postKey = intent.getStringExtra(Constants.POST_KEY);
        String oldText = intent.getStringExtra(Constants.NEWS);
        String oldTitle = intent.getStringExtra(Constants.NEWS_TITLE);
        setTitle("Editing " + oldTitle);
        etTitle.setText(oldTitle);
        etTitle.requestFocus();
        etNews.setText(oldText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_update:
                startPosting();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean validate()    {
        return EditorUtils.editTextValidator(newTitle, etTitle,"Title can't be empty! Type something") &&
            EditorUtils.editTextValidator(newText, etNews,"Can not be empty! Type something") &&
            EditorUtils.validateMainText(this, etNews.getLayout().getLineCount());
    }

    /*
        if() checks if the story was posted, if not queries for the url from the story and then posts chapter
        else if () checks if the story & chapter were posted & then updates the chapter
        else posts the chapter
     */
    private void startPosting() {
        newText = etNews.getText().toString().trim();
        newTitle = etTitle.getText().toString().trim();
        if (validate() && !postKey.equals("null")){
            postChapter();
        }
    }

    private void postChapter() {
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.NEWS_TITLE,newTitle);
        values.put(Constants.NEWS,newText);
        FireBaseUtils.mDatabaseNews.child(postKey).updateChildren(values);
        Toast.makeText(getApplicationContext(),"News successfully updated", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener =
        new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                if (button == DialogInterface.BUTTON_POSITIVE) {
                    finish();
                }
                if (button == DialogInterface.BUTTON_NEGATIVE) {
                    dialog.dismiss();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Unless posted,changes will not be saved!")
        .setPositiveButton("Understood", dialogClickListener)
        .setNegativeButton("Stay in etNews", dialogClickListener)
        .show();
    }
}
