package com.example.mymedan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import dmax.dialog.SpotsDialog;

import pub.devrel.easypermissions.EasyPermissions;

public class Laporkan extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, KategoriBottomSheet.BottomSheetListener{
    private ImageView ivCameraPreview;
    private static final String TAG = Home_Fragment.class.getSimpleName();
    private static final int CAMERA_REQUEST_CODE = 1450;
    private static final int CAMERA_PERMISSION_CODE = 1460;
    private ProgressBar newPostProgress;
    private GoogleMap mMap;
    LocationManager locationManager;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    private Marker marker;


    private String mCurrentPhotoPath;
    private TextView kategori_tittle;
    private CardView kategori_button;
    private TextView lokasiUser;
    private Button btnLaporkan;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private ImageView iconKategori;
    private FirebaseAuth firebaseAuth;
    private String kategoriLaporan;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    private String lokasi;
    private String lokasi_street;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_laporkan);

        if (EasyPermissions.hasPermissions(Laporkan.this, android.Manifest.permission.CAMERA)) {
            launchCamera();

        } else {
            //If permission is not present request for the same.
            EasyPermissions.requestPermissions(Laporkan.this, getString(R.string.permission_text), CAMERA_PERMISSION_CODE, Manifest.permission.CAMERA);
        }



        lokasiUser = findViewById(R.id.lokasi_user);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        kategori_tittle = findViewById(R.id.kategori_tittle);
        kategori_button = findViewById(R.id.kategori_button);

        newPostProgress = findViewById(R.id.new_post_progress);
        kategori_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KategoriBottomSheet bottomSheet = new KategoriBottomSheet();
                bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
            }
        });


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location mCurrentLocation = locationResult.getLastLocation();
                LatLng myCoordinates = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                lokasi = getCityName(myCoordinates);
                lokasi_street = getStreetname(myCoordinates);
                lokasiUser.setText(lokasi);

                //Toast.makeText(MainActivity.this, cityName, Toast.LENGTH_SHORT).show();

            }
        };

        if (Build.VERSION.SDK_INT >= 23) {
            Log.d("mylog", "Getting Location Permission");
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("mylog", "Not granted");
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else
                requestLocation();
        } else
            requestLocation();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


//You can change "yyyyMMdd_HHmmss as per your requirement
        newPostProgress.setVisibility(View.INVISIBLE);

        final EditText deskripsi = (EditText)findViewById(R.id.keteranganFoto);
        btnLaporkan= findViewById(R.id.btn_laporkan);
        btnLaporkan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new SpotsDialog.Builder()
                        .setContext(Laporkan.this)
                        .setTheme(R.style.Custom)

                        .build()
                        .show();
                final String randomName = UUID.randomUUID().toString();
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inSampleSize = 4;
                Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageData = baos.toByteArray();

                StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("Laporan_photo");
                final StorageReference imageFilePath = mStorage.child(randomName + ".jpg");
                imageFilePath.putBytes(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {



                        imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                    String urlImg = uri.toString();
                                    String pelapor = currentUser.getDisplayName();
                                    String email = currentUser.getEmail();
                                    String lokasiLaporan = lokasi;
                                    String lokasi_jalan = lokasi_street;
                                     String keterangan = deskripsi.getText().toString();

                                Map<String, Object> postMap = new HashMap<>();
                                postMap.put("image_url",urlImg);
                                postMap.put("deskripsi",keterangan);

                                postMap.put("kategori_laporan", kategoriLaporan);
                                postMap.put("status_laporan", "Belum Ditangani");
                                //postMap.put("image_thumb", downloadthumbUri);
                                postMap.put("pelapor",pelapor );
                                postMap.put("email", email);
                                postMap.put("lokasi", lokasi_jalan);
                                postMap.put("lokasi_lengkap", lokasiLaporan);
                                //postMap.put("Koordinat",pelapor );
                                postMap.put("tanggal", FieldValue.serverTimestamp());
                               // postMap.put("latitude", FieldValue.serverTimestamp());
                                //postMap.put("longitude", FieldValue.serverTimestamp());

                                firebaseFirestore.collection("Laporan").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {

                                        if(task.isSuccessful()){
                                            newPostProgress.setVisibility(View.INVISIBLE);
                                            Toast.makeText(Laporkan.this, "Berhasil Melaporkan", Toast.LENGTH_LONG).show();
                                            Intent mainIntent = new Intent(Laporkan.this, MainActivity.class);
                                            startActivity(mainIntent);
                                            finish();

                                        } else {

                                            Toast.makeText(Laporkan.this, "ERROR", Toast.LENGTH_LONG).show();
                                            newPostProgress.setVisibility(View.INVISIBLE);
                                        }


                                    }
                                });




                            }
                        });

                    }
                });


    }
        });
    }




    @Override
    public void onButtonClicked(String text ,String icon) {
        kategoriLaporan = text;
        iconKategori = findViewById(R.id.mini_category);
        Context c = getApplicationContext();
        int id = c.getResources().getIdentifier("drawable/"+icon, null, c.getPackageName());
        iconKategori.setImageResource(id);

      kategori_tittle.setText(text);


    }

    private String getCityName(LatLng myCoordinates) {
        String myCity = "";
        Geocoder geocoder = new Geocoder(Laporkan.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(myCoordinates.latitude, myCoordinates.longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            myCity = addresses.get(0).getAddressLine(0);
            Log.d("mylog", "Complete Address: " + addresses.toString());
            Log.d("mylog", "Address: " + address);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return myCity;
    }

    private String getStreetname(LatLng myCoordinates) {
        String myCity = "";
        String street = "";
        Geocoder geocoder = new Geocoder(Laporkan.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(myCoordinates.latitude, myCoordinates.longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            myCity = addresses.get(0).getAddressLine(0);
            street = addresses.get(0).getThoroughfare();
            Log.d("mylog", "Complete Address: " + addresses.toString());
            Log.d("mylog", "Address: " + address);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return street;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
    }

    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        Log.d("mylog", "In Requesting Location");
        if (location != null && (System.currentTimeMillis() - location.getTime()) <= 1000 * 2) {
            LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
            String cityName = getCityName(myCoordinates);
            Toast.makeText(this, cityName, Toast.LENGTH_SHORT).show();
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setNumUpdates(1);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            Log.d("mylog", "Last location too old getting new location!");
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.requestLocationUpdates(locationRequest,
                    mLocationCallback, Looper.myLooper());
        }
    }








    private void launchCamera() {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.mymedan",
                    photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            //Start the camera application
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }








    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Preview the image captured by the camera
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            ivCameraPreview = findViewById(R.id.ivCameraPreview);


            if (bitmap.getWidth() > bitmap.getHeight()) {
                Bitmap bOutput;
                float degrees = 90;//rotation degree
                Matrix matrix = new Matrix();
                matrix.setRotate(degrees);
                bOutput = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                ivCameraPreview.setImageBitmap(bOutput);

            }
            else
            {
                ivCameraPreview.setImageBitmap(bitmap);
            }




            // ivCameraPreview = getActivity().findViewById(R.id.citybackg);


        }










    }




    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, Laporkan.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        launchCamera();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "Permission has been denied");
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Laporkan.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;

    }
}