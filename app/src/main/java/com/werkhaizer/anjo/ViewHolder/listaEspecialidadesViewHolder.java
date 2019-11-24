package com.werkhaizer.anjo.ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.werkhaizer.anjo.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class listaEspecialidadesViewHolder extends RecyclerView.ViewHolder{
    View mView;


    public listaEspecialidadesViewHolder(@NonNull final View itemView) {
        super(itemView);
        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        });
    }

    private listaEspecialidadesViewHolder.ClickListener mClickListener;

    public void setEspecialidade(Context ctx, String nome){
        TextView mNomeTV = mView.findViewById(R.id.nome_esp);

        mNomeTV.setText(nome);
    }

    //interface to send callbacks
    public interface ClickListener {
        void onItemClick(View view, int position);

    }
    public void setOnClickListener(listaEspecialidadesViewHolder.ClickListener clickListener){

        mClickListener = clickListener;
    }

}
