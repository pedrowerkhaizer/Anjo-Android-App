package utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import static config.ConfiguracaoFirebase.getFirebaseDatabase;
import static helper.UsuarioFirebase.getIdentificadorUsuario;
import static utils.enderecoEditar.getBairro;
import static utils.enderecoEditar.getCep;
import static utils.enderecoEditar.getCidade;
import static utils.enderecoEditar.getKey;
import static utils.enderecoEditar.getLatitude;
import static utils.enderecoEditar.getLongitude;
import static utils.enderecoEditar.getRua;
import static utils.enderecoEditar.getTitle;
import static utils.enderecoEditar.getUf;

public class atualizarEndereco {

    public void atualizarEnderecoAdicional(){


        DatabaseReference usuariosRef = getFirebaseDatabase().child("usuarios")
                .child( getIdentificadorUsuario() ).child("endAdicional").child(getKey());

        Map<String, Object> valoresEnd = converterParaMapEndPlus();

        usuariosRef.updateChildren(valoresEnd);

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
