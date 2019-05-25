package com.example.termoboy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class fragment_transporte extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapterTransporte;
    private RecyclerView.LayoutManager layoutManager;
    private boolean guardadoTransporte = false;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;

    private final HashMap<String, Long> listaTotal = new HashMap<>();
    private long numTransporte;
    private ArrayList<transporte_item> listaDeTrasnportes = new ArrayList<>();

    public fragment_transporte() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mFirebaseAuth = FirebaseAuth.getInstance();

        final String userID = "Mimi";//mFirebaseAuth.getUid();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = mFirebaseDatabase.getReference("Dia");
        Query ultimaFecha = databaseReference.orderByKey().limitToLast(1);

        Log.d("TRANS", "Mira Query " + ultimaFecha.toString());

        //Mira la ultima fecha introducida.
        ultimaFecha.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot children : dataSnapshot.getChildren()) {

                    Log.d("TRANS", "Que dia " + children.getKey());
                    children.getRef().orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot children : dataSnapshot.getChildren()) {

                                Log.d("TRANS", "Que hora " + children.getKey());
                                if (children.getKey().equals("Transporte")) {
                                    Log.d("TRANS", "Es transporte");

                                    //Guarda valor si el usuario ha escrito alguna vez ese día
                                    //Mir los datos que hay dentro de Transporte
                                    children.getRef().addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot ds) {

                                            for (DataSnapshot entrada : ds.getChildren()) {
                                                Log.d("TRANS", "Dato enviado " + entrada.getKey() + " de " + entrada.getChildrenCount());

                                                String transporte = entrada.getKey();
                                                listaTotal.put(transporte, entrada.getChildrenCount());

                                                if (entrada.child(userID).exists()) {
                                                    guardadoTransporte = true;
                                                    Log.d("TRANS", "Clickado y guardado transporte -> " + guardadoTransporte);
                                                }
                                            }
                                            numTransporte = 0;
                                            for (Map.Entry pair : listaTotal.entrySet()) {
                                                numTransporte += (long) pair.getValue();
                                            }
                                            listaDeTrasnportes = new ArrayList<>();
                                            listaDeTrasnportes.add(new transporte_item(R.drawable.ic_bici, "Bici", controlErrorMap("Bici")));
                                            listaDeTrasnportes.add(new transporte_item(R.drawable.ic_coche, "Coche",controlErrorMap("Coche")));
                                            listaDeTrasnportes.add(new transporte_item(R.drawable.ic_tren, "Transporte Público", controlErrorMap("Tpublico")));
                                            listaDeTrasnportes.add(new transporte_item(R.drawable.ic_apie, "Caminando", controlErrorMap("Apie")));

                                        }

                                        private int controlErrorMap(String keyMap) {

                                            Log.d("TRANS", "Dato subir " + keyMap + " " + listaTotal.get(keyMap) +"/" + numTransporte);
                                            try {
                                                return (int)((listaTotal.get(keyMap) * 100) / numTransporte);
                                            } catch (NullPointerException ex) {
                                                return 0;
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError de) {
                                            Toast.makeText(getView().getContext(), "Lectura de datos cancelada", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("TRANS", "Fragment onCreateView");
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_fragment_transporte, container, false);

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
        if (isVisibleToUser) {
            adapterTransporte = new Trasnporte_Adapter(listaDeTrasnportes, guardadoTransporte);
            recyclerView.setAdapter(adapterTransporte);
        }
    }
}
