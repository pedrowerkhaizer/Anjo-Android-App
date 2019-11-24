package com.werkhaizer.anjo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.werkhaizer.anjo.ViewHolder.listaEnderecosViewHolder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import model.EnderecoAdicional;
import utils.coordenadaSelecionada;

import static helper.UsuarioFirebase.getIdentificadorUsuario;
import static utils.coordenadaSelecionada.*;
import static utils.coordenadaSelecionada.setLat;
import static utils.coordenadaSelecionada.setLng;

public class localUsuarioActivity extends AppCompatActivity {

    private LinearLayout nearMe, nearHome;

    private TextView tvHome;
    private TextView txtGPS;
    private Button btnAddEnd;
    private Button close, add;
//    private Bundle extras = new Bundle();

    private FusedLocationProviderClient mFusedLocationClient;

    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private DatabaseReference mRef;
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();


    private RecyclerView mRecyclerView;
    private DatabaseReference mRefEndereco = mFirebaseDatabase.getReference("usuarios/" + getIdentificadorUsuario() + "/endAdicional");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_usuario);

        Bundle bundle = getIntent().getExtras();

        close = findViewById(R.id.fabClose);
        add = findViewById(R.id.fabAdc);

        mRecyclerView = findViewById(R.id.rvEndAdc);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setNestedScrollingEnabled(false);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

//        try {
//            if (bundle.getString("lista") != null) {
//
//                //deixar sair sem escolher
//                close.setEnabled(true);
//                close.setVisibility(View.VISIBLE);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vibrar();
                        localUsuarioActivity.this.finish();
                    }
                });
//
//            } else if (bundle.getString("lista") == null) {
//                //n deixar sair sem escolher
//                close.setVisibility(View.GONE);
//                close.setEnabled(false);
//            }
//        } catch (Exception e) {
//            close.setVisibility(View.GONE);
//            close.setEnabled(false);
//        }


        tvHome = findViewById(R.id.lblEndHome);
        txtGPS = findViewById(R.id.lblEndGPS);

        nearMe = findViewById(R.id.llNearMe);
        nearHome = findViewById(R.id.llNearHome);
        btnAddEnd = findViewById(R.id.btnBuscarEnd);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar();
                Intent intent = new Intent(localUsuarioActivity.this, cepActivity.class);
                startActivity(intent);
            }
        });

        btnAddEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar();
                Intent intent = new Intent(localUsuarioActivity.this, cepActivity.class);
                startActivity(intent);
            }
        });

        nearMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar();
                LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
                // Verifica se o GPS está ativo
                boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
                // Caso não esteja ativo abre um novo diálogo com as configurações para
                // realizar se ativamento
                if (!enabled) {
                    alertaLocalização();
                } else {
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(localUsuarioActivity.this);
                    Permissoes.validarPermissoes(permissoes, localUsuarioActivity.this, 1);

                    if (ActivityCompat.checkSelfPermission(localUsuarioActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission
                            (localUsuarioActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(localUsuarioActivity.this, new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        // Got last known location. In some rare situations this can be null
                                        if (location != null) {
                                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                            try {
                                                List<Address> listaEndereco = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                                if (listaEndereco != null && listaEndereco.size() > 0) {
                                                    Address address = listaEndereco.get(0);
                                                    String endereco = address.getSubLocality() + ", " + address.getSubAdminArea() + ", " + address.getAdminArea();
                                                    txtGPS.setText(endereco);
                                                    coordenadaSelecionada c = new coordenadaSelecionada();
                                                    setLat(location.getLatitude());
                                                    setLng(location.getLongitude());
                                                    c.setEnd(endereco);
                                                    Intent intent = new Intent(localUsuarioActivity.this, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
//                                                    Toast.makeText(localUsuarioActivity.this, endereco, Toast.LENGTH_SHORT).show();
                                                    localUsuarioActivity.this.finish();
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                Toast.makeText(localUsuarioActivity.this, "Erro, ative a localização e tente novamente", Toast.LENGTH_SHORT).show();
                                                alertaLocalização();
                                            }
                                        }
                                    }
                                });
                        mFusedLocationClient.getLastLocation().addOnFailureListener(localUsuarioActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                alertaLocalização();
                            }
                        });
                        return;
                    } else {
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(localUsuarioActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                    try {
                                        List<Address> listaEndereco = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                        if (listaEndereco != null && listaEndereco.size() > 0) {
                                            Address address = listaEndereco.get(0);
                                            String endereco = address.getSubLocality().replace("Avenida", "Av.")
                                                    .replace("Rua", "R.")
                                                    + ", " + address.getSubAdminArea()
                                                    + ", " + address.getAdminArea();
                                            txtGPS.setText(endereco);
                                            coordenadaSelecionada c = new coordenadaSelecionada();
                                            setLat(location.getLatitude());
                                            setLng(location.getLongitude());
                                            c.setEnd(endereco);
                                            Intent intent = new Intent(localUsuarioActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
//                                            Toast.makeText(localUsuarioActivity.this, endereco, Toast.LENGTH_SHORT).show();
                                            localUsuarioActivity.this.finish();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Toast.makeText(localUsuarioActivity.this, "Erro, ative a localização e tente novamente", Toast.LENGTH_SHORT).show();
                                        alertaLocalização();
                                    }
                                }
                            }
                        });
                    }
                }
            }
//
        });
        nearHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar();
                Intent intent = new Intent(localUsuarioActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                localUsuarioActivity.this.finish();

//                Toast.makeText(localUsuarioActivity.this, tvHome.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });


        mRef = FirebaseDatabase.getInstance().getReference("usuarios/" + getIdentificadorUsuario() + "/dados");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String endereco = dataSnapshot.child("endereco").getValue().toString();
                tvHome.setText(endereco);
                Double lat = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                Double lng = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());

                coordenadaSelecionada c = new coordenadaSelecionada();
                setLat(lat);
                setLng(lng);
                c.setEnd(endereco);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
//
//        mRefresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO CHECAR SE TEM PERMISSÃO, SE A LOCALIZAÇÃO ESTÁ LIGADA E ABRE A TELA DE NOVO
//                LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
//                // Verifica se o GPS está ativo
//                boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
//                // Caso não esteja ativo abre um novo diálogo com as configurações para
//                // realizar se ativamento
//                if (!enabled) {
//                    alertaLocalização();
//                } else {
//                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(localUsuarioActivity.this);
//                    Permissoes.validarPermissoes(permissoes, localUsuarioActivity.this, 1);
//
//                    if (ActivityCompat.checkSelfPermission(localUsuarioActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(localUsuarioActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                        mFusedLocationClient.getLastLocation()
//                                .addOnSuccessListener(localUsuarioActivity.this, new OnSuccessListener<Location>() {
//                                    @Override
//                                    public void onSuccess(Location location) {
//                                        // Got last known location. In some rare situations this can be null
//                                        if (location != null) {
//                                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
//                                            try {
//                                                List<Address> listaEndereco = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                                                if (listaEndereco != null && listaEndereco.size() > 0) {
//                                                    Address address = listaEndereco.get(0);
//                                                    String endereco = address.getSubLocality() + ", " + address.getSubAdminArea() + ", " + address.getAdminArea();
//                                                    txtGPS.setText(endereco);
//                                                    Intent intent = new Intent(localUsuarioActivity.this, MainActivity.class);
////                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                    startActivity(intent);
//                                                    localUsuarioActivity.this.finish();
//                                                }
//                                            } catch (IOException e) {
//                                                e.printStackTrace();
//                                                Toast.makeText(localUsuarioActivity.this, "Erro, ative a localização e tente novamente", Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                    }
//                                });
//                        mFusedLocationClient.getLastLocation().addOnFailureListener(localUsuarioActivity.this, new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(localUsuarioActivity.this, "Deu merda no refresh", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                }
//            }
//        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        listarEnderecosAdc();
        pegarEndAtual();
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        if (ActivityCompat.checkSelfPermission(localUsuarioActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(localUsuarioActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                @Override
//                public void onSuccess(Location location) {
//                    // Got last known location. In some rare situations this can be null.
//                    if (location != null) {
//                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
//                        try {
//                            List<Address> listaEndereco = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                            if (listaEndereco != null && listaEndereco.size() > 0) {
//                                Address address = listaEndereco.get(0);
//                                String endereco = address.getSubLocality() + ", " + address.getSubAdminArea() + ", " + address.getAdminArea();
//                                txtGPS.setText(endereco);
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            });
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {

            //permission denied (negada)
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                //Alerta
                alertaValidacaoPermissao();
            } else if (permissaoResultado == PackageManager.PERMISSION_GRANTED) {
                pegarEndAtual();
            }

        }
    }


    private void alertaValidacaoPermissao() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                vibrar();
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void alertaLocalização() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Localização desligada");
        builder.setMessage("Para utilizar essa função, é necessário ativar sua localização");
        builder.setCancelable(true);
        builder.setPositiveButton("Ativar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                onStart();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void pegarEndAtual() {
//        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
//        // Verifica se o GPS está ativo
//        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        // Caso não esteja ativo abre um novo diálogo com as configurações para
//        // realizar se ativamento
//        if (!enabled) {
//            alertaLocalização();
//        } else {
        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(localUsuarioActivity.this);
            Permissoes.validarPermissoes(permissoes, localUsuarioActivity.this, 1);

            if (ActivityCompat.checkSelfPermission(localUsuarioActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission
                    (localUsuarioActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(localUsuarioActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null
                                if (location != null) {
                                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                    try {
                                        List<Address> listaEndereco = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                        if (listaEndereco != null && listaEndereco.size() > 0) {
                                            Address address = listaEndereco.get(0);
                                            String endereco = address.getSubLocality() + ", " + address.getSubAdminArea() + ", " + address.getAdminArea();
                                            txtGPS.setText(endereco);
                                            coordenadaSelecionada c = new coordenadaSelecionada();
                                            setLat(location.getLatitude());
                                            setLng(location.getLongitude());
                                            c.setEnd(endereco);

                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Toast.makeText(localUsuarioActivity.this, "Erro, ative a localização e tente novamente", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            } else {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(localUsuarioActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            try {
                                List<Address> listaEndereco = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                if (listaEndereco != null && listaEndereco.size() > 0) {
                                    Address address = listaEndereco.get(0);
                                    String endereco = address.getSubLocality() + ", " + address.getSubAdminArea() + ", " + address.getAdminArea();
                                    txtGPS.setText(endereco);
                                    coordenadaSelecionada c = new coordenadaSelecionada();
                                    setLat(location.getLatitude());
                                    setLng(location.getLongitude());
                                    Intent intent = new Intent(localUsuarioActivity.this, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    Toast.makeText(localUsuarioActivity.this, endereco, Toast.LENGTH_SHORT).show();
                                    localUsuarioActivity.this.finish();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(localUsuarioActivity.this, "Erro, ative a localização e tente novamente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }

        } catch (Exception e) {
            alertaLocalização();
        }
    }

    public void listarEnderecosAdc() {
        try {
            FirebaseRecyclerAdapter<EnderecoAdicional, listaEnderecosViewHolder> firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<EnderecoAdicional, listaEnderecosViewHolder>(
                            EnderecoAdicional.class,
                            R.layout.row_endereco_adicional,
                            listaEnderecosViewHolder.class,
                            mRefEndereco
                    ) {

                        @Override
                        protected void populateViewHolder(listaEnderecosViewHolder viewHolder, EnderecoAdicional model, int position) {
                            String endereco = model.getRua().replace("Rua", "R.").replace("Avenida", "Av.") + ", " + model.getBairro() + ", " + model.getCidade() + ", " + model.getUf();
                            viewHolder.setEndereco(localUsuarioActivity.this, model.getTitle(), endereco, model.getLatitude(), model.getLongitude());
                            //TODO CAPTURAR A LAT/LONG do endereco
                        }

                        @Override
                        public listaEnderecosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            listaEnderecosViewHolder enderecosViewHolder = super.onCreateViewHolder(parent, viewType);
                            enderecosViewHolder.setOnClickListener(new listaEnderecosViewHolder.ClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    vibrar();
                                    TextView mEnd = view.findViewById(R.id.lblEndAdc);
                                    TextView mLat = view.findViewById(R.id.lat);
                                    TextView mLng = view.findViewById(R.id.lng);

                                    String end = mEnd.getText().toString();
                                    Double lat = Double.parseDouble(mLat.getText().toString());
                                    Double lng = Double.parseDouble(mLng.getText().toString());

                                    Intent i = new Intent(localUsuarioActivity.this, MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                    //TODO PASSAR LAT/LNG
                                    setLat(lat);
                                    setLng(lng);
                                    setEnd(end);

                                    startActivity(i);
                                    localUsuarioActivity.this.finish();
//                                    Toast.makeText(localUsuarioActivity.this, end, Toast.LENGTH_SHORT).show();
                                }
                            });
                            return enderecosViewHolder;
                        }
                    };
            mRecyclerView.setAdapter(firebaseRecyclerAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void vibrar() {
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
