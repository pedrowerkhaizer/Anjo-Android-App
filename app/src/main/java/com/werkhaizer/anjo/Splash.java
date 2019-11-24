package com.werkhaizer.anjo;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import helper.Base64Custom;
import utils.FragmentAtual;
import utils.coordenadaSelecionada;
import utils.filtroConsulta;
import utils.filtroMedicos;

import static helper.UsuarioFirebase.getIdentificadorUsuario;


public class Splash extends AppCompatActivity {
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);

    }

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = mFirebaseDatabase.getReference("usuarios");
    private DatabaseReference mRefCoordenadas;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        new SplashTask().execute();

    }

    private class SplashTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... voids) {

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(Splash.this, gso);
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                updateUI(currentUser);
            }
            else{
                //NÃO CADASTRADO
                Intent intent = new Intent(Splash.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                Splash.this.finish();
            }


            //filtro padrao das consultas marcadas
            filtroConsulta.setDia(true);
            filtroConsulta.setDistancia(false);
            //filtro padrao da lista de medicos
            filtroMedicos.setDistancia(true);
            filtroMedicos.setRating(false);
            filtroMedicos.setEspecialidade("Cardiologia");
            //fragment padrão
            FragmentAtual.setMain(true);
            FragmentAtual.setConsulta(false);
            FragmentAtual.setFavorito(false);
            FragmentAtual.setPerfil(false);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            Toast.makeText(Splash.this, "Erro com a conexão ao servidor", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateUI(final FirebaseUser user) {
        try {
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if(mAuth.getCurrentUser() != null) {
                            if (dataSnapshot.child(Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail())).exists()) {
                                mAuth.updateCurrentUser(user);
                                mGoogleSignInClient.silentSignIn();
                                Intent intent = new Intent(Splash.this, MainActivity.class);
                                startActivity(intent);
                                Splash.this.finish();

                                mRefCoordenadas = FirebaseDatabase.getInstance().getReference("usuarios/" + getIdentificadorUsuario() + "/dados");
                                mRefCoordenadas.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String endereco = dataSnapshot.child("endereco").getValue().toString();
                                        Double lat = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                                        Double lng = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());

                                        //endereco padrao p pesquisar
                                        coordenadaSelecionada.setLat(lat);
                                        coordenadaSelecionada.setLng(lng);
                                        coordenadaSelecionada.setEnd(endereco);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });

                            } else if (!dataSnapshot.child(Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail())).exists()) {
                                //NÃO CADASTRADO
                                Intent intent = new Intent(Splash.this, Login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                Splash.this.finish();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        //NÃO CADASTRADO
                        Intent intent = new Intent(Splash.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        Splash.this.finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Intent intent = new Intent(Splash.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    Splash.this.finish();
                }
            });




        } catch (Exception e) {
            e.printStackTrace();
            mAuth.updateCurrentUser(user);
            Intent intent = new Intent(Splash.this, localUsuarioActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            Splash.this.finish();

        }
    }

}
