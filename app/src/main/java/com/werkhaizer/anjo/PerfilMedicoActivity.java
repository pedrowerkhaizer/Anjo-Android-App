package com.werkhaizer.anjo;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import helper.UsuarioFirebase;
import model.MedSelecionado;
import utils.coordenadaSelecionada;

import static utils.selectedMedicData.getCrm;
import static utils.selectedMedicData.getEspecialidade;

public class PerfilMedicoActivity extends AppCompatActivity {


    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private Button btnHorariosDisp;
    private Button btnBack;
    private LottieAnimationView lavFav;
    private DatabaseReference mRefMedico;
    private MedSelecionado medSelecionado = new MedSelecionado();

    private ValueEventListener valueEventListenerPerfil;

    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_medico);
        //TODO Colocar um espaço p biografia do médico
        //TODO Colocar um espaço p o médico expor comentários privados


        mRefMedico = mFirebaseDatabase.getReference("Especialidades").child(getEspecialidade()).child("medicos").child(getCrm());

        btnHorariosDisp = findViewById(R.id.btnHorariosDisp);
        btnHorariosDisp.setOnClickListener(v -> {
            vibrar();
            Intent intent = new Intent(PerfilMedicoActivity.this, horariosDispActivity.class);
            startActivity(intent);
        });

        btnBack = findViewById(R.id.btnBackLista);
        btnBack.setOnClickListener(v -> {
            vibrar();
            PerfilMedicoActivity.this.finish();

        });


        //Itens a serem preenchidos
        final TextView nome_perfil = findViewById(R.id.nome_med_perfil);
        final TextView esp_perfil = findViewById(R.id.especialidade_med_perfil);
        final TextView av_perfil = findViewById(R.id.avaliacao_med_perfil);
        final TextView conv_perfil = findViewById(R.id.convenios_med_perfil);
        final TextView dis_perfil = findViewById(R.id.distancia_med_perfil);
        final CircleImageView foto_med_perfil = findViewById(R.id.foto_med_perfil);
        lavFav = findViewById(R.id.lavFav);
        lavFav.setOnClickListener(v -> {
            vibrar();
            onStart();
        });


        valueEventListenerPerfil = mRefMedico.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //buscando as informações do crm selecionado e passando para a classe modelo do medico selecionado
                medSelecionado.setConvenio_med(dataSnapshot.child("convenio_med").getValue().toString());
                medSelecionado.setEndereco_med(dataSnapshot.child("endereco_med").getValue().toString());
                medSelecionado.setEsp_med(dataSnapshot.child("esp_med").getValue().toString());
                medSelecionado.setFoto_med(dataSnapshot.child("foto_med").getValue().toString());
                medSelecionado.setNome_med(dataSnapshot.child("nome_med").getValue().toString());
                medSelecionado.setRating_med(Double.valueOf(dataSnapshot.child("rating_med").getValue().toString()));
                medSelecionado.setCep_med(dataSnapshot.child("cep_med").getValue().toString());
                medSelecionado.setCrm_med(dataSnapshot.child("crm_med").getValue().toString());
                medSelecionado.setEmail_med(dataSnapshot.child("email_med").getValue().toString());
                medSelecionado.setNascimento_med(dataSnapshot.child("nascimento_med").getValue().toString());
                medSelecionado.setSexo_med(dataSnapshot.child("sexo_med").getValue().toString());
                medSelecionado.setCpf_med(dataSnapshot.child("cpf_med").getValue().toString());
                medSelecionado.setLatitude_med(Double.parseDouble(dataSnapshot.child("latitude_med").getValue().toString()));
                medSelecionado.setLongitude_med(Double.parseDouble(dataSnapshot.child("longitude_med").getValue().toString()));


                String nomeExibir;
                switch (medSelecionado.getSexo_med().trim()) {
                    case "M":
                        nome_perfil.setText("dr. " + medSelecionado.getNome_med());
                        break;
                    case "F":
                        nome_perfil.setText("dra. " + medSelecionado.getNome_med());
                        break;
                    default:
                        nomeExibir = medSelecionado.getSexo_med() + " " + medSelecionado.getNome_med();
                        nome_perfil.setText(medSelecionado.getNome_med());
                        Toast.makeText(PerfilMedicoActivity.this, nomeExibir, Toast.LENGTH_SHORT).show();
                        break;
                }

                Double lat = coordenadaSelecionada.getLat();
                Double lng = coordenadaSelecionada.getLng();

                LatLng posicaoInicial = new LatLng(lat, lng);
                LatLng posicaiFinal = new LatLng(medSelecionado.getLatitude_med(), medSelecionado.getLongitude_med());

                double distance = SphericalUtil.computeDistanceBetween(posicaoInicial, posicaiFinal);
                dis_perfil.setText(formatNumber(distance));

                Picasso.get().load(medSelecionado.getFoto_med()).into(foto_med_perfil);
                esp_perfil.setText(getEspecialidade());
                av_perfil.setText(String.valueOf(medSelecionado.getRating_med()));
                conv_perfil.setText("0");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        checarFavorito();
    }

    public void checarFavorito(){

        //CHECANDO SE O MEDICO SELECIONADO É FAVORITO DO USUARIO E PERMITINDO A REMOÇÃO DO MEDICO DOS FAVORITOS
        DatabaseReference med_fav = mFirebaseDatabase.getReference("usuarios/" + UsuarioFirebase.getIdentificadorUsuario() + "/med_fav");
        med_fav.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(medSelecionado.getCrm_med()).exists()) {
                    lavFav.setFrame(40);


                    lavFav.setOnClickListener(v -> {
                        vibrar();
                        i++;
                        int cliques = i%2;
                        if(cliques == 0){
                            lavFav.playAnimation();
                            lavFav.loop(false);
                            medSelecionado.salvar();
                        }
                        else{
                            lavFav.setFrame(0);
                            medSelecionado.excluir();
                        }
                    });
                }
                if (!dataSnapshot.child(medSelecionado.getCrm_med()).exists()) {
                    lavFav.setFrame(0);

                    lavFav.setOnClickListener(v -> {
                        vibrar();
                        i++;
                        int cliques = i%2;
                        if(cliques == 0){
                            lavFav.setFrame(0);

                            medSelecionado.excluir();
                        }
                        else{
                            lavFav.playAnimation();
                            lavFav.loop(false);
                            medSelecionado.salvar();
                        }

                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRefMedico.removeEventListener(valueEventListenerPerfil);
    }


    public void favorite(){
        //CHECANDO SE O MEDICO SELECIONADO É FAVORITO DO USUARIO E PERMITINDO A REMOÇÃO DO MEDICO DOS FAVORITOS
        DatabaseReference med_fav = mFirebaseDatabase.getReference("usuarios/" + UsuarioFirebase.getIdentificadorUsuario() + "/med_fav");
        med_fav.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(medSelecionado.getCrm_med()).exists()) {
                    lavFav.setFrame(40);
                    lavFav.pauseAnimation();

                    lavFav.setOnClickListener(v -> {
                        vibrar();
                        lavFav.setFrame(0);
                        lavFav.setImageDrawable(getDrawable(R.drawable.outline_favorite_border_24));
                        lavFav.pauseAnimation();
                        medSelecionado.excluir();
                    });
                }
                else {
                    vibrar();
                    lavFav.playAnimation();
                    lavFav.loop(false);
                    medSelecionado.salvar();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void vibrar() {
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 30 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(15);
        }
    }
    private String formatNumber(double distance) {
        Double distanceMeters = Math.ceil(distance);
        if ((distance / 1000) < 1) {
            String unit = "m";
            return String.format("%3.0f%s", distanceMeters, unit);
        } else if ((distance / 1000) > 100) {
            return "+99km";
        } else {
            String unit = "km";
            distance /= 1000;
            return String.format("%4.1f%s", distance, unit);
        }
    }
}