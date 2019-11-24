package com.werkhaizer.anjo;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.werkhaizer.anjo.adapter.mensagensAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import config.ConfiguracaoFirebase;
import de.hdodenhof.circleimageview.CircleImageView;
import helper.Base64Custom;
import helper.RecyclerItemClickListener;
import model.Mensagem;

import static helper.UsuarioFirebase.getIdentificadorUsuario;
import static utils.Vibrar.vibrar;
import static utils.selectedMedicData.setCrm;
import static utils.selectedMedicData.setEspecialidade;

public class chatMedActivity extends AppCompatActivity {

    private String crm, especialidade;
    private DatabaseReference mRef;
    private CircleImageView civMed;
    private TextView mNomeMed;
    private LinearLayout containerMedProfile;
    private FloatingActionButton fabEnviar;
    private Button btnVoltar;
    private EditText editMensagem;

    private DatabaseReference mRefMessages;
    private RecyclerView recyclerMensagens;
    private mensagensAdapter adapter;
    private List<Mensagem> mensagens = new ArrayList<>();
    private ChildEventListener childEventListenerMensagens;

    private Boolean isDeleted = false;

    private ClipboardManager myClipboard;
    private ClipData myClip;
    private int hasData = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_med);

        Bundle getCrm = getIntent().getExtras();
        if (getCrm != null) {
            crm = getCrm.getString("crm");
            especialidade = getCrm.getString("especialidade");
        }

        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        civMed = findViewById(R.id.civMedChat);
        mNomeMed = findViewById(R.id.title_nome_chat);
        btnVoltar = findViewById(R.id.btnBackChat);
        fabEnviar = findViewById(R.id.fabEnviarMensagem);
        editMensagem = findViewById(R.id.txtMensagemChat);
        containerMedProfile = findViewById(R.id.title_chat_med);
        recyclerMensagens = findViewById(R.id.rvChat);

        mRefMessages = ConfiguracaoFirebase.getFirebaseDatabase().child("chat")
                .child(getIdentificadorUsuario() + Base64Custom.codificarBase64(crm))
                .child("messages");


        containerMedProfile.setOnClickListener(v -> {
            vibrar(chatMedActivity.this);
            Intent intent = new Intent(chatMedActivity.this, PerfilMedicoActivity.class);
            setCrm(crm);
            setEspecialidade(especialidade);
            startActivity(intent);
        });


        if (!crm.isEmpty() && !especialidade.isEmpty()) {
            mRef = FirebaseDatabase.getInstance().getReference("Especialidades")
                    .child(especialidade)
                    .child("medicos")
                    .child(crm);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String nomeMed = dataSnapshot.child("nome_med").getValue().toString();
                    String fotoMed = dataSnapshot.child("foto_med").getValue().toString();

                    mNomeMed.setText(nomeMed);
                    Picasso.get().load(fotoMed).into(civMed);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        btnVoltar.setOnClickListener(v ->
        {
            vibrar(chatMedActivity.this);
            chatMedActivity.this.finish();
        });

        fabEnviar.setOnClickListener(v -> {
            vibrar(chatMedActivity.this);
            verificarMensagem();
        });


        //Config do adapter
        adapter = new mensagensAdapter(mensagens, getApplicationContext());

        //Config rv
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMensagens.setLayoutManager(layoutManager);
        recyclerMensagens.setHasFixedSize(true);
        recyclerMensagens.setAdapter(adapter);
        recyclerMensagens.scrollToPosition(mensagens.size() - 1);


        recyclerMensagens.addOnItemTouchListener(new RecyclerItemClickListener(chatMedActivity.this, recyclerMensagens, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {
                vibrar(chatMedActivity.this);
                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(chatMedActivity.this);
                // add a list
                String[] options = {"Copiar", "Excluir"};
                builder.setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // copiar
                            TextView txtMsgBalao = findViewById(R.id.lblMensagemTexto);
                            String text = txtMsgBalao.getText().toString();
                            myClip = ClipData.newPlainText("text", text);
                            myClipboard.setText(text);
                            myClipboard.setPrimaryClip(myClip);
                            Toast.makeText(chatMedActivity.this, "Texto copiado para área de transferência!", Toast.LENGTH_SHORT).show();

//                            setClipboard(chatMedActivity.this, editMensagem.getText().toString());
                            break;
                        case 1: // excluir
                            try {
                                TextView textData = view.findViewById(R.id.lblDataMensagem);
                                String data = textData.getText().toString();
                                //substituindo a mensagem por "mensagem excluída"
                                TextView txtMsg = view.findViewById(R.id.lblMensagemTexto);
                                txtMsg.setText(getString(R.string.vcApagouMsg));
                                excluirMensagem(data);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                });
                // MENSAGEM DO SISTEMA EU N DEIXO APARECER O DIALOG
                try {
                    TextView textData = view.findViewById(R.id.lblDataMensagem);
                    String data = textData.getText().toString();
                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));


    }

    private void verificarMensagem() {

        String textoMensagem = editMensagem.getText().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Date data_atual = calendar.getTime();

        String data = dateFormat.format(data_atual);


        if (!textoMensagem.isEmpty()) {
            mRefMessages.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                            if (dataSnap.getKey().contains(data)) {
                                hasData++;
                            }
                        }
                        if (hasData == 0) {
                            enviarSys();
                            enviarMensagem();
                        } else {
                            enviarMensagem();
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } else {

        }

    }

    private void enviarSys() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
        SimpleDateFormat dateFormatSys = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, -1);
        Date data_atual = calendar.getTime();

        String data = dateFormat.format(data_atual);
        String dataSys = dateFormatSys.format(data_atual);

        Mensagem mensagem = new Mensagem();
        Chat chat = new Chat();

        mensagem.setId_usu(getIdentificadorUsuario());
        chat.setId_usu(getIdentificadorUsuario());

        mensagem.setId_med(crm);
        chat.setId_med(crm);

//        mensagem.setEspecialidade(especialidade);
//        chat.setEspecialidade(especialidade);

        mensagem.setData(dataSys);
        chat.setData(dataSys);

        mensagem.setId_remetente("sys");
        chat.setId_remetente("sys");

        chat.sendMessage();

    }

    private void excluirMensagem(String data) {
        Mensagem mensagem = new Mensagem();
        Chat chat = new Chat();

        mensagem.setId_usu(getIdentificadorUsuario());
        chat.setId_usu(getIdentificadorUsuario());

        mensagem.setId_med(crm);
        chat.setId_med(crm);

        mensagem.setMensagem(null);
        chat.setMensagem(null);

        mensagem.setDeleted(false);
        chat.setDeleted(false);

        mensagem.setData(data);
        chat.setData(data);

        mensagem.setId_remetente(getIdentificadorUsuario());
        chat.setId_remetente(getIdentificadorUsuario());

        chat.apagarMensagem();
        isDeleted = true;
        adapter.notifyAll();
        mensagens.notifyAll();
//        recarregarMensagens();

    }

    private void enviarMensagem() {

        String textoMensagem = editMensagem.getText().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Date data_atual = calendar.getTime();

        String data = dateFormat.format(data_atual);

        Mensagem mensagem = new Mensagem();
        Chat chat = new Chat();

        mensagem.setId_usu(getIdentificadorUsuario());
        chat.setId_usu(getIdentificadorUsuario());

        mensagem.setId_med(crm);
        chat.setId_med(crm);
//        mensagem.setEspecialidade(especialidade);
//        chat.setEspecialidade(especialidade);

        mensagem.setMensagem(textoMensagem);
        chat.setMensagem(textoMensagem);

        mensagem.setDeleted(false);
        chat.setDeleted(false);

        mensagem.setData(data);
        chat.setData(data);

        mensagem.setId_remetente(getIdentificadorUsuario());
        chat.setId_remetente(getIdentificadorUsuario());

        chat.sendMessage();
        isDeleted = false;

        editMensagem.setText(null);
    }


    @Override
    protected void onStart() {
        super.onStart();
        listarMensagens();
        isDeleted = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        limparMensagens();
    }

    private void listarMensagens() {
        childEventListenerMensagens = mRefMessages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (!isDeleted) {
                    Mensagem mensagem = dataSnapshot.getValue(Mensagem.class);
                    mensagens.add(mensagem);
                    recyclerMensagens.smoothScrollToPosition(mensagens.size() - 1);
                    adapter.notifyDataSetChanged();

                } else if (isDeleted) {
                    Mensagem mensagem = dataSnapshot.getValue(Mensagem.class);
                    mensagens.add(mensagem);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                recarregarMensagens();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void recarregarMensagens() {
        isDeleted = true;
        mensagens.clear();
        mRefMessages.removeEventListener(childEventListenerMensagens);
        listarMensagens();
    }

    private void limparMensagens() {
        isDeleted = false;
        mensagens.clear();
        mRefMessages.removeEventListener(childEventListenerMensagens);
    }


    private void setClipboard(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {

            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }


    private class Chat {
        private String id_usu;
        private String id_med;
        private String id_remetente;
        private String mensagem;
        private String data;
        private Boolean deleted;

        public Chat() {
        }

        public void sendMessage() {
            DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
            DatabaseReference chatRef = firebaseRef.child("chat")
                    .child(id_usu + Base64Custom.codificarBase64(id_med))
                    .child("messages")
                    .child(data.replace("/", "-"));
//                    .push();
            chatRef.setValue(this);

//            DatabaseReference chatUsu = firebaseRef.child("usuarios")
//                    .child(id_usu)
//                    .child("chats")
//                    .child(data);
//            chatUsu.setValue(data);
//
//            DatabaseReference chatMed = firebaseRef.child("Especialidades")
//                    .child(especialidade)
//                    .child("medicos")
//                    .child(id_med)
//                    .child("chats")
//                    .child(data);
//            chatMed.setValue(data);
        }

        public void apagarMensagem() {
            DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
            DatabaseReference chatRef = firebaseRef.child("chat")
                    .child(id_usu + Base64Custom.codificarBase64(id_med))
                    .child("messages")
                    .child(data.replace("/", "-"));

            Map<String, Object> valoresMensagem = converterParaMap();
            chatRef.updateChildren(valoresMensagem);
        }

        @Exclude
        public Map<String, Object> converterParaMap() {
            HashMap<String, Object> mensagemMap = new HashMap<>();
            mensagemMap.put("id_remetente", getId_remetente() + "del");
            mensagemMap.put("data", getData());
            mensagemMap.put("mensagem", null);
            mensagemMap.put("deleted", true);

            return mensagemMap;
        }

        public Boolean getDeleted() {
            return deleted;
        }

        public void setDeleted(Boolean deleted) {
            this.deleted = deleted;
        }


        public void setId_usu(String id_usu) {
            this.id_usu = id_usu;
        }

        public void setId_med(String id_med) {
            this.id_med = id_med;
        }

        public String getId_remetente() {
            return id_remetente;
        }

        public void setId_remetente(String id_remetente) {
            this.id_remetente = id_remetente;
        }

        public String getMensagem() {
            return mensagem;
        }

        public void setMensagem(String mensagem) {
            this.mensagem = mensagem;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
