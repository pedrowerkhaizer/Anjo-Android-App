package model;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import config.ConfiguracaoFirebase;
import helper.UsuarioFirebase;


public class Usuario {

    private String id;
    private String nome;
    private String email;
    private String foto;
    private String cpf;
    private String cep;
    private String nascimento;
    private String sexo;
    private String convenio;
    private String convenioNum;
    private String endereco;
    private String enderecoPlus;
    private String senha;
    private Double latitude;
    private Double longitude;


    public Usuario() {
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuario = firebaseRef.child("usuarios").child(getId()).child("dados");

        usuario.setValue( this );

    }

    public void atualizar(){

        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference usuariosRef = database.child("usuarios")
                .child( identificadorUsuario ).child("dados");

        Map<String, Object> valoresUsuario = converterParaMap();

        usuariosRef.updateChildren( valoresUsuario );

    }

    public void atualizarConvenio(){

        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference usuariosRef = database.child("usuarios")
                .child( identificadorUsuario ).child("dados");

        Map<String, Object> valoresEnd = convenioMap();

        usuariosRef.updateChildren( valoresEnd );

    }

    @Exclude
    public Map<String, Object> converterParaMap(){

        HashMap<String, Object> usuarioMap = new HashMap<>();
//        usuarioMap.put("email", getEmail() );
        usuarioMap.put("nome", getNome() );
        usuarioMap.put("foto", getFoto() );
//        usuarioMap.put("cep", getCep());
        usuarioMap.put("convenio" , getConvenio());
        usuarioMap.put("convenioNum" , getConvenioNum());
//        usuarioMap.put("latitude", getLatitude());
//        usuarioMap.put("longitude", getLongitude());
//        usuarioMap.put("endereco", getEndereco());
//        usuarioMap.put("enderecoPlus", getEnderecoPlus());


        return usuarioMap;

    }

    @Exclude
    public Map<String, Object> convenioMap(){

        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("convenio" , getConvenio());
        usuarioMap.put("convenioNum" , getConvenioNum());


        return usuarioMap;

    }


    @Exclude
    public Map<String, Object> converterParaMapEndPlus(){

        HashMap<String, Object> endPlus = new HashMap<>();
        endPlus.put("enderecoPlus", getEnderecoPlus());

        return endPlus;

    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }


    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNascimento() {
        return nascimento;
    }

    public void setNascimento(String nascimento) {
        this.nascimento = nascimento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getConvenio() {
        return convenio;
    }

    public void setConvenio(String convenio) {
        this.convenio = convenio;
    }

    public String getConvenioNum() {
        return convenioNum;
    }

    public void setConvenioNum(String convenioNum) {
        this.convenioNum = convenioNum;
    }


    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
    public String getEnderecoPlus() {
        return enderecoPlus;
    }

    public void setEnderecoPlus(String enderecoPlus) {
        this.enderecoPlus = enderecoPlus;
    }
}
