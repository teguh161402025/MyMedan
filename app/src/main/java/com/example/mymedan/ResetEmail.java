package com.example.mymedan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetEmail extends AppCompatActivity {
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    EditText field,field2;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_email);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        field = findViewById(R.id.field1);
        field2 = findViewById(R.id.field2);
        btn = findViewById(R.id.selanjutnya);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth
                        .signInWithEmailAndPassword(currentUser.getEmail(), field.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            currentUser.updateEmail(field2.getText().toString()) .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        showMassage( "Berhasil memperbarui Email");

                                        Intent intent =new Intent(getApplicationContext(),VerifyEmail.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });



                        }
                        else {
                            showMassage( "Gagal memperbarui Email");

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
