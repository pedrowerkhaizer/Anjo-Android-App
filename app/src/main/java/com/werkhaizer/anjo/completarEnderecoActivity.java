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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import model.EnderecoAdicional;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import utils.APIRetrofitService;
import utils.CEP;
import utils.CEPDeserializer;
import utils.Localizador;

import static helper.UsuarioFirebase.getIdentificadorUsuario;

public class completarEnderecoActivity extends AppCompatActivity {

    private EditText mRua, mBairro, mCidade, mUf, mTitle;
    private Button btnDone, bntClose, btnRight;

    private ArrayList<CEP> arrayCEPs;

    private Dialog mDialog;

    //Tag para o LOG
    private static final String TAG = "logCEP";

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("usuarios/" + getIdentificadorUsuario() + "/endAdicional");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completar_endereco);

        mRua = findViewById(R.id.txtRua);
        mBairro = findViewById(R.id.txtBairro);
        mCidade = findViewById(R.id.txtCidade);
        mUf = findViewById(R.id.txtUF);
        btnDone = findViewById(R.id.btnDoneEnd);
        bntClose = findViewById(R.id.btnCloseCE);
        mTitle = findViewById(R.id.txtTitle);
        btnRight = findViewById(R.id.imgbtnDoneEnd);

        if (getIntent().hasExtra("rua")) {
            Bundle bundle = getIntent().getExtras();
            String rua = bundle.getString("rua");
            mRua.setText(rua);
        }
        if (getIntent().hasExtra("bairro")){
            Bundle bundle = getIntent().getExtras();
            String bairro = bundle.getString("bairro");
            mBairro.setText(bairro);
        }
        if (getIntent().hasExtra("cidade")){
            Bundle bundle = getIntent().getExtras();
            String cidade = bundle.getString("cidade");
            mCidade.setText(cidade);
        }
        if (getIntent().hasExtra("uf")){
            Bundle bundle = getIntent().getExtras();
            String uf = bundle.getString("uf");
            mUf.setText(uf);
        }

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDone.callOnClick();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
                vibrar();
                if (!mRua.getText().toString().isEmpty() && !mBairro.getText().toString().isEmpty() && !mCidade.getText().toString().isEmpty() && !mUf.getText().toString().isEmpty() && !mTitle.getText().toString().isEmpty()) {
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.child(mTitle.getText().toString()).exists()) {

                                Gson g = new GsonBuilder().registerTypeAdapter(CEP.class, new CEPDeserializer()).create();

                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(APIRetrofitService.BASE_URL)
                                        .addConverterFactory(GsonConverterFactory.create(g))
                                        .build();

                                final APIRetrofitService service = retrofit.create(APIRetrofitService.class);
                                Call<List<CEP>> callCEPByEnd = service.getCEPByCidadeEstadoEndereco(mUf.getText().toString(), mCidade.getText().toString(), mRua.getText().toString());
                                callCEPByEnd.enqueue(new Callback<List<CEP>>() {
                                    @Override
                                    public void onResponse(Call<List<CEP>> call, Response<List<CEP>> response) {
                                        if (!response.isSuccessful()) {
                                            vibrar();
                                            dialogNC("Erro", "Não foi possível encontrar um cep nesse endereço");
                                            mDialog.dismiss();
                                        } else {
                                            List<CEP> CEPAux = response.body();

                                            Log.d(TAG, CEPAux.toString());

                                            arrayCEPs = new ArrayList<>();

                                            for (CEP cep : CEPAux) {
                                                arrayCEPs.add(cep);
                                            }
                                            CEP CepByEnd = arrayCEPs.get(0);
                                            Toast.makeText(completarEnderecoActivity.this, CepByEnd.toString(), Toast.LENGTH_SHORT).show();

                                            //Retorno no Log
                                            Log.d(TAG, arrayCEPs.toString());

                                            EnderecoAdicional enderecoAdicional = new EnderecoAdicional();
                                            enderecoAdicional.setRua(mRua.getText().toString());
                                            enderecoAdicional.setBairro(mBairro.getText().toString());
                                            enderecoAdicional.setCidade(mCidade.getText().toString());
                                            enderecoAdicional.setUf(mUf.getText().toString());
                                            enderecoAdicional.setCep(CepByEnd.getCep());
                                            Localizador localizador = new Localizador(completarEnderecoActivity.this);
                                            LatLng local = localizador.getCoordenada(mRua.getText().toString()+ ", " +mBairro.getText().toString()+", " + mCidade.getText().toString()+", " +mUf.getText().toString());
                                            String latitude = String.valueOf(local.latitude);
                                            String longitude = String.valueOf(local.longitude);
                                            enderecoAdicional.setLatitude(latitude);
                                            enderecoAdicional.setLongitude(longitude);
                                            enderecoAdicional.setTitle(mTitle.getText().toString());
                                            enderecoAdicional.salvar();

//                                            Intent intent = new Intent(completarEnderecoActivity.this, MainActivity.class);
//                                            //TODO Pesquisar anjos no local cadastrado
//                                            startActivity(intent);
                                            completarEnderecoActivity.this.finish();
                                            Toast.makeText(completarEnderecoActivity.this, "Endereco " + enderecoAdicional.getTitle() + " foi cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                                        }
                                        mDialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(Call<List<CEP>> call, Throwable t) {
                                        vibrar();
                                        mDialog.dismiss();
                                    }
                                });

                            } else if (dataSnapshot.child(mTitle.getText().toString()).exists()) {
                                dialogNC("Erro", "Esse nome de endereço já está cadastrado");
                                mDialog.dismiss();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            mDialog.dismiss();
                        }
                    });

                } else {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(completarEnderecoActivity.this);
                    builder.setTitle("Endereço vazio");
                    builder.setMessage("Preencha todos os campos");
                    builder.setCancelable(true);
//                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
                    builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }


            }
        });

        bntClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar();
                Intent intent = new Intent(completarEnderecoActivity.this, localUsuarioActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                openDialog();
                completarEnderecoActivity.this.finish();
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
        mDialog.setCancelable(true);
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
