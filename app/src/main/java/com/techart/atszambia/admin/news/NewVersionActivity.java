package com.techart.atszambia.admin.news;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.techart.atszambia.R;
import com.techart.atszambia.constants.FireBaseUtils;
import com.techart.atszambia.utils.EditorUtils;

import java.util.HashMap;
import java.util.Map;

public class NewVersionActivity extends AppCompatActivity {
    private EditText etVersion;
    private EditText etStatus;
    private String newVersion;
    private String newStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        etVersion = findViewById(R.id.et_version);
        etStatus = findViewById(R.id.et_status);
        setTitle("Version Edit");
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
        if (id == R.id.action_update) {
            startPosting();
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean validate()    {
        return EditorUtils.editTextValidator(newStatus, etStatus,"Title can't be empty! Type something") &&
            EditorUtils.editTextValidator(newVersion, etVersion,"Can not be empty! Type something");
    }

    /*
        if() checks if the story was posted, if not queries for the url from the story and then posts chapter
        else if () checks if the story & chapter were posted & then updates the chapter
        else posts the chapter
     */
    private void startPosting() {
        newVersion = etVersion.getText().toString().trim();
        newStatus = etStatus.getText().toString().trim();
        if (validate()){
            postChapter();
        }
    }

    private void postChapter() {
        Map<String,Object> values = new HashMap<>();
        values.put("version", newVersion);
        values.put("status", newStatus);
        FireBaseUtils.mDatabaseVersion.child("-LIaZ1jO8SXe4peE3JAc").updateChildren(values);
        Toast.makeText(getApplicationContext(),"News successfully updated", Toast.LENGTH_LONG).show();
        finish();
    }
}
