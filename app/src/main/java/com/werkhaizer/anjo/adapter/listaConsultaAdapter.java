package com.werkhaizer.anjo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;
import com.werkhaizer.anjo.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import helper.Base64Custom;
import model.Consultas;
import utils.coordenadaSelecionada;

public class listaConsultaAdapter extends RecyclerView.Adapter<listaConsultaAdapter.MyViewHolder> {

    private List<Consultas> consultasList;
    private Context context;
    private DatabaseReference mRef;


    public listaConsultaAdapter(List<Consultas> listaConsultas, Context c) {
        this.consultasList = listaConsultas;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(context)
                .inflate(R.layout.row_consulta, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Consultas consultas = consultasList.get(position);

        String espMed = consultas.getEspecialidade();
        String crmMed = consultas.getCrm();
        mRef = FirebaseDatabase.getInstance().getReference("Especialidades/"+espMed+"/medicos/"+crmMed);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nomeMed = dataSnapshot.child("nome_med").getValue().toString();
                String fotoMed = dataSnapshot.child("foto_med").getValue().toString();
                String latMed = dataSnapshot.child("latitude_med").getValue().toString();
                String lngMed = dataSnapshot.child("longitude_med").getValue().toString();
                String sexoMed =  dataSnapshot.child("sexo_med").getValue().toString();


                Picasso.get().load(fotoMed).into(holder.fotoMed);
                holder.latMed.setText(latMed);
                holder.lngMed.setText(lngMed);

                Double mLatMed, mLngMed;
                mLatMed = Double.parseDouble(latMed);
                mLngMed = Double.parseDouble(lngMed);

                Double lat = coordenadaSelecionada.getLat();
                Double lng = coordenadaSelecionada.getLng();

                LatLng posicaoInicial = new LatLng(lat, lng);
                LatLng posicaiFinal = new LatLng(mLatMed, mLngMed);

                double distance = SphericalUtil.computeDistanceBetween(posicaoInicial, posicaiFinal);

                holder.distancia.setText(formatNumber(distance) + " da sua localização");

                String nomeExibir;
                if (sexoMed.toUpperCase().trim().equals("M")) {
                    nomeExibir = "dr. " + nomeMed;
                    holder.nomeMed.setText(nomeExibir);
                } else if (sexoMed.equals("F")) {
                    nomeExibir = "dra. " + nomeMed;
                    holder.nomeMed.setText(nomeExibir);
                } else {
                    holder.nomeMed.setText(nomeMed);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        String id = Base64Custom.codificarBase64(consultas.getDia() + "+" + consultas.getHorario() + "+" + consultas.getEmail_paciente() + "+" + consultas.getCrm());
        holder.id.setText(id);

        holder.especialidadeMed.setText(espMed);
        holder.horarioConsulta.setText(String.format("%s às %s", consultas.getDia(), consultas.getHorario()));
        switch (consultas.getStatus()){
            case "A":
                holder.statusConsulta.setText("Não confirmada");
                break;
            case "C" :
                holder.statusConsulta.setText("Confirmada");
                break;
            case "F":
                holder.statusConsulta.setText("Concluída");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return consultasList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nomeMed, especialidadeMed, horarioConsulta, statusConsulta, crmMed, latMed, lngMed, distancia;
        TextView id;
        CircleImageView fotoMed;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nomeMed = itemView.findViewById(R.id.nome_med_consulta);
            especialidadeMed = itemView.findViewById(R.id.especialidade_med_consulta);
            horarioConsulta = itemView.findViewById(R.id.horario_consulta);
            statusConsulta = itemView.findViewById(R.id.status_consulta);
            crmMed = itemView.findViewById(R.id.crm_med_consulta);
            latMed = itemView.findViewById(R.id.lat_med_consulta);
            lngMed = itemView.findViewById(R.id.lng_med_consulta);
            fotoMed = itemView.findViewById(R.id.foto_med_consulta);
            distancia = itemView.findViewById(R.id.distancia_med_consulta);
            id = itemView.findViewById(R.id.id_consulta_med);
        }
    }
    private String formatNumber(double distance) {
        if ((distance / 1000) < 1) {
            return "Menos de 1km";
        } else if ((distance / 1000) > 100) {
            return "+99km";
        } else {
            String unit = "km";
            distance /= 1000;
            return String.format("%4.1f%s", distance, unit);
        }
    }
}
