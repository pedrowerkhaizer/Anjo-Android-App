package fragment;


import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.werkhaizer.anjo.ViewHolder.MedicosFavViewHolder;
import com.werkhaizer.anjo.PerfilMedicoActivity;
import com.werkhaizer.anjo.R;
import com.werkhaizer.anjo.chatMedActivity;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import helper.UsuarioFirebase;
import model.MedSelecionado;

import static utils.Vibrar.vibrar;
import static utils.selectedMedicData.setCrm;
import static utils.selectedMedicData.setEspecialidade;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritosFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();


    private SearchView svFav;
    private TextView logo;
    private ImageButton btnFiltroFav;


    public FavoritosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_favoritos, container, false);

        mRecyclerView = view.findViewById(R.id.rvMedicosFavoritos);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        ConstraintLayout include404 = view.findViewById(R.id.include404);
        include404.setVisibility(View.INVISIBLE);

        svFav = view.findViewById(R.id.svFav);
        logo = view.findViewById(R.id.lblTitleFavoritos);
        btnFiltroFav = view.findViewById(R.id.btnFiltrosFav);

        Button btnVerListaMedicos = view.findViewById(R.id.btnVerListaMedicos);
        btnVerListaMedicos.setOnClickListener(v -> {
            Toast.makeText(view.getContext(), "Clicado", Toast.LENGTH_SHORT).show();
//                MainActivity mainActivity = new MainActivity();
//                mainActivity.chamarHome();
        });

        DatabaseReference mRef = mFirebaseDatabase.getReference("usuarios/" + UsuarioFirebase.getIdentificadorUsuario() + "/med_fav");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = (int) dataSnapshot.getChildrenCount();
                if (i == 0) {
                    ConstraintLayout include404 = view.findViewById(R.id.include404);
                    include404.setEnabled(true);
                    Button btnVerListaMedicos = view.findViewById(R.id.btnVerListaMedicos);
                    btnVerListaMedicos.setEnabled(true);
                    include404.setVisibility(View.VISIBLE);
                    btnVerListaMedicos.setOnClickListener(v -> {
                        Toast.makeText(view.getContext(), "Clicado", Toast.LENGTH_SHORT).show();
//                            MainActivity mainActivity = new MainActivity();
//                            mainActivity.chamarHome();
                    });
                } else if (i > 0) {
                    ConstraintLayout include404 = view.findViewById(R.id.include404);
                    include404.setEnabled(false);
                    Button btnVerListaMedicos = view.findViewById(R.id.btnVerListaMedicos);
                    btnVerListaMedicos.setEnabled(false);
                    include404.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        svFav.setOnCloseListener(() -> {
            vibrar(getContext());
            logo.setVisibility(View.VISIBLE);
            return false;
        });

        svFav.setOnSearchClickListener(v -> {
            vibrar(getContext());
            logo.setVisibility(View.GONE);
            svFav.setQueryHint("Criar uma searchview activity p os favoritos");
        });

        btnFiltroFav.setOnClickListener(v -> {
//                Bundle bundle = getArguments();
//                final String espMed = bundle.getString("title");
//                Fragment fragment = new FiltrosFragment();
//                bundle.putString("title", espMed);
//                fragment.setArguments(bundle);
//                FragmentManager manager = getFragmentManager();
//                mRecyclerView.removeAllViews();
//                manager.beginTransaction().replace(R.id.viewPager, fragment, fragment.getTag()).addToBackStack(null).commit();

            //TODO FAZER O FILTRO DOS FAVORITOS
            Toast.makeText(getContext(), "Criar um filtro p os favoritos", Toast.LENGTH_SHORT).show();
        });

        return view;
    }



    @Override
    public void onStart() {
        super.onStart();
        listarFav();
    }

    public void listarFav() {

//        Bundle bundle = getArguments();
//        String orderby = bundle.getString("filtro");
        DatabaseReference mRef = mFirebaseDatabase.getReference("usuarios/" + UsuarioFirebase.getIdentificadorUsuario() + "/med_fav");
        Query firebaseSearchQuery = mRef.orderByChild("nome_med");

        final FirebaseRecyclerAdapter<MedSelecionado, MedicosFavViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<MedSelecionado,
                MedicosFavViewHolder>(MedSelecionado.class,
                R.layout.row_medicos_favoritos, MedicosFavViewHolder.class, firebaseSearchQuery) {

            @Override
            public MedSelecionado getItem(int position) {
                return super.getItem(getItemCount() - 1 - position);
            }

            @Override
            protected void populateViewHolder(MedicosFavViewHolder viewHolder, MedSelecionado model, int position) {
                viewHolder.setMedicos(getContext(), model.getCep_med(), model.getConvenio_med(),
                        model.getCpf_med(), model.getCrm_med(), model.getEmail_med(), model.getEndereco_med(),
                        model.getEsp_med(), model.getFoto_med(), model.getNascimento_med(), model.getNome_med(),
                        model.getRating_med(), model.getSexo_med(), model.getLatitude_med(), model.getLongitude_med());

            }

            @Override
            public MedicosFavViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                final MedicosFavViewHolder medicosFavViewHolder = super.onCreateViewHolder(parent, viewType);
                medicosFavViewHolder.setOnClickListener(new MedicosFavViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        vibrar(getContext());
                        TextView crm_medTV = view.findViewById(R.id.crmMedFav);
                        String crm_med = crm_medTV.getText().toString();
                        TextView espMedTV = view.findViewById(R.id.especialidadeMedFav);
                        String espMed = espMedTV.getText().toString();
                        Intent intent = new Intent(getContext(), PerfilMedicoActivity.class);
                        setCrm(crm_med);
                        setEspecialidade(espMed);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, final int position) {
                        dialogOptions(view, getContext());

//                        vibrar();
//                        TextView crm_medTV = view.findViewById(R.id.crmMedFav);
//                        final String crm_med = crm_medTV.getText().toString();
//                        TextView espMedTV = view.findViewById(R.id.especialidadeMedFav);
//                        final String espMed = espMedTV.getText().toString();
//
//                        FloatingActionButton delete = view.findViewById(R.id.civDelete);
//                        FloatingActionButton perfil = view.findViewById(R.id.civPerfil);
//                        final LinearLayout options = view.findViewById(R.id.rowOptions);
//                        final LinearLayout principal = view.findViewById(R.id.rowPrincipal);
//
//                        options.setVisibility(View.VISIBLE);
//                        options.setEnabled(true);
//                        principal.setEnabled(false);
//
//                        delete.setOnClickListener(v -> {
//                            vibrar();
//                            MedSelecionado medSelecionado = new MedSelecionado();
//                            medSelecionado.setCrm_med(crm_med);
//                            options.setVisibility(View.GONE);
//                            options.setEnabled(false);
//                            medSelecionado.excluir();
//                            listarFav();
//                        });
//
//                        perfil.setOnClickListener(v -> {
//                            vibrar();
//                            Intent intent = new Intent(getContext(), PerfilMedicoActivity.class);
//                            intent.putExtra("crm", crm_med);
//                            intent.putExtra("espMed", espMed);
//                            startActivity(intent);
//                        });
//
//                        options.setOnClickListener(v -> {
//                            vibrar();
//                            options.setVisibility(View.GONE);
//                            options.setEnabled(false);
//                            principal.setEnabled(true);
//                        });

//                        Intent intent = new Intent(getContext(), PopupPerfilMedico.class);
//                        intent.putExtra("crm", crm_med.toString());
//                        intent.putExtra("espMed", espMed);
//                        startActivity(intent);
                    }

                    @Override
                    public void onImageClick(View view, int position) {
                        vibrar(getContext());
                        dialogOptions(view, getContext());


//                        vibrar();
//                        TextView crm_medTV = view.findViewById(R.id.crmMedFav);
//                        final String crm_med = crm_medTV.getText().toString();
//                        TextView espMedTV = view.findViewById(R.id.especialidadeMedFav);
//                        final String espMed = espMedTV.getText().toString();
//
//                        FloatingActionButton delete = view.findViewById(R.id.civDelete);
//                        FloatingActionButton perfil = view.findViewById(R.id.civPerfil);
//                        final LinearLayout options = view.findViewById(R.id.rowOptions);
//                        final LinearLayout principal = view.findViewById(R.id.rowPrincipal);
//
//                        options.setVisibility(View.VISIBLE);
//                        options.setEnabled(true);
//                        principal.setEnabled(false);
//
//                        delete.setOnClickListener(v -> {
//                            vibrar();
//                            MedSelecionado medSelecionado = new MedSelecionado();
//                            medSelecionado.setCrm_med(crm_med);
//                            options.setVisibility(View.GONE);
//                            options.setEnabled(false);
//                            medSelecionado.excluir();
//                            listarFav();
//                        });
//
//                        perfil.setOnClickListener(v -> {
//                            vibrar();
//                            Intent intent = new Intent(getContext(), PerfilMedicoActivity.class);
//                            intent.putExtra("crm", crm_med);
//                            intent.putExtra("espMed", espMed);
//                            startActivity(intent);
//                        });
//
//                        options.setOnClickListener(v -> {
//                            vibrar();
//                            options.setVisibility(View.GONE);
//                            options.setEnabled(false);
//                            principal.setEnabled(true);
//                        });
                    }
                });
                return medicosFavViewHolder;
            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public void dialogOptions(View view, Context context){
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        TextView crm_medTV = view.findViewById(R.id.crmMedFav);
        String crm_med = crm_medTV.getText().toString();
        // add a list
        String[] options = {"Excluir", "Cancelar"};
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Excluir
                    vibrar(context);
                    MedSelecionado medSelecionado = new MedSelecionado();
                    medSelecionado.setCrm_med(crm_med);
                    medSelecionado.excluir();
                    listarFav();
                    break;
                case 1: //Cancelar
                    vibrar(context);
                    dialog.dismiss();
                    break;
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
