package com.werkhaizer.anjo;

import androidx.appcompat.app.AppCompatActivity;
import fragment.ConsultasFragment;
import utils.coordenadaSelecionada;
import utils.filtroConsulta;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static utils.FragmentAtual.setConsulta;
import static utils.FragmentAtual.setFavorito;
import static utils.FragmentAtual.setMain;
import static utils.FragmentAtual.setPerfil;
import static utils.Vibrar.vibrar;

public class filtrosListaConsultaActivity extends AppCompatActivity {

    private LinearLayout dist, dia, local, sair;
    private Thread closeThread;
    private FloatingActionButton btnClose;
    private TextView tvLocalEsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros_lista_consulta);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        LinearLayout l = findViewById(R.id.llFiltrosConsultas);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout(dm.widthPixels, dm.heightPixels);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.filter_translate);
        anim.reset();
        l.clearAnimation();
        l.startAnimation(anim);

        btnClose = findViewById(R.id.btnCloseFiltroCons);
        dist = findViewById(R.id.llMenorDistCons);
        dia = findViewById(R.id.llDiaCons);
        tvLocalEsc = findViewById(R.id.txtLocalEscolhidoCons);
        local = findViewById(R.id.llBuscarCons);
        sair = findViewById(R.id.sair_filtro_consultas);


        setMain(false);
        setConsulta(true);
        setFavorito(false);
        setPerfil(false);


        sair.setOnClickListener(v -> fecharFiltro());
        tvLocalEsc.setText(coordenadaSelecionada.getEnd());

        dist.setOnClickListener(v -> {
            filtroConsulta.setDistancia(true);
            filtroConsulta.setDia(false);
//                fecharFiltro();
            Intent i = new Intent(filtrosListaConsultaActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        dia.setOnClickListener(v -> {
            filtroConsulta.setDistancia(false);
            filtroConsulta.setDia(true);
//                fecharFiltro();
            Intent i = new Intent(filtrosListaConsultaActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });


        local.setOnClickListener(v -> {
            vibrar(filtrosListaConsultaActivity.this);
            Intent intent = new Intent(filtrosListaConsultaActivity.this, localUsuarioActivity.class);
//                intent.putExtra("lista", "lista");
            startActivity(intent);
            filtrosListaConsultaActivity.this.finish();
        });

        btnClose.setOnClickListener(v -> fecharFiltro());
    }


    private void fecharFiltro(){
        vibrar(filtrosListaConsultaActivity.this);
        final LinearLayout lf = findViewById(R.id.llFiltrosConsultas);
        Animation anim = AnimationUtils.loadAnimation(filtrosListaConsultaActivity.this, R.anim.filter_back_translate);
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

}
