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
import model.Mensagem;

import static helper.UsuarioFirebase.getIdentificadorUsuario;

public class mensagensAdapter extends RecyclerView.Adapter<mensagensAdapter.myViewHolder> {

    private List<Mensagem> mensagens;
    private Context context;
    private static final int TIPO_SISTEMA = -1;
    private static final int TIPO_REMETENTE = 0;
    private static final int TIPO_DESTINATARIO = 1;
    private static final int TIPO_DEL_REMETENTE = 2;
    private static final int TIPO_DEL_DESTINATARIO = 3;


    public mensagensAdapter(List<Mensagem> lista, Context c) {
        this.context = c;
        this.mensagens = lista;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = null;
        if (viewType == TIPO_REMETENTE) {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem_remetente, parent, false);
        } else if (viewType == TIPO_DESTINATARIO) {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem_destinatario, parent, false);
        } else if (viewType == TIPO_SISTEMA) {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem_sistema, parent, false);
        } else if (viewType == TIPO_DEL_REMETENTE) {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem_remetente_deletada, parent, false);
        } else if (viewType == TIPO_DEL_DESTINATARIO) {
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem_destinatario_deletada, parent, false);
        }
        return new myViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        Mensagem mensagem = mensagens.get(position);
        if (getItemViewType(position) == TIPO_SISTEMA) {
            if (!mensagem.getData().isEmpty()) {
                holder.mensagem.setText(mensagem.getData());
            }
        } else if (getItemViewType(position) == TIPO_REMETENTE || getItemViewType(position) == TIPO_DESTINATARIO) {
            if (!mensagem.getData().isEmpty()) {
                String[] getHorario = mensagem.getData().split("-");
                String horario = getHorario[1].trim();
                String[] horasGeral = horario.split(":");
                String horasSemSegundos = horasGeral[0] + ":" + horasGeral[1];

                holder.mensagem.setText(mensagem.getMensagem());
                holder.horario.setText(horasSemSegundos);
                holder.data.setText(mensagem.getData());
            }
        }
        else if(getItemViewType(position) == TIPO_DEL_DESTINATARIO){
            if (!mensagem.getData().isEmpty()) {
                String[] getHorario = mensagem.getData().split("-");
                String horario = getHorario[1].trim();
                String[] horasGeral = horario.split(":");
                String horasSemSegundos = horasGeral[0] + ":" + horasGeral[1];

                holder.mensagem.setText(context.getString(R.string.msgDeletada));
                holder.horario.setText(horasSemSegundos);
                holder.data.setText(mensagem.getData());
            }
        } else if(getItemViewType(position) == TIPO_DEL_REMETENTE){
            if (!mensagem.getData().isEmpty()) {
                String[] getHorario = mensagem.getData().split("-");
                String horario = getHorario[1].trim();
                String[] horasGeral = horario.split(":");
                String horasSemSegundos = horasGeral[0] + ":" + horasGeral[1];

                holder.mensagem.setText(context.getString(R.string.vcApagouMsg));
                holder.horario.setText(horasSemSegundos);
                holder.data.setText(mensagem.getData());
            }
        }
    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }

    @Override
    public int getItemViewType(int position) {
        Mensagem mensagem = mensagens.get(position);
        String idUsuario = getIdentificadorUsuario();
        try {
            if (!mensagem.getDeleted()) {
                if (idUsuario.equals(mensagem.getId_remetente())) {
                    return TIPO_REMETENTE;
                } else if (mensagem.getId_remetente().equals("sys")) {
                    return TIPO_SISTEMA;
                }
                return TIPO_DESTINATARIO;
            }
            else {
                if(idUsuario.concat("del").equals(mensagem.getId_remetente())){
                    return TIPO_DEL_REMETENTE;
                }
                else {
                    return TIPO_DEL_DESTINATARIO;
                }
            }
        }
        catch (Exception e){ e.printStackTrace(); }
        return TIPO_SISTEMA;
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView mensagem, horario, data;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            mensagem = itemView.findViewById(R.id.lblMensagemTexto);
            horario = itemView.findViewById(R.id.lblHorarioMensagem);
            data = itemView.findViewById(R.id.lblDataMensagem);
        }
    }
}
