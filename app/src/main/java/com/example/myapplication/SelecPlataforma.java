package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

public class SelecPlataforma extends AppCompatActivity {

    ListView lista;
    ImageButton btnTodas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plataforma);

        lista = (ListView)findViewById(R.id.list_plata);
        btnTodas = findViewById(R.id.btn_todas);
        // Obtener tipo de estreno seleccionado (estrenados o proximos)
        String tipoEstreno = getIntent().getStringExtra("tipoEstreno");

        String[] itemNames = {"Netflix", "HBO", "Disney", "Prime"};
        Integer[] itemImages = {R.drawable.logo_netflix_removebg_preview, R.drawable.logo_hbo_removebg_preview, R.drawable.logo_disney1_removebg_preview, R.drawable.logo_prime_removebg_preview};

        list_plataformas_adaptador adapter = new list_plataformas_adaptador(this, itemNames, itemImages, tipoEstreno);
        lista.setAdapter(adapter);
        btnTodas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarTodas(); // Llamar al método mostrarTodas
            }
        });
    }

    private void mostrarTodas() {
        Intent intent = new Intent(SelecPlataforma.this, PlataformaActivity.class);
        intent.putExtra("tipoEstreno", getIntent().getStringExtra("tipoEstreno"));
        intent.putExtra("plataforma", "todas");
        startActivity(intent);
    }

    public void atras (View view){
        finish();
    }
}