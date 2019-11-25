package com.werkhaizer.anjo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import utils.coordenadaSelecionada;
import utils.filtroConsulta;
import utils.filtroMedicos;

import static utils.FragmentAtual.setConsulta;
import static utils.FragmentAtual.setFavorito;
import static utils.FragmentAtual.setMain;
import static utils.FragmentAtual.setPerfil;
import static utils.Vibrar.vibrar;
import static utils.coordenadaSelecionada.getEnd;

public class filtrosListaFavoritosActivity extends AppCompatActivity {

    private LinearLayout dist, av, local, sair;
    private Thread closeThread;
    private FloatingActionButton btnClose;
    private TextView tvLocalEsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros_lista_favoritos);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        LinearLayout l = findViewById(R.id.llFiltros);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout(dm.widthPixels, dm.heightPixels);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.filter_translate);
        anim.reset();
        l.clearAnimation();
        l.startAnimation(anim);

        btnClose = findViewById(R.id.btnCloseFiltroFav);
        dist = findViewById(R.id.llMenorDistFav);
        av = findViewById(R.id.llClassificacaoFav);
        tvLocalEsc = findViewById(R.id.txtLocalEscolhidoFav);
        sair = findViewById(R.id.sair_filtro_medicosFav);

        setMain(false);
        setConsulta(false);
        setFavorito(true);
        setPerfil(false);


        tvLocalEsc.setText(getEnd());

        sair.setOnClickListener(v -> fecharFiltro());


        dist.setOnClickListener(v -> {
            filtroMedicos.setDistancia(true);
            filtroMedicos.setRating(false);

//                fecharFiltro();
            Intent i = new Intent(filtrosListaFavoritosActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        av.setOnClickListener(v -> {
            filtroMedicos.setRating(true);
            filtroMedicos.setDistancia(false);

//                fecharFiltro();
            Intent i = new Intent(filtrosListaFavoritosActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });

        local = findViewById(R.id.llBuscar);
        local.setOnClickListener(v -> {
            vibrar();
            Intent intent = new Intent(filtrosListaFavoritosActivity.this, localUsuarioActivity.class);
//                intent.putExtra("lista", "lista");
            startActivity(intent);
            filtrosListaFavoritosActivity.this.finish();
        });

        btnClose.setOnClickListener(v -> fecharFiltro());

    }


    private void fecharFiltro() {
        vibrar();
        final LinearLayout lf = findViewById(R.id.llFiltros);
        Animation anim = AnimationUtils.loadAnimation(filtrosListaFavoritosActivity.this, R.anim.filter_back_translate);
        anim.reset();
        lf.clearAnimation();
        lf.startAnimation(anim);

        closeThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (waited < 300) {
                        sleep(160);
                        waited += 160;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lf.setAlpha(0);
                    finish();
                }
            }
        };
        closeThread.start();
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
}