package fragment;


import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.werkhaizer.anjo.EditarPerfil;
import com.werkhaizer.anjo.Login;
import com.werkhaizer.anjo.PerfilDisplayActivity;
import com.werkhaizer.anjo.R;
import com.werkhaizer.anjo.failActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import config.ConfiguracaoFirebase;
import de.hdodenhof.circleimageview.CircleImageView;
import helper.UsuarioFirebase;
import model.Usuario;
import utils.criptografia;

import static helper.UsuarioFirebase.getIdentificadorUsuario;


public class PerfilFragment extends Fragment {
    private FirebaseAuth mAuth;


    private TextView mPerfilNome, mPerfilEmail;
    private CircleImageView mPerfilFoto;
    private Button mPerfilAlergiasBtn, mPerfilHistoricoBtn, mPerfilFisicoBtn, mDisplayPerfil;
    private LinearLayout mPerfilEditarPerfil, mPerfilSairBtn;
    private static final int SELECAO_GALERIA = 200;

    private StorageReference storageRef;

    private Dialog mDialog;

    private criptografia cripto = new criptografia("4e0A3d6G0a");
    private DatabaseReference mRef;


    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        mPerfilNome = view.findViewById(R.id.perfilNomeTV);
        mPerfilEmail = view.findViewById(R.id.perfilEmailTV);
        mPerfilFoto = view.findViewById(R.id.perfilFotoCIV);

        mPerfilAlergiasBtn = view.findViewById(R.id.alergiasPerfilBTN);
        mPerfilEditarPerfil = view.findViewById(R.id.llEditarPerfil);
        mPerfilFisicoBtn = view.findViewById(R.id.aspectosFisicosPerfilBTN);
        mPerfilHistoricoBtn = view.findViewById(R.id.historicoPerfilBTN);
        mPerfilSairBtn = view.findViewById(R.id.llSairPerfil);
        mDisplayPerfil = view.findViewById(R.id.btnDisplayPerfil);

        mAuth = FirebaseAuth.getInstance();

        recuperarDadosPerfil();

        mPerfilFoto.performClick();
        mPerfilFoto.setOnTouchListener((v, event) -> {
            int actionMasked = event.getActionMasked();
            switch (actionMasked) {

                case MotionEvent.ACTION_DOWN:
                    // show preview
                    try {
                        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
                        openFoto();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "No momento não foi possível, tente novamente em instantes!", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // hide preview
                    try {
                        mDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                default:

            }

            return true;
        });


        storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        mPerfilEditarPerfil.setOnClickListener(v -> {
            vibrar();
            Intent editarPerfil = new Intent(getActivity(), EditarPerfil.class);
            startActivity(editarPerfil);
        });

        mPerfilAlergiasBtn.setOnClickListener(v -> {
            vibrar();
            Intent i = new Intent(getActivity(), failActivity.class);
            startActivity(i);
        });

        mPerfilFisicoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar();
            }
        });

        mPerfilHistoricoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar();
            }
        });

        mPerfilSairBtn.setOnClickListener(v -> {
            vibrar();
            deslogarUsu();
        });
        mDisplayPerfil.setOnClickListener(v -> {
            vibrar();
            Intent i = new Intent(getActivity(), PerfilDisplayActivity.class);
            startActivity(i);
        });

        return view;
    }

    private void recuperarDadosPerfil() {
        //Recuperar os dados do Firebase
        mRef = FirebaseDatabase.getInstance().getReference("usuarios/" + getIdentificadorUsuario() + "/dados");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String foto = dataSnapshot.child("foto").getValue().toString();
                String nome = dataSnapshot.child("nome").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                Picasso.get().load(foto).into(mPerfilFoto);
                mPerfilNome.setText(nome);
                mPerfilEmail.setText(email);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void deslogarUsu() {
        FirebaseUser user = mAuth.getCurrentUser();
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    public void openFoto() {
        FirebaseUser user = mAuth.getCurrentUser();
        //Recuperar os dados do Firebase
        mRef = FirebaseDatabase.getInstance().getReference("usuarios/" + getIdentificadorUsuario() + "/dados");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String foto = dataSnapshot.child("foto").getValue().toString();
                String nome = dataSnapshot.child("nome").getValue().toString();

                mDialog = new Dialog(getContext());
                //vamos remover o titulo da Dialog
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //vamos carregar o xml personalizado
                mDialog.setContentView(R.layout.dialog_foto);
                //Deixar quase transparente o fundo
                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.alpha(151)));

                //TODO Blur fundo (n está funcionando)
                //RenderScript renderScript = RenderScript.create(v.getContext());
                //new RSBlurProcessor(renderScript).blur(BitmapUtils.getBitmapFromView(v), 15, 1);

                mDialog.setCancelable(true);

                final ImageView imgFoto = ImageView.class.cast(mDialog.findViewById(R.id.foto_dialog));
                final TextView txtFoto = TextView.class.cast(mDialog.findViewById(R.id.txtFotoDialog));
                txtFoto.setText(nome);
                Picasso.get().load(foto).into(imgFoto);

                mDialog.show();

                // mDialog.dismiss(); -> para fechar a dialog

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
