package com.werkhaizer.anjo;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.werkhaizer.anjo.ViewHolder.listaEspecialidadesViewHolder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import model.Especialidades;
import utils.filtroMedicos;


public class listaEspecialidadesActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mRef = mFirebaseDatabase.getReference("Especialidades");

    private FloatingActionButton fabClose;
    private SearchView svEsp;
    private TextView tvListEspecialidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_especialidades);

        mRecyclerView = findViewById(R.id.rvListaEspecialidades);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        fabClose = findViewById(R.id.fabClose);
        tvListEspecialidade = findViewById(R.id.tvListEspecialidade);
        svEsp = findViewById(R.id.svEsp);

        fabClose.setOnClickListener(v -> {
            vibrar();
            finish();
        });

        svEsp.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null || newText.trim().isEmpty()) {
                    onStart();
                    return true;
                }
                firebaseSearch(newText.toUpperCase());
                return false;
            }
        });


        svEsp.setOnCloseListener(() -> {
            vibrar();
            tvListEspecialidade.setVisibility(View.VISIBLE);
            return false;
        });

        svEsp.setOnSearchClickListener(v -> {
            vibrar();
            tvListEspecialidade.setVisibility(View.GONE);
            svEsp.setQueryHint("Busque por especialidades");
        });

        onStart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        listarEspecialidades();
    }

    public void listarEspecialidades() {
        try {
            FirebaseRecyclerAdapter<Especialidades, listaEspecialidadesViewHolder> firebaseRecyclerAdapter = new
                    FirebaseRecyclerAdapter<Especialidades, listaEspecialidadesViewHolder>
                            (Especialidades.class,
                                    R.layout.row_especialidade,
                                    listaEspecialidadesViewHolder.class,
                                    mRef) {

                        @Override
                        protected void populateViewHolder(listaEspecialidadesViewHolder viewHolder, Especialidades model, int position) {
                            viewHolder.setEspecialidade(listaEspecialidadesActivity.this, model.getNome());
                        }

                        @Override
                        public listaEspecialidadesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            listaEspecialidadesViewHolder especialidadesViewHolder = super.onCreateViewHolder(parent, viewType);
                            especialidadesViewHolder.setOnClickListener((view, position) -> {
                                vibrar();
                                TextView mNomeTv = view.findViewById(R.id.nome_esp);
                                String mNome = mNomeTv.getText().toString();

                                Intent i = new Intent(listaEspecialidadesActivity.this, MainActivity.class);
//                                    i.putExtra("title", mNome);
                                filtroMedicos filtro = new filtroMedicos();
                                filtro.setEspecialidade(mNome);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                listaEspecialidadesActivity.this.finish();
                            });
                            return especialidadesViewHolder;
                        }
                    };


            mRecyclerView.setAdapter(firebaseRecyclerAdapter);
        } catch (Exception e) {
            Toast.makeText(listaEspecialidadesActivity.this, "Não foi possível concluir a ação", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseSearch(String searchText) {
        final DatabaseReference buscaMed = mFirebaseDatabase.getReference("Especialidades");

        Query firebaseSearchQuery = buscaMed.orderByChild("nome").startAt(searchText).endAt(searchText.toLowerCase() + "\uf8ff");
        FirebaseRecyclerAdapter<Especialidades, listaEspecialidadesViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Especialidades, listaEspecialidadesViewHolder>(Especialidades.class,
                R.layout.row_especialidade, listaEspecialidadesViewHolder.class, firebaseSearchQuery) {
            @Override
            protected void populateViewHolder(listaEspecialidadesViewHolder viewHolder, Especialidades model, int position) {
                viewHolder.setEspecialidade(listaEspecialidadesActivity.this, model.getNome());
            }

            @Override
            public listaEspecialidadesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                listaEspecialidadesViewHolder especialidadesViewHolder = super.onCreateViewHolder(parent, viewType);
                especialidadesViewHolder.setOnClickListener((view, position) -> {
                    vibrar();
                    TextView mNomeTv = view.findViewById(R.id.nome_esp);
                    String mNome = mNomeTv.getText().toString();

                    Intent i = new Intent(listaEspecialidadesActivity.this, MainActivity.class);
//                        i.putExtra("title", mNome);
                    filtroMedicos filtro = new filtroMedicos();
                    filtro.setEspecialidade(mNome);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    listaEspecialidadesActivity.this.finish();


                });
                return especialidadesViewHolder;
            }
        };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
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
