package com.werkhaizer.anjo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.werkhaizer.anjo.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import model.EnderecoAdicional;


public class listaEnderecosAdapter extends RecyclerView.Adapter<listaEnderecosAdapter.endViewHolder>{

    private List<EnderecoAdicional> enderecos;
    private Context context;

    public listaEnderecosAdapter(List<EnderecoAdicional> adicionalList, Context c) {
        this.enderecos = adicionalList;
        this.context = c;
    }

    @NonNull
    @Override
    public endViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemLista = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_endereco_adicional, viewGroup, false);
        return new endViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull endViewHolder endViewHolder, int i) {
        EnderecoAdicional enderecoAdicional = enderecos.get(i);

        endViewHolder.title.setText(enderecoAdicional.getTitle());
        endViewHolder.endereco.setText(enderecoAdicional.getRua() + enderecoAdicional.getBairro() + enderecoAdicional.getCidade() + enderecoAdicional.getUf());
        endViewHolder.lat.setText(enderecoAdicional.getLatitude().toString());
        endViewHolder.lng.setText(enderecoAdicional.getLongitude().toString());
    }


    @Override
    public int getItemCount() {
        return enderecos.size();
    }

    public class endViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView endereco;
        TextView lat;
        TextView lng;

        public endViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.lblTitleEnd);
            endereco = itemView.findViewById(R.id.lblEndAdc);

            lat = itemView.findViewById(R.id.lat);
            lng = itemView.findViewById(R.id.lng);
        }
    }

}

