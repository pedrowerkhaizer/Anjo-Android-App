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
import model.Especialidades;

public class listaEspecialidadesAdapter extends RecyclerView.Adapter<listaEspecialidadesAdapter.myViewHolder> {

    private List<Especialidades> especialidades;
    private Context context;

    public listaEspecialidadesAdapter(List<Especialidades> listaEspecialidades, Context c) {
        this.especialidades = listaEspecialidades;
        this.context = c;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemLista = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_especialidade, viewGroup, false);
        return new myViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, int i) {
        Especialidades esp = especialidades.get(i);
        myViewHolder.nome_esp.setText(esp.getNome());
    }

    @Override
    public int getItemCount() {
        return especialidades.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView nome_esp;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            nome_esp = itemView.findViewById(R.id.nome_esp);
        }
    }

}
