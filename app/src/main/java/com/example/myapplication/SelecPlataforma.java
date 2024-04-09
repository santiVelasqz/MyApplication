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
    Button btn_ajustes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plataforma);
        btn_ajustes = findViewById(R.id.btn_ajustes);


        lista = (ListView)findViewById(R.id.list_plata);

        // Obtener tipo de estreno seleccionado (estrenados o proximos)
        String tipoEstreno = getIntent().getStringExtra("tipoEstreno");

        String[] itemNames = {"Netflix", "HBO", "Disney", "Prime", "todas"};
        Integer[] itemImages = {R.drawable.logo_netflix_removebg_preview, R.drawable.logo_hbo_removebg_preview, R.drawable.logo_disney1_removebg_preview, R.drawable.logo_prime_removebg_preview, R.drawable.botontodas};

        list_plataformas_adaptador adapter = new list_plataformas_adaptador(this, itemNames, itemImages, tipoEstreno);
        lista.setAdapter(adapter);
        //ESTE BOTON ENVIA AL USUARIO A UNA ACTIVITY DONDE HAY OPCIONES DE AJUSTES DE LA CUENTA
        btn_ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelecPlataforma.this, AjustesActivity.class);
                startActivity(intent);
            }
        });
    }

    public void atras (View view){
        finish();
    }
}