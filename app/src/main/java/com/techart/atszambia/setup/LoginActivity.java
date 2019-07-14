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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.techart.atszambia.MainActivity;
import com.techart.atszambia.R;
import com.techart.atszambia.constants.FireBaseUtils;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    // UI references.
    private ProgressDialog mProgress;

    private EditText mUsername;
    private EditText mPassWord;

    // Firebase references.
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FireBaseUtils.mDatabaseUsers;
        ImageView background = findViewById(R.id.scrolling_background);

        mUsername = findViewById(R.id.loginUsername);
        mPassWord = findViewById(R.id.loginPassword);
        TextView mLogin = findViewById(R.id.tv_login);
        TextView mReset = findViewById(R.id.tv_reset);
        TextView mRegister = findViewById(R.id.tv_register);

        background.setColorFilter(ContextCompat.getColor(this, R.color.colorTint));
      /*Glide.with(this)
            .load(R.drawable.larva)
            .into(background);*/

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveNetworkConnection()){
                    validUserCredentials();
                }else{
                    noIntenet();
                }
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveNetworkConnection()) {
                    Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                    registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(registerIntent);
                }else {
                    noIntenet();
                }
            }
        });

        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveNetworkConnection()) {
                    Intent registerIntent = new Intent(LoginActivity.this,PasswordResetDialog.class);
                    registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(registerIntent);
                }else {
                    noIntenet();
                }
            }
        });
    }

    private void validUserCredentials() {
        mProgress = new ProgressDialog(LoginActivity.this);
        email = mUsername.getText().toString().trim();
        String password = mPassWord.getText().toString().trim();
        if (validate(email,password)) {
            mProgress.setMessage("Logging in ...");
            mProgress.setCancelable(false);
            mProgress.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        validUsersExistance();
                    }else {
                        mProgress.dismiss();
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            mUsername.setError("Unrecognized email...! Use the email you registered with");
                        }
                        else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            mPassWord.setError("Wrong password, enter the password you registered with");
                        }
                        else {
                            Toast.makeText(LoginActivity.this,"We could not log you in, try again", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
    }

    private boolean validate(String email,String password){
        boolean valid = true;
        if (password.isEmpty()) {
            mPassWord.setError("enter a valid password");
            valid = false;
        } else {
            mPassWord.setError(null);
        }

        if (email.isEmpty()) {
            mUsername.setError("enter a valid email");
            valid = false;
        } else {
            mUsername.setError(null);
        }
        return valid;
    }

    private boolean haveNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netWorkInfo = cm.getActiveNetworkInfo();
            return netWorkInfo != null && netWorkInfo.getState() == NetworkInfo.State.CONNECTED;
        }
        return false;
    }

    private void validUsersExistance() {
        final String userId = mAuth.getCurrentUser().getUid();
        if (userId != null) {
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mProgress.dismiss();
                    if (dataSnapshot.hasChild(userId)) {
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    } else {
                        Toast.makeText(LoginActivity.this, "You need to setup an Account", Toast.LENGTH_LONG).show();
                        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                        registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(registerIntent);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            mProgress.dismiss();
            Toast.makeText(LoginActivity.this, "Error encountered, Try again later", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        //disable going back to the Main activity
       moveTaskToBack(true);
    }

    private void noIntenet(){
        Toast.makeText(LoginActivity.this,"No internet...! Turn on Data or Wifi.", Toast.LENGTH_LONG).show();
    }

}