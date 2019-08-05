package com.techart.ats.admin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.techart.ats.R;
import com.techart.ats.constants.Constants;
import com.techart.ats.constants.FireBaseUtils;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by Kelvin on 30/07/2017.
 */

public class DirectoryPostActivity extends AppCompatActivity {
    private ProgressDialog mProgress;
    //Private StorageReference storageReference;
    private String address;
    private String phone;
    private String email;
    private String town;
    private EditText etAddress;
    private EditText etPhone;
    private EditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.directory_post_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        etAddress = findViewById(R.id.et_address);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        mProgress = new ProgressDialog(this);
        //branches
        final String[] towns = getResources().getStringArray(R.array.branches);
        Spinner spTowns = findViewById(R.id.sp_category);
        ArrayAdapter<String> pagesAdapter = new ArrayAdapter<>(DirectoryPostActivity.this, R.layout.tv_dropdown, towns);
        pagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pagesAdapter.notifyDataSetChanged();
        spTowns.setAdapter(pagesAdapter);
        spTowns.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                town = towns[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    /**
     * Handle action bar item clicks here. The action bar will automatically
     * handle clicks on the Home/Up button,
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_post) {
            sendPost();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Gets information  from the UI
     */
    private void getData() {
        address = etAddress.getText().toString().trim();
        phone = etPhone.getText().toString().trim();
        email = etEmail.getText().toString().trim();
    }

    private void sendPost() {
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Posting ...");
        mProgress.show();
        getData();
        String url = FireBaseUtils.mDatabaseDirectory.push().getKey();
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.TOWN, town);
        values.put(Constants.ADDRESS,address);
        values.put(Constants.PHONE, phone);
        values.put(Constants.EMAIL, email);
        FireBaseUtils.mDatabaseDirectory.child(url).setValue(values);
        mProgress.dismiss();
        Toast.makeText(DirectoryPostActivity.this, "Item Posted",LENGTH_LONG).show();
        finish();
    }
}