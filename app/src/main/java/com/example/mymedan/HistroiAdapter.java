package com.example.mymedan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HistroiAdapter extends FirestoreRecyclerAdapter<histori, HistroiAdapter.HistoriHolder> {

    public HistroiAdapter(@NonNull FirestoreRecyclerOptions<histori> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull HistoriHolder holder, int position, @NonNull histori model) {
        Date currentTime = model.getTanggal().toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());

        //You can change "yyyyMMdd_HHmmss as per your requirement

        String currentDateandTime = sdf.format(currentTime);
        Picasso.get().load(model.getImage_url()).into( holder.imgimage_url);

        holder.textViewLokasi.setText(model.getLokasi());
        holder.textViewkategori.setText(model.getKategori_laporan());
        holder.textViewdeskripsi.setText(model.getDeskripsi());
        if (model.getStatus_laporan().equals("Belum Ditangani") ){

            Picasso.get().load(R.drawable.belum).into( holder.imgstatus_laporan);
        }
        else if (model.getStatus_laporan().equals("Sedang Ditangani") ){
            Picasso.get().load(R.drawable.sedang).into( holder.imgstatus_laporan);

        }
        else {
            Picasso.get().load(R.drawable.sudah).into( holder.imgstatus_laporan);
        }


        holder.textViewtanggal.setText(currentDateandTime);
    }

    @NonNull
    @Override
    public HistoriHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.histori_item,
                parent, false);
        return new HistoriHolder(v);
    }

    class HistoriHolder extends RecyclerView.ViewHolder{
       ImageView imgimage_url;
        TextView textViewLokasi;
        TextView textViewkategori;
        TextView textViewdeskripsi;
        ImageView imgstatus_laporan;
        TextView textViewtanggal;

        public HistoriHolder(@NonNull View itemView) {
            super(itemView);
            imgimage_url = itemView.findViewById(R.id.laporan_photo);
            textViewLokasi = itemView.findViewById(R.id.lokasi_laporan);
            textViewkategori = itemView.findViewById(R.id.kategori_laporan);
            textViewdeskripsi = itemView.findViewById(R.id.deskripsi_laporan);
            imgstatus_laporan = itemView.findViewById(R.id.status_laporan);
            textViewtanggal = itemView.findViewById(R.id.waktu_lokasi_laporan);


        }
    }

}
