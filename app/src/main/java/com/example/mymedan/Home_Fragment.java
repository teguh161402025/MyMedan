package com.example.mymedan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class Home_Fragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        if (Build.VERSION.SDK_INT >= 23) {
            Log.d("mylog", "Getting Location Permission");
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("mylog", "Not granted");
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            }
        CardView btnlaporkan = (CardView) view.findViewById(R.id.btnlaporkan);
        CardView btnhelp = (CardView) view.findViewById(R.id.button_help);
        CardView btninbox = (CardView) view.findViewById(R.id.button_inbox);
        btnlaporkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),Laporkan.class);
                startActivity(intent);


            }
        }); btnhelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               showMessage("Silahkan Klik Laporan Untuk Melapor");


            }
        }); btninbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showMessage("Belum Tersedia");


            }
        });
        return view;
    }

    private void showMessage(String text) {

        Toast.makeText(getContext(),text,Toast.LENGTH_LONG).show();
    }

}
