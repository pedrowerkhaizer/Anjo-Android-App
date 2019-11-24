package com.werkhaizer.anjo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import helper.Base64Custom;
import model.Usuario;
import utils.coordenadaSelecionada;

import static helper.UsuarioFirebase.getIdentificadorUsuario;
import static utils.Vibrar.vibrar;

/**
 * Demonstrate Firebase Authentication using a Google ID Token.
 */
public class Login extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;

    private Usuario usuario = new Usuario();
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

    private DatabaseReference mRefCoordenadas;
    private Dialog mDialog;

    private Base64Custom cod = new Base64Custom();
    private DatabaseReference mRef = mFirebaseDatabase.getReference("usuarios");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.btnTerms).setOnClickListener(this);

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }
    // [END on_start_check_user]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                final GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }

                    // [START_EXCLUDE]
                    // [END_EXCLUDE]
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> updateUI(null));
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                task -> updateUI(null));
        mGoogleSignInClient.signOut();
        mGoogleSignInClient.revokeAccess();
    }

    private void updateUI(final FirebaseUser user) {
        if (user != null) {
            try {
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail())).exists()) {
                            mAuth.updateCurrentUser(user);
                            mRefCoordenadas = FirebaseDatabase.getInstance().getReference("usuarios/" + getIdentificadorUsuario() + "/dados");
                            mRefCoordenadas.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String endereco = dataSnapshot.child("endereco").getValue().toString();
                                    Double lat = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                                    Double lng = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());

                                    coordenadaSelecionada.setLat(lat);
                                    coordenadaSelecionada.setLng(lng);
                                    coordenadaSelecionada.setEnd(endereco);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                            Intent intent = new Intent(Login.this, MainActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            mDialog.dismiss();
                            Login.this.finish();

                        } else {
                            //NÃO CADASTRADO
                            cadastrarUsu();
                            Toast.makeText(Login.this, "Complete o cadastro para continuar", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        cadastrarUsu();
                        Toast.makeText(Login.this, "Usuário não encontrado, complete o cadastro para continuar", Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
                mAuth.updateCurrentUser(user);
                mRefCoordenadas = FirebaseDatabase.getInstance().getReference("usuarios/" + getIdentificadorUsuario() + "/dados");
                mRefCoordenadas.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String endereco = dataSnapshot.child("endereco").getValue().toString();
                        Double lat = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                        Double lng = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());

                        coordenadaSelecionada c = new coordenadaSelecionada();
                        coordenadaSelecionada.setLat(lat);
                        coordenadaSelecionada.setLng(lng);
                        coordenadaSelecionada.setEnd(endereco);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                Intent intent = new Intent(Login.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Login.this.finish();

            }
        } else {
            mGoogleSignInClient.revokeAccess();
            mAuth.signOut();
        }

    }


    @Override
    public void onClick(View v) {
        vibrar(this);
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            signIn();
            openDialog();
        } else if (i == R.id.btnTerms) {
            Toast.makeText(this, "Fazer termos de privacidade", Toast.LENGTH_SHORT).show();
        }
    }

    public void openDialog() {
        mDialog = new Dialog(this);
        //vamos remover o titulo da Dialog
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //vamos carregar o xml personalizado
        mDialog.setContentView(R.layout.dialog);
        //Deixar quase transparente o fundo
        try {
            Objects.requireNonNull(mDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.alpha(100)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //permitimos fechar esta dialog
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        //temos a instancia do ProgressBar!
//        final ProgressBar progressBar = ProgressBar.class.cast(mDialog.findViewById(R.id.progressBar));
        final LottieAnimationView lottieLoading = (LottieAnimationView) mDialog.findViewById(R.id.loadingLottie);


        mDialog.show();

        // mDialog.dismiss(); -> para fechar a dialog

    }

    public void cadastrarUsu() {
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            Intent intent = new Intent(Login.this, completarCadastroActivity.class);
            assert user != null;
            Uri fotoUsu = user.getPhotoUrl();
            usuario.setId(Base64Custom.codificarBase64(Objects.requireNonNull(user.getEmail())));
            usuario.setNome(user.getDisplayName());
            usuario.setEmail(user.getEmail());
            usuario.setFoto(String.valueOf(fotoUsu));
            startActivity(intent);
            Login.this.finish();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        try{
            mDialog.dismiss();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}