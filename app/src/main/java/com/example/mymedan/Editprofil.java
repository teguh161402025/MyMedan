package com.example.mymedan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class Editprofil extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    CircleImageView userImg;
    TextView changePhoto;
    TextView email;
    Uri pickedImgUrl;
    Button ubahBtn;
    int ischangePhoto = 1;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference User = firestore.collection("User");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofil);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userImg = findViewById(R.id.edit_imguser);
        pickedImgUrl =currentUser.getPhotoUrl();
        Picasso.get().load(currentUser.getPhotoUrl()).into( userImg);

        final EditText namaUser = (EditText)findViewById(R.id.edit_namauser);
        final EditText telpUser = (EditText)findViewById(R.id.edit_telpuser);
        final EditText addressUser = (EditText)findViewById(R.id.edit_adressuser);
        email = findViewById(R.id.edit_emailuser);








        DocumentReference docRef = User.document(currentUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    namaUser.setText(currentUser.getDisplayName());
                    telpUser.setText(document.getString("telepon"));
                    addressUser.setText(document.getString("alamat"));

                }
            }
        });


        email.setText(currentUser.getEmail().toString());
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(),ResetEmail.class);
                startActivity(intent);
                finish();
            }
        });


        ubahBtn = findViewById(R.id.edit_ubahbtn);
        ubahBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SpotsDialog.Builder()
                        .setContext(Editprofil.this)
                        .setTheme(R.style.Custom2)

                        .build()
                        .show();
                final String name = namaUser.getText().toString();
               // final String email = emailUser.getText().toString();
                  //
                if (ischangePhoto < 1) {
                    StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photo");
                    final StorageReference imageFilePath = mStorage.child(pickedImgUrl.getLastPathSegment());

                    imageFilePath.putFile(pickedImgUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()

                                            .setPhotoUri(uri)
                                            .setDisplayName(name)
                                            .build();

                                    //currentUser.updateEmail(email);
                                    currentUser.updateProfile(profileUpdate)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Map<String, Object> postMap = new HashMap<>();
                                                        postMap.put("nama",namaUser);


                                                        postMap.put("UID",currentUser.getUid());
                                                        postMap.put("email", currentUser.getEmail());
                                                        postMap.put("alamat", addressUser);

                                                        postMap.put("telepon",telpUser);






                                                        firebaseFirestore.collection("User").document(currentUser.getUid()).update(postMap);
                                                        showMassage("berhasil Di Ubah");
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
                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();

                    //mAuth.signInWithEmailAndPassword(email,currentUser.)
                    currentUser.updateProfile(profileUpdate)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        showMassage("berhasil Di Ubah");
                                        updateUI();
                                    }
                                }
                            });
                }












            }
        });
        changePhoto = findViewById(R.id.edit_photoprofil);
        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(Editprofil.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(Editprofil.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(Editprofil.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        BringImagePicker();

                    }

                } else {

                    BringImagePicker();

                }
            }
        });


    }


    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(Editprofil.this);

    }

    private void updateUI() {

        Intent homeActivity =new Intent(getApplicationContext(),MainActivity.class);
        startActivity(homeActivity);
        finish();
    }

    private void showMassage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                pickedImgUrl =  result.getUri();
                userImg.setImageURI(pickedImgUrl);
                ischangePhoto = 0;


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }
}
