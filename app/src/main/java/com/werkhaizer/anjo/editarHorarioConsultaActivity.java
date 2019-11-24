package com.werkhaizer.anjo;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class editarHorarioConsultaActivity extends AppCompatActivity {

    private EditText txtHoras;
    private Button btnDone;
    private Button btnVoltar;
    private String idConsulta;
    private DatabaseReference mRef;
    private String dataConsulta;
    private String[] data_horario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_horario_consulta);

        txtHoras = findViewById(R.id.txtHorarioConsultaEditar);
        btnDone = findViewById(R.id.btnConcluirEditHorario);
        btnVoltar = findViewById(R.id.btnVoltar_editar_horario);


        Bundle getConsultaID = getIntent().getExtras();
        if (getConsultaID != null) {
            idConsulta = getConsultaID.getString("idConsulta");
        }

        btnVoltar.setOnClickListener(v -> editarHorarioConsultaActivity.this.finish());

        if (!idConsulta.isEmpty()) {
            mRef = FirebaseDatabase.getInstance().getReference("consultas").child(idConsulta);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataConsulta = Objects.requireNonNull(dataSnapshot.child("horarioFormatado").getValue()).toString();
                    data_horario = dataConsulta.split(" ");
                    txtHoras.setText(data_horario[1]);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(this, "Não foi possivel recuperar o horario cadastrada na consulta", Toast.LENGTH_SHORT).show();
        }

        txtHoras.setOnClickListener(v -> {
            String[] horasStr = data_horario[1].split(":");
            int hour = Integer.parseInt(horasStr[0]);
            int minute = Integer.parseInt(horasStr[1]);

            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(editarHorarioConsultaActivity.this, (timePicker, selectedHour, selectedMinute) -> {
                String selectedHourStr = selectedHour < 10 ? "0" + selectedHour : "" + selectedHour;
                String selectedMinuteStr = selectedMinute < 10 ? "0" + selectedMinute : "" + selectedMinute;
                txtHoras.setText( selectedHourStr + ":" + selectedMinuteStr);
            }, hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Selecione um horário");
            mTimePicker.show();
        });

        btnDone.setOnClickListener(v -> {
            if (data_horario[1].equals(txtHoras.getText().toString())) {
                //concluir sem mexer no bd
                //mostrar um done dialog
                Intent intent = new Intent(editarHorarioConsultaActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
//                atualizar no bd
                Intent intent = new Intent(editarHorarioConsultaActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(this, "Criar método para atualizar horario", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
