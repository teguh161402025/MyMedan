package com.example.mymedan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    ImageView imgUserPhoto;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUrl;
    private EditText userEmail,userPassword,userPassword2,userName,userPhone,userAddres;
    private ProgressBar loadingProgress,loadingProgress2;
    private Button regbtn;
    private Button regbtn2;
    private FirebaseFirestore firebaseFirestore;
    FirebaseUser currentUser;
    ConstraintLayout register,register2;
    private FirebaseAuth mAuth;
    int ischangePhoto = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userEmail = findViewById(R.id.email);
        userPassword=findViewById(R.id.password);
        userPassword2=findViewById(R.id.r_password);
        userName=findViewById(R.id.name);
        loadingProgress=findViewById(R.id.progressBar);
        loadingProgress2=findViewById(R.id.progressBar2);
        userPhone=findViewById(R.id.phone_number);
        userAddres=findViewById(R.id.user_address);
        register= findViewById(R.id.registeremailpass);
        register2= findViewById(R.id.userinfo);
        loadingProgress.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        regbtn = findViewById(R.id.register);
        regbtn2 = findViewById(R.id.register2);
        register.setVisibility(View.VISIBLE);
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regbtn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);
                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                final String password2 = userPassword2.getText().toString();
                final String name = userName.getText().toString();
                final String phone =userPhone.getText().toString();
                final String address =userAddres.getText().toString();

                if(email.isEmpty() || password.isEmpty() || !password.equals(password2)){


                    showMassage("Please verify all fields");
                    regbtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }

                else{

                    createUSerAccount(email,name,password);

                }
            }



        });
        regbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regbtn2.setVisibility(View.VISIBLE);
                loadingProgress2.setVisibility(View.INVISIBLE);

                final String name = userName.getText().toString();
                final String phone =userPhone.getText().toString();
                final String address =userAddres.getText().toString();

                if(name.isEmpty() || phone.isEmpty() || address.isEmpty()){


                    showMassage("Please verify all fields");
                    regbtn2.setVisibility(View.VISIBLE);
                    loadingProgress2.setVisibility(View.INVISIBLE);
                }

                else{

                    updateUserInfo(name,pickedImgUrl,phone,address,mAuth.getCurrentUser());

                }
            }



        });
        imgUserPhoto = findViewById(R.id.regImg);
        imgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(Register.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(Register.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(Register.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        BringImagePicker();

                    }

                } else {

                    BringImagePicker();

                }
            }
        });
    }

    private void createUSerAccount(String email, final String name, String password) {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            showMassage("Account Create");
                            register.setVisibility(View.GONE);
                            register2.setVisibility(View.VISIBLE);
                            loadingProgress2.setVisibility(View.INVISIBLE);
                        }

                        else {
                            showMassage("Account Creation Failed"+ task.getException().getMessage());
                            regbtn.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);
                        }
                    }

                });

    }

    private void updateUserInfo(final String name, Uri pickedImgUrl, final String phone, final String adress, final FirebaseUser currentUser) {


        if(ischangePhoto < 1) {
            StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photo");
            final StorageReference imageFilePath = mStorage.child(pickedImgUrl.getLastPathSegment());
            imageFilePath.putFile(pickedImgUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)

                                    .setPhotoUri(uri)
                                    .build();
                            String urlImg = uri.toString();
                            Map<String, Object> postMap = new HashMap<>();
                            postMap.put("nama",name);

                            postMap.put("image_url",urlImg);

                            postMap.put("email", currentUser.getEmail().toString());
                            postMap.put("alamat", adress);

                            postMap.put("telepon",phone);


                            //postMap.put("Koordinat",pelapor );
                            postMap.put("tanggal_bergabung", FieldValue.serverTimestamp());
                            // postMap.put("latitude", FieldValue.serverTimestamp());
                            //postMap.put("longitude", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("User").add(postMap);
                            currentUser.updateProfile(profileUpdate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                showMassage("Register Complete");
                                                updateUI();
                                            }
                                        }
                                    });
                        }
                    });

                }
            });
        }

        else{
            String uri ="";
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();
            Map<String, Object> postMap = new HashMap<>();
            postMap.put("nama",name);
            postMap.put("image_url",uri);
            postMap.put("email", currentUser.getEmail().toString());
            postMap.put("alamat", adress);
            postMap.put("telepon",phone);


            //postMap.put("Koordinat",pelapor );
            postMap.put("tanggal_bergabung", FieldValue.serverTimestamp());
            // postMap.put("latitude", FieldValue.serverTimestamp());
            //postMap.put("longitude", FieldValue.serverTimestamp());

            firebaseFirestore.collection("User").add(postMap);
            currentUser.updateProfile(profileUpdate)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showMassage("Register Complete");
                                updateUI();
                            }
                        }
                    });

        }
    }

    private void updateUI() {

        Intent intent =new Intent(getApplicationContext(),VerifyEmail.class);
        startActivity(intent);
        finish();
    }

    private void showMassage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }

    private void openGallery() {

        Intent galleryintent  =  new Intent(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent,REQUESCODE);
    }


    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(Register.this);

    }

    private void checkAndRequestForPermission() {

        if (ContextCompat.checkSelfPermission(Register.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(Register.this ,Manifest.permission.READ_EXTERNAL_STORAGE))

                Toast.makeText(Register.this ,"Please Accept For Required Permission",Toast.LENGTH_SHORT).show();
            else{
                ActivityCompat.requestPermissions(Register.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PReqCode);

            }
        }
        else
            openGallery();




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                pickedImgUrl =  result.getUri();
                imgUserPhoto.setImageURI(pickedImgUrl);
                ischangePhoto = 0;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }
}