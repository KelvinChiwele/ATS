package com.techart.atszambia.admin.chemicals;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.techart.atszambia.R;
import com.techart.atszambia.constants.Constants;
import com.techart.atszambia.constants.FireBaseUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kelvin on 30/07/2017.
 */

public class CropPostActivity extends AppCompatActivity {
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
        String url = FireBaseUtils.mDatabaseCrops.push().getKey();
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.CROP, notice);
        FireBaseUtils.mDatabaseCrops.child(url).setValue(values);
        finish();
    }
}