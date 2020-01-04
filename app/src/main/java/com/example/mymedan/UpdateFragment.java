package com.example.mymedan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class UpdateFragment extends Fragment {


    FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference Laporan = firestore.collection("Laporan");

    private UpdateApdater adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_update, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        setUpRecyclerView(inflate);

        return inflate;

    }
    private void setUpRecyclerView(View view) {
        String pelapor = currentUser.getEmail();
        Query query = Laporan.whereEqualTo("email", pelapor).whereIn("status_laporan", Arrays.asList("Sedang Ditangani","Sudah Ditangani"));

        FirestoreRecyclerOptions<histori> options = new FirestoreRecyclerOptions.Builder<histori>()
                .setQuery(query, histori.class)
                .build();

        adapter = new UpdateApdater(options);

        RecyclerView recyclerView = view.findViewById(R.id.recycle_view_update);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
