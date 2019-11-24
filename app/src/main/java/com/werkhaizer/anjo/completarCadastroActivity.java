package com.werkhaizer.anjo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import helper.Base64Custom;
import model.Usuario;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import utils.APIRetrofitService;
import utils.CEP;
import utils.CEPDeserializer;
import utils.Localizador;
import utils.MaskEditUtil;

public class completarCadastroActivity extends AppCompatActivity {

    private Usuario usuario = new Usuario();
    private int i = 0;
    private FirebaseAuth mAuth;
//    private Bundle extras = new Bundle();

    private Button btnCadDone;
    private LinearLayout llDadosPessoais, llInfoAdicionais;
    private TextView infoCad;
    private EditText mCpf;
    private EditText mNascimento;
    private EditText mConvenio;
    private EditText mCep;
    private EditText mRua;
    private EditText mBairro;
    private EditText mCidade;
    private EditText mUF;
    private EditText mConvenioNum;
    private EditText mNome;
    private RadioGroup rgSexo;
    private RadioButton rbM, rbF, rbNone;
    private String sexo = "NÃO SELECIONADO";

    private StorageReference mStorageRef;
    private ArrayList<CEP> arrayCEPs;
    private String identificadorUsuario;

    //Tag para o LOG
    private static final String TAG = "logCEP";
    private Dialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completar_cadastro);
        //checando se o usuario já terminou o cadastro.. se sim ele é redirecionado ao mainactivity
        try {
            if (usuario.getCep() != null) {
                Intent intent = new Intent(completarCadastroActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                extras.putString("view", "home");
//                intent.putExtras(extras);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        Gson g = new GsonBuilder().registerTypeAdapter(CEP.class, new CEPDeserializer()).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIRetrofitService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        final APIRetrofitService service = retrofit.create(APIRetrofitService.class);


        //VIEW
        llDadosPessoais = findViewById(R.id.llDadosPessoais);
        llInfoAdicionais = findViewById(R.id.llInfoAdicionais);
        btnCadDone = findViewById(R.id.btnDoneCad);
        infoCad = findViewById(R.id.txtInfoCadastro);

        //edit texts
        mCep = findViewById(R.id.editCEP);
        mNascimento = findViewById(R.id.editNasicmento);
        mCpf = findViewById(R.id.editCPF);
        mConvenio = findViewById(R.id.editConvenio);
        mConvenioNum = findViewById(R.id.editConvenioNum);
        mRua = findViewById(R.id.editRua);
        mBairro = findViewById(R.id.editBairro);
        mCidade = findViewById(R.id.editCidade);
        mUF = findViewById(R.id.editUF);
        mNome = findViewById(R.id.editNome);

        mCep.addTextChangedListener(MaskEditUtil.mask(mCep, MaskEditUtil.FORMAT_CEP));
        mNascimento.addTextChangedListener(MaskEditUtil.mask(mNascimento, MaskEditUtil.FORMAT_DATA));
        mCpf.addTextChangedListener(MaskEditUtil.mask(mCpf, MaskEditUtil.FORMAT_CPF));
        mConvenioNum.addTextChangedListener(MaskEditUtil.mask(mConvenioNum, MaskEditUtil.FORMAT_CONVENIO));

        //sexo rg
        rgSexo = findViewById(R.id.rgSexo);
        rbF = findViewById(R.id.rbF);
        rbM = findViewById(R.id.rbM);
        rbNone = findViewById(R.id.rbNone);


        //Config iniciais
        mStorageRef = FirebaseStorage.getInstance().getReference();
        llDadosPessoais.setVisibility(View.VISIBLE);
        llInfoAdicionais.setVisibility(View.GONE);
        infoCad.setText(R.string.info1Cadastro);
        mAuth = FirebaseAuth.getInstance();

        btnCadDone.setOnClickListener(v -> {
            vibrar();
            i++;
            if (llInfoAdicionais.getVisibility() == View.GONE) {
                //pasa pra próxima parte do cadastro
                if (!mCpf.getText().toString().isEmpty() &&
                        !mNascimento.getText().toString().isEmpty() &&
                        !mCep.getText().toString().isEmpty()) {

                    Call<CEP> callCEPByCEP = service.getEnderecoByCEP(mCep.getText().toString());

                    callCEPByCEP.enqueue(new Callback<CEP>() {
                        @Override
                        public void onResponse(Call<CEP> call, Response<CEP> response) {
                            if (!response.isSuccessful()) {
                                dialogNC("Erro", "CEP inválido");
                                mCep.requestFocus();
                                llInfoAdicionais.setVisibility(View.GONE);
                                llDadosPessoais.setVisibility(View.VISIBLE);
                            } else {
                                CEP cep = response.body();
                                String endereco = cep.getLogradouro() + " \n" + cep.getBairro() + " \n" + cep.getLocalidade() + " \n" + cep.getUf();
                                if (endereco.equals("null \nnull \nnull \nnull")) {
                                    dialogNC("Erro", "Não foi possível encontrar nenhum endereço vinculado a esse CEP");
                                    llInfoAdicionais.setVisibility(View.GONE);
                                    llDadosPessoais.setVisibility(View.VISIBLE);
                                    mCep.requestFocus();
                                } else if (!endereco.isEmpty()) {
                                    mRua.setText(cep.getLogradouro());
                                    mBairro.setText(cep.getBairro());
                                    mCidade.setText(cep.getLocalidade());
                                    mUF.setText(cep.getUf());
                                    Localizador localizador = new Localizador(completarCadastroActivity.this);
                                    LatLng local = localizador.getCoordenada(cep.getLogradouro() + ", " + cep.getBairro() + ", " + cep.getLocalidade() + ", " + cep.getUf());
                                    Double longitude = local.longitude;
                                    Double latitude = local.latitude;
                                    usuario.setLatitude(latitude);
                                    usuario.setLongitude(longitude);
                                    llDadosPessoais.setVisibility(View.GONE);
                                    infoCad.setText(R.string.info2Cadastro);
                                    llInfoAdicionais.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<CEP> call, Throwable t) {
                            vibrar();
                            dialogNC("Erro", "Digite um CEP válido");
                            llInfoAdicionais.setVisibility(View.GONE);
                            llDadosPessoais.setVisibility(View.VISIBLE);
                        }
                    });

                } else {
                    if (mCpf.getText().toString().isEmpty()) {
                        mCpf.requestFocus();
                        dialogNC("Erro", "Digite seu CPF");
                    }
                    if (mNascimento.getText().toString().isEmpty()) {
                        mNascimento.requestFocus();
                        dialogNC("Erro", "Data de nascimento não digitada");
                    }
                    if (mCep.getText().toString().isEmpty()) {
                        mCep.requestFocus();
                        dialogNC("Erro", "CEP não digitado");
                    } else {
                        rgSexo.requestFocus();
                        dialogNC("Erro", "Sexo não selecionado");
                    }
                }

            } else if (llInfoAdicionais.getVisibility() == View.VISIBLE) {

                if (!mRua.getText().toString().isEmpty() &&
                        !mCep.getText().toString().isEmpty()) {

                    try {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        //cadastra os campos e redireciona p o mainactiviy
                        openDialog();

                        final String cep, nascimento, cpf, convenio, convenioNum, rua, bairro, cidade, uf, nome, foto, email;
                        cep = mCep.getText().toString();
                        nascimento = mNascimento.getText().toString();
                        convenio = mConvenio.getText().toString();
                        convenioNum = mConvenioNum.getText().toString();
                        rua = mRua.getText().toString();
                        bairro = mBairro.getText().toString();
                        cidade = mCidade.getText().toString();
                        uf = mUF.getText().toString();
                        nome = mNome.getText().toString();
                        email = currentUser.getEmail();
                        cpf = mCpf.getText().toString();

                        //Set Avatar
                        ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
                        TextDrawable avatarPadrao = TextDrawable.builder()
                                .beginConfig()
                                .width(512)  // width in px
                                .height(512) // height in px
                                .endConfig()
                                .buildRect(String.valueOf(nome.toUpperCase().charAt(0)), colorGenerator.getRandomColor());

                        Bitmap avatarPadraoBitmap = drawableToBitmap(avatarPadrao);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        avatarPadraoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                        byte[] dadosImagem = baos.toByteArray();

                        //Salvando imagem no firebase
                        identificadorUsuario = Base64Custom.codificarBase64(email);
                        StorageReference imageRef = mStorageRef
                                .child("imagens")
                                .child("perfil")
                                .child(identificadorUsuario + ".jpeg");
                        UploadTask uploadTask = imageRef.putBytes(dadosImagem);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(completarCadastroActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        usuario.setFoto(uri.toString());
                                        if (rbM.isChecked()) {
                                            sexo = "Masculino";
                                        }
                                        if (rbF.isChecked()) {
                                            sexo = "Feminino";
                                        }
                                        if (rbNone.isChecked()) {
                                            sexo = "Outros";
                                        }
                                        String endereco;
                                        endereco = rua.replace("Rua", "R.").replace("Avenida", "Av.")+", "+bairro+", "+cidade+", "+uf;

                                        usuario.setId(Base64Custom.codificarBase64(email));
                                        usuario.setCep(cep);
                                        usuario.setNascimento(nascimento);
                                        usuario.setCpf(cpf);
                                        usuario.setConvenio(convenio);
                                        usuario.setConvenioNum(convenioNum);
                                        usuario.setEndereco(endereco);
                                        usuario.setNome(nome);
                                        usuario.setEmail(email);
                                        usuario.setSexo(sexo);

                                        usuario.salvar();

                                        Intent intent = new Intent(completarCadastroActivity.this, localUsuarioActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        completarCadastroActivity.this.finish();
                                    }
                                });
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(completarCadastroActivity.this, "Erro desconhecido " + e, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(completarCadastroActivity.this, Login.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        completarCadastroActivity.this.finish();

                    }


                } else {
                    if (mRua.getText().toString().isEmpty()) {
                        mRua.requestFocus();
                        dialogNC("Erro", "Endereço vazio");
                    }
                    if (!rbM.isChecked() && !rbF.isChecked() && !rbNone.isChecked()) {
                        rgSexo.requestFocus();
                        dialogNC("Erro", "Sexo não selecionado");
                    }
                }
            }
        });


    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }


        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 512; // Replaced the 1 by a 96
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 512; // Replaced the 1 by a 96

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
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
    public void openDialog() {
        mDialog = new Dialog(this);
        //vamos remover o titulo da Dialog
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //vamos carregar o xml personalizado
        mDialog.setContentView(R.layout.dialog);
        //Deixar quase transparente o fundo
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.alpha(100)));
        //permitimos fechar esta dialog
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(true);
        //temos a instancia do ProgressBar!
//        final ProgressBar progressBar = ProgressBar.class.cast(mDialog.findViewById(R.id.progressBar));
        final LottieAnimationView lottieLoading = LottieAnimationView.class.cast(mDialog.findViewById(R.id.loadingLottie));


        mDialog.show();

        // mDialog.dismiss(); -> para fechar a dialog

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
