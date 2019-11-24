package com.werkhaizer.anjo;

import androidx.appcompat.app.AppCompatActivity;
import model.Usuario;
import utils.MaskEditUtil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static utils.FragmentAtual.setConsulta;
import static utils.FragmentAtual.setFavorito;
import static utils.FragmentAtual.setMain;
import static utils.FragmentAtual.setPerfil;

public class completarConvenioActivity extends AppCompatActivity {

    private Button btnDone, btnBack, btnAjuda;
    private EditText mNumConvenio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completar_convenio);

        btnBack = findViewById(R.id.btnBackConvenioNum);
        btnDone = findViewById(R.id.btnAttConvenio);
        btnAjuda = findViewById(R.id.btnAjudaNumConvenio);
        mNumConvenio = findViewById(R.id.txtNumConvenio);

        mNumConvenio.addTextChangedListener(MaskEditUtil.mask(mNumConvenio, MaskEditUtil.FORMAT_CONVENIO));


        btnBack.setOnClickListener(v-> onBackPressed());
        btnAjuda.setOnClickListener(v-> Toast.makeText(this, "Criar um fragment com a figura de um cartão de plano", Toast.LENGTH_SHORT).show());

        btnDone.setOnClickListener(v->{
            if(!mNumConvenio.getText().toString().isEmpty()){
                Bundle bundle = getIntent().getExtras();

                String strNumConvenio = mNumConvenio.getText().toString();
                String strConvenio = bundle.getString("convenio");

                Usuario usuario = new Usuario();
                usuario.setConvenio(strConvenio);
                usuario.setConvenioNum(strNumConvenio);
                usuario.atualizarConvenio();

                Intent i = new Intent(completarConvenioActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                setMain(false);
                setConsulta(false);
                setFavorito(false);
                setPerfil(true);
                startActivity(i);
            }
            else {
                Toast.makeText(this, "Digite o registro do seu plano para continuar", Toast.LENGTH_SHORT).show();
            }

        });
    }
}
