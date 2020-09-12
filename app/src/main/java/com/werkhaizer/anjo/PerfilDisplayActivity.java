package com.werkhaizer.anjo;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import static helper.UsuarioFirebase.getIdentificadorUsuario;

public class PerfilDisplayActivity extends AppCompatActivity {
    private Button btnVoltar;
    private CircleImageView mFoto;
    private TextView mNome, mIdade, mQtdConsultas, mConvenio, mAltura, mPeso;

    private DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_display);

        btnVoltar = findViewById(R.id.btnBackPerfilDs);
        mFoto = findViewById(R.id.foto_perfil_display);
        mNome = findViewById(R.id.nome_perfil_display);
        mIdade = findViewById(R.id.idade_perfil_display);
        mQtdConsultas = findViewById(R.id.qtdConsultas_perfil_display);
        mConvenio = findViewById(R.id.convenio_perfil_display);
        mAltura = findViewById(R.id.altura_perfil_display);
        mPeso = findViewById(R.id.peso_perfil_display);

        mRef = FirebaseDatabase.getInstance().getReference("usuarios/" + getIdentificadorUsuario() + "/dados");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String foto = dataSnapshot.child("foto").getValue().toString();
                String nome = dataSnapshot.child("nome").getValue().toString();
                String convenio = dataSnapshot.child("convenio").getValue().toString();

                Picasso.get().load(foto).into(mFoto);
                mNome.setText(nome);
                if(convenio.isEmpty()){
                    mConvenio.setText("Sem convênio cadastrado");
                }
                else {
                    mConvenio.setText(convenio);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnVoltar.setOnClickListener(v -> {
            vibrar();
            PerfilDisplayActivity.this.finish();
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
}
