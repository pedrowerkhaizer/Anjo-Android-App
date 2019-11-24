package com.werkhaizer.anjo;

import android.app.DatePickerDialog;
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

import static utils.Vibrar.vibrar;

public class editarDiaConsultaActivity extends AppCompatActivity {

    private EditText txtDia;
    private Button btnProximo;
    private Button btnVoltar;
    private String idConsulta;
    private DatabaseReference mRef;
    private String dataConsulta;
    private String[] data_horario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_dia_consulta);

        btnVoltar = findViewById(R.id.btnVoltarEditarDia);
        txtDia = findViewById(R.id.txtDiaConsultaEditar);
        btnProximo = findViewById(R.id.btnProxEditarDia);

        Bundle getConsultaID = getIntent().getExtras();
        if (getConsultaID != null) {
            idConsulta = getConsultaID.getString("idConsulta");
        }

        if (!idConsulta.isEmpty()) {
            mRef = FirebaseDatabase.getInstance().getReference("consultas").child(idConsulta);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataConsulta = Objects.requireNonNull(dataSnapshot.child("horarioFormatado").getValue()).toString();
                    data_horario = dataConsulta.split(" ");
                    txtDia.setText(data_horario[0]);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(this, "Não foi possivel recuperar a data cadastrada na consulta", Toast.LENGTH_SHORT).show();
        }

        btnVoltar.setOnClickListener(v -> {
            vibrar(editarDiaConsultaActivity.this);
            editarDiaConsultaActivity.this.finish();
        });

        txtDia.setFocusable(false);

        txtDia.setOnClickListener(v -> {
            vibrar(editarDiaConsultaActivity.this);
            int dia, mes, ano;
            String[] dataCompleta;
            dataCompleta = data_horario[0].split("/");
            dia = Integer.parseInt(dataCompleta[0]);
            mes = Integer.parseInt(dataCompleta[1]) - 1;
            ano = Integer.parseInt(dataCompleta[2]);

//            Calendar mcurrentTime = Calendar.getInstance();
//            int mYear = mcurrentTime.get(Calendar.YEAR);
//            int mMonth = mcurrentTime.get(Calendar.MONTH);
//            int mDay = mcurrentTime.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog mDatePicker;
            mDatePicker = new DatePickerDialog(editarDiaConsultaActivity.this, (datepicker, selectedyear, selectedmonth, selectedday) -> {
                // TODO Auto-generated method stub
                /*      Your code   to get date and time    */
                selectedmonth++;
                String selectedmonthStr = selectedmonth < 10 ? "0" + selectedmonth : "" + selectedmonth;
                String selecteddayStr = selectedday < 10 ? "0" + selectedday : "" + selectedday;
                txtDia.setText(selecteddayStr + "/" + selectedmonthStr + "/" + selectedyear);
            }, ano, mes, dia);
            mDatePicker.setTitle("Selecione um dia");
            mDatePicker.show();

//            Calendar mcurrentTime = Calendar.getInstance();
//            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//            int minute = mcurrentTime.get(Calendar.MINUTE);
//            TimePickerDialog mTimePicker;
//            mTimePicker = new
//
//                    TimePickerDialog(editarDiaConsultaActivity.this, new TimePickerDialog.OnTimeSetListener() {
//                @Override
//                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                    txtDia.setText(selectedHour + ":" + selectedMinute);
//                }
//            }, hour, minute, true);//Yes 24 hour time
//            mTimePicker.setTitle("Select Time");
//            mTimePicker.show();

        });

        btnProximo.setOnClickListener(v -> {
            if(data_horario[0].equals(txtDia.getText().toString())){
                //Ir pra próxima tela
                Intent intent = new Intent(editarDiaConsultaActivity.this, editarHorarioConsultaActivity.class);
                intent.putExtra("idConsulta", idConsulta);
                startActivity(intent);
            }
            else{
                //Atualizar no bd
                Intent intent = new Intent(editarDiaConsultaActivity.this, editarHorarioConsultaActivity.class);
                intent.putExtra("idConsulta", idConsulta);
                startActivity(intent);
                Toast.makeText(this, "Criar método para atualizar data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
