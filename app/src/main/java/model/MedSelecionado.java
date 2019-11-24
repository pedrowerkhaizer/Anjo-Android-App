package model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import java.util.HashMap;
import java.util.Map;
import config.ConfiguracaoFirebase;
import helper.UsuarioFirebase;

public class MedSelecionado {
    private String cep_med;
    private String convenio_med;
    private String cpf_med;
    private String crm_med;
    private String email_med;
    private String endereco_med;
    private String esp_med;
    private String foto_med;
    private String nascimento_med;
    private String nome_med;
    private Double rating_med;
    private String sexo_med;
    private Double latitude_med;
    private Double longitude_med;

    public void salvar(){
        String id_usu = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference medicoFav = firebaseRef.child("usuarios/").child(id_usu).child("med_fav").child(getCrm_med());

        medicoFav.setValue( this );

    }

    public void excluir(){
        String id_usu = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference medicoFav = firebaseRef.child("usuarios/").child(id_usu).child("med_fav").child(getCrm_med());

        medicoFav.removeValue();
    }

    public void atualizar(){

        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference medicoFav = database.child("usuarios/")
                .child(identificadorUsuario)
                .child("med_fav")
                .child(getCrm_med());

        Map<String, Object> valoresMedFav = converterParaMap();

        medicoFav.updateChildren( valoresMedFav );

    }

    @Exclude
    public Map<String, Object> converterParaMap(){

        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email_med", getEmail_med());
        usuarioMap.put("nome_med", getNome_med() );
        usuarioMap.put("foto_med", getFoto_med() );
        usuarioMap.put("cep_med", getCep_med());
        usuarioMap.put("convenio_med" , getConvenio_med());
        usuarioMap.put("endereco_med", getEndereco_med());
        usuarioMap.put("esp_med", getEsp_med());
        usuarioMap.put("rating_med", getRating_med());
        usuarioMap.put("crm_med", getCrm_med());
        usuarioMap.put("nascimento_med", getNascimento_med());
        usuarioMap.put("sexo_med", getSexo_med());
        usuarioMap.put("cpf_med", getCpf_med());
        usuarioMap.put("latitude_med", getLatitude_med());
        usuarioMap.put("longitude_med", getLongitude_med());


        return usuarioMap;

    }

    public Double getLatitude_med() {
        return latitude_med;
    }

    public void setLatitude_med(Double latitude_med) {
        this.latitude_med = latitude_med;
    }

    public Double getLongitude_med() {
        return longitude_med;
    }

    public void setLongitude_med(Double longitude_med) {
        this.longitude_med = longitude_med;
    }

    public String getCep_med() {
        return cep_med;
    }

    public void setCep_med(String cep_med) {
        this.cep_med = cep_med;
    }

    public String getConvenio_med() {
        return convenio_med;
    }

    public void setConvenio_med(String convenio_med) {
        this.convenio_med = convenio_med;
    }

    public String getCpf_med() {
        return cpf_med;
    }

    public void setCpf_med(String cpf_med) {
        this.cpf_med = cpf_med;
    }

    public String getCrm_med() {
        return crm_med;
    }

    public void setCrm_med(String crm_med) {
        this.crm_med = crm_med;
    }

    public String getEmail_med() {
        return email_med;
    }

    public void setEmail_med(String email_med) {
        this.email_med = email_med;
    }

    public String getEndereco_med() {
        return endereco_med;
    }

    public void setEndereco_med(String endereco_med) {
        this.endereco_med = endereco_med;
    }

    public String getEsp_med() {
        return esp_med;
    }

    public void setEsp_med(String esp_med) {
        this.esp_med = esp_med;
    }

    public String getFoto_med() {
        return foto_med;
    }

    public void setFoto_med(String foto_med) {
        this.foto_med = foto_med;
    }

    public String getNascimento_med() {
        return nascimento_med;
    }

    public void setNascimento_med(String nascimento_med) {
        this.nascimento_med = nascimento_med;
    }

    public String getNome_med() {
        return nome_med;
    }

    public void setNome_med(String nome_med) {
        this.nome_med = nome_med;
    }

    public Double getRating_med() {
        return rating_med;
    }

    public void setRating_med(Double rating_med) {
        this.rating_med = rating_med;
    }

    public String getSexo_med() {
        return sexo_med;
    }

    public void setSexo_med(String sexo_med) {
        this.sexo_med = sexo_med;
    }
}
