package com.techart.atszambia.admin.efekto;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.techart.atszambia.R;
import com.techart.atszambia.constants.Constants;
import com.techart.atszambia.constants.FireBaseUtils;
import com.techart.atszambia.models.Pest;
import com.techart.atszambia.models.Products;
import com.techart.atszambia.utils.EditorUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by Kelvin on 30/07/2017.
 */

public class EfektoChemicalPostActivity extends AppCompatActivity {
    private ProgressDialog mProgress;
    private String name;
    private String chemicalType;

    private List<String> crops = new ArrayList<>();
    private EditText etName;
    private Spinner spChemicalTypes;
    private RecyclerView rvChemicals;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chemical_post_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Add a chemical ");
        etName = findViewById(R.id.et_chemicalName);
        mProgress = new ProgressDialog(this);

        rvChemicals = findViewById(R.id.rv_crop);
        rvChemicals.setHasFixedSize(true);

        RecyclerView.LayoutManager recyclerViewLayoutManager = new GridLayoutManager(this, 3);
        rvChemicals.setLayoutManager(recyclerViewLayoutManager);
        //Categories
        setSpinnerCategories();
        bindView();

    }

    private void setSpinnerCategories() {
        final String[] tvChemicalTypes = getResources().getStringArray(R.array.chemical_types);
        spChemicalTypes = findViewById(R.id.sp_category);
        ArrayAdapter<String> pagesAdapter = new ArrayAdapter<>(EfektoChemicalPostActivity.this, R.layout.tv_dropdown, tvChemicalTypes);
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
        switch (item.getItemId()){
            case R.id.action_post:
                if (validate()) {
                    sendPost();
                    return true;
                }
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void sendPost() {
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Posting ...");
        mProgress.show();
        String url = FireBaseUtils.mDatabaseChemicals.child(chemicalType).push().getKey();
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.NAME,name);
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        values.put(Constants.IMAGE_URL, "default");
        values.put(Constants.NUM_REVIEWS, 0);
        values.put(Constants.CROPS, crops);
        FireBaseUtils.mDatabaseChemicals.child(chemicalType).child(url).setValue(values);
        onChemicalPosted(chemicalType,url);
        mProgress.dismiss();
        Toast.makeText(EfektoChemicalPostActivity.this, "Item Posted",LENGTH_LONG).show();
        finish();
    }


    /**
     * validates the entries before submission
     * @return true if successful
     */
    private boolean validate(){
        name  = etName.getText().toString().trim();
        TextView tvChemicalError =  (TextView)spChemicalTypes.getSelectedView();
        return EditorUtils.dropDownValidator(chemicalType,getResources().getString(R.string.default_chemical),tvChemicalError) &&
                EditorUtils.editTextValidator(name,etName,"Type in title");
    }

    public static void onChemicalPosted(String chemicalType, String post_key) {
        FireBaseUtils.mDatabaseProducts.child(chemicalType).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Products products = mutableData.getValue(Products.class);
                if (products == null) {
                    return Transaction.success(mutableData);
                }
                products.setCount(products.getCount() + 1 );
                // Set value and report transaction success
                mutableData.setValue(products);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {

            }
        });
    }

    /**
     * Displays chemicals on load
     */
    private void bindView() {
        FirebaseRecyclerOptions<Pest> response = new FirebaseRecyclerOptions.Builder<Pest>()
                                                            .setQuery(FireBaseUtils.mDatabaseEfektoPest, Pest.class)
                                                            .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pest, ViewHolder>(response) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_notifications, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, @NonNull final Pest model) {
                viewHolder.btPests.setText(model.getName());
                viewHolder.btPests.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        crops.add(model.getName());
                        Toast.makeText(EfektoChemicalPostActivity.this,model.getName() + " added", Toast.LENGTH_LONG).show();
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

    @Override
    protected void onStart(){
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView btPests;
        public View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            btPests = itemView.findViewById(R.id.bt_crop);
            this.mView = itemView;
        }
    }
}