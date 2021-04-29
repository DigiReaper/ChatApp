package developer.adithya.chatapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import developer.adithya.chatapp.Model.Users;

public class RegisterActivity3 extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser fUser;
    private DatabaseReference dbUsers;
    private Button next;
    private StorageReference mStorageRef;
    private ImageView UImg;
    private EditText Name;
    private StorageReference dbImg;
    public static final int IMAGE_REQUEST = 11;
    private Uri imageUri ;
    private UploadTask uploadTask;
    private DatabaseReference dbUser;
    private static final String TAG = "ProfileActivity";
    private ImageButton editImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

        firebaseAuth = FirebaseAuth.getInstance();
        fUser = firebaseAuth.getCurrentUser();
        dbUsers = FirebaseDatabase.getInstance().getReference("Users");
        dbImg = FirebaseStorage.getInstance().getReference("profile");

        next = findViewById(R.id.button);
        editImg = findViewById(R.id.btnEdit2);
        UImg = findViewById(R.id.imageView2);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        Name = findViewById(R.id.editTextTextPersonName);

        UpdateUI();


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dbUsers.child(fUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){

                            if (!Name.getText().toString().isEmpty()){

                                HashMap<String , Object> tmap = new HashMap<>();

                                tmap.put("name" , Name.getText().toString());
                                dbUsers.child(fUser.getUid()).updateChildren(tmap);

                                startActivity(new Intent(RegisterActivity3.this , MainActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                                );
                                finish();
                            }

                        }else{

                            if (!Name.getText().toString().isEmpty()){
                                SaveUser();
                                startActivity(new Intent(RegisterActivity3.this , MainActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                                );
                                finish();
                            }else {
                                Name.setError("Please enter your name");
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




            }
        });


        editImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(RegisterActivity3.this);
            }
        });


    }

    private void SaveUser() {

        HashMap<String , Object> umap = new HashMap<>();

        umap.put("id" , fUser.getUid());
        umap.put("name" , Name.getText().toString());
        umap.put("phoneNumber" , fUser.getPhoneNumber());
        umap.put("status" , "offline");


        dbUsers.child(fUser.getUid()).updateChildren(umap);


    }

    private void UpdateUI(){
        Log.d(TAG, "UpdateUI: ");
        dbUsers.child(fUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Users user = snapshot.getValue(Users.class);
                    assert user != null;
                    Name.setText(user.getName());
                    Glide.with(RegisterActivity3.this)
                            .asBitmap()
                            .load(user.getImageUrl())
                            .into(UImg);

                }else {
                    UImg.setImageResource(R.drawable.ic_default);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void UploadImage(){
        Log.d(TAG, "UploadImage: Started");

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity3.this);
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
                            Toast.makeText(RegisterActivity3.this, "Uploaded", Toast.LENGTH_SHORT).show();

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
                                        dbUsers.child(fUser.getUid()).child("imageUrl").setValue(UriString);
//                                        finish();
//                                        overridePendingTransition(0, 0);
//                                        startActivity(getIntent());
//                                        overridePendingTransition(0, 0);
//

                                        dbUsers.child(fUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    Users user = snapshot.getValue(Users.class);
                                                    assert user != null;
                                                    Name.setText(user.getName());
                                                    Glide.with(RegisterActivity3.this)
                                                            .asBitmap()
                                                            .load(user.getImageUrl())
                                                            .into(UImg);

                                                }else {
                                                    UImg.setImageResource(R.drawable.ic_default);
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
                            Toast.makeText(RegisterActivity3.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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