package com.techart.atszambia.setup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Handles registration process
 */
public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPhoneNumber;
    private EditText etDistrict;
    private EditText etLogin;
    private EditText etPassword;
    private EditText etRepeatedPassword;
    private String firstPassword;
    private String name;
    private String phone;
    private String district;
    private String email;
    private ProgressDialog mProgress;
    private TextView tvRegisterOption;
    private String signingInAs;
    private RelativeLayout rv_userType;

    private static final String PHONE_NUMBER = "09[5-7][0-9]{7}";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.et_username);
     //   etPhoneNumber = findViewById(R.id.et_phone);
        etDistrict = findViewById(R.id.et_district);

        rv_userType = findViewById(R.id.rv_userType);
        tvRegisterOption = findViewById(R.id.tv_register_option);
        etLogin = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etRepeatedPassword = findViewById(R.id.et_repeatPassword);
        TextView btRegister = findViewById(R.id.tv_register);
        btRegister.setClickable(true);
        ImageView background = findViewById(R.id.scrolling_background);

        background.setColorFilter(ContextCompat.getColor(this, R.color.colorTint));
        Glide.with(this)
                .load(R.drawable.larva)
                .into(background);

        final String[] options = new String[] {"Click me","Farmer", "Stockist"};

        Spinner signUpAs = findViewById(R.id.spinner);

        ArrayAdapter<String> pagesAdapter = new ArrayAdapter<>(this, R.layout.tv_dropdown, options);
        pagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pagesAdapter.notifyDataSetChanged();

        signUpAs.setAdapter(pagesAdapter);
        signUpAs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                signingInAs = options[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

        etLogin.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                email = etLogin.getText().toString().trim().trim();
                if (email.endsWith("atszambia.co.zm")) {
                    signingInAs = "ATS Staff";
                    rv_userType.setVisibility(View.GONE);
                }else {
                    rv_userType.setVisibility(View.VISIBLE);
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
                    values.put("phoneNumber",phone);
                    values.put("imageUrl","default");
                    values.put("district",district);
                    values.put("signedAs",signingInAs);
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
       // phone =  etPhoneNumber.getText().toString().trim();
        district =  etDistrict.getText().toString().trim();
        email = etLogin.getText().toString().trim();
        boolean valid = true;

        if (name.isEmpty() || name.length() < 4) {
            etUsername.setError("enter a valid name");
            valid = false;
        } else {
            etUsername.setError(null);
        }

       /* if (phone.isEmpty()) {
            etPhoneNumber.setError("enter a valid phone number");
            valid = false;
        } else if (!phone.matches(PHONE_NUMBER)){
            etPhoneNumber.setError("check the digits");
            valid = false;
        } else {
            etUsername.setError(null);
        }*/

        if (district.isEmpty()) {
            etDistrict.setError("enter a valid name for better service");
            valid = false;
        } else {
            etUsername.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etLogin.setError("enter a valid email address");
            valid = false;
        } else {
            //setDropDownVisibility(email);
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

        if (email.endsWith("atszambia.co.zm")) {
            return valid;
        }else if (signingInAs.equals("Click me")) {
            tvRegisterOption.setError("Click to select either ATS staff, Farmer or stockist");
            valid = false;
        } else {
            tvRegisterOption.setError(null);
        }
        return valid;
    }
}

