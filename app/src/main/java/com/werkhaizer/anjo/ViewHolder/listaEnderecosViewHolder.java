package com.werkhaizer.anjo.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.werkhaizer.anjo.R;
import com.werkhaizer.anjo.editarEnderecoActivity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import helper.Base64Custom;

import static helper.UsuarioFirebase.getIdentificadorUsuario;
import static utils.enderecoEditar.setKey;
import static utils.enderecoEditar.setTitle;
import static utils.enderecoEditar.setTitle_antigo;

public class listaEnderecosViewHolder extends RecyclerView.ViewHolder {
    View mView;


    public listaEnderecosViewHolder(@NonNull final View itemView) {
        super(itemView);
        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEndClickListener.onItemClick(v, getAdapterPosition());
            }
        });
    }

    private listaEnderecosViewHolder.ClickListener mEndClickListener;

    public void setEndereco(final Context c, final String nome, String endereco, final String lat, final String lng){
        TextView mNome = mView.findViewById(R.id.lblTitleEnd);
        TextView mEnd = mView.findViewById(R.id.lblEndAdc);
        TextView mLat = mView.findViewById(R.id.lat);
        TextView mLng = mView.findViewById(R.id.lng);
        ImageButton btnEditar = mView.findViewById(R.id.btnEditarLocal);

        mNome.setText(nome);
        mEnd.setText(endereco);
        mLat.setText(lat);
        mLng.setText(lng);
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrar = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 30 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrar.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    vibrar.vibrate(15);
                }
                setTitle(nome);

                String keyEncoded = Base64Custom.codificarBase64(lat +" "+ lng);
                setKey(keyEncoded);

//                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("usuarios")
//                        .child(getIdentificadorUsuario())
//                        .child("endAdicional");
//                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
//                            String endKey = childSnapshot.getKey();
//                            setKey(endKey);
//                            Toast.makeText(c, ""+endKey, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
                Intent i = new Intent(c, editarEnderecoActivity.class);
                c.startActivity(i);
            }
        });
    }

    //interface to send callbacks
    public interface ClickListener {
        void onItemClick(View view, int position);

    }

    public void setOnClickListener(listaEnderecosViewHolder.ClickListener clickListener){

        mEndClickListener = clickListener;
    }



}
