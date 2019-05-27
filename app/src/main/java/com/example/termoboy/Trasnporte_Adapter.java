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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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
import java.util.concurrent.CountDownLatch;

public class Trasnporte_Adapter extends RecyclerView.Adapter<Trasnporte_Adapter.TransporteViewHolder> {

    private ArrayList<transporte_item> listaTransportesA;
    private boolean cliked = false;
    private Object sincronizedObj = new Object();

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;

    public Trasnporte_Adapter(ArrayList<transporte_item> listaTransportesA, boolean mClicked) {
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
        private CardView mBgCard;
        private long numeroTransporte;
        private boolean mainClicked = false;
        private String transporteID;
        //Faltaria lo de la barra de progreso

        public TransporteViewHolder(@NonNull final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgtransporte);
            mTextView = itemView.findViewById(R.id.nombreTransporte);
            mProgress = itemView.findViewById(R.id.progressBar);
            mBgCard = itemView.findViewById(R.id.cardviewItemsTrasnporte);

            ThreadProgress thread = new ThreadProgress();
            thread.start();

            itemView.setOnClickListener(new ListenerClick());

            if (cliked) {
                viewClicked();
            }
        }

        private void viewClicked() {
            synchronized (sincronizedObj) {
                cliked = true;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sincronizedObj.notifyAll();
            }
        }

        class ListenerClick implements View.OnClickListener{

            private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            @Override
            public void onClick(View v) {
                if (!cliked) {
                    mainClicked = true;
                    //mBgCard.setBackgroundColor(itemView.getContext().getColor(R.color.colorPrimaryDark));
                    viewClicked();

                    Date date = new Date();
                    mFirebaseAuth = FirebaseAuth.getInstance();

                    final String userID = mFirebaseAuth.getUid();

                    mFirebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = mFirebaseDatabase.getReference("Dia");

                    //Guarda valor si el usuario ha escrito alguna vez ese día
/*
                                                DatabaseReference a = transporte.getRef();
                                                a.child("Coche").setValue("eeee");*/
                    Map<String, Object> dato = new HashMap<>();
                    dato.put(userID,"");

                    //Crea si no existe la crea y si existe escribe encima (no hya mucha diferencia)
                    databaseReference.child(dateFormat.format(date)+"/Transporte/"+transporteID).updateChildren(dato);
                }
            }
        }

        class ThreadProgress extends Thread {

            @Override
            public void run() {
                synchronized (sincronizedObj) {
                    try {
                        sincronizedObj.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (!mainClicked) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                int numCount = 0;
                mProgress.post(new Runnable() {
                    @Override
                    public void run() {
                        mProgress.setVisibility(View.VISIBLE);
                    }
                });

                do {
                    final int numProgress = numCount;
                    mProgress.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgress.setProgress(numProgress);
                        }
                    });
                    try {
                        Thread.sleep( numProgress/3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    numCount++;
                } while (numCount <= numeroTransporte);
            }
        }

    }

    @Override
    public void onBindViewHolder(@NonNull TransporteViewHolder transporteViewHolder, int i) {
        transporte_item currentItem = listaTransportesA.get(i);

        transporteViewHolder.imageView.setImageResource(currentItem.getTransporteImg());
        transporteViewHolder.mTextView.setText(currentItem.getTransporteNombre());
        transporteViewHolder.numeroTransporte = currentItem.gettransporteCantidad();
        transporteViewHolder.transporteID = currentItem.getTransporteID();
        transporteViewHolder.mProgress.setMax(100);
        if(!this.cliked){
            transporteViewHolder.mProgress.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return listaTransportesA.size();
    }

}
