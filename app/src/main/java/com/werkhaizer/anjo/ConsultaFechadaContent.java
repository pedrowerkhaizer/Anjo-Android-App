package com.werkhaizer.anjo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.werkhaizer.anjo.adapter.listaConsultaAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import helper.RecyclerItemClickListener;
import model.Consultas;
import utils.coordenadaSelecionada;
import utils.filtroConsulta;

import static helper.UsuarioFirebase.getIdentificadorUsuario;
import static utils.Vibrar.vibrar;
import static utils.statusConsulta.setStatus;


public class ConsultaFechadaContent extends Fragment {

    private RecyclerView recyclerView;
    private listaConsultaAdapter adapter;
    private ArrayList<Consultas> listaConsultas = new ArrayList<>();
    private DatabaseReference consultasUsuRef;
    private DatabaseReference consultasRef;
    private ValueEventListener valueEventListenerConsultas;


    public ConsultaFechadaContent() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.content_consulta_fechada, container, false);

        recyclerView = view.findViewById(R.id.rvConsultaFechada);
        consultasUsuRef = FirebaseDatabase.getInstance().getReference("usuarios").child(getIdentificadorUsuario()).child("consultas");
        consultasRef = FirebaseDatabase.getInstance().getReference("consultas");


        //Configurar o adapter
//        //TODO pegar os dados do médico de acordo com a crm do medico da consulta
        adapter = new listaConsultaAdapter(listaConsultas, getActivity());
//
//        //configurar o rv
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        vibrar(getContext());
                        TextView mId = view.findViewById(R.id.id_consulta_med);
                        String idStr = mId.getText().toString();
                        Intent intent = new Intent(getContext(), VerHorarioActivity.class);
                        intent.putExtra("idConsulta", idStr);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }));


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Quando carregar o fragment, recuperar as consultas
        setStatus("F");
        listarConsultas();
    }


    @Override
    public void onStop() {
        super.onStop();
        consultasUsuRef.removeEventListener(valueEventListenerConsultas);

    }


    public void listarConsultas() {
        valueEventListenerConsultas = consultasUsuRef.child("F").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > listaConsultas.size()) {
                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        consultasRef.child(Objects.requireNonNull(dados.getKey())).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String crm = Objects.requireNonNull(dataSnapshot.child("crm").getValue()).toString();
                                String dia = Objects.requireNonNull(dataSnapshot.child("dia").getValue()).toString();
                                String email_paciente = Objects.requireNonNull(dataSnapshot.child("email_paciente").getValue()).toString();
                                String especialidade = Objects.requireNonNull(dataSnapshot.child("especialidade").getValue()).toString();
                                String horario = Objects.requireNonNull(dataSnapshot.child("horario").getValue()).toString();
                                String horarioFormatado = Objects.requireNonNull(dataSnapshot.child("horarioFormatado").getValue()).toString();
                                String lat = Objects.requireNonNull(dataSnapshot.child("lat").getValue()).toString();
                                String lng = Objects.requireNonNull(dataSnapshot.child("lng").getValue()).toString();
                                String obs_paciente = Objects.requireNonNull(dataSnapshot.child("obs_paciente").getValue()).toString();
                                String status = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();

                                Consultas c = new Consultas();
                                c.setCrm(crm);
                                c.setDia(dia);
                                c.setEmail_paciente(email_paciente);
                                c.setEspecialidade(especialidade);
                                c.setHorario(horario);
                                c.setHorarioFormatado(horarioFormatado);
                                c.setLat(lat);
                                c.setLng(lng);
                                c.setObs_paciente(obs_paciente);
                                c.setStatus(status);

                                listaConsultas.add(c);

                                if (filtroConsulta.getDistancia()) {
                                    Collections.sort(listaConsultas, (o1, o2) -> {
                                        Double latitude = coordenadaSelecionada.getLat();
                                        Double longitude = coordenadaSelecionada.getLng();
                                        LatLng posicaoInicial = new LatLng(latitude, longitude);

                                        LatLng posicaoFinal1 = new LatLng(Double.parseDouble(o1.getLat()), Double.parseDouble(o1.getLng()));
                                        double distance1 = SphericalUtil.computeDistanceBetween(posicaoInicial, posicaoFinal1);

                                        LatLng posicaoFinal2 = new LatLng(Double.parseDouble(o2.getLat()), Double.parseDouble(o2.getLng()));
                                        double distance2 = SphericalUtil.computeDistanceBetween(posicaoInicial, posicaoFinal2);

                                        return formatNumber(distance1).compareTo(formatNumber(distance2));
                                    });
                                } else {
                                    //dia
                                    Collections.sort(listaConsultas, (o1, o2) -> {
                                        String data1BD[] = o1.getHorarioFormatado().split(" ");
                                        String data1[] = data1BD[0].split("/");
                                        int diaData1 = Integer.parseInt(data1[0]);
                                        int mesData1 = Integer.parseInt(data1[1]) - 1;
                                        int anoData1 = Integer.parseInt(data1[2]);
                                        String horario1[] = data1BD[1].split(":");
                                        int horaData1 = Integer.parseInt(horario1[0]);
                                        int minutoData1 = Integer.parseInt(horario1[1]);

                                        Calendar calendar1 = Calendar.getInstance();
                                        calendar1.set(Calendar.YEAR, anoData1);
                                        calendar1.set(Calendar.MONTH, mesData1);
                                        calendar1.set(Calendar.DAY_OF_MONTH, diaData1);
                                        calendar1.set(Calendar.HOUR_OF_DAY, horaData1);
                                        calendar1.set(Calendar.MINUTE, minutoData1);

                                        //Calendar 2
                                        String data2BD[] = o2.getHorarioFormatado().split(" ");
                                        String data2[] = data2BD[0].split("/");
                                        int diaData2 = Integer.parseInt(data2[0]);
                                        int mesData2 = Integer.parseInt(data2[1]) - 1;
                                        int anoData2 = Integer.parseInt(data2[2]);
                                        String horario2[] = data2BD[1].split(":");
                                        int horaData2 = Integer.parseInt(horario2[0]);
                                        int minutoData2 = Integer.parseInt(horario2[1]);

                                        Calendar calendar2 = Calendar.getInstance();
                                        calendar2.set(Calendar.YEAR, anoData2);
                                        calendar2.set(Calendar.MONTH, mesData2);
                                        calendar2.set(Calendar.DAY_OF_MONTH, diaData2);
                                        calendar2.set(Calendar.HOUR_OF_DAY, horaData2);
                                        calendar2.set(Calendar.MINUTE, minutoData2);

                                        return calendar1.getTime().compareTo(calendar2.getTime());
                                    });
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void recarregarConsultas() {
        listaConsultas.clear();
        consultasRef.removeEventListener(valueEventListenerConsultas);
        listarConsultas();
    }

    private String formatNumber(double distance) {
        String unit = "km";
        distance /= 1000;
        return String.format("%4.1f%s", distance, unit);

    }

}

