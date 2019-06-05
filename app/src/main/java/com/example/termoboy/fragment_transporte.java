package com.example.termoboy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class fragment_transporte extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapterTransporte;
    private RecyclerView.LayoutManager layoutManager;
    private String guardadoTransporte = null;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    String userIDString = "ERROR";

    private HashMap<String, Long> listaTotal = new HashMap<>();
    private long numTransporte;
    private ArrayList<transporte_item> listaDeTrasnportes = new ArrayList<>();

    public fragment_transporte() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = mFirebaseDatabase.getReference("Dia");
        Query ultimaFecha = databaseReference.orderByKey().limitToLast(1);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser usuario = mFirebaseAuth.getCurrentUser();

        if (usuario != null) {
            userIDString = usuario.getUid();
        }
        Log.d("TRANS", userIDString);

        MiraHoraInternet horaInternetURL = new MiraHoraInternet();
        String fFechaInternet = horaInternetURL.getDate();
        Log.d("TRANS tiempo URL", fFechaInternet );

        //Peta si se quita el día y est adentro de la app
        databaseReference.child(fFechaInternet + "/Transporte").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaTotal = new HashMap<>();
                for (DataSnapshot entrada : dataSnapshot.getChildren()) {
                    Log.d("TRANS", "Dato enviado " + entrada.getKey() + " de " + entrada.getChildrenCount());

                    String transporte = entrada.getKey();
                    listaTotal.put(transporte, entrada.getChildrenCount());

                    if (entrada.child(userIDString).exists()) {
                        guardadoTransporte = entrada.getKey().toString();
                        Log.d("TRANS", "Clickado y guardado transporte -> " + guardadoTransporte);
                    }
                }
                numTransporte = 0;
                for (Map.Entry pair : listaTotal.entrySet()) {
                    numTransporte += (long) pair.getValue();
                }
                recargarDatosBarraProgreso();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private int controlErrorMap(String keyMap) {
        try {
            return (int) ((listaTotal.get(keyMap) * 100) / numTransporte);
        } catch (NullPointerException ex) {
            return 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_fragment_transporte, container, false);

        recyclerView = view.findViewById(R.id.recycledTransporte);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        //recargarDatosBarraProgreso();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //recargarDatosBarraProgreso();
        }
    }

    private void recargarDatosBarraProgreso() {

        Log.d("TRANS","Metodo recarga Barra");
        listaDeTrasnportes = new ArrayList<>();
        listaDeTrasnportes.add(new transporte_item(R.drawable.ic_bici, getString(R.string.bici), "Bici", controlErrorMap("Bici")));
        listaDeTrasnportes.add(new transporte_item(R.drawable.ic_coche, getString(R.string.coche), "Coche", controlErrorMap("Coche")));
        listaDeTrasnportes.add(new transporte_item(R.drawable.ic_tren, getString(R.string.tPub), "Tpublico", controlErrorMap("Tpublico")));
        listaDeTrasnportes.add(new transporte_item(R.drawable.ic_apie, getString(R.string.Caminando), "Apie", controlErrorMap("Apie")));

        adapterTransporte = new Trasnporte_Adapter(listaDeTrasnportes, guardadoTransporte);
        recyclerView.setAdapter(adapterTransporte);
    }
}
