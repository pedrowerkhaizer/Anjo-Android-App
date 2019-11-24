package fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.werkhaizer.anjo.PerfilMedicoActivity;
import com.werkhaizer.anjo.PopupPerfilMedico;
import com.werkhaizer.anjo.R;
import com.werkhaizer.anjo.adapter.listaMedicosAdapter;
import com.werkhaizer.anjo.confirmarEditarEnderecoFragment;
import com.werkhaizer.anjo.filtrosListaMedicoActivity;
import com.werkhaizer.anjo.listaEspecialidadesActivity;
import com.werkhaizer.anjo.pesquisaMedicosActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import config.ConfiguracaoFirebase;
import helper.RecyclerItemClickListener;
import model.Medicos;
import utils.coordenadaSelecionada;
import utils.filtroMedicos;

import static utils.selectedMedicData.setCrm;
import static utils.selectedMedicData.setEspecialidade;


public class ListaMedicosFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private Dialog mDialog;

    private ImageButton btnFiltros;
    private LinearLayout llTitleMedicos;
    private ImageButton svMedicos;
    private Button btnEspecialidades;
    private listaMedicosAdapter adapter;
    private ArrayList<Medicos> listaMedicos = new ArrayList<>();
    private DatabaseReference medicosRef;
    private ValueEventListener valueEventListenerMedicos;

    public ListaMedicosFragment() {
        // Required empty public constructor
    }


    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_medicos, container, false);


        mRecyclerView = view.findViewById(R.id.recyclerViewMedicos);
        medicosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("Especialidades")
                .child(filtroMedicos.getEspecialidade()).child("medicos");

        //Configurar o adapter
        adapter = new listaMedicosAdapter(listaMedicos, getActivity());

        //configurar o rv
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);

        ConstraintLayout clErro = view.findViewById(R.id.clErroListaMed);
        clErro.setVisibility(View.INVISIBLE);

        //Configura o evento de clique no recyclerview
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        mRecyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                vibrar();
                                TextView crm_medTV = view.findViewById(R.id.crm_med);
                                String crm_med = crm_medTV.getText().toString();
                                Intent intent = new Intent(getContext(), PerfilMedicoActivity.class);
                                setCrm(crm_med);
                                setEspecialidade(filtroMedicos.getEspecialidade());
                                startActivity(intent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                vibrar();
                                TextView crm_medTV = view.findViewById(R.id.crm_med);
                                String crm_med = crm_medTV.getText().toString();
                                Intent intent = new Intent(getContext(), PopupPerfilMedico.class);
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


        DatabaseReference mRef = mFirebaseDatabase.getReference("Especialidades/" + filtroMedicos.getEspecialidade() + "/medicos");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = (int) dataSnapshot.getChildrenCount();
                if (i == 0) {
                    ConstraintLayout clErro = view.findViewById(R.id.clErroListaMed);
                    clErro.setVisibility(View.VISIBLE);
                } else {
                    ConstraintLayout clErro = view.findViewById(R.id.clErroListaMed);
                    clErro.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btnFiltros = view.findViewById(R.id.btnFiltros);
        svMedicos = view.findViewById(R.id.svMedicos);
        llTitleMedicos = view.findViewById(R.id.llTitleMedicos);


        btnEspecialidades = view.findViewById(R.id.btnEspecialidade);
        btnEspecialidades.setOnClickListener(v -> {
            vibrar();
            Intent intent = new Intent(getContext(), listaEspecialidadesActivity.class);
            startActivity(intent);
        });


        btnEspecialidades.setText(filtroMedicos.getEspecialidade());


        svMedicos.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), pesquisaMedicosActivity.class);
            startActivity(i);
        });

        //TODO
        btnFiltros.setOnClickListener(v -> {
            vibrar();
            Intent i = new Intent(getContext(), filtrosListaMedicoActivity.class);
            startActivity(i);
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        listarMedicos();
    }

    @Override
    public void onStop() {
        super.onStop();
        medicosRef.removeEventListener(valueEventListenerMedicos);
    }

    @Override
    public void onPause() {
        super.onPause();
        medicosRef.removeEventListener(valueEventListenerMedicos);
    }

    @Override
    public void onResume() {
        super.onResume();
        listarMedicos();
    }

    public void listarMedicos() {
        valueEventListenerMedicos = medicosRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > listaMedicos.size()) {
                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        Medicos medicos = dados.getValue(Medicos.class);
                        listaMedicos.add(medicos);
                    }

                    if (filtroMedicos.getRating()) {
                        Collections.sort(listaMedicos, (o1, o2) -> {
                            //decrescente
                            return o2.getRating_med().compareTo(o1.getRating_med());
                        });
                    } else {
                        Collections.sort(listaMedicos, (o1, o2) -> {
                            Double lat = coordenadaSelecionada.getLat();
                            Double lng = coordenadaSelecionada.getLng();
                            LatLng posicaoInicial = new LatLng(lat, lng);

                            LatLng posicaoFinal1 = new LatLng(o1.getLatitude_med(), o1.getLongitude_med());
                            double distance1 = SphericalUtil.computeDistanceBetween(posicaoInicial, posicaoFinal1);

                            LatLng posicaoFinal2 = new LatLng(o2.getLatitude_med(), o2.getLongitude_med());
                            double distance2 = SphericalUtil.computeDistanceBetween(posicaoInicial, posicaoFinal2);

                            //crescente
                            return formatNumber(distance1).compareTo(formatNumber(distance2));
                        });
                    }

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String formatNumber(double distance) {
        String unit = "km";
        distance /= 1000;
        return String.format("%4.1f%s", distance, unit);

    }

    private void vibrar() {
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 30 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(15);
        }
    }
}

