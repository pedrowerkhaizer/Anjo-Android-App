package helper;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import config.ConfiguracaoFirebase;

import static helper.UsuarioFirebase.getIdentificadorUsuario;

public class ConsultasHelper {
    private String email_paciente;
    private String crm;
    private String dia;
    private String horario;
    private String especialidade;
    private String obs_paciente;
    private String status;
    private String comentario;
    private String horarioFormatado;
    private String lat;
    private String lng;


    public ConsultasHelper() {
    }

    public void salvar() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        //id(Base64) = data da consulta + hora da consulta + email do paciente + crm do medico
        String id = Base64Custom.codificarBase64(dia + "+" + horario + "+" + email_paciente + "+" + crm);

        //Salvando todas as informaçoes no nó geral
        DatabaseReference consultas = firebaseRef.child("consultas")
                .child(id);
        consultas.setValue(this);

        //Salvando o id no nó do usuario
        DatabaseReference consultasUsuario = firebaseRef.child("usuarios")
                .child(getIdentificadorUsuario())
                .child("consultas")
                .child(status)
                .child(id);
        consultasUsuario.setValue(id);

        //salvando o id no nó do médico
        DatabaseReference consultasMedico = firebaseRef.child("Especialidades")
                .child(especialidade)
                .child("medicos")
                .child(crm)
                .child("consultas")
                .child(status)
                .child(id);
        consultasMedico.setValue(id);

        //salvando o horario marcado no nó medico
        String horarioConsulta = dia + " - " + horario;
        DatabaseReference horarioMedico = firebaseRef.child("Especialidades")
                .child(especialidade)
                .child("medicos")
                .child(crm)
                .child("horarios")
                .child("marcados")
                .child(horarioConsulta);
        horarioMedico.setValue(horarioConsulta);
    }


    public void atualizarStatus() {
        Map<String, Object> valoresEnd = statusMap();

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        //id(Base64) = data da consulta + hora da consulta + email do paciente + crm do medico
        String id = Base64Custom.codificarBase64(dia + "+" + horario + "+" + email_paciente + "+" + crm);


        //alterar o status no nó do usuario
        DatabaseReference consultasUsuario = firebaseRef.child("usuarios")
                .child(getIdentificadorUsuario())
                .child("consultas");
        consultasUsuario.limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (status.equals("A")) {
                    //Checando se existe o caminho e se o id da consulta está inserido nele
                    try {
                        consultasUsuario.child("F").child(id).removeValue();

                        //salvando no novo nó de status do usuario
                        consultasUsuario.child(status).child(id).setValue(id);

                        //alterando o status no nó geral
                        DatabaseReference consultas = firebaseRef.child("consultas")
                                .child(id);
                        consultas.updateChildren(valoresEnd);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                    //Checando se existe o caminho e se o id da consulta está inserido nele
                    try {
                        consultasUsuario.child("C").child(id).removeValue();

                        //salvando no novo nó de status do usuario
                        consultasUsuario.child(status).child(id).setValue(id);

                        //alterando o status no nó geral
                        DatabaseReference consultas = firebaseRef.child("consultas")
                                .child(id);
                        consultas.updateChildren(valoresEnd);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                if (status.equals("C")) {
                    //Checando se existe o caminho e se o id da consulta está inserido nele
                    try {
                        consultasUsuario.child("F").child(id).removeValue();

                        //salvando no novo nó de status do usuario
                        consultasUsuario.child(status).child(id).setValue(id);

                        //alterando o status no nó geral
                        DatabaseReference consultas = firebaseRef.child("consultas")
                                .child(id);
                        consultas.updateChildren(valoresEnd);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    //Checando se existe o caminho e se o id da consulta está inserido nele
                    try {
                        consultasUsuario.child("A").child(id).removeValue();

                        //salvando no novo nó de status do usuario
                        consultasUsuario.child(status).child(id).setValue(id);

                        //alterando o status no nó geral
                        DatabaseReference consultas = firebaseRef.child("consultas")
                                .child(id);
                        consultas.updateChildren(valoresEnd);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                if (status.equals("F")) {
                    //Checando se existe o caminho e se o id da consulta está inserido nele
                    try {
                        consultasUsuario.child("A").child(id).removeValue();

                        //salvando no novo nó de status do usuario
                        consultasUsuario.child(status).child(id).setValue(id);

                        //alterando o status no nó geral
                        DatabaseReference consultas = firebaseRef.child("consultas")
                                .child(id);
                        consultas.updateChildren(valoresEnd);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    //Checando se existe o caminho e se o id da consulta está inserido nele
                    try {
                        consultasUsuario.child("C").child(id).removeValue();

                        //salvando no novo nó de status do usuario
                        consultasUsuario.child(status).child(id).setValue(id);

                        //alterando o status no nó geral
                        DatabaseReference consultas = firebaseRef.child("consultas")
                                .child(id);
                        consultas.updateChildren(valoresEnd);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //alterando o status no nó do médico
        DatabaseReference consultasMedico = firebaseRef.child("Especialidades")
                .child(especialidade)
                .child("medicos")
                .child(crm)
                .child("consultas");
        consultasMedico.limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!getStatus().equals("A") && dataSnapshot.child("A").exists()) {
                    if (dataSnapshot.child("A").child(id).exists()) {
                        consultasMedico.child("A").child(id).removeValue();
                    }
                }
                if (!getStatus().equals("C") && dataSnapshot.child("C").exists()) {
                    if (dataSnapshot.child("C").child(id).exists()) {
                        consultasMedico.child("C").child(id).removeValue();
                    }
                }
                if (!getStatus().equals("F") && dataSnapshot.child("F").exists()) {
                    if (dataSnapshot.child("F").child(id).exists()) {
                        consultasMedico.child("F").child(id).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        consultasMedico.child(status).child(id).setValue(id);

    }


    @Exclude
    public Map<String, Object> statusMap() {

        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("status", getStatus());


        return usuarioMap;

    }


    public String getHorarioFormatado() {
        return horarioFormatado;
    }

    public void setHorarioFormatado(String horarioFormatado) {
        this.horarioFormatado = horarioFormatado;
    }

    public String getEmail_paciente() {
        return email_paciente;
    }

    public void setEmail_paciente(String email_paciente) {
        this.email_paciente = email_paciente;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getObs_paciente() {
        return obs_paciente;
    }

    public void setObs_paciente(String obs_paciente) {
        this.obs_paciente = obs_paciente;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }


}