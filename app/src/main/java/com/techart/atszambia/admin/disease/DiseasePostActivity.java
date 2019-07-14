package com.techart.atszambia.admin.disease;

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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.techart.atszambia.R;
import com.techart.atszambia.constants.Constants;
import com.techart.atszambia.constants.FireBaseUtils;
import com.techart.atszambia.utils.EditorUtils;
import com.techart.atszambia.utils.ImageUtils;
import com.techart.atszambia.utils.UploadUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;
import static com.techart.atszambia.utils.ImageUtils.hasPermissions;

/**
 * Created by Kelvin on 30/07/2017.
 */

public class DiseasePostActivity extends AppCompatActivity {
    private ProgressDialog mProgress;
   // private StorageReference storageReference;
    private String name;

    private static final int GALLERY_REQUEST = 1;
    private Uri uri;
    private ImageView ibSample;

    private EditText etNews;
    private EditText etNewsTitle;

    private String realPath;
    private String problemType;
    private String caseStudyUrl;
    private String cropName;
    private StorageReference filePath;

    private int PERMISSION_ALL = 1;
    private final String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseasepost);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Post disease");
        etNewsTitle = findViewById(R.id.et_crop);
        caseStudyUrl = getIntent().getStringExtra(Constants.CASE_STUDY_URL);
        cropName = getIntent().getStringExtra(Constants.CROP_NAME);
        mProgress = new ProgressDialog(this);



        ibSample = findViewById(R.id.iv_crop);

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

        final String[] problemTypes =  getResources().getStringArray(R.array.disorders);
        //Disease type
        Spinner spDiseaseType = findViewById(R.id.sp_type);
        ArrayAdapter<String> diseaseTypeAdapter = new ArrayAdapter<>(DiseasePostActivity.this, R.layout.tv_dropdown, problemTypes);
        diseaseTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        diseaseTypeAdapter.notifyDataSetChanged();

        spDiseaseType.setAdapter(diseaseTypeAdapter);
        spDiseaseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                problemType = problemTypes[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
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
                            ActivityCompat.requestPermissions(DiseasePostActivity.this, PERMISSIONS, PERMISSION_ALL);
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
        name = etNewsTitle.getText().toString().trim();
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
        values.put(Constants.NAME, name);
        values.put(Constants.IMAGE_URL,downloadImageUrl);
        values.put(Constants.CASE_STUDY_URL,caseStudyUrl);
        values.put(Constants.DISEASE_TYPE,problemType);
        values.put(Constants.NUM_VIEW,0);
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        FireBaseUtils.mDatabaseDiseases.child(caseStudyUrl).child(url).setValue(values);
        mProgress.dismiss();
        Toast.makeText(DiseasePostActivity.this, "Item Posted",LENGTH_LONG).show();
    }

    /**
     * Uploads image to cloud storage
     */
    private void upload() {
        final ProgressDialog mProgress = new ProgressDialog(DiseasePostActivity.this);
        mProgress.setMessage("Uploading photo, please wait...");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
        final String url = FireBaseUtils.mDatabaseDiseases.push().getKey();
        filePath =FireBaseUtils.mStorageDiseaase.child(cropName.toLowerCase() +"/"+ url);
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
                    sendPost(task.getResult().toString(),url);
                    mProgress.dismiss();
                    finish();

                } else {
                    // Handle failures
                    UploadUtils.makeNotification("Image upload failed",DiseasePostActivity.this);
                }
            }
        });
    }


    /**
     * validates the entries before submission
     * @return true if successful
     */
    private boolean validate(){
        String newsTitle = etNewsTitle.getText().toString().trim();
//        name = etNews.getText().toString().trim();
        return EditorUtils.editTextValidator(newsTitle,etNewsTitle,"Type in disease name") &&
               // EditorUtils.editTextValidator(name,etNews, "Type in message") &&
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