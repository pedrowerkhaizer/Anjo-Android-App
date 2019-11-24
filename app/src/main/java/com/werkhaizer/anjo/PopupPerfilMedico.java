package com.werkhaizer.anjo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
import de.hdodenhof.circleimageview.CircleImageView;
import utils.coordenadaSelecionada;

import static utils.selectedMedicData.getCrm;
import static utils.selectedMedicData.getEspecialidade;

public class PopupPerfilMedico extends Activity implements View.OnClickListener {

    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference medicos;

    private TextView mPerfilNomeMed, mPerfilEspMed;
    private ImageView mPerfilFotoMed;
    private FloatingActionButton closeBtn;
    private Button goMedico;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_perfil_medico);

        closeBtn = findViewById(R.id.closeDialog_btn);
        goMedico = findViewById(R.id.btn_goMedico);
        closeBtn.setOnClickListener(this);

        mPerfilEspMed = findViewById(R.id.especialidade_med_perfil);
        mPerfilFotoMed = findViewById(R.id.foto_med_perfil);
        mPerfilNomeMed = findViewById(R.id.nome_med_perfil);


//        Intent i = getIntent();
//        final String med_selecionado = i.getStringExtra("crm");
//        final String esp_selecionado = i.getStringExtra("espMed");

        medicos = mFirebaseDatabase.getReference("Especialidades").child(getEspecialidade()).child("medicos").child(getCrm());


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int widht = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (widht * .9), (int) (height * .75));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = +20;

        getWindow().setAttributes(params);
        final TextView nome_perfil = findViewById(R.id.nome_med_perfil);
        final TextView esp_perfil = findViewById(R.id.especialidade_med_perfil);
        final TextView av_perfil = findViewById(R.id.avaliacao_med_perfil);
        final TextView conv_perfil = findViewById(R.id.convenios_med_perfil);
        final TextView dis_perfil = findViewById(R.id.distancia_med_perfil);
        final CircleImageView foto_med_perfil = findViewById(R.id.foto_med_perfil);
        final TextView crm_perfil = findViewById(R.id.crm_med_perfil);

        goMedico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar();
//                String crm_med = crm_perfil.getText().toString();
                Intent intent = new Intent(getApplicationContext(), PerfilMedicoActivity.class);
//                intent.putExtra("espMed", esp_selecionado);
//                intent.putExtra("crm", crm_med);

//                FAZER ANIMAÇÃO AO PASSAR DE TELA
//                Pair[] pairs = new Pair[3];
//                pairs[0] = new Pair<View, String>(mPerfilNomeMed, "nomeMedPerfil");
//                pairs[1] = new Pair<View, String>(mPerfilEspMed, "espMedPerfil");
//                pairs[2] = new Pair<View, String>(mPerfilFotoMed, "fotoMedPerfil");
//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(PopupPerfilMedico.this, pairs);
//                startActivity(intent, options.toBundle());

                startActivity(intent);
                PopupPerfilMedico.this.finish();

            }
        });

        medicos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String convenio_med = dataSnapshot.child("convenio_med").getValue().toString();
                String endereco_med = dataSnapshot.child("endereco_med").getValue().toString();
                String esp_med = dataSnapshot.child("esp_med").getValue().toString();
                String foto_med = dataSnapshot.child("foto_med").getValue().toString();
                String nome_med = dataSnapshot.child("nome_med").getValue().toString();
                String rating_med = dataSnapshot.child("rating_med").getValue().toString();
                Double lat_med = (Double.parseDouble(dataSnapshot.child("latitude_med").getValue().toString()));
                Double lng_med = (Double.parseDouble(dataSnapshot.child("longitude_med").getValue().toString()));


                Double lat = coordenadaSelecionada.getLat();
                Double lng = coordenadaSelecionada.getLng();

                LatLng posicaoInicial = new LatLng(lat, lng);
                LatLng posicaiFinal = new LatLng(lat_med, lng_med);

                double distance = SphericalUtil.computeDistanceBetween(posicaoInicial, posicaiFinal);
                dis_perfil.setText(formatNumber(distance));

                crm_perfil.setText(getCrm());
                nome_perfil.setText(nome_med);
//                nome_perfil.setText(nome);
                Picasso.get().load(foto_med).into(foto_med_perfil);
                esp_perfil.setText(esp_med);
                av_perfil.setText(rating_med);
                conv_perfil.setText("0");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        vibrar();
        switch (v.getId()) {
            case R.id.closeDialog_btn:
                PopupPerfilMedico.this.finish();
                break;
        }
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
