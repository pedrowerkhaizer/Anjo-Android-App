package com.werkhaizer.anjo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import config.ConfiguracaoFirebase;
import de.hdodenhof.circleimageview.CircleImageView;
import helper.UsuarioFirebase;
import model.Usuario;
import utils.MaskEditUtil;

import static helper.UsuarioFirebase.atualizarFotoUsuario;
import static helper.UsuarioFirebase.getIdentificadorUsuario;
import static helper.UsuarioFirebase.getUsuarioAtual;


public class EditarPerfil extends AppCompatActivity implements View.OnClickListener {
    private CircleImageView mEditImage;
    private EditText mNome, mEmail, mCep, mEndereco, mConvenio, mConvenioNum;
    private Button mEditarFoto, btnAddConvenio,  btnEditarConvenio, btnExcluirConvenio, mCloseBtn, mDoneBtn;
    private TextView mTitle;
    private LinearLayout containerConvenio;

    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageRef;
    private String identificadorUsuario;
    private Usuario usuarioLogado = new Usuario();


    private Dialog mDialog;
    private String cepAtualizado, nomeAtualizado, enderecoAtualizado, convenioAtualizado, numConvAtualizado;
    private DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        //Configurações iniciais
        mRef = FirebaseDatabase.getInstance().getReference("usuarios/" + getIdentificadorUsuario() + "/dados");


        mNome = findViewById(R.id.editNomePerfil);
        mCep = findViewById(R.id.editCEPPerfil);
        mEmail = findViewById(R.id.editEmailPerfil);
        mEndereco = findViewById(R.id.editEnderecoPerfil);
        mConvenio = findViewById(R.id.editConvenioPerfil);
        mConvenioNum = findViewById(R.id.editConvenioNumPerfil);
        containerConvenio = findViewById(R.id.containerConvenio);
        btnAddConvenio = findViewById(R.id.btnAddConvenio);
        mEditImage = findViewById(R.id.editarFotoCIV);
        mEditarFoto = findViewById(R.id.editarFotoBtn);
        mCloseBtn = findViewById(R.id.btn_closeEdit);
        mTitle = findViewById(R.id.title_toolbarEditPerfil);
        mDoneBtn = findViewById(R.id.btn_doneEdit);
        btnEditarConvenio = findViewById(R.id.btnEditarConvenio);
        btnExcluirConvenio = findViewById(R.id.btnExcluirConvenio);

        mEmail.setFocusable(false);
        mEmail.setFocusableInTouchMode(false);


        //formatando os edittexts
        mCep.addTextChangedListener(MaskEditUtil.mask(mCep, MaskEditUtil.FORMAT_CEP));
        mConvenioNum.addTextChangedListener(MaskEditUtil.mask(mConvenioNum, MaskEditUtil.FORMAT_CONVENIO));


        //Recuperar os dados do Firebase
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String email = dataSnapshot.child("email").getValue().toString();
                String cep = dataSnapshot.child("cep").getValue().toString();
                String nome = dataSnapshot.child("nome").getValue().toString();
                String endereco = dataSnapshot.child("endereco").getValue().toString();
                String convenio = dataSnapshot.child("convenio").getValue().toString();
                String convenioNum = dataSnapshot.child("convenioNum").getValue().toString();
                String foto = dataSnapshot.child("foto").getValue().toString();
                Double lat = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                Double lng = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
                String sexo = dataSnapshot.child("sexo").getValue().toString();
                String nascimento = dataSnapshot.child("nascimento").getValue().toString();

                Picasso.get().load(foto).into(mEditImage);
                mEmail.setText(email);
                mCep.setText(cep);
                mNome.setText(nome);
                mEndereco.setText(endereco);
                mTitle.setText(nome);

                if (!convenio.isEmpty()) {
                    containerConvenio.setVisibility(View.VISIBLE);
                    btnAddConvenio.setVisibility(View.GONE);
                    mConvenio.setText(convenio);
                    mConvenioNum.setText(convenioNum);
                } else {
                    containerConvenio.setVisibility(View.GONE);
                    btnAddConvenio.setVisibility(View.VISIBLE);
                    mConvenio.setText(null);
                    mConvenioNum.setText(null);
                }

                usuarioLogado.setFoto(foto);
                usuarioLogado.setEmail(email);
                usuarioLogado.setCep(cep);
                usuarioLogado.setNome(nome);
                usuarioLogado.setEndereco(endereco);
                usuarioLogado.setConvenioNum(convenioNum);
                usuarioLogado.setConvenio(convenio);
                usuarioLogado.setLatitude(lat);
                usuarioLogado.setLongitude(lng);
                usuarioLogado.setSexo(sexo);
                usuarioLogado.setNascimento(nascimento);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                vibrar();
                Toast.makeText(EditarPerfil.this, "" + databaseError, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(EditarPerfil.this, failActivity.class);
                startActivity(i);
                EditarPerfil.this.finish();
            }
        });

        btnAddConvenio.setOnClickListener(v -> {
            Intent i = new Intent(this, adicionarConvenioActivity.class);
            startActivity(i);
            EditarPerfil.this.finish();
        });

        btnExcluirConvenio.setOnClickListener(v ->{
            Usuario usuario = new Usuario();
            usuario.setConvenio("");
            usuario.setConvenioNum("");
            usuario.atualizarConvenio();
            Intent i = new Intent(EditarPerfil.this, EditarPerfil.class);
            startActivity(i);
            EditarPerfil.this.finish();
        });

        btnEditarConvenio.setOnClickListener(v -> {
            Intent i = new Intent(this, adicionarConvenioActivity.class);
            startActivity(i);
            EditarPerfil.this.finish();
        });

        //configurando o storage do firebase
        storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

        //ativando o evento click para alterar a foto de perfil
        mEditImage.setOnClickListener(this);
        mEditarFoto.setOnClickListener(this);


        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nomeAtualizado = mNome.getText().toString();
                convenioAtualizado = mConvenio.getText().toString();
                numConvAtualizado = mConvenioNum.getText().toString();

                if (nomeAtualizado != usuarioLogado.getNome() ||
                        convenioAtualizado != usuarioLogado.getConvenio() ||
                        numConvAtualizado != usuarioLogado.getConvenioNum()) {
                    mNome.setFocusable(false);
                    mConvenio.setFocusable(false);
                    mConvenioNum.setFocusable(false);

                    openDoneDialog();
                    new CountDownTimer(2500, 300) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            vibrar();
                            finish();
                        }
                    }.start();


                    //Atualizar no perfil
                    UsuarioFirebase.atualizarNomeUsuario(nomeAtualizado);

                    //Atualizar no RDB
                    usuarioLogado.setNome(nomeAtualizado);
                    usuarioLogado.setConvenio(convenioAtualizado);
                    usuarioLogado.setConvenioNum(numConvAtualizado);
                    usuarioLogado.atualizar();

                } else {
                    finish();
                }

            }
        });

        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar();
                finish();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//TODO PESQUISAR COMO CHAMAR UMA TELA PARA FAZER O CROP DA IMAGEM DO PERFIL
        if (resultCode == RESULT_OK) {

            Bitmap imagem = null;
            try {
                //Selecionar apenas da galeria

                switch (requestCode) {
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;
                }
                //caso  tenha sido escolhido uma imagem
                if (imagem != null) {
                    //configurar imagem
                    mEditImage.setImageBitmap(imagem);
                    final Bitmap finalImagem = imagem;


                    mDoneBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            vibrar();
//                            mEditProgressbar.setVisibility(View.VISIBLE);
                            openDialog();
                            //Recuperar dados da imagem para o firebase
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            finalImagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                            byte[] dadosImagem = baos.toByteArray();

                            //Salvando imagem no firebase
                            StorageReference imageRef = storageRef
                                    .child("imagens")
                                    .child("perfil")
                                    .child(identificadorUsuario + ".jpeg");
                            UploadTask uploadTask = imageRef.putBytes(dadosImagem);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditarPerfil.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                    mEditProgressbar.setVisibility(View.GONE);
                                    mDialog.dismiss();
                                    //Recuperar o local da foto
                                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            //Atualizar a foto no perfil
                                            atualizarFotoUsuario(uri);

                                            //Atualizar a foto no firebase

                                            usuarioLogado.setFoto(uri.toString());
                                            usuarioLogado.setNome(usuarioLogado.getNome());
//                                            usuarioLogado.setEmail(usuarioLogado.getEmail());
                                            usuarioLogado.atualizar();
                                            Toast.makeText(EditarPerfil.this, "Sua foto foi atualizada!", Toast.LENGTH_SHORT).show();
                                            openDoneDialog();
                                            EditarPerfil.this.finish();
                                        }
                                    });
                                }
                            });
                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void escolherFoto() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (i.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(i, SELECAO_GALERIA);

        }
    }


    @Override
    public void onClick(View v) {
        vibrar();
        switch (v.getId()) {
            case R.id.editarFotoBtn:
                escolherFoto();
                break;
            case R.id.editarFotoCIV:
                escolherFoto();
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        Intent i = new Intent(EditarPerfil.this, MainActivity.class);
////        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(i);
        EditarPerfil.this.finish();
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

    public void openDoneDialog() {

        //DEIXAR O FUNDO BORRADO
//        RenderScript renderScript = RenderScript.create(EditarPerfil.this);
//        new RSBlurProcessor(renderScript).blur(getBitmapFromView(v), 15, 1);

        mDialog = new Dialog(this);
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