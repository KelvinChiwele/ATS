package com.techart.atszambia.admin;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.ServerValue;
import com.techart.atszambia.R;
import com.techart.atszambia.constants.Constants;
import com.techart.atszambia.constants.FireBaseUtils;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by Kelvin on 30/07/2017.
 */

public class NoticePostActivity extends AppCompatActivity {
    private String notice;

    private EditText etNotice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Ask a notice");
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
        String url = FireBaseUtils.mDatabaseNotifications.push().getKey();
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.MESSAGE, notice);
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        FireBaseUtils.mDatabaseNotifications.child(url).setValue(values);
        Toast.makeText(NoticePostActivity.this, "Item Posted",LENGTH_LONG).show();
        finish();
    }
}