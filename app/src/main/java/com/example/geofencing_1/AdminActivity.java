package com.example.geofencing_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class AdminActivity extends AppCompatActivity {

    EditText disease, medication;
    Bitmap bitmap;
    public static final int PICK_IMAGE = 1;
    ImageView imageView;
    Uri mImageUri;
    String url = "";

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mDatabase = FirebaseDatabase.getInstance().getReference("medicines");
        mStorageRef = FirebaseStorage.getInstance().getReference("medicines");
        disease = findViewById(R.id.adddisease);
        medication = findViewById(R.id.addmedication);
        Button upload = findViewById(R.id.upload);
        Button selectImage = findViewById(R.id.pickimage);
        imageView = findViewById(R.id.imageView);


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dis = disease.getText().toString();
                String med = medication.getText().toString();
                if(dis.equals("") || med.equals("")){
                    Toast.makeText(AdminActivity.this, "Some Sections are found missing!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Uploading...", Toast.LENGTH_SHORT).show();
                    uploadFile(dis, med);
                }
                startActivity(new Intent(getApplicationContext(), AdminActivity.class)); // refreshing
                finish();
            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage();
//                selectImage();
            }
        });



    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(String dis, String med) {
        if (mImageUri != null) {
//            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
//                    + "." + getFileExtension(mImageUri));
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + "jpeg");
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    url = uri.toString();
                                    if(dis.equals("") || med.equals("") || url.equals("")){
                                        Toast.makeText(getApplicationContext(), "Some Error Occured While Uploading! Try Again Later.",Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Helper obj = new Helper(dis, med, url);
                                        mDatabase.push().setValue(obj);
                                    }
                                    //disease.setText(uri.toString());
                                }
                            });
//                            Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mProgressBar.setProgress(0);
//                                }
//                            }, 500);
                            Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_LONG).show();
//                            Upload upload = new Upload(mEditTextFileName.getText().toString().trim(),
//                                    taskSnapshot.getDownloadUrl().toString());
//                            String uploadId = mDatabaseRef.push().getKey();
//                            mDatabaseRef.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                //.setAspectRatio(1,1)
                .start(this);
    }

    private void selectImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }


    protected final void onActivityResult(final int requestCode, final int
            resultCode, final Intent i) {
        super.onActivityResult(requestCode, resultCode, i);

//        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && i != null){
//                Uri imageUri = i.getData();
//                loadImage();
//        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(i);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(bitmap);
//                btn.setVisibility(View.GONE);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //Exception error = result.getError();
                Toast.makeText(this, "Some Exceptions Found!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    }
