package com.werkhaizer.anjo;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import utils.APIRetrofitService;
import utils.CEP;
import utils.CEPDeserializer;
import utils.MaskEditUtil;

import static utils.enderecoEditar.getCep;
import static utils.enderecoEditar.setBairro;
import static utils.enderecoEditar.setCep;
import static utils.enderecoEditar.setCidade;
import static utils.enderecoEditar.setRua;
import static utils.enderecoEditar.setUf;

public class editarCepFragment extends Fragment {

    private Button btnVoltar;
    private Button btnProximo, btnSemCep;
    private EditText mCep;
    private Dialog mDialog;


    public editarCepFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cep_editar_endereco, container, false);

        btnVoltar = v.findViewById(R.id.btnVoltarEditarCep);
        btnProximo = v.findViewById(R.id.btnEditarProxCep);
        btnSemCep = v.findViewById(R.id.btnEditarSemCep);
        mCep = v.findViewById(R.id.txtEditarCep);

        mCep.requestFocus();

        mCep.setText(getCep().toString());

        mCep.addTextChangedListener(MaskEditUtil.mask(mCep, MaskEditUtil.FORMAT_CEP));

        Gson g = new GsonBuilder().registerTypeAdapter(CEP.class, new CEPDeserializer()).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIRetrofitService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        final APIRetrofitService service = retrofit.create(APIRetrofitService.class);

        btnSemCep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar();


            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.viewPagerEditarEndereco, new editarApelidoEnderecoFragment()).commit();
            }
        });

        btnProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mCep.getText().toString().isEmpty()) {
                    vibrar();
                    Call<CEP> callCEPByCEP = service.getEnderecoByCEP(mCep.getText().toString());

                    callCEPByCEP.enqueue(new Callback<CEP>() {
                        @Override
                        public void onResponse(Call<CEP> call, Response<CEP> response) {
                            if (!response.isSuccessful()) {
                                vibrar();
                                dialogNC("Erro", "CEP inválido");
                                mCep.requestFocus();
                            } else {
                                vibrar();
                                CEP cep = response.body();
                                String endereco = cep.getLogradouro() + " \n" + cep.getBairro() + " \n" + cep.getLocalidade() + " \n" + cep.getUf();
                                if (endereco.equals("null \nnull \nnull \nnull")) {
                                    dialogNC("Erro", "Não foi possível encontrar nenhum endereço vinculado a esse CEP");
                                    mCep.requestFocus();
                                } else if (!endereco.isEmpty()) {

                                    setBairro(cep.getBairro());
                                    setUf(cep.getUf());
                                    setRua(cep.getLogradouro());
                                    setCidade(cep.getLocalidade());
                                    setCep(cep.getCep());



                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.viewPagerEditarEndereco, new confirmarEditarEnderecoFragment()).commit();

                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<CEP> call, Throwable t) {
                            vibrar();
                            dialogNC("Erro", "Digite um CEP válido");
                            mCep.requestFocus();
                        }
                    });
                }
                else {
                    Toast.makeText(getActivity(), "Favor digitar um Cep", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    public void openDialog() {
        mDialog = new Dialog(getActivity());
        //vamos remover o titulo da Dialog
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //vamos carregar o xml personalizado
        mDialog.setContentView(R.layout.dialog);
        //Deixar quase transparente o fundo
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.alpha(100)));
        // não permitimos fechar esta dialog
        mDialog.setCancelable(false);
        //temos a instancia do ProgressBar!
//        final ProgressBar progressBar = ProgressBar.class.cast(mDialog.findViewById(R.id.progressBar));
        final LottieAnimationView lottieLoading = LottieAnimationView.class.cast(mDialog.findViewById(R.id.loadingLottie));


        mDialog.show();

        // mDialog.dismiss(); -> para fechar a dialog

    }

    private void dialogNC(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mDialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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

}
