package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelecEstreno extends AppCompatActivity {
    Button btn_ajustes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selec_estreno);

        // Inicialización del botón dentro de onCreate()
        btn_ajustes = findViewById(R.id.btn_ajustes);

        Button btnestrenados = findViewById(R.id.btn_estrenados);
        Button btnproximos = findViewById(R.id.btn_proximos);
        Button btnhoy = findViewById(R.id.btn_estrenos_hoy);

        btnestrenados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a SelecPlataforma para ver estrenos pasados
                Intent intent = new Intent(SelecEstreno.this, SelecPlataforma.class);
                intent.putExtra("tipoEstreno", "estrenados");
                startActivity(intent);
            }
        });

        btnhoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a SelecPlataforma para ver estrenos pasados
                Intent intent = new Intent(SelecEstreno.this, PlataformaActivity.class);
                intent.putExtra("tipoEstreno", "hoy");
                startActivity(intent);
            }
        });

        btnproximos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a SelecPlataforma para ver próximos estrenos
                Intent intent = new Intent(SelecEstreno.this, SelecPlataforma.class);
                intent.putExtra("tipoEstreno", "proximos");
                startActivity(intent);
            }
        });
        //ESTE BOTON ENVIA AL USUARIO A UNA ACTIVITY DONDE HAY OPCIONES DE AJUSTES DE LA CUENTA
        btn_ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelecEstreno.this, AjustesActivity.class);
                startActivity(intent);
            }
        });
    }



    public void atras (View view){
        finish();
    }
}

