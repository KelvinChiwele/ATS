package com.techart.atszambia;

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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.techart.atszambia.constants.Constants;
import com.techart.atszambia.constants.FireBaseUtils;
import com.techart.atszambia.models.Products;
import com.techart.atszambia.utils.EditorUtils;
import com.techart.atszambia.utils.ImageUtils;
import com.techart.atszambia.utils.UploadUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.techart.atszambia.utils.ImageUtils.hasPermissions;

/**
 * Created by Kelvin on 30/07/2017.
 * Handles actions related to asking question
 */

public class AskActivity extends AppCompatActivity {
    //string resources
    private String question;
    private String crop;
    private String problemType;
    private String realPath;

    private TextView tvProblemError;
    private Spinner spCrops;
    private Spinner spDiseaseType;
    private ImageView ibSample;
    private EditText etQuestion;
    private StorageReference filePath;
    private String userUrl;
    private Long pageCount;

    private ProgressBar progressBar;

    //image
    private static final int GALLERY_REQUEST = 1;
    private Uri uri;

    //Permission
    private final int PERMISSION_ALL = 1;
    private final String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_activity);
        etQuestion = findViewById(R.id.et_question);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ibSample = findViewById(R.id.ib_item);

        final String[] problemTypes =  getResources().getStringArray(R.array.problem_category);
        //Disease type
        spDiseaseType = findViewById(R.id.sp_category);
        ArrayAdapter<String> diseaseTypeAdapter = new ArrayAdapter<>(AskActivity.this, R.layout.tv_dropdown, problemTypes);
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

        //Crop type
        final String[] crops = getResources().getStringArray(R.array.crops);
        spCrops = findViewById(R.id.sp_crop);
        ArrayAdapter<String> pagesAdapter = new ArrayAdapter<>(AskActivity.this, R.layout.tv_dropdown, crops);
        pagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pagesAdapter.notifyDataSetChanged();

        spCrops.setAdapter(pagesAdapter);
        spCrops.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                crop = crops[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                            ActivityCompat.requestPermissions(AskActivity.this, PERMISSIONS, PERMISSION_ALL);
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
     * @return true if successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_post:
                if (validate()){
                    upload();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void countQuestions(final String downloadImageUrl, final String url) {
        FireBaseUtils.mDatabaseQuestions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pageCount = dataSnapshot.getChildrenCount();
                sendPost(downloadImageUrl,url,pageCount);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /**
     * Sends information to database
     * @param downloadImageUrl url of upload image
     */
    private void sendPost(String downloadImageUrl,String url,Long questionNumber) {
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.CROP, crop);
        values.put(Constants.PROBLEM_TYPE, problemType);
        values.put(Constants.QUESTION,question);
        values.put(Constants.POST_KEY,url);
        values.put(Constants.QUESTION_NUMBER,questionNumber);
        values.put(Constants.USER_URL, FireBaseUtils.getUiD());
        values.put(Constants.EMAIL, FireBaseUtils.getEmail());
        values.put(Constants.USER_NAME, userUrl);
        values.put(Constants.ANSWER_COUNT,0);
        values.put(Constants.CLIENT_CROP, FireBaseUtils.getUiD()+ " " + crop);
        values.put(Constants.IMAGE_URL,downloadImageUrl);
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        FireBaseUtils.mDatabaseQuestions.child(url).setValue(values);
    }



    /**
     * Uploads image to cloud storage
     */
    private void upload() {
        userUrl = FireBaseUtils.getAuthor();
        final String url = FireBaseUtils.mDatabaseNews.push().getKey();
        final ProgressDialog mProgress = new ProgressDialog(AskActivity.this);
        mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgress.setMessage("Uploading photo, please wait...");
        mProgress.setProgress(0);
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
        filePath = FireBaseUtils.mStorageQuestions.child(problemType + "/" + url);
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
                    onQuestionAsked();
                    countQuestions(task.getResult().toString(),url);
                    FireBaseUtils.updateNotifications("FAQ", crop, "asked a question on " + " " + problemType.toLowerCase() + " affecting " + crop, url, question, task.getResult().toString());
                    mProgress.dismiss();
                    finish();

                } else {
                    // Handle failures
                    UploadUtils.makeNotification("Image upload failed",AskActivity.this);
                }
            }
        });
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                mProgress.setProgress(currentProgress);
            }
        });
    }

    /**
     * validates the entries before submission
     * @return true if successful
     */
    private boolean validate(){
        question = etQuestion.getText().toString().trim();
        tvProblemError = (TextView)spDiseaseType.getSelectedView();
        //ui components
        TextView tvCropError = (TextView) spCrops.getSelectedView();
        return  EditorUtils.dropDownValidator(problemType,getResources().getString(R.string.default_problem_category),tvProblemError) &&
                EditorUtils.dropDownValidator(crop, getResources().getString(R.string.default_crop), tvCropError) &&
                EditorUtils.editTextValidator(question,etQuestion,"Type in the question");

    }

    private boolean isImageAttached(){
        return EditorUtils.imagePathValdator(this,realPath);
    }


    private static void onQuestionAsked() {
        FireBaseUtils.mDatabaseResources.child("FAQ").runTransaction(new Transaction.Handler() {
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