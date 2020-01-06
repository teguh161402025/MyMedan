package com.example.mymedan;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference User = firestore.collection("User");
    FirebaseUser currentUser ;
    CircleImageView userImg;
    LinearLayout ubah_button;
    TextView Username,Email,Telp;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        ubah_button = view.findViewById(R.id.ubah_profil);
        CardView  Logout=(CardView) view.findViewById(R.id.logout);
         Username= view.findViewById(R.id.user_name);
        Email =view.findViewById(R.id.user_email);
         Telp = view.findViewById(R.id.user_telp);
        userImg = view.findViewById(R.id.user_img);

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent =new Intent(getContext(),LoginActivity.class);
                startActivity(intent);
                getActivity().finish();

            }
        });
        DocumentReference docRef = User.document(currentUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    Username.setText(currentUser.getDisplayName());
                    Picasso.get().load(currentUser.getPhotoUrl()).into( userImg);
                    Email.setText(currentUser.getEmail());
                    Telp.setText(document.getString("telepon"));

                }
            }
        });


        ubah_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity().getApplication(),Editprofil.class);
                startActivity(intent);

            }
        });

        return view;
    }





}
