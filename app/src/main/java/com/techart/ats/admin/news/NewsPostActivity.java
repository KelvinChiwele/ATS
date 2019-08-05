package com.techart.ats.admin.news;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.techart.ats.R;
import com.techart.ats.constants.Constants;
import com.techart.ats.constants.FireBaseUtils;
import com.techart.ats.models.Products;
import com.techart.ats.utils.EditorUtils;
import com.techart.ats.utils.ImageUtils;
import com.techart.ats.utils.UploadUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;
import static com.techart.ats.utils.ImageUtils.hasPermissions;

/**
 * Created by Kelvin on 30/07/2017.
 */

public class NewsPostActivity extends AppCompatActivity {
    private ProgressDialog mProgress;
   // private StorageReference storageReference;
    private String news;
    private String newsTitle;

    private static final int GALLERY_REQUEST = 1;
    private Uri uri;
    private ImageView ibSample;

    private EditText etNews;
    private EditText etNewsTitle;

    private String realPath;
    private StorageReference filePath;

    private final int PERMISSION_ALL = 1;
    private final String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newspost);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Write news");
        etNews = findViewById(R.id.et_news);
        etNewsTitle = findViewById(R.id.et_newTitle);

        mProgress = new ProgressDialog(this);

        ibSample = findViewById(R.id.ib_item);

        ibSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    onGetPermission();
                }  else {
                    Intent imageIntent = new Intent();
                    imageIntent.setType("image/*");
                    imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(imageIntent,GALLERY_REQUEST);
                }
            }
        });
    }


    /**
     * requests for permission in android >= 23
     */
    @TargetApi(23)
    private void onGetPermission() {
        // only for MarshMallow and newer versions
        if(!hasPermissions(this, PERMISSIONS)){
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
     * @param requestCode
     * @param permissions permissions to be requested
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
                            ActivityCompat.requestPermissions(NewsPostActivity.this, PERMISSIONS, PERMISSION_ALL);
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
        int id = item.getItemId();
        if (id == R.id.action_post && validate()) {
            upload();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Sends information to database
     * @param downloadImageUrl url of upload image
     */
    private void sendPost(String downloadImageUrl,String url) {
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.NEWS, news);
        values.put(Constants.NEWS_TITLE,newsTitle);
        values.put(Constants.USER_URL,FireBaseUtils.getUiD());
        values.put(Constants.USER_NAME,FireBaseUtils.getAuthor());
        values.put(Constants.IMAGE_URL,downloadImageUrl);
        values.put(Constants.NUM_VIEW,0);
        values.put(Constants.NUM_COMMENTS,0);
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        FireBaseUtils.mDatabaseNews.child(url).setValue(values);
        mProgress.dismiss();
        Toast.makeText(NewsPostActivity.this, "Item Posted",LENGTH_LONG).show();
        finish();
    }

    /**
     * Uploads image to cloud storage
     */
    private void upload() {
        mProgress = new ProgressDialog(NewsPostActivity.this);
        mProgress.setMessage("Uploading photo, please wait...");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
        final String url = FireBaseUtils.mDatabaseNews.push().getKey();
        filePath =FireBaseUtils.mStorageNews.child(FireBaseUtils.getAuthor() +"/"+ url);
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
                    onNewsPosted();
                    sendPost(task.getResult().toString(),url);
                } else {
                    // Handle failures
                    UploadUtils.makeNotification("Image upload failed",NewsPostActivity.this);
                    finish();
                }
            }
        });
    }

    private static void onNewsPosted() {
        FireBaseUtils.mDatabaseProducts.child("News").runTransaction(new Transaction.Handler() {
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
     * validates the entries before submission
     * @return true if successful
     */
    private boolean validate(){
        newsTitle = etNewsTitle.getText().toString().trim();
        news = etNews.getText().toString().trim();
        return EditorUtils.editTextValidator(newsTitle,etNewsTitle,"Type in title") &&
                EditorUtils.editTextValidator(news,etNews, "Type in message") &&
                EditorUtils.imagePathValdator(this,realPath);
    }

    /**
     * Called upon selecting an image
     * @param requestCode
     * @param resultCode was operation successful or not
     * @param data data returned from the operation
     */
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null ) {
            uri = data.getData();
            realPath = ImageUtils.getRealPathFromUrl(this, uri);
            Uri uriFromPath = Uri.fromFile(new File(realPath));
            setImage(ibSample,uriFromPath);
        }
    }

    /**
     * inflates image into the image view
     * @param image component into which image will be inflated
     * @param uriFromPath uri of image to be inflated
     */
    private void setImage(ImageView image,Uri uriFromPath) {
        RequestOptions options = new RequestOptions()
                .centerCrop();
        Glide.with(this)
                .load(uriFromPath)
                .apply(options)
                .into(image);
    }
}