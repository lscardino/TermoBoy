package com.example.termoboy;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class Trasnporte_Adapter extends RecyclerView.Adapter<Trasnporte_Adapter.TransporteViewHolder> {

    private ArrayList<transporte_item> listaTransportesA;
    private boolean cliked = false;
    private Object sincronizedObj = new Object();

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
        private long numeroTransporte;
        private boolean mainClicked = false;
        //Faltaria lo de la barra de progreso

        public TransporteViewHolder(@NonNull final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgtransporte);
            mTextView = itemView.findViewById(R.id.nombreTransporte);
            mProgress = itemView.findViewById(R.id.progressBar);

            ThreadProgress thread = new ThreadProgress();
            thread.start();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!cliked) {
                        mainClicked = true;
                        viewClicked();
                    }
                }
            });

            if (cliked) {
                viewClicked();
            }
        }

        private void viewClicked() {
            synchronized (sincronizedObj) {
                cliked = true;
                sincronizedObj.notifyAll();
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
                        Thread.sleep(3 + numProgress);
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
