package com.techart.atszambia.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.techart.atszambia.R;
import com.techart.atszambia.admin.casestudy.CasePostActivity;
import com.techart.atszambia.admin.chemicals.ChemicalPostActivity;
import com.techart.atszambia.admin.chemicals.ChemicalsListActivity;
import com.techart.atszambia.admin.chemicals.CropPostActivity;
import com.techart.atszambia.admin.disease.DiseaseListActivity;
import com.techart.atszambia.admin.efekto.EfektoChemicalPostActivity;
import com.techart.atszambia.admin.efekto.EfektoPestPostActivity;
import com.techart.atszambia.admin.news.NewVersionActivity;
import com.techart.atszambia.constants.Constants;

/**
 * Created by Kelvin on 30/07/2017.
 */

public class PostDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.post_dialog);
        ListView list = findViewById(R.id.list);
        final String[] options = new String[] {
                "Products",
                "Add Case",
                "Add Chemical",
                "Add Efekto Chemical",
                "View Chemicals",
                "Add Crop",
                "Add Disease",
                "Add Pest",
                "Edit Disease",
                "Directory",
                "Message",
                "Version",
                "Programs"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, options);
        list.setAdapter(adapter);
        list.setStackFromBottom(true);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent postIntent;
                switch (options[position]) {
                    case "Products":
                        postIntent = new Intent(PostDialogActivity.this,CategoryPostActivity.class);
                        startActivity(postIntent);
                        break;
                    case "Add Case":
                        postIntent = new Intent(PostDialogActivity.this,CasePostActivity.class);
                        startActivity(postIntent);
                        break;
                    case "Add Chemical":
                        postIntent = new Intent(PostDialogActivity.this,ChemicalPostActivity.class);
                        startActivity(postIntent);
                        break;
                    case "Add Disease":
                        postIntent = new Intent(PostDialogActivity.this,DiseaseListActivity.class);
                        postIntent.putExtra(Constants.NAME,"Add");
                        startActivity(postIntent);
                        break;
                    case "Add Efekto Chemical":
                        postIntent = new Intent(PostDialogActivity.this,EfektoChemicalPostActivity.class);
                        startActivity(postIntent);
                        break;
                    case "View Chemicals":
                        postIntent = new Intent(PostDialogActivity.this,ChemicalsListActivity.class);
                        startActivity(postIntent);
                        break;
                    case "Add Crop":
                        postIntent = new Intent(PostDialogActivity.this,CropPostActivity.class);
                        startActivity(postIntent);
                        break;
                    case "Add Pest":
                        postIntent = new Intent(PostDialogActivity.this,EfektoPestPostActivity.class);
                        startActivity(postIntent);
                        break;
                    case "Edit Disease":
                        postIntent = new Intent(PostDialogActivity.this,DiseaseListActivity.class);
                        postIntent.putExtra(Constants.NAME,"Edit");
                        startActivity(postIntent);
                        break;
                    case "Programs" :

                        break;
                    case "Message":
                        postIntent = new Intent(PostDialogActivity.this,NoticePostActivity.class);
                        startActivity(postIntent);
                        break;
                    case "Directory":
                        postIntent = new Intent(PostDialogActivity.this,DirectoryPostActivity.class);
                        startActivity(postIntent);
                        break;
                    case "Version":
                        postIntent = new Intent(PostDialogActivity.this,NewVersionActivity.class);
                        startActivity(postIntent);
                        break;
                        default:
                            Toast.makeText(PostDialogActivity.this,"Invalid selection",Toast.LENGTH_LONG).show();
                    }
            }

        });
    }
}
