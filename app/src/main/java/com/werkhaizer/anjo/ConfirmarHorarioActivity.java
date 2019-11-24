package com.werkhaizer.anjo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import helper.Base64Custom;
import helper.ConsultasHelper;

import static helper.UsuarioFirebase.getIdentificadorUsuario;
import static utils.Vibrar.vibrar;
import static utils.selectedMedicData.getCrm;
import static utils.selectedMedicData.getDia;
import static utils.selectedMedicData.getEspecialidade;
import static utils.selectedMedicData.getHorario;
import static utils.selectedMedicData.getSimpleHorario;

public class ConfirmarHorarioActivity extends AppCompatActivity {
    private TextView mDia, mHorario, mEndereco, mNomeMed, mRatingMed, mCrmMed, mEspMed;
    private Button btnVoltar, btnConfirmar;
    private Button btnOk;
    private EditText editObservacao;
    private CircleImageView mFotoMed;
    private Dialog mDialog;

    private DatabaseReference mRefHorario;
    private DatabaseReference mRefHorariosUsu;
    private DatabaseReference mRefConsultas;
    private ValueEventListener valueEventListenerHorario;
    private ConsultasHelper consultas = new ConsultasHelper();

    private Boolean isConflitante = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_horario);

//        Bundle bundle = getIntent().getExtras();
//        String dia = bundle.getString("dia");
//        String horario = bundle.getString("horario");
//        String espMed = bundle.getString("esp");
//        String crm = bundle.getString("crm");
        mRefHorariosUsu = FirebaseDatabase.getInstance().getReference("usuarios")
                .child(getIdentificadorUsuario());
        mRefConsultas = FirebaseDatabase.getInstance().getReference("consultas");

        mDia = findViewById(R.id.dia_confirmar_display);
        mHorario = findViewById(R.id.horario_confirmar_display);
        btnVoltar = findViewById(R.id.btnVoltarHorario);
        mCrmMed = findViewById(R.id.crm_display_confirmar);
        mEspMed = findViewById(R.id.esp_med_display_confirmar);
        mEndereco = findViewById(R.id.endereco_confirmar_display);
        mNomeMed = findViewById(R.id.nome_med_display_confirmar);
        mRatingMed = findViewById(R.id.rating_med_display_confirmar);
        btnConfirmar = findViewById(R.id.btnConfirmarHorario);
        btnOk = findViewById(R.id.btn_confirmar_horario);
        editObservacao = findViewById(R.id.txtObservacao_paciente);
        mFotoMed = findViewById(R.id.civFoto_med_confirmar);

        mDia.setText(getDia());
        mHorario.setText(getHorario());
        mCrmMed.setText(getCrm());
        mEspMed.setText(getEspecialidade());

        btnOk.setOnClickListener(v -> {
            vibrar(this);
            salvarConsulta();
        });
        btnConfirmar.setOnClickListener(v -> btnOk.callOnClick());


        btnVoltar.setOnClickListener(v -> {
            vibrar(this);
            onBackPressed();
        });


        mRefHorario = FirebaseDatabase.getInstance().getReference("Especialidades")
                .child(getEspecialidade())
                .child("medicos")
                .child(getCrm());
        valueEventListenerHorario = mRefHorario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nome = dataSnapshot.child("nome_med").getValue().toString();
                String foto = dataSnapshot.child("foto_med").getValue().toString();
                String rating = dataSnapshot.child("rating_med").getValue().toString();
                String endereco = dataSnapshot.child("endereco_med").getValue().toString();
                String lat = dataSnapshot.child("latitude_med").getValue().toString();
                String lng = dataSnapshot.child("longitude_med").getValue().toString();
                consultas.setLat(lat);
                consultas.setLng(lng);

                mNomeMed.setText(nome);
                mEndereco.setText(endereco);
                mRatingMed.setText(rating);
                Picasso.get().load(foto).into(mFotoMed);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

//    public void salvarHorario() {
//        String horario = mHorario.getText().toString();
//        String dia = mDia.getText().toString();
//        String email_paciente = Base64Custom.decodificarBase64(getIdentificadorUsuario());
//        String crm = mCrmMed.getText().toString();
//        String obsPacient = editObservacao.getText().toString().trim().equals("") ?
//                "O paciente não proveu informações adicionais." : editObservacao.getText().toString();
//
//        mRefHorariosUsu.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.child("consultas").exists()) {
//                    if (dataSnapshot.child("consultas").child("A").exists()) {
//                        searchConsultas(email_paciente, crm, obsPacient, "A");
//                    }
//                    else if (dataSnapshot.child("consultas").child("C").exists()) {
//                        searchConsultas(email_paciente, crm, obsPacient, "C");
//                    }
//                    else if(!dataSnapshot.child("consultas").child("A").exists() && !dataSnapshot.child("consultas").child("C").exists()){
//                        salvarConsulta(crm, obsPacient, email_paciente);
//                    }
//
//                }
//                if (!dataSnapshot.child("consultas").exists()){
//                    salvarConsulta(crm, obsPacient, email_paciente);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//
//    }
//
//    private void searchConsultas(String email_paciente, String crm, String obsPacient, String status) {
//        mRefHorariosUsu.child("consultas").child(status).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    String horarioFormatado = data.getKey().replace("-", "/");
//                    if (horarioFormatado.equals(getSimpleHorario())) {
//                        Toast.makeText(ConfirmarHorarioActivity.this, "Horario conflitante", Toast.LENGTH_SHORT).show();
//                    }
//                    else if (!horarioFormatado.equals(getSimpleHorario())) {
//                        salvarConsulta(crm, obsPacient, email_paciente);
//                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void salvarConsulta() {
        String horario = mHorario.getText().toString();
        String dia = mDia.getText().toString();
        String email_paciente = Base64Custom.decodificarBase64(getIdentificadorUsuario());
        String crm = mCrmMed.getText().toString();
        String obsPacient = editObservacao.getText().toString().trim().equals("") ?
                "O paciente não proveu informações adicionais." : editObservacao.getText().toString();

        openRelaxDialog(dia, horario);

        //salvando a consulta na tabela geral, usuario e medico
        consultas.setObs_paciente(obsPacient);
        consultas.setCrm(crm);
        consultas.setDia(dia);
        consultas.setHorario(horario);
        consultas.setEspecialidade(mEspMed.getText().toString());
        consultas.setEmail_paciente(email_paciente);
        consultas.setStatus("A"); //Não confirmada
        consultas.setHorarioFormatado(getSimpleHorario());
        consultas.salvar();
    }


    public void openRelaxDialog(String diaStr, String horarioStr) {

        //DEIXAR O FUNDO BORRADO
//        RenderScript renderScript = RenderScript.create(EditarPerfil.this);
//        new RSBlurProcessor(renderScript).blur(getBitmapFromView(v), 15, 1);

        mDialog = new Dialog(this);
        //vamos remover o titulo da Dialog
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //vamos carregar o xml personalizado
        mDialog.setContentView(R.layout.dialog_horario_marcado);
        //Deixar quase transparente o fundo
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.alpha(100)));
        // não permitimos fechar esta dialog
        mDialog.setCancelable(false);
        //temos a instancia do ProgressBar!
//        final ProgressBar progressBar = ProgressBar.class.cast(mDialog.findViewById(R.id.progressBar));
        final TextView dia = mDialog.findViewById(R.id.dia_dialog);
        final TextView horario = mDialog.findViewById(R.id.horario_dialog);
        final Button btnVerAnjos = mDialog.findViewById(R.id.btnVerAnjos_dialog);
        final FloatingActionButton fabFecharDialog = mDialog.findViewById(R.id.fabFecharDialog);

        if (dia != null) {
            dia.setText(diaStr);
        }
        if (horario != null) {
            horario.setText(horarioStr);
        }

        if (btnVerAnjos != null) {
            btnVerAnjos.setOnClickListener(v -> {
                vibrar(this);
                Intent i = new Intent(ConfirmarHorarioActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            });
        }
        if (fabFecharDialog != null) {
            fabFecharDialog.setOnClickListener(v -> btnVerAnjos.callOnClick());
        }


        mDialog.show();

        // mDialog.dismiss(); -> para fechar a dialog

    }

    @Override
    protected void onStop() {
        super.onStop();
        mRefHorario.removeEventListener(valueEventListenerHorario);
    }
}
