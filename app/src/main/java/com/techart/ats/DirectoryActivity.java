package com.techart.ats;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.techart.ats.constants.Constants;
import com.techart.ats.constants.FireBaseUtils;
import com.techart.ats.models.Directory;

import static com.techart.ats.utils.ImageUtils.hasPermissions;

public class DirectoryActivity extends AppCompatActivity {

    private RecyclerView rvDirectory;
    private final String[] PERMISSIONS = {Manifest.permission.CALL_PHONE};
    private int PERMISSION_ALL = 1;

    private String phoneNumber;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Setup spinner
        Button btList = findViewById(R.id.more);
        btList.setText(R.string.directory);
        rvDirectory = findViewById(R.id.rv_directory);
        rvDirectory.setHasFixedSize(true);

        recyclerViewLayoutManager = new GridLayoutManager(this,2);
        rvDirectory.setLayoutManager(recyclerViewLayoutManager);
        bindView();
    }

    private void bindView() {
        FirebaseRecyclerOptions<Directory> response = new FirebaseRecyclerOptions.Builder<Directory>()
                                                             .setQuery(FireBaseUtils.mDatabaseDirectory.orderByChild(Constants.TOWN), Directory.class)
                                                             .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Directory, DirectoryViewHolder>(response) {
            @NonNull
            @Override
            public DirectoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.item_directory, parent, false);
                return new DirectoryViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DirectoryViewHolder viewHolder, int position, @NonNull final Directory model) {
                phoneNumber = model.getPhone();
                viewHolder.tvTown.setText(model.getTown());
                viewHolder.tvAddress.setText(model.getAddress());
                viewHolder.btCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:"+ model.getRepresentative()));
                        startActivity(callIntent);
                    }
                });
                viewHolder.btCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            onGetPermission(model.getPhone());
                        }   else {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:"+ model.getPhone()));
                            startActivity(callIntent);
                        }

                    }
                });

                viewHolder.btEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",model.getEmail(),null));
                        startActivity(Intent.createChooser(emailIntent,"Send email..."));
                    }
                });
            }
        };
        rvDirectory.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    /**
     * requests for permission in android >= 23
     */
    @TargetApi(23)
    private void onGetPermission(String phoneNumber) {
        // only for MarshMallow and newer versions
        if(!hasPermissions(this, PERMISSIONS)){
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                onPermissionDenied();
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
        } else {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:"+ phoneNumber));
            startActivity(callIntent);
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
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:"+ phoneNumber));
            startActivity(callIntent);
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
                            ActivityCompat.requestPermissions(DirectoryActivity.this, PERMISSIONS, PERMISSION_ALL);
                        }
                        if (button == DialogInterface.BUTTON_NEGATIVE) {
                            dialog.dismiss();
                        }
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("YOU NEED TO ALLOW APP TO ACTION DIAL")
                .setMessage("Without this permission you can action dial")
                .setPositiveButton("ALLOW", dialogClickListener)
                .setNegativeButton("DENY", dialogClickListener)
                .show();
    }



    public static class DirectoryViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvTown;
        TextView tvAddress;
        Button btCall;
        Button btEmail;
        View mView;

        public DirectoryViewHolder(View itemView) {
            super(itemView);
            tvTown = itemView.findViewById(R.id.tv_town);
            tvAddress = itemView.findViewById(R.id.tv_address);
            btCall = itemView.findViewById(R.id.bt_call);
            btEmail = itemView.findViewById(R.id.bt_email);

            this.mView = itemView;
        }
    }
}
