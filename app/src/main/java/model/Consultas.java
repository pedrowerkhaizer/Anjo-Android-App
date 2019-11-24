package model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import config.ConfiguracaoFirebase;
import helper.Base64Custom;
import helper.UsuarioFirebase;

import static helper.UsuarioFirebase.getIdentificadorUsuario;

public class Consultas {
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

    public Consultas() {
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

    public String getEmail_paciente() {
        return email_paciente;
    }

    public void setEmail_paciente(String email_paciente) {
        this.email_paciente = email_paciente;
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

    public String getHorarioFormatado() {
        return horarioFormatado;
    }

    public void setHorarioFormatado(String horarioFormatado) {
        this.horarioFormatado = horarioFormatado;
    }

}
