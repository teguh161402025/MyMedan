package com.example.mymedan;

import com.google.firebase.Timestamp;

public class Update {
    String image_url;
    String kategori_laporan;
    String lokasi;
    String deskripsi;
    String status_laporan;
    Timestamp tanggal;
    public Update(){

    }
    public Update(String image_url,String kategori_laporan,String lokasi  , String deskripsi,String status_laporan ,Timestamp tanggal)
    {
        this.image_url = image_url;
        this.lokasi = lokasi;
        this.kategori_laporan = kategori_laporan;
        this.deskripsi = deskripsi;
        this.status_laporan = status_laporan;
        this.tanggal = tanggal;


    }

    public String getImage_url() {
        return image_url;
    }

    public String getKategori_laporan() {
        return kategori_laporan;
    }

    public String getLokasi() {
        return lokasi;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getStatus_laporan() {
        return status_laporan;
    }

    public Timestamp getTanggal() {
        return tanggal;
    }
}
