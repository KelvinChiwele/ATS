package com.techart.atszambia.admin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.ServerValue;
import com.techart.atszambia.R;
import com.techart.atszambia.constants.Constants;
import com.techart.atszambia.constants.FireBaseUtils;
import com.techart.atszambia.utils.EditorUtils;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by Kelvin on 30/07/2017.
 */

public class CategoryPostActivity extends AppCompatActivity {
    private ProgressDialog mProgress;
    private String chemicalType;
    private Spinner spChemicalTypes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_post_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Add a category ");
        mProgress = new ProgressDialog(this);

        //Categories
        final String[] tvChemicalTypes = getResources().getStringArray(R.array.categories);
        spChemicalTypes = findViewById(R.id.sp_category);
        ArrayAdapter<String> pagesAdapter = new ArrayAdapter<>(CategoryPostActivity.this, R.layout.tv_dropdown, tvChemicalTypes);
        pagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pagesAdapter.notifyDataSetChanged();
        spChemicalTypes.setAdapter(pagesAdapter);
        spChemicalTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chemicalType = tvChemicalTypes[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_post && validate()) {
            sendPost();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void sendPost() {
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Posting ...");
        mProgress.show();
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.CATEGORY,chemicalType);
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        values.put(Constants.IMAGE_URL, "default");
        values.put(Constants.COUNT, 0);
        FireBaseUtils.mDatabaseProducts.child(chemicalType).setValue(values);
        mProgress.dismiss();
        Toast.makeText(CategoryPostActivity.this, "Item Posted",LENGTH_LONG).show();
        finish();
    }


    /**
     * validates the entries before submission
     * @return true if successful
     */
    private boolean validate(){
        TextView tvChemicalError =  (TextView)spChemicalTypes.getSelectedView();
        return EditorUtils.dropDownValidator(chemicalType,getResources().getString(R.string.default_chemical),tvChemicalError);
    }

}