package com.example.termoboy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class fragment_transporte extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapterTransporte;
    private RecyclerView.LayoutManager layoutManager;
    private boolean guardadoTransporte;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;

    public fragment_transporte() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();

        String userID = mFirebaseAuth.getUid();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = mFirebaseDatabase.getReference("Dia");
        databaseReference = mFirebaseDatabase.getReference("Dia/" + databaseReference.orderByKey().limitToLast(1)+ "Transporte");
        if(databaseReference.child(userID).getKey() != null){
            guardadoTransporte = true;
        }else{
            guardadoTransporte = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_fragment_transporte, container, false);

        ArrayList<transporte_item> listaDeTrasnportes = new ArrayList<>();
        listaDeTrasnportes.add(new transporte_item(R.drawable.ic_bici,"Bici",10));
        listaDeTrasnportes.add(new transporte_item(R.drawable.ic_coche,"Coche",30));
        listaDeTrasnportes.add(new transporte_item(R.drawable.ic_tren,"Transporte Público",40));
        listaDeTrasnportes.add(new transporte_item(R.drawable.ic_apie,"Caminando",50));

        recyclerView = view.findViewById(R.id.recycledTransporte);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        adapterTransporte = new Trasnporte_Adapter(listaDeTrasnportes, guardadoTransporte);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapterTransporte);

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
        }
    }
}
