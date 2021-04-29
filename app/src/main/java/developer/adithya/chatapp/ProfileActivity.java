package developer.adithya.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.util.HashMap;

import developer.adithya.chatapp.Model.Users;

public class ProfileActivity extends AppCompatActivity {
    private EditText Name;
    private TextView PhoneNum;
    private ImageView profileImage ;
    private ImageButton edit , editImg;
    private Button save , cancel;
    private Users user;
    private StorageReference dbImg;
    public static final int IMAGE_REQUEST = 11;
    private Uri imageUri ;
    private UploadTask uploadTask;
    private DatabaseReference dbUser;
    private static final String TAG = "ProfileActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Name = findViewById(R.id.editTextTextPersonName2);
        PhoneNum = findViewById(R.id.textView10);
        profileImage = findViewById(R.id.imageView3);
        edit = findViewById(R.id.btnEdit);
        save = findViewById(R.id.button2);
        cancel = findViewById(R.id.button3);
        editImg = findViewById(R.id.btnEdit2);

        dbImg = FirebaseStorage.getInstance().getReference("profile");



        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        dbUser = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());

        UpdateUI();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name.setEnabled(true);
                save.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                Name.setSelected(true);


            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = Name.getText().toString();
                if (!newName.isEmpty()){
                    dbUser.child("name").setValue(newName);
                    Name.setEnabled(false);
                }else {
                    Name.setError("Enter a name");
                }

                Name.setEnabled(false);
                save.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(Users.class);
                        assert user != null;
                        Name.setText(user.getName());
                        PhoneNum.setText(user.getPhoneNumber());
                        if (user.getImageUrl().equals("default")){
                            profileImage.setImageResource(R.drawable.ic_default);
                        }else  {
                            Glide.with(ProfileActivity.this)
                                    .asBitmap()
                                    .load(user.getImageUrl())
                                    .into(profileImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                Name.setEnabled(false);
                save.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
            }

        });

        editImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(ProfileActivity.this);
            }
        });
    }

    private void UpdateUI() {
        dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(Users.class);
                assert user != null;
                Name.setText(user.getName());
                PhoneNum.setText(user.getPhoneNumber());
                if (user.getImageUrl().equals("default")){
                    profileImage.setImageResource(R.drawable.ic_default);
                }else  {
                    Glide.with(ProfileActivity.this)
                            .asBitmap()
                            .load(user.getImageUrl())
                            .into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void UploadImage(){
        Log.d(TAG, "UploadImage: Started");

        final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        final StorageReference fileReference =  dbImg.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if (imageUri != null){
            

            Log.d(TAG, "UploadImage:             1");

            uploadTask = fileReference.putFile(imageUri);
            Log.d(TAG, "UploadImage:             2");
            uploadTask
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }

                                    // Continue with the task to get the download URL
                                    return fileReference.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        String UriString = downloadUri.toString();
                                        dbUser.child("imageUrl").setValue(UriString);


//                                        finish();
//                                        overridePendingTransition(0, 0);
//                                        startActivity(getIntent());
//                                        overridePendingTransition(0, 0);

                                        dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    Users user = snapshot.getValue(Users.class);
                                                    assert user != null;
                                                    Name.setText(user.getName());
                                                    Glide.with(ProfileActivity.this)
                                                            .asBitmap()
                                                            .load(user.getImageUrl())
                                                            .into(profileImage);

                                                }else {
                                                    profileImage.setImageResource(R.drawable.ic_default);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    } else {
                                        // Handle failures
                                        // ...
                                    }
                                }
                            });
                            
                            
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
        else {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_LONG).show();
        }





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == IMAGE_REQUEST
//                && resultCode == RESULT_OK
//                && data != null
//                && data.getData() != null
//                ) {
//
//            tUri = data.getData();
//
//
//            if (uploadTask != null && uploadTask.isInProgress()){
//                Toast.makeText(this, "Upload in progress", Toast.LENGTH_SHORT).show();
//            }else {
//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .setAspectRatio(1,1)
//                        .start(this);
//            }
//
//        }


//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
//                && resultCode == RESULT_OK
//                && data != null
//                && data.getData() != null
//        ){
//            Log.d(TAG, "onActivityResult: if Invoked");
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//
//            if (requestCode == RESULT_OK){
//                Log.d(TAG, "onActivityResult: if 2 invoked");
//                imageUri = result.getUri();
//                UploadImage();
//            }
//        }else {
//            Log.d(TAG, "onActivityResult: else invoked");
//        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.d(TAG, "onActivityResult: if init");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: if 2 init");
                imageUri = result.getUri();
                UploadImage();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, "onActivityResult: Error: " + error.getMessage());
            }
        }
    }
}