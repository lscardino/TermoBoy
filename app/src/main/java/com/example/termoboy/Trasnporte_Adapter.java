package com.example.termoboy;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class Trasnporte_Adapter extends RecyclerView.Adapter<Trasnporte_Adapter.TransporteViewHolder> {

    private ArrayList<transporte_item> listaTransportesA;
    private String cliked = null;
    private Object sincronizedObj = new Object();

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;


    public Trasnporte_Adapter(ArrayList<transporte_item> listaTransportesA, String mClicked) {
        Log.d("ADAPTER", "Entra al Adaptador");
        this.listaTransportesA = listaTransportesA;
        this.cliked = mClicked;
    }

    @NonNull
    @Override
    public TransporteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.transporte_item, viewGroup, false);
        TransporteViewHolder tvh = new TransporteViewHolder(mView);
        return tvh;
    }


    class TransporteViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView mTextView;
        private ProgressBar mProgress;
        private CardView mBgCardView;
        private long numeroTransporte;
        private boolean mainClicked = false;
        private String transporteID;
        private Map<String, Object> datoSubir;
        private CountDownLatch latchPrepararBarraListener;

        public TransporteViewHolder(@NonNull final View itemView) {
            super(itemView);
            Log.d("ADAPTER", "Relacion de FK ");
            imageView = itemView.findViewById(R.id.imgtransporte);
            mTextView = itemView.findViewById(R.id.nombreTransporte);
            mProgress = itemView.findViewById(R.id.progressBar);
            mBgCardView = itemView.findViewById(R.id.cardviewItemsTrasnporte);
            mProgress.setMax(100);
            this.latchPrepararBarraListener = new CountDownLatch(1);

            ThreadProgress thread = new ThreadProgress(this.latchPrepararBarraListener);
            thread.start();

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Para ver si fue Clicado antes
            if (cliked != null) {
                viewClicked();
            }
            itemView.setOnClickListener(new ListenerClick());
        }

        private void viewClicked() {
            synchronized (sincronizedObj) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("ADAPTER", "Despierten Todos !");
                sincronizedObj.notifyAll();
            }
            if (cliked.equals(this.transporteID)) {
                //mBgCardView.setBackgroundColor(itemView.getContext().getColor(R.color.colorPrimaryDark));
            }
        }

        private class ListenerClick implements View.OnClickListener {

            @Override
            public void onClick(View v) {
                if (cliked == null) {
                    mainClicked = true;
                    cliked = transporteID;
                    viewClicked();

                    mFirebaseAuth = FirebaseAuth.getInstance();

                    final String userID = mFirebaseAuth.getUid();

                    datoSubir = new HashMap<>();
                    datoSubir.put(userID, "Ha votado");

                    mFirebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = mFirebaseDatabase.getReference("Dia");

                    //Guarda valor si el usuario ha escrito alguna vez ese día
/*
                                                DatabaseReference a = transporte.getRef();
                                                a.child("Coche").setValue("eeee");*/

                    MiraHoraInternet horaInternetURL = new MiraHoraInternet();
                    String fFechaInternet = horaInternetURL.getDate();
                    Log.d("ADAPTER ", "Sube datos a " + fFechaInternet);

                    //Crea si no existe la crea y si existe escribe encima (no hya mucha diferencia)
                    databaseReference.child(fFechaInternet + "/Transporte").child(transporteID).updateChildren(datoSubir);
                }
            }
        }

        private class ThreadProgress extends Thread {
            private CountDownLatch downLatchPreparar;

            ThreadProgress(CountDownLatch latch) {
                this.downLatchPreparar = latch;
            }
            @Override
            public void run() {
                synchronized (sincronizedObj) {
                    try {
                        Log.d("ADAPTER", "Espera para BarraProgreso");
                        sincronizedObj.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (cliked != null) {

                    int numCount = 0;
                    mProgress.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgress.setVisibility(View.VISIBLE);
                        }
                    });
                    try {
                        this.downLatchPreparar.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("ADAPTER", "Que comienzo a contar MAX " + numeroTransporte);
                    do {
                        final int numProgress = numCount;
                        mProgress.post(new Runnable() {
                            @Override
                            public void run() {
                                mProgress.setProgress(numProgress);
                            }
                        });
                        try {
                            Thread.sleep(numProgress / 3);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        numCount++;
                    } while (numCount <= numeroTransporte);
                }else{
                    if(!mainClicked){
                        Log.d("ADAPTER", "No soy el Clicado");
                    }
                }
            }
        }

    }

    @Override
    public void onBindViewHolder(@NonNull TransporteViewHolder transporteViewHolder, int i) {
        transporte_item currentItem = listaTransportesA.get(i);

        Log.d("ADAPTER", "Recoge los datos de " + currentItem.getTransporteNombre());
        transporteViewHolder.imageView.setImageResource(currentItem.getTransporteImg());
        transporteViewHolder.mTextView.setText(currentItem.getTransporteNombre());
        transporteViewHolder.numeroTransporte = currentItem.gettransporteCantidad();
        transporteViewHolder.transporteID = currentItem.getTransporteID();
        if (this.cliked == null) {
            //transporteViewHolder.mProgress.setVisibility(View.INVISIBLE);
        }
        transporteViewHolder.latchPrepararBarraListener.countDown();
    }

    @Override
    public int getItemCount() {
        return listaTransportesA.size();
    }

}
