package com.werkhaizer.anjo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

public class adicionarConvenioActivity extends AppCompatActivity {

    private Button btnClose, btnProx, btnAdcConvenio;
    private AutoCompleteTextView mConvenio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_convenio);

        btnClose = findViewById(R.id.btnCloseConvenio);
        btnProx = findViewById(R.id.btnProxNumConvenio);
        btnAdcConvenio = findViewById(R.id.btnAdcNovoConvenio);
        mConvenio = findViewById(R.id.txtNomeConvenio);

        btnClose.setOnClickListener(v -> adicionarConvenioActivity.this.finish());

        btnProx.setOnClickListener(v ->{
            if(!mConvenio.getText().toString().isEmpty()) {
                String strConvenio = mConvenio.getText().toString();
                Intent i = new Intent(adicionarConvenioActivity.this, completarConvenioActivity.class);
                i.putExtra("convenio", strConvenio);
                startActivity(i);
            }
            else {
                Toast.makeText(this, "Escolha um convênio", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
