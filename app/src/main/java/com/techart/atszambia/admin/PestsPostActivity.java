package com.techart.atszambia.admin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.techart.atszambia.R;
import com.techart.atszambia.constants.Constants;
import com.techart.atszambia.constants.FireBaseUtils;
import com.techart.atszambia.models.Crop;
import com.techart.atszambia.utils.ImageUtils;
import com.techart.atszambia.viewholder.ChemicalPostViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;
import static com.techart.atszambia.R.id;

/**
 * Created by Kelvin on 30/07/2017.
 */

public class PestsPostActivity extends AppCompatActivity {
    private String name;
    private String make;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;


    private static final int CAMERA_REQUEST = 1;
    private Uri uri;
    private ImageButton ibImage1;
    private RecyclerView rvChemicals;

    private List<String> crops = new ArrayList<>();

    private EditText etName;

    int image;
    Intent imageIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pest_post_activity);
        Toolbar toolbar = findViewById(id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Adding a Pest ");
        etName = findViewById(id.et_chemicalName);


        ibImage1 = findViewById(id.ib_item);
        final String[] makes = new String[] { "Methomyl","Hornet", "Doom", "Target","Tobacco" };

        Spinner spMakes = findViewById(id.sp_category);


        rvChemicals = findViewById(R.id.rv_pest);
        rvChemicals.setHasFixedSize(true);

        RecyclerView.LayoutManager recyclerViewLayoutManager = new GridLayoutManager(this, 3);
        rvChemicals.setLayoutManager(recyclerViewLayoutManager);

        ArrayAdapter<String> pagesAdapter = new ArrayAdapter<>(PestsPostActivity.this, R.layout.tv_dropdown, makes);
        pagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pagesAdapter.notifyDataSetChanged();

        spMakes.setAdapter(pagesAdapter);
        spMakes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                make = makes[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ibImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image = 1;
                imageIntent = new Intent();
                imageIntent.setType("image/*");
                imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(imageIntent,CAMERA_REQUEST);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_post) {
            startPosting();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData()
    {
        name  = etName.getText().toString().trim();
    }

    private void sendPost(String downloadImageUrl) {
        getData();
        String url = FireBaseUtils.mDatabasePests.push().getKey();
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.NAME,name);
        values.put(Constants.IMAGE_URL,downloadImageUrl);
        values.put(Constants.DESCRIPTION,"Pest");
        values.put(Constants.IMAGE_URL,downloadImageUrl);
        FireBaseUtils.mDatabasePests.child(url).setValue(values);
        Toast.makeText(PestsPostActivity.this, "Item Posted",LENGTH_LONG).show();
        finish();
    }

    private void startPosting() {
        StorageReference filePath = FireBaseUtils.mStoragePests.child(make).child(uri.getLastPathSegment());
        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String downloadImageUrl = taskSnapshot.getStorage().getDownloadUrl().toString();
                sendPost(downloadImageUrl);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null ) {
            uri = data.getData();
            String realPath = ImageUtils.getRealPathFromUrl(this, uri);
            Uri uriFromPath = Uri.fromFile(new File(realPath));
            Toast.makeText(this, realPath, Toast.LENGTH_LONG).show();
            setImage(ibImage1,uriFromPath);
        }
    }

     private void setImage(ImageButton image,Uri uriFromPath)
    {
        RequestOptions options = new RequestOptions()
                .centerCrop();
        Glide.with(this)
                .load(uriFromPath)
                .apply(options)
                .into(image);
    }

    /**
     * Displays chemicals on load
     */
    private void bindView() {
        FirebaseRecyclerOptions<Crop> response = new FirebaseRecyclerOptions.Builder<Crop>()
                                                         .setQuery(FireBaseUtils.mDatabaseCrops, Crop.class)
                                                         .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Crop, ChemicalPostViewHolder>(response) {
            @NonNull
            @Override
            public ChemicalPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_notifications, parent, false);
                return new ChemicalPostViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ChemicalPostViewHolder viewHolder, int position, @NonNull final Crop model) {
                viewHolder.btPests.setText(model.getCrop());
                viewHolder.btPests.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        crops.add(model.getCrop());
                        Toast.makeText(PestsPostActivity.this,model.getCrop() + " added", Toast.LENGTH_LONG).show();
                    }
                });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        };
        rvChemicals.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView btPests;
        View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            btPests = itemView.findViewById(R.id.bt_crop);
            this.mView = itemView;
        }

        public void setButtonBackground(Context context) {
            btPests.setBackground( context.getResources().getDrawable(R.drawable.et_circular_background));
        }
    }
}