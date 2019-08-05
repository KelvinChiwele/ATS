package com.techart.ats;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.techart.ats.constants.Constants;
import com.techart.ats.constants.FireBaseUtils;
import com.techart.ats.models.Products;
import com.techart.ats.utils.ImageUtils;
import com.techart.ats.utils.NumberUtils;
import com.techart.ats.viewholder.ProductsViewHolder;


public class MoreActivity extends AppCompatActivity{
    private RecyclerView rvCategory;
    private ProgressBar progressBar;
    private boolean mProcessView;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String category = getIntent().getStringExtra(Constants.NAME);
        setTitle(category);
        progressBar = findViewById(R.id.pb_loading);
        rvCategory = findViewById(R.id.rv_more);
        rvCategory.setHasFixedSize(true);
        if (category.equals("Resources")){
            rvCategory.setLayoutManager( new LinearLayoutManager( this,
                    RecyclerView.VERTICAL,
                    false ) );
            bindResources();
        } else {
            rvCategory.setLayoutManager( new LinearLayoutManager( this,
                    RecyclerView.VERTICAL,
                    false ) );
            bindCategory();
        }
    }

    /**
     * Binds view to the recycler view
     */
    private void bindCategory() {
        FirebaseRecyclerOptions<Products> response = new FirebaseRecyclerOptions.Builder<Products>()
                                                             .setQuery(FireBaseUtils.mDatabaseProducts, Products.class)
                                                             .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Products, ProductsViewHolder>(response) {
            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_products, parent, false);
                return new ProductsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder viewHolder, int position, @NonNull final Products model) {
                progressBar.setVisibility(View.GONE);
                viewHolder.tvTitle.setText(getString(R.string.title,model.getName()));
                viewHolder.tvCount.setText(getString(R.string.post_count,model.getCount()));
                if (model.getClients() != null && model.getClicks() != null){
                    viewHolder.tvViews.setText(getString(R.string.post_views, NumberUtils.setPlurality(model.getClicks(),"view"),NumberUtils.setPlurality(model.getClients(),"client")));
                }
                viewHolder.tvDescription.setText(model.getDescription());
                viewHolder.setIvImage(MoreActivity.this, ImageUtils.getImageUrl(model.getCategory()));
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectIntent(model.getCategory(),model.getCount());
                    }
                });

                viewHolder.setProductViewed(model.getCategory());
            }
        };
        rvCategory.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }


    private void addToProductViews(final String category) {
        mProcessView = true;
        FireBaseUtils.mDatabaseProductViews.child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mProcessView) {
                    if (dataSnapshot.hasChild(FireBaseUtils.getUiD())) {
                        FireBaseUtils.onProductsClicks(category);
                        mProcessView = false;
                    } else {
                        FireBaseUtils.addProductsView(category);
                        FireBaseUtils.onProductsViewed(category);
                        FireBaseUtils.onProductsClicks(category);
                        mProcessView = false;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void addToResourceViews(final String category) {
        mProcessView = true;
        FireBaseUtils.mDatabaseResourceViews.child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mProcessView) {
                    if (dataSnapshot.hasChild(FireBaseUtils.getUiD())) {
                        FireBaseUtils.onResourceClicks(category);
                        mProcessView = false;
                    } else {
                        FireBaseUtils.addResourceView(category);
                        FireBaseUtils.onResourceViewed(category);
                        FireBaseUtils.onResourceClicks(category);
                        mProcessView = false;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    /**
     * Binds view to the recycler view
     */
    private void bindResources() {
        FirebaseRecyclerOptions<Products> response = new FirebaseRecyclerOptions.Builder<Products>()
                                                             .setQuery(FireBaseUtils.mDatabaseResources, Products.class)
                                                             .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Products, ProductsViewHolder>(response) {
            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_products, parent, false);
                return new ProductsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder viewHolder, int position, @NonNull final Products model) {
                progressBar.setVisibility(View.GONE);
                viewHolder.tvTitle.setText(getString(R.string.title,model.getName()));
                viewHolder.tvCount.setText(getString(R.string.post_count,model.getCount()));
                viewHolder.tvDescription.setText(model.getDescription());
                if (model.getImageUrl() != null){
                    viewHolder.setIvImage(MoreActivity.this, ImageUtils.getImageUrl(model.getCategory()));
                }


                if (model.getClients() != null && model.getClicks() != null){
                    viewHolder.tvViews.setText(getString(R.string.post_views, NumberUtils.setPlurality(model.getClicks(),"view"),NumberUtils.setPlurality(model.getClients(),"client")));
                }

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectIntent(model.getCategory(),model.getCount());
                    }
                });
                viewHolder.setResourceViewed(model.getCategory());
            }
        };
        rvCategory.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }


    /**
     * Resolves activity to start
     * @param activity name of category clicked
     */
    private void selectIntent(String activity, Long count){
        switch(activity) {
            case "Programs":
                addToResourceViews(activity);
                Intent intent = new Intent(MoreActivity.this, CropProgramActivity.class);
                startActivity(intent);
                break;
            case "FAQ":
                if (count == 0){
                    Toast.makeText(MoreActivity.this,"No questions to display, press ask to post",Toast.LENGTH_LONG).show();
                } else {
                    addToResourceViews(activity);
                    intent = new Intent(MoreActivity.this,  QuestionActivity.class);
                    startActivity(intent);
                }
                break;
            case "News":
                addToResourceViews(activity);
                intent = new Intent(MoreActivity.this,  NewsActivity.class);
                intent.putExtra(Constants.COUNT,count.intValue());
                startActivity(intent);
                break;
            default:
                if (count == 0){
                    Toast.makeText(MoreActivity.this,"No items to display",Toast.LENGTH_LONG).show();
                } else {
                    addToProductViews(activity);
                    intent = new Intent(MoreActivity.this,  ChemicalActivity.class);
                    intent.putExtra(Constants.CATEGORY,activity);
                    startActivity(intent);
                }
        }
    }
}
