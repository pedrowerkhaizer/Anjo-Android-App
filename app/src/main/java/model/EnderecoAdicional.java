package model;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import config.ConfiguracaoFirebase;
import helper.Base64Custom;

import static helper.UsuarioFirebase.getIdentificadorUsuario;

public class EnderecoAdicional {
    private String title;
    private String cep;
    private String rua;
    private String bairro;
    private String cidade;
    private String uf;
    private String latitude;
    private String longitude;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public EnderecoAdicional() {
    }

    public void salvar(){

        String keyEncoded = Base64Custom.codificarBase64(getLatitude()+ " " + getLongitude());
        DatabaseReference usuario = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios").child(getIdentificadorUsuario()).child("endAdicional").child(keyEncoded);

        usuario.setValue( this );

    }

    public void atualizarEnderecoPlus(){

            String identificadorUsuario = getIdentificadorUsuario();
            DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();

            DatabaseReference usuariosRef = database.child("usuarios")
                    .child( identificadorUsuario ).child("dados").child("endAdicional");

            Map<String, Object> valoresEnd = converterParaMapEndPlus();

            usuariosRef.updateChildren( valoresEnd );

        }

        @Exclude
        public Map<String, Object> converterParaMapEndPlus(){

            HashMap<String, Object> endPlus = new HashMap<>();
            endPlus.put("title", getTitle());
            endPlus.put("cep", getCep());
            endPlus.put("rua", getRua());
            endPlus.put("bairro", getBairro());
            endPlus.put("cidade", getCidade());
            endPlus.put("uf", getUf());
            endPlus.put("latitude", getLatitude());
            endPlus.put("longitude", getLongitude());


            return endPlus;

    }

}
