package com.techart.ats;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.techart.ats.admin.PostDialogActivity;
import com.techart.ats.constants.Constants;
import com.techart.ats.constants.FireBaseUtils;
import com.techart.ats.disease.DiseaseListActivity;
import com.techart.ats.models.Disease;
import com.techart.ats.models.ImageUrl;
import com.techart.ats.models.Products;
import com.techart.ats.models.Stamp;
import com.techart.ats.models.Users;
import com.techart.ats.models.Version;
import com.techart.ats.others.AppRater;
import com.techart.ats.setup.LoginActivity;
import com.techart.ats.utils.ImageUtils;
import com.techart.ats.utils.NumberUtils;
import com.techart.ats.utils.UploadUtils;
import com.techart.ats.viewholder.DiseaseViewHolder;
import com.techart.ats.viewholder.ProductsViewHolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static com.techart.ats.utils.ImageUtils.hasPermissions;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseRecyclerAdapter firebaseRecyclerAdapterResources;
    FirebaseRecyclerAdapter firebaseRecyclerAdapterProducts;
    FirebaseRecyclerAdapter firebaseRecyclerAdapterDisease;
    private FirebaseAuth mAuth;
    private Intent intent;
    private boolean isAttached;
    private SharedPreferences mPref;
    private SharedPreferences.Editor editor;
    private RecyclerView rvCategory;
    private RecyclerView rvDisease;
    private RecyclerView rvInformation;
    private int versionCode = 0;
    private FloatingActionButton fab;
    private boolean mProcessView;
    private ProgressBar progressBarInformation;
    private ProgressBar progressBarProducts;
    private ProgressBar progressBarDiseases;
    private int mStamp;
    private int lastAccessedTime;
    private TextView textCartItemCount;
    private int mCartItemCount = 0;
    RelativeLayout linearLayout;
    TextView tvUpload;
    ImageButton ibDp;
    ImageButton ibCancel;
    StorageReference filePath;


    //image
    private static final int GALLERY_REQUEST = 1;
    private Uri uri;
    private String currentPhotoUrl;
    //Permission
    private final int PERMISSION_ALL = 1;
    private String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()==null) {
                    Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(loginIntent);
                }
            }
        };
        progressBarInformation = findViewById(R.id.pb_information);
        progressBarProducts = findViewById(R.id.pb_products);
        progressBarDiseases = findViewById(R.id.pb_disease);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iInformation = new Intent(MainActivity.this,AskActivity.class);
                startActivity(iInformation);
            }
        });
        mPref = getSharedPreferences(String.format("%s",getString(R.string.app_name)),MODE_PRIVATE);
        NestedScrollView nestedScrollView = findViewById(R.id.nestedscroll);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });

        haveNetworkConnection();
        Button resources = findViewById(R.id.btn_information);
        Button chemicals = findViewById(R.id.btn_chemicals);
        Button diseases = findViewById(R.id.btn_diseases);

        rvCategory = findViewById(R.id.rv_category);
        rvCategory.setHasFixedSize(true);
        rvCategory.setLayoutManager( new LinearLayoutManager( this,
                LinearLayoutManager.HORIZONTAL,
                false ) );
        bindCategory();

        rvDisease = findViewById(R.id.rv_disease);
        rvDisease.setHasFixedSize(true);
        rvDisease.setLayoutManager( new LinearLayoutManager( this,
                LinearLayoutManager.HORIZONTAL,
                false ) );
        bindDiseases();

        rvInformation = findViewById(R.id.rv_information);
        rvInformation.setHasFixedSize(true);
        rvInformation.setLayoutManager( new LinearLayoutManager( this,
                LinearLayoutManager.HORIZONTAL,
                false ) );

        //More
        diseases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this,  CaseActivity.class);
                startActivity(intent);
            }
        });
        resources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this,  MoreActivity.class);
                intent.putExtra(Constants.NAME,"Resources");
                startActivity(intent);
            }
        });

        chemicals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this,  MoreActivity.class);
                intent.putExtra(Constants.NAME,"Chemicals");
                startActivity(intent);
            }
        });
        bindInformation();
        setupDrawer(toolbar);
        loadProfilePicture();
        blink();
        AppRater.app_launched(MainActivity.this);
        FirebaseMessaging.getInstance().subscribeToTopic("all");
    }

    private void setupDrawer(Toolbar toolbar) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            //Name, email address, and profile photo Url
            FirebaseMessaging.getInstance().subscribeToTopic("all");
            TextView tvUser = header.findViewById(R.id.tvUser);
            TextView tvEmail = header.findViewById(R.id.tvEmail);
            tvUpload = header.findViewById(R.id.tv_upload);
            ibDp = header.findViewById(R.id.iv_change);
            ibCancel = header.findViewById(R.id.iv_remove);
            linearLayout = header.findViewById(R.id.ll_header_main);

            if (currentPhotoUrl != null && currentPhotoUrl.length() > 7) {
                setIvImage(currentPhotoUrl);
            }
            tvUser.setText(FireBaseUtils.getAuthor());
            tvEmail.setText(FireBaseUtils.getEmail());
            tvUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (FireBaseUtils.staff.contains(FireBaseUtils.getEmail())) {
                        Intent admin = new Intent(MainActivity.this, PostDialogActivity.class);
                        startActivity(admin);
                    }
                }
            });

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentPhotoUrl != null && currentPhotoUrl.length() > 7) {
                        Intent intent = new Intent(MainActivity.this, FullImageActivity.class);
                        intent.putExtra(Constants.IMAGE_URL, currentPhotoUrl);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "No image, please upload", Toast.LENGTH_LONG).show();
                    }

                }
            });

            ibDp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        onGetPermission();
                    } else {
                        Intent imageIntent = new Intent();
                        imageIntent.setType("image/*");
                        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(imageIntent, GALLERY_REQUEST);
                    }
                }
            });

            ibCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tvUpload.setVisibility(View.GONE);
                    ibDp.setVisibility(View.VISIBLE);
                    ibCancel.setVisibility(View.GONE);
                    Glide.with(MainActivity.this)
                            .load(R.drawable.placeholder)
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        linearLayout.setBackground(resource);
                                    }
                                }
                            });
                }
            });

            tvUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tvUpload.setVisibility(View.GONE);
                    upload();
                }
            });
        }
    }

    /**
     * Uploads image to cloud storage
     */
    private void upload() {
        final ProgressDialog mProgress = new ProgressDialog(MainActivity.this);
        mProgress.setMessage("Uploading photo, please wait...");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
        filePath = FireBaseUtils.mStorageQuestions.child("Profiles" + "/" + FireBaseUtils.getUiD());
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        //uploading the image
        UploadTask uploadTask = filePath.putBytes(data);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    mProgress.dismiss();
                    updateProfile(task);
                    UploadUtils.makeNotification("Image upload complete", MainActivity.this);
                } else {
                    // Handle failures
                    UploadUtils.makeNotification("Image upload failed", MainActivity.this);
                }
                ibDp.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateProfile(@NonNull Task<Uri> task) {
        FireBaseUtils.mDatabaseUsers.child(FireBaseUtils.getUiD()).child(Constants.IMAGE_URL).setValue(task.getResult().toString());
        loadProfilePicture();
    }

    private void loadProfilePicture() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FireBaseUtils.mDatabaseUsers.child(FireBaseUtils.getUiD()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Users users = dataSnapshot.getValue(Users.class);
                    if (users.getImageUrl() != null && users.getImageUrl().length() > 7) {
                        currentPhotoUrl = users.getImageUrl();
                        setIvImage(users.getImageUrl());
                        ImageUrl imageUrl = ImageUrl.getInstance();
                        imageUrl.setImageUrl(currentPhotoUrl);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    /**
     * requests for permission in android >= 23
     */
    @TargetApi(23)
    private void onGetPermission() {
        // only for MarshMallow and newer versions
        if (!hasPermissions(this, PERMISSIONS)) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                onPermissionDenied();
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY_REQUEST);
        }
    }

    /**
     * Trigger gallery selection for a photo
     *
     * @param requestCode
     * @param permissions  permissions to be requested
     * @param grantResults granted results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY_REQUEST);
        } else {
            //do something like displaying a message that he did not allow the app to access gallery and you wont be able to let him select from gallery
            onPermissionDenied();
        }
    }

    /**
     * Displays when permission is denied
     */
    private void onPermissionDenied() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, PERMISSION_ALL);
                        }
                        if (button == DialogInterface.BUTTON_NEGATIVE) {
                            dialog.dismiss();
                        }
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("YOU NEED TO ALLOW ACCESS TO MEDIA STORAGE")
                .setMessage("Without this permission you can not upload an image")
                .setPositiveButton("ALLOW", dialogClickListener)
                .setNegativeButton("DENY", dialogClickListener)
                .show();
    }


    public void setIvImage(String ivImage) {
        RequestOptions options = new RequestOptions()
                .fitCenter();
        Glide.with(this)
                .load(ivImage)
                .apply(options)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            linearLayout.setBackground(resource);
                        }
                    }
                });
    }

    private void blink() {
        Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blinking_animation);
        fab.startAnimation(startAnimation);
    }

    /**
     * Binds view to the recycler view
     */
    private void bindCategory() {
        FirebaseRecyclerOptions<Products> response = new FirebaseRecyclerOptions.Builder<Products>()
                                                            .setQuery(FireBaseUtils.mDatabaseProducts, Products.class)
                                                            .build();
        firebaseRecyclerAdapterProducts = new FirebaseRecyclerAdapter<Products, ProductsViewHolder>(response) {
            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_products, parent, false);
                return new ProductsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder viewHolder, int position, @NonNull final Products model) {
                progressBarProducts.setVisibility(View.GONE);
                viewHolder.tvTitle.setText(getString(R.string.title,model.getName()));
                viewHolder.tvCount.setText(getString(R.string.post_count,model.getCount()));
                if (model.getClients() != null && model.getClicks() != null){
                    viewHolder.tvViews.setText(getString(R.string.post_views, NumberUtils.setPlurality(model.getClicks(),"view"),NumberUtils.setPlurality(model.getClients(),"client")));
                }
                viewHolder.tvDescription.setText(model.getDescription());
                viewHolder.setIvImage(MainActivity.this, ImageUtils.getImageUrl(model.getCategory()));
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectIntent(model.getCategory(),model.getCount());
                    }
                });

                viewHolder.setProductViewed(model.getCategory());
            }
        };
        rvCategory.setAdapter(firebaseRecyclerAdapterProducts);
        firebaseRecyclerAdapterProducts.notifyDataSetChanged();
    }



    private void addToProductViews(final String category) {
        mProcessView = true;
        FireBaseUtils.mDatabaseProductViews.child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mProcessView) {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null){
                        if (dataSnapshot.hasChild(FireBaseUtils.getUiD())) {
                            FireBaseUtils.onProductsClicks(category);
                            mProcessView = false;
                        } else {
                            FireBaseUtils.addProductsView(category);
                            FireBaseUtils.onProductsViewed(category);
                            FireBaseUtils.onProductsClicks(category);
                            mProcessView = false;
                        }
                    } else {
                        Toast.makeText(MainActivity.this,"Kindly wait, we are still setting up your account",Toast.LENGTH_LONG).show();
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
                    if (FirebaseAuth.getInstance().getCurrentUser() != null){
                        if (dataSnapshot.hasChild(FireBaseUtils.getUiD())) {
                            FireBaseUtils.onResourceClicks(category);
                            mProcessView = false;
                        } else {
                            FireBaseUtils.addResourceView(category);
                            FireBaseUtils.onResourceViewed(category);
                            FireBaseUtils.onResourceClicks(category);
                            mProcessView = false;
                        }
                    } else {
                        Toast.makeText(MainActivity.this,"Kindly wait, we are still setting up your account",Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Binds view to the recycler view
     */
    private void bindDiseases() {
        FirebaseRecyclerOptions<Disease> response = new FirebaseRecyclerOptions.Builder<Disease>()
                                                             .setQuery(FireBaseUtils.mDatabaseCaseStudy, Disease.class)
                                                             .build();
        firebaseRecyclerAdapterDisease = new FirebaseRecyclerAdapter<Disease, DiseaseViewHolder>(response) {
            @NonNull
            @Override
            public DiseaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_disease, parent, false);
                return new DiseaseViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DiseaseViewHolder viewHolder, int position, @NonNull final Disease model) {
                final String post_key = getRef(position).getKey();
                progressBarDiseases.setVisibility(View.GONE);
                if (model.getImageUrl() != null){
                    viewHolder.setIvImage(MainActivity.this, model.getImageUrl());
                }
                viewHolder.tvCrop.setText(model.getName());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent = new Intent(MainActivity.this,  DiseaseListActivity.class);
                        intent.putExtra(Constants.CASE_STUDY_URL,post_key);
                        intent.putExtra(Constants.CROP_NAME,model.getName());
                        startActivity(intent);
                    }
                });
            }
        };
        rvDisease.setAdapter(firebaseRecyclerAdapterDisease);
        firebaseRecyclerAdapterDisease.notifyDataSetChanged();
    }

    /**
     * Binds view to the recycler view
     */
    private void bindInformation() {
        FirebaseRecyclerOptions<Products> response = new FirebaseRecyclerOptions.Builder<Products>()
                                                            .setQuery(FireBaseUtils.mDatabaseResources, Products.class)
                                                            .build();
        firebaseRecyclerAdapterResources = new FirebaseRecyclerAdapter<Products, ProductsViewHolder>(response) {
            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_products, parent, false);
                return new ProductsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder viewHolder, int position, @NonNull final Products model) {
                progressBarInformation.setVisibility(View.GONE);
                viewHolder.tvTitle.setText(getString(R.string.title,model.getName()));
                viewHolder.tvCount.setText(getString(R.string.post_count,model.getCount()));
                viewHolder.tvDescription.setText(model.getDescription());
                if (model.getImageUrl() != null){
                    viewHolder.setIvImage(MainActivity.this, ImageUtils.getImageUrl(model.getCategory()));
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
        rvInformation.setAdapter(firebaseRecyclerAdapterResources);
        firebaseRecyclerAdapterResources.notifyDataSetChanged();
    }


    /**
     * Resolves activity to start
     * @param activity name of category clicked
     */
    private void selectIntent(String activity, Long count){
        switch(activity) {
            case "Programs":
                addToResourceViews(activity);
                intent = new Intent(MainActivity.this,  CropProgramActivity.class);
                startActivity(intent);
                break;
            case "FAQ":
                if (count == 0){
                    Toast.makeText(MainActivity.this,"No questions to display, press ask to post",Toast.LENGTH_LONG).show();
                } else {
                    addToResourceViews(activity);
                    intent = new Intent(MainActivity.this,  QuestionActivity.class);
                    startActivity(intent);
                }
                break;
            case "News":
                addToResourceViews(activity);
                intent = new Intent(MainActivity.this,  NewsActivity.class);
                intent.putExtra(Constants.COUNT,count.intValue());
                startActivity(intent);
                break;
            default:
                if (count == 0){
                    Toast.makeText(MainActivity.this,"No items to display",Toast.LENGTH_LONG).show();
                } else {
                    addToProductViews(activity);
                    intent = new Intent(MainActivity.this,  ChemicalActivity.class);
                    intent.putExtra(Constants.CATEGORY,activity);
                    startActivity(intent);
                }
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        firebaseRecyclerAdapterResources.startListening();
        firebaseRecyclerAdapterProducts.startListening();
        firebaseRecyclerAdapterDisease.startListening();
        getVersionCode();
        checkVersion();
        mStamp = mPref.getInt(Constants.STAMP_KEY,0);
        stamp();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_question) {
            Intent editorDevotionIntent = new Intent(MainActivity.this, MyQuestionsActivity.class);
            startActivity(editorDevotionIntent);
        } else if (id == R.id.nav_logout) {
            logOut();
        }   else if (id == R.id.nav_contact) {
            Intent editorDevotionIntent = new Intent(MainActivity.this, DirectoryActivity.class);
            startActivity(editorDevotionIntent);
        }   else if (id == R.id.nav_about) {
            Intent editorDevotionIntent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(editorDevotionIntent);
        }   else if (id == R.id.nav_facebook) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/ATS-Agrochemicals-Limited-435889980186767/"));
            startActivity(browserIntent);
        }  else if (id == R.id.nav_shareapp) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.techart.ats");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else if (id == R.id.nav_report) {
            sendFeedback();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Email client intent to send support mail
     * Appends the necessary device information to email body
     * useful when providing support
     */
    private void sendFeedback() {
        String body = getAppVersion();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"atszambiaappservice@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Report on abnormal behaviour");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        this.startActivity(Intent.createChooser(intent, this.getString(R.string.choose_email_client)));
    }

    @Nullable
    private String getAppVersion() {
        String body = null;
        try {
            body = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "Could not read Package Manager", Toast.LENGTH_LONG).show();
        }
        return body;
    }

    @Nullable
    private int getVersionCode() {
        try {
            versionCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "Could not read app version code", Toast.LENGTH_LONG).show();
        }
        return versionCode;
    }

    /**
     * Checks for internet connection
     */
    private void haveNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netWorkInfo = cm.getActiveNetworkInfo();
            if (netWorkInfo != null && netWorkInfo.getState() == NetworkInfo.State.CONNECTED) {
                Toast.makeText(getApplicationContext(),"Connected", Toast.LENGTH_LONG).show();
                return;
            }
        }
        Toast.makeText(getApplicationContext(),"No internet Connection", Toast.LENGTH_LONG).show();
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu menu to be inflated
     * @return return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_notifications);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = actionView.findViewById(R.id.cart_badge);

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
        return true;
    }

    private void setupBadge() {
        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * Resolves section of the menu which was clicked
     * @param item selected menu item
     * @return onClick status
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_notifications:
                intent = new Intent(MainActivity.this, NotificationsActivity.class);
                intent.putExtra(Constants.STAMP_KEY,lastAccessedTime);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Loads the list of staff from database to Shared preferences for easy access
     */
    private void checkVersion() {
        FireBaseUtils.mDatabaseVersion.child("-LIaZ1jO8SXe4peE3JAc").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Version version = dataSnapshot.getValue(Version.class);
                if (versionCode < Integer.valueOf(version.getVersion())) {
                    switch (version.getStatus()) {
                        case "Urgent":
                            outDatedVersionUrgent();
                            break;
                        case "Optional":
                            outDatedVersion();
                            break;
                        default:
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Loads the list of staff from database to Shared preferences for easy access
     */
    private void stamp() {
        FireBaseUtils.mDatabaseStamp.child("-LIaZ1jO8SXe4peE3JAc").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Stamp stamp = dataSnapshot.getValue(Stamp.class);
                lastAccessedTime = stamp.getTimeCreated().intValue();
                if (textCartItemCount != null) {
                    if (mStamp >= lastAccessedTime) {
                        textCartItemCount.setVisibility(View.GONE);
                    } else {
                        textCartItemCount.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void outDatedVersion() {
        if (isAttached){
            DialogInterface.OnClickListener dialogClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                            if (button == DialogInterface.BUTTON_POSITIVE) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.techart.ats"));
                                startActivity(browserIntent);
                            }
                            if (button == DialogInterface.BUTTON_NEGATIVE) {
                                editor = mPref.edit();
                                editor.putLong("time", System.currentTimeMillis());
                                editor.apply();
                                dialog.dismiss();
                            }
                        }
                    };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.outdated))
                    .setMessage(getString(R.string.update_reason))
                    .setPositiveButton("UPDATE", dialogClickListener)
                    .setNegativeButton("LATER", dialogClickListener)
                    .show();
        }

    }


    private void outDatedVersionUrgent() {
        if (isAttached){
            DialogInterface.OnClickListener dialogClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                            if (button == DialogInterface.BUTTON_POSITIVE){
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.techart.ats"));
                                startActivity(browserIntent);
                            }
                        }
                    };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.outdated))
                    .setMessage(getString(R.string.urgent))
                    .setCancelable(false)
                    .setPositiveButton("UPDATE", dialogClickListener)
                    .show();
        }

    }

    /**
     * Called upon selecting an image
     *
     * @param requestCode
     * @param resultCode  was operation successful or not
     * @param data        data returned from the operation
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            String realPath = ImageUtils.getRealPathFromUrl(this, uri);
            Uri uriFromPath = Uri.fromFile(new File(realPath));
            setIvImage(uriFromPath);
        }
    }

    public void setIvImage(Uri ivImage) {
        tvUpload.setVisibility(View.VISIBLE);
        ibDp.setVisibility(View.GONE);
        ibCancel.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(ivImage)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            linearLayout.setBackground(resource);
                        }
                    }
                });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttached = true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
    }


    private void logOut() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE)
                        {
                            FirebaseAuth.getInstance().signOut();
                        }
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }
}
