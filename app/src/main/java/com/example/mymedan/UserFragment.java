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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserFragment extends Fragment {


    FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    CircleImageView userImg;
    LinearLayout ubah_button;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        ubah_button = view.findViewById(R.id.ubah_profil);
        CardView  Logout=(CardView) view.findViewById(R.id.logout);
        TextView  Username=(TextView) view.findViewById(R.id.user_name);
        TextView  Email =(TextView) view.findViewById(R.id.user_email);
        userImg = view.findViewById(R.id.user_img);

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

       Username.setText(currentUser.getDisplayName());
        Picasso.get().load(currentUser.getPhotoUrl()).into( userImg);
        Email.setText(currentUser.getEmail());

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
