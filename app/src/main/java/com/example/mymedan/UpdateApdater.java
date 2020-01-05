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
import java.util.Date;
import java.util.Locale;

public class UpdateApdater extends FirestoreRecyclerAdapter<histori,UpdateApdater.UpdateHolder> {

    public UpdateApdater(@NonNull FirestoreRecyclerOptions<histori> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UpdateHolder holder, int position, @NonNull histori model) {
        Date currentTime = model.getTanggal().toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());

        //You can change "yyyyMMdd_HHmm.ss as per your requirement

        String currentDateandTime = sdf.format(currentTime);
       Picasso.get().load(model.getImage_url()).into( holder.imgimage_url);


        holder.textViewLokasi.setText(model.getLokasi());
        holder.textViewkategori.setText(model.getKategori_laporan());
        holder.textViewdeskripsi.setText("Laporan anda pada ,"+currentDateandTime+" ,"+model.getStatus_laporan());
        if (model.getStatus_laporan().equals("Belum Ditangani") ){

            Picasso.get().load(R.drawable.belum).into( holder.imgstatus_laporan);
        }
        else if (model.getStatus_laporan().equals("Sedang Ditangani") ){
            Picasso.get().load(R.drawable.sedang).into( holder.imgstatus_laporan);

        }
        else {
            Picasso.get().load(R.drawable.sudah).into( holder.imgstatus_laporan);
        }

    }

    @NonNull
    @Override
    public UpdateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_item,
                parent, false);
        return new UpdateHolder(v);
    }

    class UpdateHolder extends RecyclerView.ViewHolder{
        ImageView imgimage_url;
       TextView textViewLokasi;
        TextView textViewkategori;
        TextView textViewdeskripsi;
        ImageView imgstatus_laporan;
        public UpdateHolder(@NonNull View itemView) {
            super(itemView);
            imgimage_url = itemView.findViewById(R.id.img_update);
            textViewLokasi = itemView.findViewById(R.id.text_update_lokasi);
            textViewkategori = itemView.findViewById(R.id.text_update_kategori);
            textViewdeskripsi = itemView.findViewById(R.id.text_update);
            imgstatus_laporan = itemView.findViewById(R.id.laporan_status);

        }
    }
}
