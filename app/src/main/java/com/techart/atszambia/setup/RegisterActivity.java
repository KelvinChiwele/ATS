package com.techart.atszambia.setup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ServerValue;
import com.techart.atszambia.MainActivity;
import com.techart.atszambia.R;
import com.techart.atszambia.constants.Constants;
import com.techart.atszambia.constants.FireBaseUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles registration process
 */
public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etLogin;
    private EditText etPassword;
    private EditText etRepeatedPassword;
    private String firstPassword;
    private String name;
    private String province;
    private String email;
    private ProgressDialog mProgress;
    private AutoCompleteTextView atvProvince;
    String[] provinces;
    List<String> listProvinces ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.et_username);
       // etDistrict = findViewById(R.id.et_district);
        atvProvince =  findViewById(R.id.et_district);
        etLogin = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etRepeatedPassword = findViewById(R.id.et_repeatPassword);
        TextView btRegister = findViewById(R.id.tv_register);
        btRegister.setClickable(true);
        //ImageView background = findViewById(R.id.scrolling_background);

        provinces = getResources().getStringArray(R.array.list_of_provinces);
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                                               (this,android.R.layout.simple_list_item_1,provinces);
        atvProvince.setAdapter(adapter);
        listProvinces = Arrays.asList(provinces);
       /* background.setColorFilter(ContextCompat.getColor(this, R.color.colorTint));
        Glide.with(this)
                .load(R.drawable.larva)
                .into(background);*/

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveNetworkConnection()) {
                    if (validateCredentials()) {
                        startRegister();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this,"Ensure that your internet is working",Toast.LENGTH_LONG ).show();
                }
            }
        });
    }

    private boolean haveNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netWorkInfo = cm.getActiveNetworkInfo();
            return netWorkInfo != null && netWorkInfo.getState() == NetworkInfo.State.CONNECTED;
        }
        return false;
    }

    /**
     * implementation of the registration
     */
    private void startRegister() {
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Signing Up  ...");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,firstPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Map<String,Object> values = new HashMap<>();
                    values.put("name",name);
                    //values.put("phoneNumber",phone);
                    values.put("imageUrl","default");
                    values.put("province",province);
                    values.put("signedAs","Farmer");
                    values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);

                    FireBaseUtils.mDatabaseUsers.child(FireBaseUtils.getUiD()).setValue(values);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();
                    if (user != null) {
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "User profile updated.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                        mProgress.dismiss();
                        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    } else {
                        mProgress.dismiss();
                        Toast.makeText(RegisterActivity.this, "Error encountered, Please try again later", Toast.LENGTH_LONG).show();
                    }
                } else {
                    mProgress.dismiss();
                    if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                        etLogin.setError("User already exits, use another email address");
                    }
                    else {
                        Toast.makeText(RegisterActivity.this,"Error encountered, Please try again later", Toast.LENGTH_LONG ).show();
                    }
                }
            }
        });
    }

    /**
     * Validates the entries
     * @return true if they all true
     */
    private boolean validateCredentials() {
        firstPassword =  etPassword.getText().toString().trim();
        String repeatedPassword = etRepeatedPassword.getText().toString().trim();
        name =  etUsername.getText().toString().trim();
        province =  atvProvince.getText().toString().trim();
        email = etLogin.getText().toString().trim();
        boolean valid = true;

        if (name.isEmpty() || name.length() < 4) {
            etUsername.setError("enter a valid name");
            valid = false;
        } else {
            etUsername.setError(null);
        }

        if (province.isEmpty()) {
            atvProvince.setError("Type atleast two letters of your province and choose from the displayed list");
            valid = false;
        } else {
            atvProvince.setError(null);
        }

        if (!listProvinces.contains(province)) {
            atvProvince.setError("Type atleast two letters of your province and choose from the displayed list e.g Ea");
            valid = false;
        } else {
            atvProvince.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etLogin.setError("enter a valid email address");
            valid = false;
        } else {
            etLogin.setError(null);
        }

        if (firstPassword.isEmpty() || firstPassword.length() < 4) {
            etPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        if (firstPassword.equals(repeatedPassword)) {
            etPassword.setError(null);
        } else {
            etRepeatedPassword.setError("password does not match first password");
            valid = false;
        }
        return valid;
    }
}

