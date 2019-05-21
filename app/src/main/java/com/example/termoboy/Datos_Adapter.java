package com.example.termoboy;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class Datos_Adapter extends RecyclerView.Adapter<Datos_Adapter.DatosViewHolder> {
    private ArrayList<Datos_item> listaDatossA;

    public static class DatosViewHolder extends RecyclerView.ViewHolder{
        public TextView txtParameto;
        public TextView txtValor;
        public TextView txtMedida;

        public DatosViewHolder(@NonNull View itemView) {
            super(itemView);
            txtParameto = itemView.findViewById(R.id.txtParametro);
            txtValor = itemView.findViewById(R.id.txtValorParametro);
            txtMedida = itemView.findViewById(R.id.txtMedida);

        }
    }

    public Datos_Adapter(ArrayList<Datos_item> listaDatossA){
        this.listaDatossA = listaDatossA;
    }

    @NonNull
    @Override
    public DatosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.datos_item, viewGroup,false);
        DatosViewHolder tvh = new DatosViewHolder(v);
        return tvh;
    }

    @Override
    public void onBindViewHolder(@NonNull DatosViewHolder DatosViewHolder, int i) {
        Datos_item currentItem = listaDatossA.get(i);

        DatosViewHolder.txtParameto.setText(currentItem.getNombre());
        DatosViewHolder.txtValor.setText(currentItem.getValor());
        DatosViewHolder.txtMedida.setText(currentItem.getMedida());

    }

    @Override
    public int getItemCount() {
        return listaDatossA.size();
    }
}
