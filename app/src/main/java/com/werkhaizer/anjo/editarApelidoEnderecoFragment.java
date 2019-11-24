package com.werkhaizer.anjo;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import config.ConfiguracaoFirebase;
import utils.atualizarEndereco;

import static helper.UsuarioFirebase.getIdentificadorUsuario;
import static utils.enderecoEditar.getKey;
import static utils.enderecoEditar.getTitle;
import static utils.enderecoEditar.setBairro;
import static utils.enderecoEditar.setCep;
import static utils.enderecoEditar.setCidade;
import static utils.enderecoEditar.setLatitude;
import static utils.enderecoEditar.setLongitude;
import static utils.enderecoEditar.setRua;
import static utils.enderecoEditar.setTitle;
import static utils.enderecoEditar.setUf;


public class editarApelidoEnderecoFragment extends Fragment {

    private Button btnProximo, btnExcluir;
    private EditText mApelidoEndereco;
    private Button btnVoltar, btnDone;
    private DatabaseReference mRefEndereco;
    private Dialog mDialog;


    public editarApelidoEnderecoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_editar_apelido_endereco, container, false);

        btnProximo = v.findViewById(R.id.btnProximoApelidoEndereco);
        btnExcluir = v.findViewById(R.id.btnExcluirEndereco);
        mApelidoEndereco = v.findViewById(R.id.txtApelidoEndereco);
        btnDone =  v.findViewById(R.id.btnDoneEditarApelido);

        mApelidoEndereco.setText(getTitle());

        btnVoltar = v.findViewById(R.id.btnVoltarEditarApelido);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar();
                getActivity().finish();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnProximo.callOnClick();
            }
        });

        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference usuario = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios").child(getIdentificadorUsuario()).child("endAdicional").child(getKey());
                usuario.setValue( null );

                getActivity().finish();

            }
        });

        btnProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefEndereco = FirebaseDatabase.getInstance().getReference("usuarios")
                        .child(getIdentificadorUsuario())
                        .child("endAdicional")
                        .child(getKey());
                vibrar();
                String apelidoEndereco = mApelidoEndereco.getText().toString().trim();
                if (!apelidoEndereco.isEmpty()) {
                    setTitle(apelidoEndereco);

                    mRefEndereco.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("cep").exists()) {
                                String cep = dataSnapshot.child("cep").getValue().toString();
                                String bairro = dataSnapshot.child("bairro").getValue().toString();
                                String cidade = dataSnapshot.child("cidade").getValue().toString();
                                String latitude = dataSnapshot.child("latitude").getValue().toString();
                                String longitude = dataSnapshot.child("longitude").getValue().toString();
                                String rua = dataSnapshot.child("rua").getValue().toString();
                                String uf = dataSnapshot.child("uf").getValue().toString();

                                setCep(cep);
                                setBairro(bairro);
                                setCidade(cidade);
                                setLatitude(latitude);
                                setLongitude(longitude);
                                setRua(rua);
                                setUf(uf);

                                atualizarEndereco attEnd = new atualizarEndereco();
                                attEnd.atualizarEnderecoAdicional();

//                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                                fragmentTransaction.replace(R.id.viewPagerEditarEndereco, new editarCepFragment()).commit();


                                openDoneDialog();
                                new CountDownTimer(2500, 300) {
                                    public void onTick(long millisUntilFinished) {
                                    }

                                    public void onFinish() {
                                        vibrar();
                                        getActivity().finish();
                                    }
                                }.start();

                            } else {
                                Toast.makeText(getActivity(), "Erro ao recuperar o endereço cadastrado. Se o erro persistir, favor reportar a nossa equipe de suporte :)", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {
                    mApelidoEndereco.requestFocus();
                    Toast.makeText(getActivity(), "Dê algum apelido para o endereço", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return v;
    }

    private void vibrar() {
        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 30 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(15);
        }
    }
    public void openDoneDialog() {

        //DEIXAR O FUNDO BORRADO
//        RenderScript renderScript = RenderScript.create(EditarPerfil.this);
//        new RSBlurProcessor(renderScript).blur(getBitmapFromView(v), 15, 1);

        mDialog = new Dialog(getActivity());
        //vamos remover o titulo da Dialog
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //vamos carregar o xml personalizado
        mDialog.setContentView(R.layout.dialog_done);
        //Deixar quase transparente o fundo
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.alpha(100)));
        // não permitimos fechar esta dialog
        mDialog.setCancelable(false);
        //temos a instancia do ProgressBar!
//        final ProgressBar progressBar = ProgressBar.class.cast(mDialog.findViewById(R.id.progressBar));
        final LottieAnimationView lottieLoading = LottieAnimationView.class.cast(mDialog.findViewById(R.id.doneLottie));

        mDialog.show();

        // mDialog.dismiss(); -> para fechar a dialog

    }

}
