package com.techart.ats.admin.disease;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.techart.ats.R;
import com.techart.ats.constants.Constants;
import com.techart.ats.constants.FireBaseUtils;
import com.techart.ats.utils.EditorUtils;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by Kelvin on 30/07/2017.
 */

public class DiseaseEditActivity extends AppCompatActivity {
   // private StorageReference storageReference;
    private String caseStudyUrl;
    private String url;

    private String management;
    private String symptoms;
    private String generalnformation;

    private EditText etManagement;
    private EditText etSymptoms;
    private EditText etGeneralnformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Edit disease");
        caseStudyUrl = getIntent().getStringExtra(Constants.CASE_STUDY_URL);
        url = getIntent().getStringExtra(Constants.POST_KEY);

        management = getIntent().getStringExtra(Constants.MANAGEMENT);
        symptoms = getIntent().getStringExtra(Constants.SYMPTOMS);
        generalnformation = getIntent().getStringExtra(Constants.GENERAL_INFORMATION);

        etManagement = findViewById(R.id.et_management);
        etGeneralnformation = findViewById(R.id.et_generalInformation);
        etSymptoms = findViewById(R.id.et_symptoms);

        etGeneralnformation.setText(generalnformation);
        etSymptoms.setText(symptoms);
        etManagement.setText(management);
    }


    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu menu resource to be inflated
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        management = etManagement.getText().toString().trim();
        symptoms = etSymptoms.getText().toString().trim();
        generalnformation = etGeneralnformation.getText().toString().trim();
        int id = item.getItemId();
        if (id == R.id.action_post && validate()) {
            sendPost();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Sends information to database
     */
    private void sendPost() {
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.MANAGEMENT,management);
        values.put(Constants.GENERAL_INFORMATION,generalnformation);
        values.put(Constants.SYMPTOMS,symptoms);

        FireBaseUtils.mDatabaseDiseases.child(caseStudyUrl).child(url).updateChildren(values);
        Toast.makeText(DiseaseEditActivity.this, "Item Posted",LENGTH_LONG).show();
        finish();
    }


    /**
     * validates the entries before submission
     * @return true if successful
     */
    private boolean validate(){
        return EditorUtils.editTextValidator(generalnformation,etGeneralnformation,"Type in information") &&
                EditorUtils.editTextValidator(symptoms,etSymptoms, "Type in symptoms") &&
                EditorUtils.editTextValidator(management,etManagement, "Type in management");
    }
}