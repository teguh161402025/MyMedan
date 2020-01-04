package com.example.mymedan;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class KategoriBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.kategori_laporan, container, false);

       CardView jalanRusak = v.findViewById(R.id.kategori_jalanrusak);
       CardView lampuJalanRusak = v.findViewById(R.id.kategori_lampujalanrusak);
       CardView pohonTumbang= v.findViewById(R.id.kategori_pohontumbang);
       CardView fasiliatsUmum= v.findViewById(R.id.kategori_fasilitasumum);
       CardView parkirLiar= v.findViewById(R.id.kategori_parkirliar);
       CardView lainNya= v.findViewById(R.id.kategori_lainnya);
        jalanRusak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked("Jalan Rusak" , "jalan1");
                dismiss();
            }
        });
        lampuJalanRusak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked("Lampu Jalan Rusak" , "lampujalan2");
                dismiss();
            }
        });
        pohonTumbang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked("Pohon Tumbang", "pohon1");
                dismiss();
            }
        });
        fasiliatsUmum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked("Fasilitas Umum", "taman1");
                dismiss();
            }
        });
        parkirLiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked("Parkir Liar", "parkir1");
                dismiss();
            }
        });
        lainNya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked("Kategori Lain nya", "lain1");
                dismiss();
            }
        });

        return v;
    }

    public interface BottomSheetListener {
        void onButtonClicked(String text ,String icon);

        void onMapReady(GoogleMap googleMap);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }
}
