package com.werkhaizer.anjo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;
import com.werkhaizer.anjo.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import model.Medicos;
import utils.coordenadaSelecionada;

public class listaMedicosAdapter extends RecyclerView.Adapter<listaMedicosAdapter.myViewHolder> {

    private List<Medicos> medicosList;
    private Context context;

    public listaMedicosAdapter(List<Medicos> listaMedicos, Context c) {
        this.medicosList = listaMedicos;
        this.context = c;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(context).inflate(R.layout.row_medicos_lista, parent, false);

        return new myViewHolder(itemLista);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        Medicos medicos = medicosList.get(position);

        Double lat_med, lng_med;
        lat_med = medicos.getLatitude_med();
        lng_med = medicos.getLongitude_med();

        String sexo_med, nome;
        sexo_med = medicos.getSexo_med();
        nome = medicos.getNome_med();

        holder.rating.setText(medicos.getRating_med().toString());
        holder.crm.setText(medicos.getCrm_med());
        holder.lat.setText(lat_med.toString());
        holder.lng.setText(lng_med.toString());


        Double lat = coordenadaSelecionada.getLat();
        Double lng = coordenadaSelecionada.getLng();

        LatLng posicaoInicial = new LatLng(lat, lng);
        LatLng posicaiFinal = new LatLng(lat_med, lng_med);

        double distance = SphericalUtil.computeDistanceBetween(posicaoInicial, posicaiFinal);

        holder.endereco.setText(formatNumber(distance) + " da sua localização");

        String nomeExibir;
        if (sexo_med.toUpperCase().trim().equals("M")) {
            nomeExibir = "dr. " + nome;
            holder.nome.setText(nomeExibir);
        } else if (sexo_med.equals("F")) {
            nomeExibir = "dra. " + nome;
            holder.nome.setText(nomeExibir);
        } else {
            holder.nome.setText(nome);
        }

        Picasso.get().load(medicos.getFoto_med()).into(holder.foto);

    }

    @Override
    public int getItemCount() {
        return medicosList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        ImageView foto;
        TextView endereco, nome, rating, crm, lat, lng;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            endereco = itemView.findViewById(R.id.distancia_med);
            foto = itemView.findViewById(R.id.foto_med);
            nome = itemView.findViewById(R.id.nome_med);
            rating = itemView.findViewById(R.id.rating_med);
            crm = itemView.findViewById(R.id.crm_med);
            lat = itemView.findViewById(R.id.lat_med);
            lng = itemView.findViewById(R.id.lng_med);
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
