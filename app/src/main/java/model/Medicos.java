package model;

import androidx.annotation.NonNull;

public class Medicos {
    String cep_med;
    String convenio_med;
    String cpf_med;
    String crm_med;
    String email_med;
    String endereco_med;
    String esp_med;
    String foto_med;
    String nascimento_med;
    String nome_med;
    Double rating_med;
    String sexo_med;
    Double latitude_med;
    Double longitude_med;

    @NonNull
    @Override
    public String toString() {
        return nome_med;
    }

    public Double getLongitude_med() {
        return longitude_med;
    }

    public void setLongitude_med(Double longitude_med) {
        this.longitude_med = longitude_med;
    }


    public Double getLatitude_med() {
        return latitude_med;
    }

    public void setLatitude_med(Double latitude_med) {
        this.latitude_med = latitude_med;
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
