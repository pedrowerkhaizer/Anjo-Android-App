package com.werkhaizer.anjo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import helper.ConsultasHelper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static utils.Vibrar.vibrar;

public class VerHorarioActivity extends AppCompatActivity {
    private Button btnVoltar, btnSettings;
    private Button btnEditarHorario, btnMensagem, btnCancelarHorario;
    private CircleImageView civMed;
    private TextView mNomeMed, mEspMed, mCRM, mRating, mDia, mHorario, mEndereco;
    private DatabaseReference mRef;
    private DatabaseReference mRefMed;

    private Button btnProxima, btnAberta, btnConcluida;
    private String idConsulta;
    private ConsultasHelper consultas = new ConsultasHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_horario);

        Bundle getConsultaID = getIntent().getExtras();
        if (getConsultaID != null) {
            idConsulta = getConsultaID.getString("idConsulta");
        }

        btnVoltar = findViewById(R.id.btnVoltarHorario);
        btnSettings = findViewById(R.id.btnSettingsHorario);
        btnEditarHorario = findViewById(R.id.btn_editar_horario);
        btnMensagem = findViewById(R.id.btn_mensagem_horario);
        btnCancelarHorario = findViewById(R.id.btn_cancelar_horario);
        mNomeMed = findViewById(R.id.nome_med_display_horario);
        mEspMed = findViewById(R.id.esp_med_display_horario);
        mCRM = findViewById(R.id.crm_display_horario);
        mRating = findViewById(R.id.rating_med_display_horario);
        mDia = findViewById(R.id.dia_display_horario);
        mHorario = findViewById(R.id.horario_display_horario);
        mEndereco = findViewById(R.id.endereco_display_horario);
        civMed = findViewById(R.id.civFoto_med_horario);

        btnProxima = findViewById(R.id.btnProxima_horario);
        btnAberta = findViewById(R.id.btnAberto_horario);
        btnConcluida = findViewById(R.id.btnConcluida_horario);

        if(!idConsulta.isEmpty()){
            mRef = FirebaseDatabase.getInstance().getReference("consultas").child(idConsulta);
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String crm = dataSnapshot.child("crm").getValue().toString();
                    String emailPaciente = dataSnapshot.child("email_paciente").getValue().toString();
                    String especialidade = dataSnapshot.child("especialidade").getValue().toString();
                    String dia = dataSnapshot.child("dia").getValue().toString();
                    String horario = dataSnapshot.child("horario").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();

                    mCRM.setText(crm);
                    mDia.setText(dia);
                    mHorario.setText(horario);
                    mEspMed.setText(especialidade);


                    consultas.setStatus(status);
                    consultas.setHorario(horario);
                    consultas.setDia(dia);
                    consultas.setEmail_paciente(emailPaciente);
                    consultas.setCrm(crm);
                    consultas.setEspecialidade(especialidade);


                    mRefMed = FirebaseDatabase.getInstance().getReference("Especialidades")
                            .child(especialidade)
                            .child("medicos")
                            .child(crm);
                    mRefMed.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String ratingMed = dataSnapshot.child("rating_med").getValue().toString();
                            String endereco = dataSnapshot.child("endereco_med").getValue().toString();
                            String foto = dataSnapshot.child("foto_med").getValue().toString();
                            String nome = dataSnapshot.child("nome_med").getValue().toString();

                            mRating.setText(ratingMed);
                            mEndereco.setText(endereco);
                            mNomeMed.setText(nome);
                            Picasso.get().load(foto).into(civMed);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        btnVoltar.setOnClickListener(v ->
        {
            vibrar(VerHorarioActivity.this);
            VerHorarioActivity.this.finish();
        });


        btnAberta.setOnClickListener(v ->{
            vibrar(VerHorarioActivity.this);
            consultas.setStatus("A");
            consultas.atualizarStatus();
            Intent intent = new Intent(VerHorarioActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        btnProxima.setOnClickListener(v ->{
            vibrar(VerHorarioActivity.this);
            consultas.setStatus("C");
            consultas.atualizarStatus();
            Intent intent = new Intent(VerHorarioActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        btnConcluida.setOnClickListener(v ->{
            vibrar(VerHorarioActivity.this);
            consultas.setStatus("F");
            consultas.atualizarStatus();
            Intent intent = new Intent(VerHorarioActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        btnEditarHorario.setOnClickListener(v -> {
            vibrar(VerHorarioActivity.this);
            if (getConsultaID != null) {
                idConsulta = getConsultaID.getString("idConsulta");
                Intent intent = new Intent(VerHorarioActivity.this, editarDiaConsultaActivity.class);
                intent.putExtra("idConsulta", idConsulta);
                startActivity(intent);
            }
            else{
                Toast.makeText(this, "Não foi possível achar a consulta requerida", Toast.LENGTH_SHORT).show();
            }

        });

        btnMensagem.setOnClickListener(v-> {
            vibrar(VerHorarioActivity.this);
            Intent intent = new Intent(VerHorarioActivity.this, chatMedActivity.class);
            intent.putExtra("crm", mCRM.getText().toString());
            intent.putExtra("especialidade", mEspMed.getText().toString());
            startActivity(intent);
        });
    }
}
