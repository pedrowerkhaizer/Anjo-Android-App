package com.werkhaizer.anjo;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import utils.Localizador;
import utils.atualizarEndereco;
import utils.enderecoEditar;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import static utils.enderecoEditar.getTitle;
import static utils.enderecoEditar.getBairro;
import static utils.enderecoEditar.getCidade;
import static utils.enderecoEditar.getRua;
import static utils.enderecoEditar.getUf;
import static utils.enderecoEditar.setLatitude;
import static utils.enderecoEditar.setLongitude;

public class confirmarEditarEnderecoFragment extends Fragment {

    private EditText mUf, mCidade, mRua, mApelido, mBairro;
    private Button btnDone, btnVoltar;


    public confirmarEditarEnderecoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_confirmar_editar_endereco, container, false);

        mUf = v.findViewById(R.id.txtEditarUF);
        mCidade = v.findViewById(R.id.txtEditarCidade);
        mRua = v.findViewById(R.id.txtEditarRua);
        mApelido = v.findViewById(R.id.txtEditarTitle);
        btnDone = v.findViewById(R.id.btnDoneEditarEnd);
        btnVoltar = v.findViewById(R.id.btnVoltarCompletarEditar);
        mBairro = v.findViewById(R.id.txtEditarBairro);

        if (getUf() != null) {
            mUf.setText(getUf());
        }
        if(getCidade() != null){
            mCidade.setText(getCidade());
        }
        if (getRua() != null){
            mRua.setText(getRua());
        }
        if (getTitle() != null)
        {
            mApelido.setText(getTitle());
        }
        if(getBairro() != null){
            mBairro.setText(getBairro());
        }

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar();
                Localizador localizador = new Localizador(getActivity());
                LatLng local = localizador.getCoordenada(mRua.getText().toString()+ ", " +mBairro.getText().toString()+", " + mCidade.getText().toString()+", " +mUf.getText().toString());
                Double latitude = local.latitude;
                Double longitude = local.longitude;
                setLatitude(String.valueOf(latitude));
                setLongitude(String.valueOf(longitude));
                atualizarEndereco attEnd = new atualizarEndereco();
                attEnd.atualizarEnderecoAdicional();


                getActivity().finish();
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.viewPagerEditarEndereco,  new editarCepFragment()).commit();
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
}
