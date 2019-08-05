package com.techart.ats.admin;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.techart.ats.R;
import com.techart.ats.constants.Constants;
import com.techart.ats.constants.FireBaseUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kelvin on 30/07/2017.
 */

public class PestPostActivity extends AppCompatActivity {
    private String notice;

    private EditText etNotice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Post a crop");
        etNotice = findViewById(R.id.et_question);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_post) {
            sendPost();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData()
    {
        notice = etNotice.getText().toString().trim();
    }

    private void sendPost() {
        getData();
        String url = FireBaseUtils.mDatabasePest.push().getKey();
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.PEST, notice);
        FireBaseUtils.mDatabasePest.child(url).setValue(values);
        finish();
    }
}