package com.werkhaizer.anjo;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.werkhaizer.anjo.adapter.listaMedicosAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import config.ConfiguracaoFirebase;
import helper.RecyclerItemClickListener;
import model.Medicos;
import utils.filtroMedicos;
import utils.teclado;

import static utils.selectedMedicData.setCrm;
import static utils.selectedMedicData.setEspecialidade;

public class pesquisaMedicosActivity extends AppCompatActivity {

    private EditText mPesquisaTexto;
    private ImageButton btnClose;
    private LinearLayout containerInfo;
    private ArrayList<Medicos> listaMedicos = new ArrayList<>();
    private DatabaseReference medicosRef;
    private listaMedicosAdapter adapter;
    private ValueEventListener valueEventListenerMedicos;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisa_medicos);

        medicosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("Especialidades")
                .child(filtroMedicos.getEspecialidade()).child("medicos");

        mPesquisaTexto = findViewById(R.id.sedtMedicos);
        btnClose = findViewById(R.id.btnClosePesquisaMed);
        containerInfo = findViewById(R.id.containerInfoPesquisaMedicos);
        mRecyclerView = findViewById(R.id.rvPesquisaMedicos);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


        //Configurar o adapter
        adapter = new listaMedicosAdapter(listaMedicos, this);

        //configurar o rv
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);

        mPesquisaTexto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                listaMedicos.clear();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    containerInfo.setVisibility(View.GONE);
                    btnClose.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.VISIBLE);

                } else if (count == 0) {
                    containerInfo.setVisibility(View.VISIBLE);
                    btnClose.setVisibility(View.INVISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                listarMedicos(mPesquisaTexto.getText().toString());
            }
        });



        btnClose.setOnClickListener(v -> {
            mPesquisaTexto.setText(null);
            mPesquisaTexto.clearFocus();
            teclado.hideKeyboard(getApplicationContext(), mPesquisaTexto);
        });


        //Configura o evento de clique no recyclerview
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        mRecyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                vibrar();
                                TextView crm_medTV = view.findViewById(R.id.crm_med);
                                String crm_med = crm_medTV.getText().toString();
                                Intent intent = new Intent(pesquisaMedicosActivity.this, PerfilMedicoActivity.class);
                                setCrm(crm_med);
                                setEspecialidade(filtroMedicos.getEspecialidade());
                                startActivity(intent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                vibrar();
                                TextView crm_medTV = view.findViewById(R.id.crm_med);
                                String crm_med = crm_medTV.getText().toString();
                                Intent intent = new Intent(pesquisaMedicosActivity.this, PopupPerfilMedico.class);
                                setCrm(crm_med);
                                setEspecialidade(filtroMedicos.getEspecialidade());
                                startActivity(intent);
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

    }


    public void listarMedicos(String nome) {
        valueEventListenerMedicos = medicosRef.addValueEventListener(new ValueEventListener() {
            ArrayList<Medicos> listClone = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (listClone.size() != dataSnapshot.getChildrenCount()){
                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        Medicos medicos = dados.getValue(Medicos.class);
                        listClone.add(medicos);
                    }
                        for (Medicos medico : listClone) {
                            if (medico.getNome_med().matches("(?i)(" + nome + ").*")) {
                                listaMedicos.add(medico);
                            }
                            else if(medico.getCrm_med().matches("(?i)(" + nome + ").*")) {
                                listaMedicos.add(medico);
                            }

                        }
                    }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
//    @Override
//    public void onStop() {
//        super.onStop();
//        medicosRef.removeEventListener(valueEventListenerMedicos);
//    }
private void vibrar() {
    Vibrator v = (Vibrator) pesquisaMedicosActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 30 milliseconds
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        v.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE));
    } else {
        //deprecated in API 26
        v.vibrate(15);
    }
}
}
