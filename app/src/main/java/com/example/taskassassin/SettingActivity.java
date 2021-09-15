package com.example.taskassassin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.iceteck.silicompressorr.SiliCompressor;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingActivity extends AppCompatActivity {

    private Button signOutBtn, aboutBtn;
    private FirebaseAuth mAuth;
    private CircleImageView userProfileImage;
    int Image_Request_Code = 7;
    Uri FilePathUri;
    FirebaseUser firebaseUser;
    private StorageReference imageRef;
    private TextView passowrdResetTxt;
    private DatabaseReference thumbReference;
    private StorageTask<UploadTask.TaskSnapshot> storageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().hide();

        userProfileImage = findViewById(R.id.setting_profile);
        passowrdResetTxt = findViewById(R.id.password_reset_txt);
        aboutBtn = findViewById(R.id.about_btn);
        signOutBtn = findViewById(R.id.sign_out);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        thumbReference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());


        imageRef = FirebaseStorage.getInstance().getReference().child("image");
        displayImage();

        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aboutIntent = new Intent(SettingActivity.this, AboutActivity.class);
                Bundle bndlAnimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_right, R.anim.fix_anim).toBundle();
                startActivity(aboutIntent, bndlAnimation);
            }
        });

        passowrdResetTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.sendPasswordResetEmail(firebaseUser.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getBaseContext(), "Check your email", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(SettingActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), Image_Request_Code);


            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                Intent signInIntent = new Intent(SettingActivity.this, SignInActivity.class);
                startActivity(signInIntent);
            }
        });
    }

    private void displayImage() {
        thumbReference.child("thumb_image").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue().toString();
                if (!url.equals("default")){
                    Glide.with(SettingActivity.this).load(url)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .into(userProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            CropImage.activity(FilePathUri)
                    .setAspectRatio(1, 1)
                    .start(SettingActivity.this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());


                 Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxHeight(200)
                        .setMaxWidth(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_filePath);



                userProfileImage.setImageBitmap(thumb_bitmap);
                if (storageTask != null && storageTask.isInProgress()) {
                    Toast.makeText(this, "Uplaod in progress", Toast.LENGTH_LONG).show();
                } else {
                    uploadImage();
                }

           } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                System.out.println(error);
            }
         }
        }

    private String getExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

     private void uploadImage(){
        final StorageReference storageReference = imageRef.child("thumb").child(firebaseUser.getUid() + getExtension(FilePathUri));
         storageTask = storageReference.putFile(FilePathUri);
         Task<Uri> urlTask = storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
             @Override
             public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                 if (!task.isSuccessful()) {
                     throw task.getException();
                 }

                 // Continue with the task to get the download URL
                 return storageReference.getDownloadUrl();
             }
         }).addOnCompleteListener(new OnCompleteListener<Uri>() {
             @Override
             public void onComplete(@NonNull Task<Uri> task) {
                 if (task.isSuccessful()) {
                     Uri downloadUri = task.getResult();
                     System.out.println(downloadUri);
                     thumbReference.child("thumb_image").setValue(""+downloadUri);
                     Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_LONG).show();
                 } else {

                     Toast.makeText(getApplicationContext(), "Cann't download the image", Toast.LENGTH_LONG).show();
                     // Handle failures
                     // ...
                 }
             }
         });
    }


}
