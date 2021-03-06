package com.example.mymedan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmail extends AppCompatActivity {
    private String kategoriLaporan;
    TextView email;
    Button feriv;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        feriv = findViewById(R.id.btn_feriv);
        email = findViewById(R.id.emailT);
        email.setText(currentUser.getEmail());
        currentUser.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    showMassage("Periksa Kotak Masuk Email Anda dan Verifikasi Email Anda");
                } else {
                    showMassage("Gagal Mengirim Email Verifikasi");
                }
            }
        });
        feriv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (currentUser.isEmailVerified()) {

                            feriv.setText("Selanjutnya");
                            Intent homeActivity =new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(homeActivity);
                            finish();
                        }
                        else{
                            showMassage("Periksa Kotak Masuk Email Anda dan Verifikasi Email Anda");
                            currentUser.sendEmailVerification();
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());



                        }
                    }
                });

            }
        });

    }

    private void showMassage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }
}
