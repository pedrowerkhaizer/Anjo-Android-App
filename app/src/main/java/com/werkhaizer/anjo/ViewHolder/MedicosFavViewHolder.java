package com.werkhaizer.anjo.ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;
import com.werkhaizer.anjo.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import utils.coordenadaSelecionada;

public class MedicosFavViewHolder extends RecyclerView.ViewHolder {
    View mView;

    public MedicosFavViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        itemView.setOnClickListener(v -> clickListener.onItemClick(v, getAdapterPosition()));

        itemView.setOnLongClickListener(v -> {
            clickListener.onItemLongClick(v, getAdapterPosition());
            return true;
        });

        mView.findViewById(R.id.fotoMedFav).setOnClickListener(v -> clickListener.onImageClick(mView, getAdapterPosition()));

    }
    private MedicosFavViewHolder.ClickListener clickListener;

    public void setMedicos(Context ctx, String cep_med, String convenio_med, String cpf_med , String crm_med , String email_med, String endereco_med,
                           String esp_med, String foto, String nascimento_med , String nome, Double rating, String sexo_med, Double lat_med, Double lng_med){
        TextView mEnderecoTV = mView.findViewById(R.id.distanciaMedFav);
        ImageView mFotoIV = mView.findViewById(R.id.fotoMedFav);
        TextView mNomeTV = mView.findViewById(R.id.nomeMedFav);
        TextView mRatingTV = mView.findViewById(R.id.ratingMedFav);
        TextView mEspTV = mView.findViewById(R.id.especialidadeMedFav);
        TextView mCrmTV = mView.findViewById(R.id.crmMedFav);


        mEspTV.setText(esp_med);
        String nomeExibir;

        coordenadaSelecionada mC = new coordenadaSelecionada();

        Double lat = mC.getLat();
        Double lng = mC.getLng();

        LatLng posicaoInicial = new LatLng(lat , lng);
        LatLng posicaiFinal = new LatLng(lat_med , lng_med);

        double distance = SphericalUtil.computeDistanceBetween(posicaoInicial, posicaiFinal);

        mEnderecoTV.setText(formatNumber(distance) + " da sua localização");

        if(sexo_med.equals("M")){
            nomeExibir = "dr. " + nome;
            mNomeTV.setText(nomeExibir);
        }
        else if(sexo_med.equals("F")){
            nomeExibir = "dra. " + nome;
            mNomeTV.setText(nomeExibir);
        }
        else
        {
            mNomeTV.setText(nome);
        }
        mCrmTV.setText(crm_med);
        mRatingTV.setText(rating.toString());
        Picasso.get().load(foto).into(mFotoIV);
    }

    //interface to send callbacks
    public interface ClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
        void onImageClick(View view, int position);
    }

    public void setOnClickListener(MedicosFavViewHolder.ClickListener yclickListener){
        clickListener = yclickListener;
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
