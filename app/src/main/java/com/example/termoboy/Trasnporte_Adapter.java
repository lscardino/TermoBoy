package com.example.termoboy;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Trasnporte_Adapter extends RecyclerView.Adapter<Trasnporte_Adapter.TransporteViewHolder> {
private ArrayList<transporte_item> listaTransportesA;

    public static class TransporteViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView mTextView;
        //Faltaria lo de la barra de progreso

        public TransporteViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgtransporte);
            mTextView = itemView.findViewById(R.id.nombreTransporte);
        }
    }

    public Trasnporte_Adapter(ArrayList<transporte_item> listaTransportesA){
        this.listaTransportesA = listaTransportesA;
    }

    @NonNull
    @Override
    public TransporteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.transporte_item, viewGroup,false);
        TransporteViewHolder tvh = new TransporteViewHolder(v);
        return tvh;
    }

    @Override
    public void onBindViewHolder(@NonNull TransporteViewHolder transporteViewHolder, int i) {
        transporte_item currentItem = listaTransportesA.get(i);

        transporteViewHolder.imageView.setImageResource(currentItem.getTransporteImg());
        transporteViewHolder.mTextView.setText(currentItem.getTransporteNombre());

    }

    @Override
    public int getItemCount() {
        return listaTransportesA.size();
    }
}
