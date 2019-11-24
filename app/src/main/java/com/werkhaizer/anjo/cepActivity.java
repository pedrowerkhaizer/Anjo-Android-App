package com.werkhaizer.anjo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import utils.APIRetrofitService;
import utils.CEP;
import utils.CEPDeserializer;
import utils.MaskEditUtil;

public class cepActivity extends AppCompatActivity {

    private EditText mCep;
    private Button mProx, mClose, mSemCep;

    private ArrayList<CEP> arrayCEPs;

    private Dialog mDialog;

    //Tag para o LOG
    private static final String TAG = "logCEP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cep);
        mCep = findViewById(R.id.txtCep1);
        mProx = findViewById(R.id.btnProxCep);
        mClose = findViewById(R.id.btnCloseCEP);
        mSemCep = findViewById(R.id.btnSemCep);

        mCep.requestFocus();

        mCep.addTextChangedListener(MaskEditUtil.mask(mCep, MaskEditUtil.FORMAT_CEP));

        Gson g = new GsonBuilder().registerTypeAdapter(CEP.class, new CEPDeserializer()).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIRetrofitService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        final APIRetrofitService service = retrofit.create(APIRetrofitService.class);


        mProx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar();
                openDialog();
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
                            String endereco = cep.getLogradouro() +" \n"+ cep.getBairro() +" \n" +cep.getLocalidade()+  " \n"+ cep.getUf();
                            if(endereco.toString().equals("null \nnull \nnull \nnull")){
                                dialogNC("Erro", "Não foi possível encontrar nenhum endereço vinculado a esse CEP");
                                mCep.requestFocus();
                            }
                            else if(!endereco.isEmpty()){

                                Intent intent = new Intent(cepActivity.this, completarEnderecoActivity.class);
                                intent.putExtra("rua", cep.getLogradouro());
                                intent.putExtra("bairro", cep.getBairro());
                                intent.putExtra("cidade", cep.getLocalidade());
                                intent.putExtra("uf", cep.getUf());

                                //TODO Recuperar a lat/long do cep
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                cepActivity.this.finish();
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
        });

        mSemCep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar();
                Intent intent = new Intent(cepActivity.this, completarEnderecoActivity.class);
                startActivity(intent);
                cepActivity.this.finish();
            }
        });


        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar();
                Intent intent = new Intent(cepActivity.this, localUsuarioActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                cepActivity.this.finish();

            }
        });
    }
    public void openDialog() {
        mDialog = new Dialog(this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
    private void vibrar()
    {
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
